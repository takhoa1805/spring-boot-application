const express = require("express");
const router = express.Router();
const {createNewSession,getSession,getSessionResult} = require("../controllers/controllers");



router.get("/test", async (req, res) => {
  try {

      res.json("hello world");
  } catch (err) {
      res.status(500).json({ error: "Server error" });
  }
});


router.post("/sessions/new", createNewSession);

router.get("/sessions/join/:sessionId", getSession);

router.get("/sessions/result/:sessionMongoId",getSessionResult)


module.exports = router;
