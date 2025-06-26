const Session = require("../models/Session");
const QuizVersion = require("../models/QuizVersion");
const Question = require("../models/Question");
const Answer = require("../models/Answer");
const generateRandomCode = require("../utils/codeGenerator");

exports.createNewSession = async (req, res) => {
    try {
        const quizVersionId = req.body.quizVersionId;
        const sessionCode = generateRandomCode();

        const quiz = await QuizVersion.findById(quizVersionId);

        let bulkUpdates = [];
        const questions = await Question.find({ quiz_version: quizVersionId }).sort({ position: 1 });


        // Append answers to question
        for (var question of questions){
            const foundAnswers = await Answer.find({ question: question._id }).select("_id");

            if (foundAnswers.length > 0) {
                bulkUpdates.push({
                    updateOne: {
                        filter: { _id: question._id },
                        update: { $set: { answers: foundAnswers.map(ans => ans._id) } }
                    }
                });
            }

        }

        // Perform bulk update in a single operation
        if (bulkUpdates.length > 0) {
            await Question.bulkWrite(bulkUpdates);
        }

        quiz.questions = questions;
        await quiz.save();


        const firstQuestion = quiz?.questions[0] ? await Question.findById(quiz.questions[0]._id) : null;



        if (!quiz) {
            res.status(500).json({ error: "Error loading quiz" });
        }

        const newSession = await Session.create({ 
            workflowState: 'WAITING',
            sessionCode: sessionCode,
            quizVersionId: quizVersionId,
            players: [],
            leaderBoard: [],
            currentQuestion: null,
            nextQuestion: firstQuestion
        });

        res.json(newSession);
        }
    catch (error) {
        console.error("Error creating session:", error);
        res.status(500).json({ error: "Failed to create session" });
        }
    }

exports.getSessionResult= async (req, res) => {
    try {
        const { sessionMongoId } = req.params;
        const session = await Session.findOne({ _id: sessionMongoId})
                .populate('leaderBoard.player') // Populating the player field inside the leaderBoard array
                .exec();
        

        if (!session) {
            return res.status(404).json({ message: "Session not found" });
        }   else {
            console.log("Get session result",session?.leaderBoard);
            return res.json({
            leaderBoard: session?.leaderBoard,
            }
        )}

        


    } catch(error) {
        console.error("Error fetching session:", error);
        res.status(500).json({ error: "Failed to load session" });
    }
}

// exports.createNewSession = async (req, res) => {
//     try {
//         const quizVersionId = req.body.quizVersionId;
//         const sessionCode = generateRandomCode();
//         const quiz = await QuizVersion.findById(quizVersionId);

//         if (!quiz) {
//             return res.status(404).json({ error: "Quiz not found" });
//         }

//         const questions = await Question.find({ quiz_version: quizVersionId });
//         if (!questions.length) {
//             return res.status(404).json({ error: "No questions found for this quiz" });
//         }

//         // Append answers to questions efficiently
//         const bulkUpdates = [];
//         for (const question of questions) {
//             const foundAnswers = await Answer.find({ question: question._id }).select("_id");

//             if (foundAnswers.length > 0) {
//                 bulkUpdates.push({
//                     updateOne: {
//                         filter: { _id: question._id },
//                         update: { $set: { answers: foundAnswers.map(ans => ans._id) } }
//                     }
//                 });
//             }
//         }

//         if (bulkUpdates.length > 0) {
//             await Question.bulkWrite(bulkUpdates);
//         }

//         quiz.questions = questions.map(q => q._id); // Store only references
//         await quiz.save();

//         // Ensure firstQuestion exists before proceeding
//         const firstQuestion = questions.length > 0 ? questions[0] : null;

//         const newSession = await Session.create({
//             workflowState: "WAITING",
//             sessionCode: sessionCode,
//             quizVersionId: quizVersionId,
//             players: [],
//             leaderBoard: [],
//             currentQuestion: null,
//             nextQuestion: firstQuestion ? firstQuestion._id : null
//         });

//         res.json(newSession);
//     } catch (error) {
//         console.error("Error creating session:", error);
//         res.status(500).json({ error: "Failed to create session" });
//     }
// };


exports.getSession = async (req, res) => {
    try {
        const { sessionId } = req.params;
        const session = await Session.findOne({ sessionCode: sessionId, workflowState: "WAITING" });
        console.log("session",session);

        if (!session) {
            return res.status(404).json({ message: "Session not found" });
        }

        return res.json({msg:"Okay"});
    } catch (error) {
        console.error("Error fetching session:", error);
        res.status(500).json({ error: "Failed to load session" });
    }
}


// const Document = require("../models/Document");
// const getPermissions = require("../services/documentService");

// exports.getDocument = async (req, res) => {
//     try {
//       const { docId } = req.params;
//       let document = await Document.findOne({ docId });
  
//       if (!document) {
//         //// If document doesn't exist, create a new one
//         // document = new Document({
//         //   docId,
//         //   content: JSON.stringify([{ type: "paragraph", content: [] }]), // Default ProseMirror content
//         // });
//         // await document.save();

//         res.status(404).json({ message: "Document not found"});

//       } 
  
//       res.json(document);
//     } catch (error) {
//       console.error("Error fetching document:", error);
//       res.status(500).json({ error: "Failed to load document" });
//     }
//   };

// exports.saveDocument = async (req, res) => {
//   try {
//     const { content } = req.body;
//     let doc = await Document.findById(req.params.id);

//     if (doc) {
//       doc.content = content;
//       doc.lastUpdated = Date.now();
//     } else {
//       doc = new Document({ _id: req.params.id, content });
//     }

//     await doc.save();
//     res.json({ message: "Document saved successfully" });
//   } catch (error) {
//     res.status(500).json({ error: error.message });
//   }
// };
