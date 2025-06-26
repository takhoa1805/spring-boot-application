const mongoose = require('mongoose');

const QuizVersionSchema = new mongoose.Schema({
    quiz_id: { type: mongoose.Schema.Types.UUID,required: true },
    date_updated: { type: Date, default: Date.now },
    is_game: { type: Boolean, default: false },
    date_started: { type: Date },
    date_ended: { type: Date },
    max_players: { type: Number },
    time_limit: { type: Number },
    title: { type: String, required: true, default:"untitled" },
    show_correct_answer: { type: Boolean, default: false },
    allowed_attempts: { type: Number, default: 1 },
    description: { type: String },
    shuffle_answers: { type: Boolean, default: false },
    questions: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Question' ,default:[]}]
}, { timestamps: true });

module.exports = mongoose.model('QuizVersion', QuizVersionSchema,'quizVersion');
