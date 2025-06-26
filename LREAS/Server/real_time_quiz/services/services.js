const Session = require('../models/Session');
const Question = require('../models/Question'); // <-- important
const Answer = require('../models/Answer');     // <-- important
const Player = require('../models/Player');
const QuizVersion = require('../models/QuizVersion');


async function enterRoom({userId,sessionCode,alias,isTeacher}){

    if (!userId || !sessionCode || !alias) {
        return {error: "userId, sessionCode and alias are required"};
    }

    const session = await Session.findOne({sessionCode:sessionCode});
    if (!session) {
        return {error: "Session not found"};
    }

    if (session.workflowState.toLowerCase() !== 'waiting') {
        return {error: "Session is not available"};
    }

    if (isTeacher) {

        return {player:null,session:session};
    }   else{
        const player = new Player({userId,alias,session:session._id});
        session.players.push({player:player._id,alias:alias});
        await session.save();
        await player.save();
        return {player,session:session};
    } 

}

async function closeHall({sessionCode}){
    const session = await Session.findOne({sessionCode:sessionCode});

    if (!session) {
        return {error: "Session not found"};
    }

    if (session.workflowState.toLowerCase() !== 'waiting') {
        return {error: "Session is not available"};
    }
    session.workflowState = 'STARTED';

    const leaderBoard =[];
    for (var player of session.players){
        const data = {
            player:player.player._id,
            score:0
        }
        leaderBoard.push(data);
    }

    session.leaderBoard = leaderBoard;


    await session.save();
    return {message:"Quiz started"};
}


async function calculateScoresAndLeaderBoard({sessionCode,answers}){
    const session = await Session.findOne({ sessionCode })
            .populate('leaderBoard.player') // Populating the player field inside the leaderBoard array
            .exec();

    if (!session) {
        return {error: "Session not found"};
    } else if (session.workflowState.toLowerCase() !== 'started') {
        return {message: "Session is not available or has already ended"};
    }

    
    var sortedAnswers =(answers.length>0) ? answers.sort((a, b) => new Date(a.time) - new Date(b.time)) : [];
    var leaderBoard = session?.leaderBoard;

    if(!leaderBoard){
        return {error: "Cannot get leader board"};

    }

    var givenScore = 1000;

    for (var sortedAnswer of sortedAnswers){
        console.log('sortedAnswer',sortedAnswer);
        const answer = await Answer.findOne({_id:sortedAnswer.answerId,question:sortedAnswer.questionId});

        if (givenScore<0){
            break;
        }

        if (!answer){
            // return session.leaderBoard;
            continue;
        }

        if (answer.is_correct){

            const foundPlayer = await Player.findOne({userId:sortedAnswer.userId,session:session._id});
            if (!foundPlayer){
                continue;
            }


            var playerIndex = leaderBoard.findIndex(player => player.player._id.toString() === foundPlayer._id.toString());

            console.log('foundPlayer',foundPlayer);
            console.log('playerIndex',playerIndex);
            if (playerIndex == -1){
                continue;
            }
            leaderBoard[playerIndex].score += givenScore;

            console.log('Updated leaderboard',leaderBoard);
    
            givenScore--;
        }


    }

    session.leaderBoard = leaderBoard;

    await session.save();

    return session.leaderBoard;

    // Check if this is the first question and there is nothing on the leader board

}

async function getNextQuestion({sessionCode}){
    // const session = await Session.findOne({sessionCode:sessionCode});
    const session = await Session.findOne({sessionCode:sessionCode})
    .populate({
      path: 'nextQuestion',
      populate: {
        path: 'answers', // populate answers inside nextQuestion
      }
    }).populate({
        path:'quizVersionId',
        populate:{
            path:'questions'
        }
    });
    //load next question
    // const quizVersion = await 
  
    if (!session) {
        return {error: "Session not found"};
    } else if (session.workflowState.toLowerCase() !== 'started') {
        return {message: "Session is not available or has already ended"};
    }


    if (session.nextQuestion === null) {
        session.workflowState = 'ENDED';
        session.currentQuestion=null;
        await session.save();
        return {question:null,sessionMongoId:session._id};
    }

    const upComingQuestionIndex = session.quizVersionId.questions.findIndex((question)=>
        question._id.toString() === session.nextQuestion._id.toString()
    )+1;


    const upComingQuestion = session.quizVersionId.questions.length === upComingQuestionIndex ? null : session.quizVersionId.questions[upComingQuestionIndex];

    const res = {question:session.nextQuestion,sessionMongoId:session._id};

    session.currentQuestion = session.nextQuestion;
    session.nextQuestion = upComingQuestion;


    await session.save();



    return res;
}


async function getCorrectAnswers({sessionCode}){
    const session = await Session.findOne({sessionCode:sessionCode})
    .populate({
      path: 'currentQuestion',
      populate: {
        path: 'answers', // populate answers inside nextQuestion
      }
    });

    if (!session) {
        return {error: "Session not found"};
    } else if (session.workflowState.toLowerCase() !== 'started') {
        return {message: "Session is not available or has already ended"};
    }

    return {
        answers: session.currentQuestion.answers
    }
}

async function updateAnswer({sessionCode,userId,answerId,questionId}){
    const session = await Session.findOne({sessionCode:sessionCode});
    if (!session) {
        return {error: "Session not found"};
    } else if (session.workflowState.toLowerCase() !== 'started') {
        return {message: "Session is not available or has already ended"};
    }
    const players = session.players;
    // const player = session.players.find((player)=>player.player._id.toString() === userId);
    if (!player) {
        return {error: "Player not found"};
    }

    for (var player of players){
        if (player.player._id.toString() === userId){
            player.answers.push({
                question:questionId,
                answer:answerId,
                answerTimestamp:new Date()
            });
        }
    }

    session.players = players;

    await session.save();

    return {answers:player.answers};

}


module.exports = {enterRoom,closeHall,calculateScoresAndLeaderBoard,getNextQuestion,getCorrectAnswers,updateAnswer};