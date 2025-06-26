import { useParams, useNavigate } from "react-router-dom";
import { Header } from "../../../components/Header";
import { NavigationDrawer } from "../../../components/NavigationDrawer";
import "./styles/EnterCompetitiveQuizPage.css";
import { 
    EditBlackIcon, MoreBlackIcon, StarBlackIcon,
    ClockIcon, ExclamationTriangleIcon,
    AvatarIcon, StartWhiteIcon, ExitBlackIcon,
    LeftArrowBlackIcon, RemainingIcon
} from "../../../images/Icon";
import {
    getQuizInfo, startTraditionalQuiz
} from "../../../api";

import { startNewQuizSession} from "../../../api/competitiveQuizApi";
import QuizHall  from "./components/QuizHall";
import TimePicker from "./components/TimePicker";
import Alert from '@mui/material/Alert';


import React from "react";
import { Button } from "@mui/material";

export default function EnterCompetitiveQuizPage() {
    const navigate = useNavigate();
    const { resourceId } = useParams();
    const [quizInfo, setQuizInfo] = React.useState(null);
    const [sessionCode, setSessionCode] = React.useState(null);
    const [second, setSecond] = React.useState(null);
    const [minute, setMinute] = React.useState(null);
    const [isAutoChangeQuestion, setIsAutoChangeQuestion] = React.useState(false);
    const [alertOn, setAlertOn] = React.useState(false);

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
        const time = new Date(quizInfo.lastModifiedTime);
        const formattedDate = time.toDateString();
        return formattedDate;
    };

    const handleStartQuiz = () => {

        if (isAutoChangeQuestion && (minute == null || second == null)) {
            setAlertOn(true);
            return;
        }  



        startNewQuizSession({quizVersionId:quizInfo.quizVersionId}).then(
            res => {
                // navigate(`/quiz/tradition/${res.data.attemptId}`);
                setSessionCode(res);
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

    return (
        sessionCode ? (
            <QuizHall sessionCode={sessionCode} 
                        isAutoChangeQuestion={isAutoChangeQuestion}
                        second={second}
                        minute={minute}
                        />
        ) : (
            <div className="enter-quiz-page">
                <Header />
                <div className="enter-quiz-page-container" style={{justifyContent: "center"}}>
                    <div className="container">
                        <div className="main-container">
                            <div className="back" onClick={() => navigate('/content')}>
                                <img src={LeftArrowBlackIcon} alt="Back" />
                                <div className="text">Back</div>
                            </div>
    
                            <div className="main-box">
                                <div className={`title ${quizInfo?.quizName ? "" : "no-title"}`}>
                                    {quizInfo?.quizName || "No title found"}
                                </div>
    
                                <div className="options">
                                    <img src={EditBlackIcon} onClick={() => navigate(`/quiz/editor/${resourceId}`)} alt="Edit" />
                                    <img src={StarBlackIcon} alt="Star" />
                                    <img className="rotate" src={MoreBlackIcon} alt="More" />
                                </div>
    
                                <div className="main">
                                    <div className={`description ${quizInfo?.quizDescription ? "" : "no-description"}`}>
                                        {quizInfo?.quizDescription || "No description found"}
                                    </div>
    
                                        <TimePicker
                                            setSecond={setSecond}
                                            setMinute={setMinute}
                                            setIsAutoChangeQuestion={setIsAutoChangeQuestion}
                                            second={second}
                                            minute={minute}
                                            isAutoChangeQuestion={isAutoChangeQuestion}
                                        />
                                        {alertOn && (
                                        <Alert severity="error">Please select time</Alert>

                                        )}


    
                                    <div className="bottom">
                                        <div className="user">
                                            <img src={AvatarIcon} alt="Avatar" />
                                            <div className="right">
                                                <div className="username">{quizInfo?.ownerName || ""}</div>
                                                <div className="status">Last updated: {handleDateTime()}</div>
                                            </div>
                                        </div>
    
                                        <div className="action-buttons">
                                            <Button
                                                className="submit-button"
                                                // disabled={!quizInfo || !quizInfo.remainingAttempts || quizInfo.remainingAttempts <= 0}
                                                onClick={handleStartQuiz}
                                                variant="contained"
                                            >
                                                <img src={StartWhiteIcon} alt="Start" />
                                                <div className="text" >
                                                    {quizInfo?.isInProgress ? "Resume" : "Start new session"}
                                                </div>
                                            </Button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    );
    
};