const mongoose = require('mongoose');

const SessionSchema = new mongoose.Schema({
    // quizId: { type: mongoose.Schema.Types.ObjectId, ref: 'Quiz', required: true },
    // dateUpdated: { type: Date, default: Date.now },
    // isGame: { type: Boolean, default: false },
    // dateStarted: { type: Date },
    // dateEnded: { type: Date },
    // maxPlayers: { type: Number },
    // time_limit: { type: Number },
    // title: { type: String, required: true },
    // showCorrectAnswer: { type: Boolean, default: false },
    // allowedAttempts: { type: Number, default: 1 },
    // description: { type: String },
    // shuffleAnswers: { type: Boolean, default: false },
    // questions: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Question' }],

    workflowState: { type: String, default: 'NOT_AVAILABLE' }, // NOT_AVAILABLE, AVAILABLE, STARTED, ENDED, WAITING
    sessionCode: { type: String},
    quizVersionId: { type: mongoose.Schema.Types.ObjectId, ref: 'QuizVersion', required: true },
    players: [
        {
            player:{type: mongoose.Schema.Types.ObjectId, ref: 'Player'},
            alias: { type: String, required: true },
            doQuiz: { type: mongoose.Schema.Types.ObjectId, ref: 'DoQuiz' },
            score: { type: Number },
            answers: [
                {
                    question: { type: mongoose.Schema.Types.ObjectId, ref: 'Question', required: true },
                    answer: { type: mongoose.Schema.Types.ObjectId, ref: 'Answer', required: true },
                    answerTimestamp: { type: Date }
                }
            ],
            rank: { type: Number }
        }
    ], defaul:[],
    currentQuestion: { type: mongoose.Schema.Types.ObjectId, ref: 'Question' },
    nextQuestion: { type: mongoose.Schema.Types.ObjectId, ref: 'Question' },
    leaderBoard: [
        {
            player: { type: mongoose.Schema.Types.ObjectId, ref: 'Player' },
            score: { type: Number }
        }
    ],default:[],


}, { timestamps: true });

module.exports = mongoose.model('Session', SessionSchema);
