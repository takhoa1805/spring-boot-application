const WebSocket = require("ws");
const Document = require("../models/Document");

let clients = {};

const setupWebSocket = (server) => {
  const wss = new WebSocket.Server({ server });

  wss.on("connection", (ws) => {
    ws.on("message", async (message) => {
      const { docId, content } = JSON.parse(message);

      if (!clients[docId]) {
        clients[docId] = new Set();
      }
      clients[docId].add(ws);

      await Document.findByIdAndUpdate(docId, { content, lastUpdated: Date.now() }, { upsert: true });

      clients[docId].forEach((client) => {
        if (client !== ws && client.readyState === WebSocket.OPEN) {
          client.send(JSON.stringify({ docId, content }));
        }
      });
    });

    ws.on("close", () => {
      for (const docId in clients) {
        clients[docId].delete(ws);
      }
    });
  });

  console.log("WebSocket server started");
};

module.exports = setupWebSocket;
