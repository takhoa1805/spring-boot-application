const mongoose = require("mongoose");

const DocumentSchema = new mongoose.Schema({
  content: { type: String, required: true , default: ""},
  currentEditors: { type: [String], default: [] },
  updatedAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model("Document", DocumentSchema);
