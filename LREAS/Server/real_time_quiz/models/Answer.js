const mongoose = require('mongoose');

const AnswerSchema = new mongoose.Schema({
    question: { type: mongoose.Schema.Types.ObjectId, ref: 'Question', required: true },
    text: { type: String, required: true },
    is_correct: { type: Boolean, default: false }
}, { timestamps: true });

module.exports = mongoose.model('Answer', AnswerSchema,'answer');
