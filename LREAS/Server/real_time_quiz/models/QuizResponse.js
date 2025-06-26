const mongoose = require('mongoose');

const QuizResponseSchema = new mongoose.Schema({
    question: { type: mongoose.Schema.Types.ObjectId, ref: 'Question', required: true },
    answer: { type: mongoose.Schema.Types.ObjectId, ref: 'Answer', required: true },
    doQuizId: { type: String, required: true },
    score: { type: Number }
}, { timestamps: true });

module.exports = mongoose.model('QuizResponse', QuizResponseSchema,'quizResponse');
