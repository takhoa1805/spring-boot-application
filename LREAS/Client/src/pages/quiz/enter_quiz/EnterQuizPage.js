import { useParams, useNavigate } from "react-router-dom";
import { Header } from "../../../components/Header";
import { NavigationDrawer } from "../../../components/NavigationDrawer";
import "./styles/EnterQuizPage.css";
import { 
    EditBlackIcon, MoreBlackIcon, StarBlackIcon,
    ClockIcon, ExclamationTriangleIcon,
    AvatarIcon, StartWhiteIcon, ExitBlackIcon,
    LeftArrowBlackIcon, RemainingIcon
} from "../../../images/Icon";
import {
    getQuizInfo, startTraditionalQuiz
} from "../../../api";
import React from "react";

export default function EnterQuizPage() {
    const navigate = useNavigate();
    const { resourceId } = useParams();
    const [quizInfo, setQuizInfo] = React.useState(null);

    React.useEffect(() => {
        async function fetchData() {
            await getQuizInfo(resourceId).then(
                res => {
                    setQuizInfo(res.data);
                }
            ).catch(
                e => console.log(e)
            )
        };
        fetchData();
    }, []);

    const handleDateTime = () => {
        if (!quizInfo) {
            return "";
        }

        const formatNumber = (num) => {
            return num < 10 ? "0" + num : num;
        };

        const time = new Date(quizInfo.lastModifiedTime);
        const formattedDate = formatNumber(time.getDate()) + "/" + formatNumber(time.getMonth() + 1)
                                + "/" + formatNumber(time.getFullYear()) + " " + formatNumber(time.getHours()) + ":" + formatNumber(time.getMinutes());
        return formattedDate;
    };

    const handleStartQuiz = async () => {
        await startTraditionalQuiz(resourceId).then(
            res => {
                if (res.data.success) {
                    navigate(`/quiz/tradition/${res.data.attemptId}`);
                }
            }
        ).catch(
            e => console.log(e)
        );
    };

    const formatTime = (seconds) => {
        if (!seconds) {
            return "Not set";
        }

        const handleValue = (input, text) => {
            return `${input > 0 ? input + " " + text + (input > 1 ? "s" : "") : ""}`;
        };

        const hour = Math.floor(seconds / 3600);
        const min = Math.floor((seconds - (hour * 3600)) / 60);
        const sec = Math.floor(seconds % 60);
        return `${handleValue(hour, "hour")} ${handleValue(min, "minute")} ${handleValue(sec, "second")}`;
    };

    const handleDisableStartButton = () => {
        if (quizInfo?.isInProgress) {
            return false;
        }

        if (
            !quizInfo ||
            !quizInfo.remainingAttempts ||
            quizInfo.remainingAttempts <= 0 ||
            quizInfo.numberOfQuestions <= 0
        ) {
            return true;
        }

        if (quizInfo.startTime && quizInfo.endTime) {
            const startTime = new Date(quizInfo.startTime);
            const endTime = new Date(quizInfo.endTime);
            const currentTime = new Date();
            
            // Check if the current time is outside the start and end time
            if (currentTime < startTime || currentTime > endTime) {
                return true;
            }
        }

        return false;
    };

    const handleNumber = (num, altValue) => {
        if (num === null || num === undefined) {
            return altValue;
        }
        return num;
    };

    const handleExplanation = () => {
        if (quizInfo?.role?.toLowerCase() === "viewer") {
            return "You are not allowed to start this quiz.";
        }

        if (quizInfo?.remainingAttempts <= 0) {
            return "You have no remaining attempts.";
        }

        if (quizInfo?.numberOfQuestions <= 0) {
            return "This quiz has no questions.";
        }

        if (quizInfo?.startTime && quizInfo?.endTime) {
            const startTime = new Date(quizInfo.startTime);
            const endTime = new Date(quizInfo.endTime);
            const currentTime = new Date();
            
            // Check if the current time is outside the start and end time
            if (currentTime < startTime || currentTime > endTime) {
                return "This quiz is not available at this time.";
            }
        }
    };

    return (
        <div className="enter-quiz-page">
            <Header/>
            <div className="enter-quiz-page-container">
                <div className="container">
                    <div className="main-container">
                        <div
                            className="back"
                            onClick={() => navigate('/content')}
                        >
                            <img
                                src={LeftArrowBlackIcon}
                            />
                            <div className="text">
                                Back
                            </div>
                        </div>

                        <div className="main-box">
                            <div className={`traditional-quiz-title ${quizInfo?.quizName ? "" : "no-title"}`}>
                                {quizInfo?.quizName || "No title found"}
                            </div>

                            <div className="options">
                                {
                                    quizInfo?.role?.toLowerCase() !== "viewer" &&
                                    <img
                                        src={EditBlackIcon}
                                        onClick={() => navigate(`/quiz/editor/${resourceId}`)}
                                    />
                                }

                                {/* <img
                                    src={StarBlackIcon}
                                />

                                <img
                                    className="rotate"
                                    src={MoreBlackIcon}
                                /> */}
                            </div>

                            <div className="main">
                                <div className={`description ${quizInfo?.quizDescription ? "" : "no-description"}`}>
                                    {quizInfo?.quizDescription || "No description found"}
                                </div>

                                <div className="quiz-info">
                                    <div className="info">
                                        <img
                                            src={ClockIcon}
                                        />
                                        <div className="text">
                                            Time limit: {formatTime(quizInfo?.timeLimit)}
                                        </div>
                                    </div>

                                    <div className="info">
                                        <img
                                            src={ExclamationTriangleIcon}
                                        />
                                        <div className="text">
                                            Attempts allowed: {handleNumber(quizInfo?.attemptsAllowed, "Not set")}
                                        </div>
                                    </div>

                                    <div className="info">
                                        <img
                                            src={RemainingIcon}
                                        />
                                        <div className="text">
                                            Remaining attempts: {handleNumber(quizInfo?.remainingAttempts, "Unknown")}
                                        </div>
                                    </div>
                                </div>

                                <div className="bottom">
                                    <div className="user">
                                        <img
                                            src={quizInfo?.avtPath || AvatarIcon}
                                        />
                                        <div className="right">
                                            <div className="username">
                                                {quizInfo?.ownerName || ""}
                                            </div>

                                            <div className="status">
                                                Last updated: {handleDateTime()}
                                            </div>
                                        </div>
                                    </div>

                                    <div className="action-buttons">
                                        <button
                                            className="start-button"
                                            disabled={handleDisableStartButton()}
                                            onClick={() => handleStartQuiz()}
                                        >
                                            <img
                                                src={StartWhiteIcon}
                                            />
                                            <div
                                                className="text"
                                            >
                                                {quizInfo?.isInProgress ? "Resume" : "Start"}
                                            </div>
                                        </button>

                                        <button
                                            className="attempt-button"
                                            onClick={() => navigate(`/quiz/result/${resourceId}`)}
                                        >
                                            <img
                                                src={ExitBlackIcon}
                                            />
                                            <div className="text">
                                                Previous attempts
                                            </div>
                                        </button>
                                    </div>
                                </div>

                                {
                                    handleDisableStartButton() &&
                                    <div className="error-explanation">
                                        <img
                                            src={ExclamationTriangleIcon}
                                        />
                                        <div className="text">
                                            {handleExplanation()}
                                        </div>
                                    </div>
                                }
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};