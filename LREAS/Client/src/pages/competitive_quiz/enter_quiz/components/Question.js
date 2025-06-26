import React, { useEffect } from "react";
// Assuming sendMessage is correctly imported from your utils
import { sendMessage } from "../../../../utils/socket";
import "../styles/Question.css"; // Import the styles
import { Button, Typography } from "@mui/material";

// Added 'socket' and 'sessionCode' to the expected props
export default function Question({ currentQuestion, socket, sessionCode,isAutoChangeQuestion,minute,second }) {
    const [correctAnswers, setCorrectAnswers] = React.useState([]);
    const [showAnswer, setShowAnswer] = React.useState(false);
    const [answerCount, setAnswerCount] = React.useState(0);
    const [timeLeft, setTimeLeft] = React.useState(isAutoChangeQuestion ? (minute * 60 + second) : null);

    // Basic validation or default for answerCount if needed

    // Handler function for the button click
    const handleNextQuestionClick = () => {
        // Check if sessionCode and socket are available
        if (!sessionCode) {
            console.error("Session code is missing, cannot emit 'next' event.");
            return;
        }
        if (!socket) {
            console.error("Socket instance is missing, cannot emit 'next' event.");
            return;
        }

        console.log(`Emitting 'next' event for session: ${sessionCode}`);

        // Use the sendMessage utility (consistent with QuizHall)
        sendMessage({
            message: { sessionCode: sessionCode }, // Send sessionCode in the message payload
            event: "next",                       // The event name to emit
            socket: socket                       // The socket instance to use
        });

        setShowAnswer(false);
        setAnswerCount(0); // Reset answer count for the next question
        // Optional: Add feedback like disabling the button after click
        // e.g., set a state variable like 'isLoadingNext' to true
    };

    const handleTimeOverClick = () => {
        // Check if sessionCode and socket are available
        if (!sessionCode) {
            console.error("Session code is missing, cannot emit 'time over' event.");
            return;
        }
        if (!socket) {
            console.error("Socket instance is missing, cannot emit 'time over' event.");
            return;
        }

        console.log(`Emitting 'time over' event for session: ${sessionCode}`);

        // Use the sendMessage utility (consistent with QuizHall)
        sendMessage({
            message: { sessionCode: sessionCode }, // Send sessionCode in the message payload
            event: "time_over",                       // The event name to emit
            socket: socket                       // The socket instance to use
        });
        
        // Optional: Add feedback like disabling the button after click
        // e.g., set a state variable like 'isLoadingNext' to true
    };

    const handleLeaderBoardClick = () => {
        // Check if sessionCode and socket are available
        if (!sessionCode) {
            console.error("Session code is missing, cannot emit 'leaderboard' event.");
            return;
        }
        if (!socket) {
            console.error("Socket instance is missing, cannot emit 'leaderboard' event.");
            return;
        }

        console.log(`Emitting 'leaderboard' event for session: ${sessionCode}`);

        // Use the sendMessage utility (consistent with QuizHall)
        sendMessage({
            message: { sessionCode: sessionCode }, // Send sessionCode in the message payload
            event: "leaderboard",                       // The event name to emit
            socket: socket                       // The socket instance to use
        });
        setShowAnswer(false);
        setAnswerCount(0); // Reset answer count for the next question

        // Optional: Add feedback like disabling the button after click
        // e.g., set a state variable like 'isLoadingNext' to true
    };

    useEffect(() => {
        function onAnswerReceived(data) {
            console.log('data',data);
            for (let answer of data){
                if (answer.is_correct){
                    setCorrectAnswers(correctAnswers => [...correctAnswers, answer]);
                }
            }
            setShowAnswer(true);
        }
        function onStudentAnswerReceived(){
            setAnswerCount(prevCount => {
                console.log("answerCount before", prevCount);
                const newCount = prevCount + 1;
                console.log("answerCount after", newCount);
                return newCount;
            });
        }
        
        socket.on("answer_received", onStudentAnswerReceived);        

        socket.on("answers", onAnswerReceived);

        return () => {
            socket.off("answer_received", onStudentAnswerReceived);
            socket.off("answers", onAnswerReceived);

        };

    }
    , []);

    useEffect(() => {
        if (!isAutoChangeQuestion) return; // Skip if not auto change question
        
        let timerId;
        timerId = setInterval(() => {
            setTimeLeft(prev => prev - 1);
            }, 1000);
        if (showAnswer){
            clearInterval(timerId);
            return;
        }
        if (timeLeft <= 0) {
            // console.log(`Time's up`);
            clearInterval(timerId);
            handleTimeOverClick();
        }
        return () => {
            clearInterval(timerId);
        }

    },[isAutoChangeQuestion,timeLeft]);

    return (
        <div className="question-container">
            {showAnswer ? (
                <>
                    <div className="question-title">
                        Time Over!
                    </div>
                    <Typography variant="h4">Correct answer is:</Typography>
                </>
            ):(
                <>
                    <Typography variant="h2" className="question-title">
                        {currentQuestion.title}
                    </Typography>

                    <Typography className="answer-count-display">
                        Answers Received: {answerCount}
                    </Typography>
                </>
            )}
            


            {showAnswer ? (
                <div className="answer-options">
                    {correctAnswers.map((answer, index) => (
                        <button
                            key={answer.answerId}
                            className={`answer-button answer-${index}`}
                            disabled
                        >
                            {answer.text}
                        </button>
                    ))}
                </div>
            ) : (
                <div className="answer-options">
                    {currentQuestion.answers.map((answer, index) => (
                        <button
                            key={answer.answerId}
                            className={`answer-button answer-${index}`}
                            disabled
                        >
                            {answer.text}
                        </button>
                    ))}
                </div>
            )}


            {!showAnswer &&(
                <div className="next-question-action"> {/* Wrapper div for positioning/styling */}
                    <Button
                        className="next-question-button"   // Class for styling
                        onClick={handleTimeOverClick} // Attach the handler
                        variant="contained"
                        color="error"
                    >
                        Time over {isAutoChangeQuestion && `in ${timeLeft} seconds`}
                    </Button>
                </div>
            )}

            <div className="next-question-action"> {/* Wrapper div for positioning/styling */}
                <Button
                    className="next-question-button"   // Class for styling
                    onClick={handleLeaderBoardClick} // Attach the handler
                    variant="contained"
                    color="primary"
                >
                    Show leader board
                </Button>
            </div>
        </div>
    );
}