const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");
const http = require("http");
const morgan = require("morgan");
const { WebSocketServer } = require("ws");
const { Server } = require("socket.io");
const {socketHandler} = require("./utils/socketIo");
const { instrument } = require("@socket.io/admin-ui");
const { connectRabbitMQ, pushToQueue, popAllAnswers } = require("./utils/rabbitmq");

require("dotenv").config();




const connectDB = require("./config/db");
const routes = require("./routes/routes");


const app = express();
const server = http.createServer(app);


// const io = new Server(server);
const io = new Server(server, {
  cors: {
    origin: "*", // Allow all origins
    methods: ["GET", "POST"]
  }
});
instrument(io, {
  auth: false,
  mode: "development",
});

connectRabbitMQ();
socketHandler(io);


app.use(morgan('tiny'));

// Middleware
// app.use(cors());
app.use(cors({
  origin: "*", // Allow all origins
  methods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
  allowedHeaders: ["Content-Type", "Authorization"]
}));
app.use(express.json());


// Routes
app.use("/", routes);

const PORT = process.env.PORT || 5001;
connectDB();
server.listen(PORT,"0.0.0.0", () => console.log(`🚀 Server running on port ${PORT}`));
