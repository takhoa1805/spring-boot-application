import React, { useState, useEffect } from "react";
import { useParams, useNavigate, Navigate } from "react-router-dom";
import { Header } from "../../../../components/Header";
import "../styles/QuizHall.css";
import { getToken, parseJwt } from "../../../../utils";
import { URL, sendMessage } from "../../../../utils/socket";
import Question from "./Question";
import LeaderBoard from "./LeaderBoard";
import { io } from 'socket.io-client';




export default function QuizHall({ sessionCode,isAutoChangeQuestion,minute,second }) {
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
            // setAnswerCount(0);
        }

        function onQuizEnded(res) {
            console.log("quiz ended");
            setCurrentQuestion(null);
            setIsQuizEnded(true);
            // setLeaderBoard(true);
            setIsTimeOver(false);
            setIsStarted(false);
            navigate(`/quiz/competitive/result/${res.sessionMongoId}`)
            // setAnswerCount(0);
            
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
            console.log(leaderBoard);
            // setAnswerCount(0);
        }

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
                alias: "Teacher"
            },
            event: "teacher_enter",
            socket
        });

        return () => {
            socket.off("connect", onConnect);
            socket.off("disconnect", onDisconnect);
            socket.off("new_player_joined", onNewPlayerJoined);
            socket.off("next_question", onNextQuestion);
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
                    socket={socket}
                    setIsQuizEnded={setIsQuizEnded}

                />
            ) : currentQuestion && isStarted ? (
                <Question
                    currentQuestion={currentQuestion}
                    socket={socket}
                    sessionCode={sessionCode}
                    isAutoChangeQuestion={isAutoChangeQuestion}
                    minute={minute}
                    second={second}

                />
            ) : (
                <div className="enter-quiz-page">
                    <h1>{sessionCode}</h1>
                    <div className="enter-quiz-page-container" style={{justifyContent: "center"}}>
                        <div className="quiz-hall">
                            <h2>Players Joined</h2>
                            <div className="players-list">
                                {players.length > 0 ? (
                                    players.map((player, index) => (
                                        <div key={index} className="player-card">
                                            <span className="player-alias">{player.alias}</span>
                                        </div>
                                    ))
                                ) : (
                                    <p>No players have joined yet...</p>
                                )}
                            </div>

                            {/* Start Quiz Button */}
                            <button
                                className="start-button"
                                onClick={handleStartQuiz}
                                disabled={isStarted}
                            >
                                {isStarted ? "Quiz Started" : "Start Quiz"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );


}
