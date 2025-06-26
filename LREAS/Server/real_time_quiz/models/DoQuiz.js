const mongoose = require('mongoose');

const DoQuizSchema = new mongoose.Schema({
    quizVersionId: { type: mongoose.Schema.Types.ObjectId, ref: 'QuizVersion', required: true },
    user: { type: String, required: true },
    startTime: { type: Date },
    time_limit: { type: Number }, // in seconds
    submitTime: { type: Date },
    totalScore: { type: Number },
    maxScore: { type: Number }
}, { timestamps: true });

module.exports = mongoose.model('DoQuiz', DoQuizSchema,'doQuiz');
