const mongoose = require('mongoose');

const QuestionSchema = new mongoose.Schema({
    quiz_version: { type: mongoose.Schema.Types.ObjectId, ref: 'QuizVersion', required: true },
    time_limit: { type: Number },
    date_updated: { type: Date, default: Date.now },
    score: { type: Number,default:100 },
    date_created: { type: Date, default: Date.now },
    position: { type: Number ,default:0},
    title: { type: String, required: true , default:"untitled"},
    image: { type: String }, // Image path
    answers: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Answer',default:[] }]
}, { timestamps: true });

module.exports = mongoose.model('Question', QuestionSchema,'question');
