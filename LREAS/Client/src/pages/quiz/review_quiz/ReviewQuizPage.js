import React from "react";
import { useParams } from "react-router-dom";
import { 
    getQuizResult
} from "../../../api";
import "./styles/ReviewQuizPage.css";
import {
    FullscreenIcon,
    HomeIcon, CollapseIcon, CrossIcon,
    TickIcon, FinishIcon
} from "../../../images/Icon";
import { useNavigate } from 'react-router-dom';

export default function ReviewQuizPage() {
    const { attemptId } = useParams();
    const navigate = useNavigate();
    const [questionItem, setQuestionItem] = React.useState(null);
    const [questionIdx, setQuestionIdx] = React.useState(0);
    const [isCollapsed, setIsCollapsed] = React.useState(false);
    const [initialBoardWidth, setInitialBoardWidth] = React.useState("");
    const board = document.getElementById("question-board");
    const collapseIcon = document.getElementById("collapse-icon");
    const [quizItem, setQuizItem] = React.useState(null);
    const [questionMark, setQuestionMark] = React.useState({});
    const [questionsScore, setQuestionsScore] = React.useState(null);
    const [quizScore, setQuizScore] = React.useState(0);

    const fetchData = async () => {
        await getQuizResult(attemptId).then(
            res => {
                const data = res.data;
                setQuizItem(data);
                setQuestionItem(data.questions[0]);
                setQuestionIdx(0);

                setQuestionMark(
                    data.questions.reduce((acc, question) => {
                        let numOfCorrect = 0;
                        let numOfSubmittedCorrect = 0;

                        for (let c of question.choices) {
                            if (c.correct) {
                                numOfCorrect++;
                                if (c.submittedCorrect) {
                                    numOfSubmittedCorrect++;
                                }
                            }
                        }

                        let flagColor = 0;
                        if (numOfSubmittedCorrect !== numOfCorrect && numOfSubmittedCorrect > 0) {
                            flagColor = 1;
                        }
                        else if (numOfSubmittedCorrect === 0) {
                            flagColor = 2;
                        }

                        acc[question.questionId] = {
                            flagCorrect: flagColor
                        };
                        return acc;
                    }, {})
                );
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

            const tempQuestionsScore = quizItem.questions.reduce((acc, question) => {
                const tempQuestionScore = {};
                tempQuestionScore["maxScore"] = question.points;

                let numOfCorrect = 0;
                const score = question.choices.reduce((score, choice) => {
                    if (choice.correct) {
                        numOfCorrect++;

                        if (choice.submittedCorrect) {
                            return score + 1;
                        }
                    }
                    return score;
                }, 0);
                tempQuestionScore["score"] = ((score / numOfCorrect) * question.points);

                acc[question.questionId] = tempQuestionScore;
                return acc;
            }, {});
            setQuestionsScore(tempQuestionsScore); 
            setQuizScore(
                Object.keys(tempQuestionsScore).reduce((score, questionId) => {
                    return score + tempQuestionsScore[questionId].score;
                }, 0)
            );
        }
    }, [quizItem]);

    const Answer = ({ answerItem }) => {
        const getStatus = () => {
            if (!quizItem.showCorrectAnswer) {
                if (answerItem.submittedCorrect && answerItem.correct) {
                    return (
                        <img src={TickIcon}/>
                    );
                }
                else if (answerItem.submittedCorrect && !answerItem.correct) {
                    return (
                        <img src={CrossIcon}/>
                    );
                }
                return null;
            }

            if (answerItem.submittedCorrect !== answerItem.correct) {
                if (answerItem.correct) {
                    return null;
                }

                return (
                    <img src={CrossIcon}/>
                );
            }
            else if (!answerItem.correct) {
                return null;
            }
            
            return (
                <img src={TickIcon}/>
            );
        };

        React.useEffect(() => {
            const answerElement = document.getElementById(answerItem.choiceId);
            if (quizItem.showCorrectAnswer) {
                if (answerItem.correct) {
                    answerElement.style.backgroundColor = "rgb(205 243 207)";
                }
                else {
                    answerElement.style.backgroundColor = "rgb(255 207 207)";
                }
            }
            else {
                if (answerItem.submittedCorrect && answerItem.correct) {
                    answerElement.style.backgroundColor = "rgb(205 243 207)";
                }
                else if (answerItem.submittedCorrect && !answerItem.correct) {
                    answerElement.style.backgroundColor = "rgb(255 207 207)";
                }
                else {
                    answerElement.style.backgroundColor = "rgb(236 231 236)";
                }
            }
        });

        return (
            <div
                className="answer"
                id={answerItem.choiceId}
            >
                <div className="text">
                    {answerItem.answer}
                </div>
                
                <div className="checkmark">
                    {getStatus()}
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

    const handleKeyDown = (e) => {
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
    };

    return (
        <div
            className="review-quiz-page"
            onKeyDown={e => handleKeyDown(e)}
            tabIndex="0"
        >
            <div className="container">
                <div className="question-content">
                    <div className="question">
                        <div className="text">{questionItem?.question}</div>
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
                            <div className="info">
                                <div className="note">
                                    <div className="text">
                                        Your correct answer
                                        <img src={TickIcon}/>
                                    </div>

                                    <div className="text">
                                        Your wrong answer
                                        <img src={CrossIcon}/>
                                    </div>
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
                    </div>

                    <div className="small-buttons navigation-buttons">
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
                            className="collapse-review-quiz"
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
                                <div className="review-quiz-divider"/>

                                <div className="review-quiz-title">
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
                                                            ${questionMark && questionMark[q.questionId]?.flagCorrect === 0 ? "question-item-correct" : ""} 
                                                            ${questionMark && questionMark[q.questionId]?.flagCorrect === 1 ? "question-item-correct-partial" : ""} 
                                                            ${questionMark && questionMark[q.questionId]?.flagCorrect === 2 ? "question-item-wrong" : ""}
                                                            ${questionItem.questionId === q.questionId ? "question-item-selected" : ""}
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
                                        onClick={() => navigate(`/quiz/result/${quizItem.resourceId}`)}
                                    >
                                        <img
                                            src={FinishIcon}
                                        />

                                        <div className="text">
                                            Finish review
                                        </div>
                                    </button>
                                </div>

                                <div className="result">
                                    {
                                        quizItem && questionItem && questionsScore &&
                                        <>
                                            <div className="text">
                                                Quiz score: {`${quizScore} / ${quizItem?.totalPoints}`}
                                            </div>

                                            <div className="text">
                                                Question score: {`${questionsScore[questionItem.questionId].score} / ${questionsScore[questionItem.questionId].maxScore}`}
                                            </div>
                                        </>
                                    }
                                </div>
                            </>
                        }
                    </div>
                </div>
            </div>
        </div>
    );
};