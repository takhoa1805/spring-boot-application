const {enterRoom
    ,closeHall
    ,calculateScoresAndLeaderBoard
    ,getNextQuestion,
    getCorrectAnswers

} = require('../services/services');

const { createQueue, pushToQueue, popAllAnswers } = require("./rabbitmq");

  
function sleep(ms) {
    return new Promise((resolve) => {
        setTimeout(resolve, ms);
    });
    }

function socketHandler(io) {
  io.on('connection', (socket) => {
    console.log('a user connected');
    socket.on('disconnect', () => {
      console.log('user disconnected');
    });

    socket.on('student_enter', (msg) => {
        enterRoom({userId:msg.userId,sessionCode:msg.sessionCode,alias:msg.alias,isTeacher:false}).then((result)=>{
            if(result.error){
                socket.emit('error',result.error);
            }else{
                // console.log('Player joined:',result.session);
                socket.join(msg.sessionCode);
                socket.emit('player',result.player);
                socket.to(msg.sessionCode).emit('new_player_joined',result.session.players);
                console.log("new player joined: ",result.session.players);
            }
        });
    });

    socket.on('teacher_enter',(msg)=>{
        enterRoom({userId:msg.userId,sessionCode:msg.sessionCode,alias:msg.alias,isTeacher:true}).then((result)=>{
            if(result.error){
                socket.emit('error',result.error);
            }else{
                socket.join(msg.sessionCode);
                console.log("Teacher joined:",result.session);  
            }
        });
    });

    socket.on("start_quiz", async (msg) => {
        try {
            const closeResult = await closeHall({ sessionCode: msg.sessionCode });
            if (closeResult.error) {
                socket.emit("error", closeResult.error);
                return;
            }
            socket.join(msg.sessionCode);


            // Create a queue for this quiz session
            await createQueue(msg.sessionCode);

            const nextQuestionResult = await getNextQuestion({ sessionCode: msg.sessionCode });
            if (nextQuestionResult.error) {
                socket.emit("error", nextQuestionResult.error);
                return;
            }
            console.log('nextQuestionResult',nextQuestionResult);


            const nextQuestion = {
                questionId: nextQuestionResult.question._id,
                title: nextQuestionResult.question.title,
                score: nextQuestionResult.question.score,
                quiz_version: nextQuestionResult.question.quiz_version,
                answers: nextQuestionResult.question.answers.map((answer) => ({ answerId:answer._id,text: answer.text })),
            };

            // await sleep(2000);
            if (nextQuestion){
                socket.to(msg.sessionCode).emit("next_question", nextQuestion);
                socket.emit("next_question", nextQuestion);
                return;
            }   else {
                socket.to(msg.sessionCode).emit("quiz_ended", {mesasge:"Quiz ended",sessionMongoId:nextQuestionResult?.sessionMongoId});
                socket.emit("quiz_ended", {mesasge:"Quiz ended",sessionMongoId:nextQuestionResult?.sessionMongoId});
                console.log("Quizz ended for session: ",msg.sessionCode);
                return;
            }
      

        } catch (error) {
            console.error("❌ Error starting quiz:", error);
            socket.emit("error", "Failed to start quiz.");
        }
    });

    socket.on('time_over',async (msg)=>{
        socket.to(msg.sessionCode).emit('answering_time_over',{message:"Time over"});
        socket.emit('answering_time_over',{message:"Time over"});


        getCorrectAnswers({sessionCode:msg.sessionCode}).then((result)=>{
            if(result.error){
                socket.emit('error',result.error);
            }
            console.log('result.answers',result.answers);
            socket.to(msg.sessionCode).emit('answers',result.answers);
            socket.emit('answers',result.answers);

        });


    }
    );

    socket.on('leaderboard',async(msg)=>{
        // Pop all answers from the queue
        const allAnswers = await popAllAnswers(msg.sessionCode);
        if (!allAnswers || allAnswers?.length === 0) {
            console.warn(`⚠️ No answers found for session: ${msg.sessionCode}`);
        }

        console.log('allAnswers',allAnswers);
        // Pass answers to the function that calculates scores and updates leaderboard
        const leaderBoardResult = await calculateScoresAndLeaderBoard({
            sessionCode: msg.sessionCode,
            answers: allAnswers,
        });

        if (leaderBoardResult.error) {
            socket.emit("error", leaderBoardResult.error);
            return;
        }

        // Emit updated leaderboard
        socket.to(msg.sessionCode).emit("leaderboard", leaderBoardResult);
        socket.emit("leaderboard", leaderBoardResult);
        console.log("leaderBoardResult.leaderBoard",leaderBoardResult);
    });

    socket.on('next',(msg)=>{
        getNextQuestion({sessionCode:msg.sessionCode}).then((result)=>{
            if(result.error){
                socket.emit('error',result.error);
                return;
            }
            if (!result.question){
                socket.to(msg.sessionCode).emit('quiz_ended',{message:"Quiz ended",sessionMongoId:result?.sessionMongoId});
                socket.emit('quiz_ended',{message:"Quiz ended",sessionMongoId:result?.sessionMongoId});
                console.log("Quizz ended for session: ",msg.sessionCode);
                return;
            }
            const nextQuestion = {
                questionId: result.question._id,
                title:result.question.title,
                score: result.question.score,
                quiz_version: result.question.quiz_version,
                answers: result.question.answers.map((answer)=>{
                    return {
                        answerId:answer._id,text:answer.text,
                    }
                })
            }
            // console.log('Next question:',nextQUestion);
            if (nextQuestion){
                console.log("nextQuestion",nextQuestion);
                socket.to(msg.sessionCode).emit('next_question',nextQuestion);
                socket.emit("next_question", nextQuestion);
                return;
            }   else {
                console.log("Quiz ended");
                socket.to(msg.sessionCode).emit("quiz_ended", {mesasge:"Quiz ended",sessionMongoId:result?.sessionMongoId});
                socket.emit("quiz_ended", {mesasge:"Quiz ended",sessionMongoId:result?.sessionMongoId});
                console.log("Quizz ended for session: ",msg.sessionCode);
                return;
            }
            

        });
    });

    socket.on('quiz_answer',(msg)=>{
        socket.to(msg.sessionCode).emit('answer_received',msg.answer);
        socket.emit('answer_received',msg.answer);
        pushToQueue({sessionCode:msg.sessionCode,answerData:{
            userId:msg.userId,
            questionId:msg.questionId,
            answerId:msg.answerId,
            time: msg.time
        }});
    });

  });

}


module.exports = {socketHandler};