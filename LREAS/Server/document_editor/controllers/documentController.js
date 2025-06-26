const Document = require("../models/Document");
const getPermissions = require("../services/documentService");

exports.getDocument = async (req, res) => {
    try {
      const { docId } = req.params;
      let document = await Document.findOne({ docId });
  
      if (!document) {

        res.status(404).json({ message: "Document not found"});

      } 
  
      res.json(document);
    } catch (error) {
      console.error("Error fetching document:", error);
      res.status(500).json({ error: "Failed to load document" });
    }
  };

exports.saveDocument = async (req, res) => {
  try {
    const { content } = req.body;
    let doc = await Document.findById(req.params.id);

    if (doc) {
      doc.content = content;
      doc.lastUpdated = Date.now();
    } else {
      doc = new Document({ _id: req.params.id, content });
    }

    await doc.save();
    res.json(doc);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};
