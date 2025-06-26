import { Header } from "../../../components/Header";
import { useParams, useNavigate } from "react-router-dom";
import {
    LeftArrowBlackIcon, ReviewIcon
} from "../../../images/Icon";
import "./styles/QuizResultPage.css";
import { CircularProgressbar } from "react-circular-progressbar";
import React from "react";
import "./styles/CircularProgressbar.css";
import {
    getAllQuizAttempts
} from "../../../api";

export default function QuizResultPage() {
    const { resourceId } = useParams();
    const navigate = useNavigate();
    const [attempts, setAttempts] = React.useState([]);

    React.useEffect(() => {
        async function fetchData() {
            await getAllQuizAttempts(resourceId).then(
                res => {
                    setAttempts(res.data.quizResults);
                }
            ).catch(
                e => console.log(e)
            );
        };
        fetchData();
    }, []);

    const CircularProgress = ({ percentage, index }) => {
        const getGradient = () => {
            if (percentage < 50) {
                return ["#FF8A65", "#FF7043", "#FF5722"];
            }
            if (percentage < 80) {
                return ["#FFD54F", "#FFCA28", "#FFA000"];
            }
            return ["#66BB6A", "#2E7D32", "#1B5E20"];
        };
        
        const getTextColor = () => {
            if (percentage < 50) {
                return "#FF5722";
            }
            if (percentage < 80) {
                return "#FFA000";
            }
            return "#1B5E20";
        };

        const [startColor, midColor, endColor] = getGradient();
        const textColor = getTextColor();

        return (
            <div className="progress-bar">
                <svg className="linear-gradient">
                    <defs>
                        <linearGradient id={`${index}gradientColor`} x1="0%" y1="0%" x2="100%" y2="0%">
                            <stop offset="0%" stopColor={startColor} />
                            <stop offset="50%" stopColor={midColor} />
                            <stop offset="100%" stopColor={endColor} />
                        </linearGradient>
                    </defs>
                </svg>

                <CircularProgressbar
                    value={percentage}
                    text={`${percentage.toFixed(1)}%`}
                    styles={{
                        path: {
                            stroke: `url(#${index}gradientColor)`,
                            strokeLinecap: "round"
                        },
                        trail: {
                            stroke: "#EAEAEA"
                        },
                        text: {
                            fill: textColor,
                            fontSize: "1.25rem",
                            fontWeight: "bold",
                        },
                    }}
                />
            </div>
        )
    };
    
    const Attempt = ({ attemptItem, index }) => {
        const [percentage, setPercentage] = React.useState(0);

        React.useEffect(() => {
            if (attemptItem) {
                if (!attemptItem.totalScore || !attemptItem.maxScore) {
                    setPercentage(0);
                }
                else {
                    setPercentage((attemptItem.totalScore / attemptItem.maxScore) * 100);
                }
            }
        }, [attemptItem]);

        const handleText = (text) => {
            if (!text) {
                return "Unknown";
            }
            text = text.replace("_", " ");
            return text.charAt(0) + text.slice(1).toLowerCase();
        };

        const handleTextOrigin = (text) => {
            if (!text) {
                return "Unknown";
            }
            return text;
        };

        const handleTime = (timeValue) => {
            if (!timeValue) {
                return "Unknown";
            }

            return new Date(timeValue).toLocaleString("en-GB");
        };

        const handleFormatTime = (seconds) => {
            if (!seconds) {
                return "Unknown";
            }
    
            const handleValue = (input, text) => {
                return `${input > 0 ? input + " " + text + (input > 1 ? "s" : "") : ""}`;
            };
    
            const hour = Math.floor(seconds / 3600);
            const min = Math.floor((seconds - (hour * 3600)) / 60);
            const sec = Math.floor(seconds % 60);
            return `${handleValue(hour, "hour")} ${handleValue(min, "minute")} ${handleValue(sec, "second")}`;
        };

        const handleScore = (score, maxScore) => {
            if (!score || !maxScore) {
                if (maxScore !== 0 && score === 0) {
                    return `0 / ${maxScore}`;
                }
                return "Unknown";
            }
            return `${score.toFixed(2)} / ${maxScore.toFixed(2)}`;
        };

        return (
            <div className="attempt">
                <div className="quiz-result-title">
                    <div className="text">
                        {`Attempt ${index + 1}`}
                    </div>
                </div>

                <div className="main">
                    <div className="info">
                        <div className="left">
                            <div className="text">
                                Status
                            </div>

                            <div className="text">
                                Time started
                            </div>

                            <div className="text">
                                Time completed
                            </div>

                            <div className="text">
                                Duration
                            </div>

                            <div className="text">
                                Score
                            </div>

                            <div className="text">
                                User
                            </div>
                        </div>

                        <div className="right">
                            <div className="text">
                                {handleText(attemptItem?.status)}
                            </div>

                            <div className="text">
                                {handleTime(attemptItem?.startTime)}
                            </div>

                            <div className="text">
                                {handleTime(attemptItem?.submitTime)}
                            </div>

                            <div className="text">
                                {handleFormatTime(attemptItem?.duration)}
                            </div>

                            <div className="text">
                                {handleScore(attemptItem?.totalScore, attemptItem?.maxScore)}
                            </div>

                            <div className="text">
                                {handleTextOrigin(attemptItem?.username)}
                            </div>
                        </div>
                    </div>

                    <CircularProgress
                        percentage={percentage}
                        index={index}
                    />
                </div>

                <div
                    className="review-button"
                    onClick={() => {
                        if (attemptItem.submitTime) {
                            navigate(`/quiz/review/${attemptItem.attemptId}`);
                        }
                        else {
                            navigate(`/quiz/tradition/${attemptItem.attemptId}`);
                        }
                    }}
                >
                    <img className="icon" src={ReviewIcon}/>

                    <div className="text">
                        {attemptItem.submitTime ? "Review" : "Resume"}
                    </div>
                </div>
            </div>
        );
    };

    return (
        <div className="quiz-result-page">
            <Header/>
            <div className="quiz-result-page-container">
                <div className="container">
                    <div className="main-container">
                        <div
                            className="back"
                        >
                            <img
                                src={LeftArrowBlackIcon}
                                onClick={() => navigate(`/quiz/enter/${resourceId}`)}
                            />
                            <div className="text">
                                Back
                            </div>
                        </div>

                        <div className="attempts">
                            {
                                attempts?.map((attempt, index) => {
                                    return (
                                        <Attempt
                                            key={index}
                                            attemptItem={attempt}
                                            index={index}
                                        />
                                    )
                                })
                            }
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};