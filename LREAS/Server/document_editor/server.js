const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");
const http = require("http");
const morgan = require("morgan");
const { WebSocketServer } = require("ws");
require("dotenv").config();




const connectDB = require("./config/db");
const documentRoutes = require("./routes/documentRoutes");

const app = express();
const server = http.createServer(app);


app.use(morgan('tiny'));

// Middleware
// app.use(cors());

app.use(
    cors({
      origin: "*",
      methods: ["GET", "POST"],
      allowedHeaders: ["Content-Type"],
    })
  );
app.use(express.json());


// Routes
app.use("/", documentRoutes);

// Start WebSocket Server
const wss = new WebSocketServer({ server });
// console.log("hello world");
wss.on("connection", (ws) => {
  console.log("✅ WebSocket connected");
  const editTimeouts = new Map();  

  ws.on("message", async (message) => {
    try {
      const { username,docId, content } = JSON.parse(message);
      console.log(`📥 Received update for doc: ${docId} from user ${username}`, content);

        const tmpDoc = await require("./models/Document").findById(docId);

      // Broadcast updates to all clients
      wss.clients.forEach((client) => {
        if (client !== ws && client.readyState === 1) {
          client.send(JSON.stringify({ docId, content ,currentEditors:tmpDoc.currentEditors||[]}));
          console.log("📤 Broadcasted update to other clients`);")
        }
      });


      // Save to database
      await require("./models/Document").findOneAndUpdate(
        { _id:docId },
        { content, updatedAt: new Date() },
        { upsert: true }
      );


        // 2) Update currentEditors
      const key = `${docId}:${username}`;

      // If first time or already removed, push username into currentEditors
      await require("./models/Document").findByIdAndUpdate(
        docId,
        { $addToSet: { currentEditors: username } }
      );

      // 3) Reset the 10-second timer
      if (editTimeouts.has(key)) {
        clearTimeout(editTimeouts.get(key));
      }
      const timeout = setTimeout(async () => {
        // remove after 10s of inactivity
        const tmpDoc = await require("./models/Document").findByIdAndUpdate(
          docId,
          { $pull: { currentEditors: username } }
        );
        editTimeouts.delete(key);

        // Broadcast updates to all clients
        wss.clients.forEach((client) => {
          if (client !== ws && client.readyState === 1) {
            client.send(JSON.stringify({ docId, content:tmpDoc.content,updateEditors:true ,currentEditors:tmpDoc.currentEditors||[]}));
            console.log("📤 Broadcasted update current editors`);")
          }
        });


      }, 10_000);

      editTimeouts.set(key, timeout);




    } catch (error) {
      console.error("Error processing message:", error);
    }
  });

  ws.on("close", () => console.log("❌ WebSocket disconnected"));
});

// Start HTTP + WebSocket Server
const PORT = process.env.PORT || 5000;
connectDB();
server.listen(PORT,"0.0.0.0", () => console.log(`🚀 Server running on port ${PORT}`));
