import { useParams, useNavigate } from "react-router-dom";
import { Header } from "../../../components/Header";
import { NavigationDrawer } from "../../../components/NavigationDrawer";
import "./styles/DoCompetitiveQuizPage.css";
import { 
    EditBlackIcon, MoreBlackIcon, StarBlackIcon,
    ClockIcon, ExclamationTriangleIcon,
    AvatarIcon, StartWhiteIcon, ExitBlackIcon,
    LeftArrowBlackIcon, RemainingIcon
} from "../../../images/Icon";
import {
    getQuizInfo, startTraditionalQuiz
} from "../../../api";

import { startNewQuizSession,joinSession} from "../../../api/competitiveQuizApi";
import QuizHall  from "./components/QuizHall";

import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Alert from '@mui/material/Alert';




import React from "react";

export default function DoCompetitiveQuizPage() {
    const navigate = useNavigate();
    const { resourceId } = useParams();
    const [quizInfo, setQuizInfo] = React.useState(null);
    const [sessionCode, setSessionCode] = React.useState(null);
    const [alertOn,setAlertOn] = React.useState(false);
    const [invalidAlias,setInvalidAlias] = React.useState(false);
    const [sessionCodeValid, setSessionCodeValid] = React.useState(false);
    const [alias,setAlias] = React.useState(null);

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
        startNewQuizSession({quizVersionId:quizInfo.quizVersionId}).then(
            res => {
                // navigate(`/quiz/tradition/${res.data.attemptId}`);
                setSessionCode(res);
            }
        ).catch(
            e => console.log(e)
        );
    };

    const handleChangeSessionCode =(e) => {
        setAlertOn(false);
        setSessionCode(e.target.value);
    };

    const handleChangeAlias = (e) =>{
        setAlias(e.target.value);
    }

    const handleEnterButton = async () => {
        if (!sessionCode){
            setAlertOn(true);
        } else if (alias == null){
            setInvalidAlias(true);
        } else {
            setAlertOn(false);
            setInvalidAlias(false);
            await joinSession({sessionCode}).then(
                res => {
                    if (res){
                        // navigate(`/quiz/competitive/${sessionCode}`);
                        setSessionCodeValid(true);
                    } else {
                        setAlertOn(true);
                    }
                }
            ).catch(
                e => console.log(e)
            )
        }
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        handleEnterButton(); // same logic as your button handler
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
        (sessionCodeValid && !invalidAlias) ? (<QuizHall sessionCode={sessionCode} alias={alias}/>):(
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
                                    Enter Session Code
                                </div>
    
    
                                <div className="main">
                                <Box
                                    sx={{ width: 500, maxWidth: '100%' }}
                                    component="form"
                                    onSubmit={handleSubmit}
                                    >
                                    <TextField 
                                        fullWidth 
                                        label="Session Code" 
                                        id="sessionCode"
                                        type="number" 
                                        required
                                        value={sessionCode}
                                        onChange={handleChangeSessionCode}
                                        placeholder="Enter session code"
                                        margin="normal"
                                    />
                                    {alertOn && (
                                        <Alert severity="error" variant="outlined">
                                        Invalid session code
                                        </Alert>
                                    )}
                                    <TextField 
                                        fullWidth 
                                        label="Alias" 
                                        id="alias"
                                        type="text" 
                                        required
                                        value={alias}
                                        onChange={handleChangeAlias}
                                        placeholder="Enter alias"
                                        margin="normal"
                                    />
                                    {invalidAlias && (
                                        <Alert severity="error" variant="outlined">
                                        Invalid alias
                                        </Alert>
                                    )}
                                    <Button
                                        type="submit"
                                        variant="contained"
                                        sx={{ mt: 2 }}
                                    >
                                        Enter
                                    </Button>
                                    </Box>
                                </div>
                            </div>
            
                        </div>
                    </div>
                </div>
            </div>)
        )
    
};