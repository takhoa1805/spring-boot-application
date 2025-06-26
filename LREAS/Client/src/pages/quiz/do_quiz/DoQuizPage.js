import React from "react";
import { useParams } from "react-router-dom";
import { 
    getQuizByAttempt, submitTraditionalQuiz
} from "../../../api";
import "./styles/DoQuizPage.css";
import {
    ExitBlackIcon, StartWhiteIcon, FullscreenIcon,
    SoundIcon, HomeIcon, ResetBlackIcon, CollapseIcon
} from "../../../images/Icon";
import { useNavigate } from 'react-router-dom';

export default function DoQuizPage() {
    const { attemptId } = useParams();
    const navigate = useNavigate();
    const [questionItem, setQuestionItem] = React.useState(null);
    const [questionIdx, setQuestionIdx] = React.useState(0);
    const [isCollapsed, setIsCollapsed] = React.useState(false);
    const [initialBoardWidth, setInitialBoardWidth] = React.useState("");
    const [isMultipleChoices, setIsMultipleChoices] = React.useState(null);
    const timeLeftRef = React.useRef(Infinity);
    const timerRef = React.useRef(null);
    const timerDisplayRef = React.useRef(null);
    const [questionMarkDone, setQuestionMarkDone] = React.useState({});
    const board = document.getElementById("question-board");
    const collapseIcon = document.getElementById("collapse-icon");

    const quizReducer = (quiz, action) => {
        switch(action.type) {
            case "UPDATE_ANSWER":
                return {
                    ...quiz,
                    questions: quiz.questions.map(q => {
                        if (q.questionId === action.questionId) {
                            return {
                                ...q,
                                choices: q.choices.map(a => {
                                    if (a.choiceId === action.choiceId) {
                                        return {
                                            ...a,
                                            correct: action.isChecked
                                        };
                                    }
                                    return a;
                                })
                            };
                        }
                        return q;
                    })
                };
            case "RESET_ANSWER":
                const newQuiz = {
                    ...quiz,
                    questions: quiz.questions.map(q => {
                        return {
                            ...q,
                            choices: q.choices.map(a => {
                                return {
                                    ...a,
                                    correct: false
                                };
                            })
                        };
                    })
                };

                setQuestionMarkDone(
                    newQuiz.questions.reduce((acc, question) => {
                        acc[question.questionId] = {
                            checked: false,
                            choices: question.choices.reduce((acc2, choice) => {
                                acc2[choice.choiceId] = false;
                                return acc2;
                            }, {})
                        };
                        return acc;
                    }, {})
                );

                return newQuiz;
            case "INITIAL":
                return {
                    ...action.initial
                };
            default:
                return quiz;
        }
    };
    const [quizItem, dispatch] = React.useReducer(quizReducer, {});

    const fetchData = async () => {
        await getQuizByAttempt(attemptId).then(
            res => {
                const data = res.data;

                const startTime = new Date(data.startTime);
                const currTime = new Date();
                timeLeftRef.current = data.timeLimit - Math.abs(currTime.getTime() - startTime.getTime()) / 1000;

                dispatch({
                    type: "INITIAL",
                    initial: data.quiz
                });
                setQuestionItem(data.quiz.questions[0]);
                setQuestionMarkDone(
                    data.quiz.questions.reduce((acc, question) => {
                        acc[question.questionId] = {
                            checked: false,
                            choices: question.choices.reduce((acc2, choice) => {
                                acc2[choice.choiceId] = false;
                                return acc2;
                            }, {})
                        };
                        return acc;
                    }, {})
                );
                setQuestionIdx(0);
                setIsMultipleChoices(data.isMultipleChoices);
            }
        ).catch(
            e => console.log(e)
        );
    };

    React.useEffect(() => {
        fetchData();

        setInitialBoardWidth(
            document.getElementById("question-board").style.width
        );
    }, []);

    React.useEffect(() => {
        if (quizItem && quizItem.questions) {
            setQuestionItem(quizItem.questions[questionIdx]);
        }
    }, [quizItem]);

    const Answer = ({ answerItem }) => {
        const [isChecked, setIsChecked] = React.useState(answerItem.correct);

        const handleSaveAnswer = (isChecked) => {
            dispatch({
                type: "UPDATE_ANSWER",
                questionId: questionItem.questionId,
                choiceId: answerItem.choiceId,
                isChecked: isChecked
            });
        };

        const handleMarkDone = (isChecked) => {
            const questionId = questionItem.questionId;
            const answerId = answerItem.choiceId;
            setQuestionMarkDone(prev => {
                const newChoicesValue = {
                    ...prev[questionId].choices,
                    [answerId]: isChecked
                };
                let isDone = false;
                for (var key in newChoicesValue) {
                    isDone ||= newChoicesValue[key];
                }
                return {
                    ...prev,
                    [questionId]: {
                        checked: isDone,
                        choices: newChoicesValue
                    }
                };
            });
        };

        return (
            <div
                className="answer"
                id={answerItem.choiceId}
            >
                <div className="text">
                    {answerItem.answer}
                </div>
                
                <div
                    className={`circle-checkbox ${isChecked ? "circle-checkbox-checked" : ""}`}
                    onMouseDown={() => setIsChecked(!isChecked)}
                    onMouseUp={() => {
                        setTimeout(() => {
                            handleMarkDone(isChecked);
                            handleSaveAnswer(isChecked);
                        }, 210);
                    }}
                >
                    {
                        isChecked &&
                        <div className="checkmark"/>
                    }
                </div>
            </div>
        );
    };

    const handleCollapse = () => {
        setIsCollapsed(!isCollapsed);

        if (!isCollapsed) {
            board.style.width = "3%";
            collapseIcon.style.transform = "rotate(180deg)";
        }
        else {
            board.style.width = initialBoardWidth;
            collapseIcon.style.transform = "";
        }
    };

    const handleNavigateQuestion = (nextIdx) => {
        if (nextIdx >= quizItem.questions.length || nextIdx < 0) {
            return;
        }
        
        setQuestionItem(quizItem.questions[nextIdx]);
        setQuestionIdx(nextIdx);
    };

    const handleResetAnswers = () => {
        dispatch({
            type: "RESET_ANSWER"
        });
    };

    const handleTimer = (seconds) => {
        const handleLeadingZero = (input) => {
            if (input <= 0) {
                return "00";
            }
            return `${input < 10 ? "0" : ""}${input}`;
        };
        
        const hour = Math.floor(seconds / 3600);
        const min = Math.floor((seconds - (hour * 3600)) / 60);
        const sec = Math.floor(seconds % 60);
        return `${handleLeadingZero(hour)}:${handleLeadingZero(min)}:${handleLeadingZero(sec)}`;
    };

    const handleSubmit = async () => {
        const data = {
            attemptId: attemptId,
            submittedQuestions: quizItem.questions
        };
        await submitTraditionalQuiz(attemptId, data).then(
            res => {
                navigate(`/quiz/enter/${quizItem.resourceId}`);
            }
        ).catch(
            e => console.log(e)
        );
    };

    React.useEffect(() => {
        timerRef.current = setInterval(() => {
            if (timeLeftRef.current <= 0) {
                clearInterval(timerRef.current);
                handleSubmit();
                return;
            }
            timeLeftRef.current -= 1;
            if (timerDisplayRef.current) {
                timerDisplayRef.current.innerText = handleTimer(timeLeftRef.current);
            }
        }, 1000);

        return () => clearInterval(timerRef.current);
    }, []);

    const handleKeyDown = (e) => {
        setTimeout(() => {
            if (e.key === "ArrowLeft") {
                handleNavigateQuestion(questionIdx - 1);
            }
            else if (e.key === "ArrowRight") {
                handleNavigateQuestion(questionIdx + 1);
            }
            else {
                const sheet = document.getElementById("sheet");
                const gridComputedStyle = window.getComputedStyle(sheet);
                const gridColumnCount = gridComputedStyle.getPropertyValue("grid-template-columns").split(" ").length;
    
                if (e.key === "ArrowDown") {
                    handleNavigateQuestion(questionIdx + gridColumnCount);
                }
                else if (e.key === "ArrowUp") {
                    handleNavigateQuestion(questionIdx - gridColumnCount);
                }
            }
        }, 250);
    };

    const getInfoText = () => {
        if (!isMultipleChoices) {
            return "";
        }

        if (isMultipleChoices[questionItem.position]) {
            return "Select one or more answers";
        }
        return "Select one answer";
    };

    return (
        <div
            className="do-quiz-page"
            onKeyDown={e => handleKeyDown(e)}
            tabIndex="0"
        >
            <div className="container">
                <div className="question-content">
                    <div className="question">
                        <div>{questionItem?.question}</div>
                    </div>

                    <div className="content-container">
                        <div className="image-frame">
                            <div className="image">
                                <img
                                    className="upload-image"
                                    src={questionItem?.imageUrl}
                                />
                            </div>
                        </div>

                        <div className="answers-container">
                            <div className="header-answers">
                                <div className="info-text">
                                    {getInfoText()}
                                </div>

                                <div className="action-buttons">
                                    <button
                                        className="previous-button"
                                        onClick={() => handleNavigateQuestion(questionIdx - 1)}
                                    >
                                        <div className="text">
                                            Previous
                                        </div>
                                    </button>

                                    <button
                                        className="next-button"
                                        onClick={() => handleNavigateQuestion(questionIdx + 1)}
                                    >
                                        <div className="text">
                                            Next
                                        </div>
                                    </button>
                                </div>
                            </div>

                            <div className="answers">
                                {
                                    questionItem &&
                                    questionItem.choices.map(answer => {
                                        return (
                                            <Answer
                                                key={answer.choiceId}
                                                answerItem={answer}
                                            />
                                        );
                                    })
                                }
                            </div>
                        </div>
                    </div>

                    <div className="small-buttons setting-buttons">
                        <div className="image-wrapper">
                            <img
                                src={FullscreenIcon}
                            />
                        </div>

                        <div className="image-wrapper">
                            <img
                                src={SoundIcon}
                            />
                        </div>
                    </div>

                    <div className="small-buttons navigation-buttons">
                        <div
                            className="image-wrapper"
                            onClick={() => handleResetAnswers()}
                        >
                            <img
                                src={ResetBlackIcon}
                            />
                        </div>

                        <div
                            className="image-wrapper"
                            onClick={() => navigate("/content")}
                        >
                            <img
                                src={HomeIcon}
                            />
                        </div>
                    </div>
                </div>

                <div className="question-board" id="question-board">
                    <div className="question-board-container">
                        <div
                            className="collapse-do-quiz"
                            onClick={() => handleCollapse()}
                        >
                            <img
                                id="collapse-icon"
                                src={CollapseIcon}
                            />
                        </div>
                        
                        {
                            !isCollapsed &&
                            <>
                                <div className="do-quiz-divider"/>

                                <div className="do-quiz-title">
                                    Remaining time
                                </div>

                                <div
                                    className="countdown"
                                    ref={timerDisplayRef}
                                >
                                </div>

                                <div className="do-quiz-divider"/>

                                <div className="do-quiz-title">
                                    Quiz navigation
                                </div>

                                <div className="question-list">
                                    <div className="sheet" id="sheet">
                                        {
                                            quizItem &&
                                            quizItem.questions?.map((q, i) => {
                                                return (
                                                    <div
                                                        key={i}
                                                        className={
                                                            `question-item 
                                                                ${questionItem.position === q.position ? "question-item-selected" : ""} 
                                                                ${questionMarkDone && questionMarkDone[q.questionId].checked ? "question-item-done" : ""}
                                                            `
                                                        }
                                                        onClick={() => {
                                                            setQuestionItem(q);
                                                            setQuestionIdx(i);
                                                        }}
                                                    >
                                                        <div className="question-numer">
                                                            {i + 1}
                                                        </div>
                                                    </div>
                                                );
                                            })
                                        }
                                    </div>
                                </div>

                                <div className="action-buttons">
                                    <button
                                        className="exit-button"
                                        onClick={() => navigate(`/quiz/enter/${quizItem.resourceId}`)}
                                    >
                                        <img
                                            src={ExitBlackIcon}
                                        />

                                        <div className="text">
                                            Exit
                                        </div>
                                    </button>

                                    <button
                                        className="submit-button"
                                        onClick={() => handleSubmit()}
                                    >
                                        <img
                                            src={StartWhiteIcon}
                                        />

                                        <div className="text">
                                            Submit
                                        </div>
                                    </button>
                                </div>
                            </>
                        }
                    </div>
                </div>
            </div>
        </div>
    );
};