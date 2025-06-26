import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Header } from "../../../../components/Header";
import "../styles/EnterCompetitiveQuizPage.css";
import { getToken, parseJwt } from "../../../../utils";
import { URL, sendMessage } from "../../../../utils/socket";
import Question from "./Question";
import LeaderBoard from "./LeaderBoard";
import Typography from '@mui/material/Typography';
import CircularProgress from '@mui/material/CircularProgress';
import { io } from 'socket.io-client';





export default function QuizHall({ sessionCode,alias }) {
    const socket = io(URL);
    const JWT_loginToken = getToken();
    const userInfo = parseJwt(JWT_loginToken);
    const navigate = useNavigate();    

    const [isConnected, setIsConnected] = useState(socket.connected);
    const [players, setPlayers] = useState([]);
    const [isStarted, setIsStarted] = useState(false);
    const [currentQuestion, setCurrentQuestion] = useState(null);
    const [leaderBoard, setLeaderBoard] = useState(null);
    const [isTimeOver, setIsTimeOver] = useState(false);
    const [isQuizEnded, setIsQuizEnded] = useState(false);
    const [showAnswer, setShowAnswer] = useState(false);
    

    const [correctAnswers, setCorrectAnswers] = useState([]);
    const [isCurrentAnswerCorrect, setIsCurrentAnswerCorrect] = useState(false);    
    const [currentAnswer,setCurrentAnswer] = useState(null);


    useEffect(() => {
        function onConnect() {
            setIsConnected(true);
        }

        function onDisconnect() {
            setIsConnected(false);
        }

        function onNewPlayerJoined(players) {
            setPlayers(players);
            console.log("players", players);
        }

        function onNextQuestion(question) {
            setCurrentQuestion(question);
            setLeaderBoard(null);
            setIsTimeOver(false);
            setIsQuizEnded(false);
            setIsStarted(true);

            setCurrentAnswer(null);
            setShowAnswer(false);
            setIsCurrentAnswerCorrect(false);
            // setAnswerCount(0);
        }

        function onQuizEnded(res) {
            console.log("quiz ended");
            setCurrentQuestion(null);
            setIsQuizEnded(true);
            // setLeaderBoard(true);
            setIsTimeOver(false);
            setIsStarted(false);
            // setAnswerCount(0);
            navigate(`/quiz/competitive/result/${res.sessionMongoId}`)
            
        }

        function onTimeOver() {
            setIsTimeOver(true);
            setCurrentQuestion(null);
            setLeaderBoard(null);
            setIsQuizEnded(false);
            setIsStarted(true);
            // setAnswerCount(0);

        }

        function onLeaderBoard(leaderBoard) {
            setLeaderBoard(leaderBoard);
            setCurrentQuestion(null);
            setIsTimeOver(false);
            setIsQuizEnded(false);
            setIsStarted(true);
            // console.log(leaderBoard);
            // setAnswerCount(0);
        }

        function onAnswerReceived(data) {
            for (let answer of data){
                if (answer.is_correct){
                    console.log(currentAnswer,answer);
                    if (currentAnswer?.answerId == answer._id){
                        setIsCurrentAnswerCorrect(true);
                    }
                    setCorrectAnswers(correctAnswers => [...correctAnswers, answer]);
                }
            }
            console.log("show correct answers",correctAnswers);
            setShowAnswer(true);
        }

        socket.on("answers", onAnswerReceived);
        socket.on("connect", onConnect);
        socket.on("disconnect", onDisconnect);
        socket.on("new_player_joined", onNewPlayerJoined);
        socket.on("next_question", onNextQuestion);
        socket.on("quiz_ended", onQuizEnded);
        socket.on("time_over", onTimeOver);
        socket.on("leaderboard", onLeaderBoard);


        sendMessage({
            message: {
                sessionCode: sessionCode,
                userId: userInfo.id,
                alias: alias
            },
            event: "student_enter",
            socket
        });

        return () => {
            socket.off("connect", onConnect);
            socket.off("disconnect", onDisconnect);
            socket.off("new_player_joined", onNewPlayerJoined);
            socket.off("next_question", onNextQuestion);
            socket.off("answers", onAnswerReceived);

        };
    }, [sessionCode, userInfo.id]);

    const handleStartQuiz = () => {
        sendMessage({
            message: { sessionCode },
            event: "start_quiz",
            socket
        });

        setIsStarted(true);
    };


    return (
        <>
            <Header />

            {leaderBoard ? (
                <LeaderBoard
                    leaderBoard={leaderBoard}
                    currentQuestion={currentQuestion}
                    sessionCode={sessionCode}
                />
            ) : currentQuestion && isStarted ? (
                <Question
                    currentQuestion={currentQuestion}
                    socket={socket}
                    sessionCode={sessionCode}
                    alias={alias}
                    userInfo={userInfo}
                    showAnswer={showAnswer}
                    setShowAnswer={setShowAnswer}
                    correctAnswers={correctAnswers} 
                    setCorrectAnswers={setCorrectAnswers}
                    isCurrentAnswerCorrect={isCurrentAnswerCorrect}
                    setIsCurrentAnswerCorrect={setIsCurrentAnswerCorrect}
                    currentAnswer={currentAnswer}
                    setCurrentAnswer={setCurrentAnswer}
                />
            ) : (
                <div className="enter-quiz-page">
                    <div className="enter-quiz-page-container" style={{justifyContent: "center"}}>
                        <div className="quiz-hall">
                            <Typography variant="h4" gutterBottom>
                                Are You Ready {alias}?
                            </Typography>
                            <CircularProgress />

                        </div>
                    </div>
                </div>
            )}
        </>
    );


}
