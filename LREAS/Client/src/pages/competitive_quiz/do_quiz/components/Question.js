import React,{useState,useEffect} from "react";
// Assuming sendMessage is correctly imported from your utils
import { sendMessage } from "../../../../utils/socket";
import "../styles/Question.css"; // Import the styles
import CircularProgress from '@mui/material/CircularProgress';
import CheckIcon from '@mui/icons-material/Check';
import ClearIcon from '@mui/icons-material/Clear';
import { Box,Alert,Typography } from "@mui/material";

// Added 'socket' and 'sessionCode' to the expected props
export default function Question({ 
    currentQuestion, 
    socket, 
    sessionCode,
    alias,
    userInfo,
    showAnswer,
    setShowAnswer,
    correctAnswers, 
    setCorrectAnswers,
    isCurrentAnswerCorrect,
    setIsCurrentAnswerCorrect,
    currentAnswer,
    setCurrentAnswer
 }) {

    return (
        <div className="question-container">
            {/* Question Title */}
            <Typography variant="h2" className="question-title">
                {currentQuestion.title}
            </Typography>
    
            {showAnswer  ? (
                    <>
                        {currentAnswer ? (
                            <>
                                <div className="answer-count-display">
                                    Your choice is:
                                </div>
            
                                <div className="answer-options">
                                    <button
                                        className={`answer-button answer`}
                                        disabled
                                    >
                                        <Typography variant="h4" className="question-title">
                                            {currentAnswer.text}
                                        </Typography>
                                    </button>
                                    
                                </div>
                            </>
                        ):(                   
                            <>
                                <div className="answer-count-display">
                                You did not make a choice!
                                </div>
                            </>
                        )
                        }
                    
                    
                        {correctAnswers.some(answer => answer._id === currentAnswer?.answerId) ? (
                            <Alert icon={<CheckIcon fontSize="inherit" />} severity="success" sx={{mt:'2em'}}>
                                    Your answer is correct!
                            </Alert>
                        ) : (
                            <Alert icon={<ClearIcon fontSize="inherit" />} severity="error" sx={{mt:'2em'}}>
                                    Your answer is incorrect!
                            </Alert>
                        )}
                    </>
            ) : 
            (
                <>
                    {

            
                        currentAnswer ? (
                            <>
                                <div className="answer-count-display">
                                    Your choice is:
                                </div>
                
                                <div className="answer-options">
                                    <button
                                        className={`answer-button answer`}
                                        disabled
                                    >
                                        <Typography variant="h4" className="question-title">
                                            {currentAnswer.text}
                                        </Typography>
                                    </button>
                                    
                                </div>
                
                                <div className="answer-count-display">
                                    Waiting for other players...
                                </div>
                                <CircularProgress />
                            </>
                        ) : (
                                <div className="answer-options">
                                    {currentQuestion.answers.map((answer, index) => (
                                        <button
                                            key={answer.answerId}
                                            className={`answer-button answer-${index}`}
                                            onClick={() => {
                                                setCurrentAnswer(answer);
                                                console.log("currentQuesiont",currentQuestion);
                                                console.log('selected answer',currentAnswer,answer);
                                                sendMessage({
                                                    message: {
                                                        sessionCode: sessionCode,
                                                        answerId: answer.answerId,
                                                        alias: alias,
                                                        questionId: currentQuestion.questionId,
                                                        userId: userInfo.id,
                                                        time: Date.now()
                                                    },
                                                    event: "quiz_answer",
                                                    socket: socket
                                                });
                                            }}
                                        >
                                        <Typography variant="h4" className="question-title">
                                            {answer.text}
                                        </Typography>
                                        </button>
                                    ))}
                                </div>
                            )   
                    }
                </>
            )
            }
        </div>
    );
}