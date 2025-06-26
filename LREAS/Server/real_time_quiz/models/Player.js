const mongoose = require('mongoose');

const PlayerSchema = new mongoose.Schema({
    userId: { type: mongoose.Schema.Types.UUID,required: true },
    alias: { type: String, required: true },
    session: { type: mongoose.Schema.Types.ObjectId, ref: 'Session', required: true },
}, { timestamps: true });

module.exports = mongoose.model('Player', PlayerSchema,'player');
