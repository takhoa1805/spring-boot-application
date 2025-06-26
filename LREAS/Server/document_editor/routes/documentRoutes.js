const express = require("express");
const Document = require("../models/Document");
const router = express.Router();
const {getPermissions} = require("../services/documentService");
// Get document editor by ID
router.get("/:docId/editor", async (req, res) => {
  console.log('Getting doc editor id: ', req.params.docId);
  try {

      const hasPermission = await getPermissions({
          token: req.headers.authorization,
          mongoId: req.params.docId,
          readPermission: true,
          writePermission: true
      });

      if (!hasPermission) return res.status(403).json({ error: "Permission denied" });

      const doc = await Document.findById(req.params.docId);
      if (!doc) return res.status(404).json({ message: "Document not found" });

      res.json(doc);
  } catch (err) {
      res.status(500).json({ error: "Server error" });
  }
});

// Get document viewer by ID
router.get("/:docId/viewer", async (req, res) => {
  console.log('Getting doc viewer id: ', req.params.docId);
  try {
      const hasPermission = await getPermissions({
          token: req.headers.authorization,
          mongoId: req.params.docId,
          readPermission: true,
          writePermission: false
      });

      if (!hasPermission) return res.status(403).json({ error: "Permission denied" });

      const doc = await Document.findById(req.params.docId);
      if (!doc) return res.status(404).json({ message: "Document not found" });

      res.json(doc);
  } catch (err) {
      res.status(500).json({ error: "Server error" });
  }
});


// Save or update document content
router.post("/:docId", async (req, res) => {
    console.log('Getting doc id: ',req.params.docId);

  try {
    const { content } = req.body;
    const updatedDoc = await Document.findOneAndUpdate(
      { _id: req.params.docId },
      { content, updatedAt: new Date() },
      { new: true, upsert: true }
    );
    console.log("Document updated");
    res.json(updatedDoc);
  } catch (err) {
    res.status(500).json({ error: "Server error" });
  }
});

router.post("/new", async (req, res) => {
    try {
      const document = new Document();
      await document.save();
      res.json(document);
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  });

module.exports = router;
