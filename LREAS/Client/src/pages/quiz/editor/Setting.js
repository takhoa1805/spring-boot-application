import {
    ClockIcon, ResetBlackIcon, SaveWhiteIcon, ExitBlackIcon,
    MedalIcon, DuplicateWhiteIcon, DeleteIcon,
    AttemptIcon, CalendarIcon, DescriptionIcon,
    StartQuizIcon, GamesIcon
} from '../../../images/Icon';
import './styles/Setting.css';
import React from 'react';
import { ExitPopup } from './ExitPopup';
import DescriptionPopup from './DescriptionPopup';
import {Games} from '@mui/icons-material';
import QuizIcon from '@mui/icons-material/Quiz';
import { useNavigate } from 'react-router-dom';

export function Setting({
    questionItem, quizItem, onUpdate, onSave,
    onReset
}) {
    const navigate = useNavigate();
    
    const [isOpenExit, setIsOpenExit] = React.useState(false);
    const [isGame, setIsGame] = React.useState(quizItem.isGame !== null ? quizItem.isGame : false);
    const [isShuffle, setIsShuffle] = React.useState(quizItem.shuffleAnswers !== null ? quizItem.shuffleAnswers : false);
    const [showCorrectAnswer, setShowCorrectAnswer] = React.useState(quizItem.showCorrectAnswer !== null ? quizItem.showCorrectAnswer : false);
    const [attemptsAllowed, setAttemptsAllowed] = React.useState(quizItem.allowedAttempts !== null ? quizItem.allowedAttempts : 0);
    const [timeLimit, setTimeLimit] = React.useState(questionItem.time !== null ? questionItem.time : 0);
    const [totalTimeLimit, setTotalTimeLimit] = React.useState(quizItem.totalTime !== null ? quizItem.totalTime : 0);
    const [points, setPoints] = React.useState(questionItem.points !== null ? questionItem.points : 0);
    const [description, setDescription] = React.useState("");
    const [isOpenDescription, setIsOpenDescription] = React.useState("");

    // mounting ref
    const isGameRef = React.useRef(null);
    const isShuffleRef = React.useRef(null);
    const showCorrectAnswerRef = React.useRef(null);
    const attemptsAllowedRef = React.useRef(null);
    const totalTimeLimitRef = React.useRef(null);
    const timeLimitRef = React.useRef(null);
    const startTimeRef = React.useRef(null);
    const endTimeRef = React.useRef(null);
    const pointsRef = React.useRef(null);

    const convertToLocalISOString = (isoString) => {
        if (!isoString) {
            return ""; // Handle null or undefined values
        }

        const date = new Date(isoString);
        const localISO = new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString(); // Adjust for local time
        return localISO.slice(0, 19); // remove the milliseconds and 'Z'
    };
    const [startTime, setStartTime] = React.useState(convertToLocalISOString(quizItem.startTime));
    const [endTime, setEndTime] = React.useState(convertToLocalISOString(quizItem.endTime));

    const handleInputNumber = (e, setFunc) => {
        const numericValue = Number(e.target.value.replace(/[^0-9]/g, ""));
        setFunc(numericValue);
    };

    const handleStartSession = () => {
        navigate(`/quiz/competitive/enter/${quizItem.resourceId}`);
    }

    const handleStartQuiz = ()=>{
        navigate(`/quiz/enter/${quizItem.resourceId}`);
    }

    React.useEffect(() => {
        isGameRef.current = true;
        isShuffleRef.current = true;
        showCorrectAnswerRef.current = true;
        attemptsAllowedRef.current = true;
        totalTimeLimitRef.current = true;
        timeLimitRef.current = true;
        startTimeRef.current = true;
        endTimeRef.current = true;
        pointsRef.current = true;
    }, []);

    React.useEffect(() => {
        if (!quizItem) {
            return;
        }

        const newIsGame = quizItem.isGame !== null ? quizItem.isGame : false;
        const newIsShuffle = quizItem.shuffleAnswers !== null ? quizItem.shuffleAnswers : false;
        const newShowCorrectAnswer = quizItem.showCorrectAnswer !== null ? quizItem.showCorrectAnswer : false;
        const newAttemptsAllowed = quizItem.allowedAttempts !== null ? quizItem.allowedAttempts : 0;
        const newTotalTimeLimit = quizItem.totalTime !== null ? quizItem.totalTime : 0;
        const newStartTime = quizItem.startTime !== null ? convertToLocalISOString(quizItem.startTime) : "";
        const newEndTime = quizItem.endTime !== null ? convertToLocalISOString(quizItem.endTime) : "";
        const newDescription = quizItem.description !== null ? quizItem.description : "";

        if (newIsGame !== isGame) {
            isGameRef.current = true;
            setIsGame(newIsGame);
        }
        if (newIsShuffle !== isShuffle) {
            isShuffleRef.current = true;
            setIsShuffle(newIsShuffle);
        }
        if (newShowCorrectAnswer !== showCorrectAnswer) {
            showCorrectAnswerRef.current = true;
            setShowCorrectAnswer(newShowCorrectAnswer);
        }
        if (newAttemptsAllowed !== attemptsAllowed) {
            attemptsAllowedRef.current = true;
            setAttemptsAllowed(newAttemptsAllowed);
        }
        if (newTotalTimeLimit !== totalTimeLimit) {
            totalTimeLimitRef.current = true;
            setTotalTimeLimit(newTotalTimeLimit);
        }
        if (newStartTime !== startTime) {
            startTimeRef.current = true;
            setStartTime(newStartTime);
        }
        if (newEndTime !== endTime) {
            endTimeRef.current = true;
            setEndTime(newEndTime);
        }
        if (newDescription !== description) {
            setDescription(newDescription);
        }
    }, [quizItem]);

    React.useEffect(() => {
        if (!questionItem) {
            return;
        }

        const newTimeLimit = questionItem.time !== null ? questionItem.time : 0;
        const newPoints = questionItem.points !== null ? questionItem.points : 0;

        if (newTimeLimit !== timeLimit) {
            timeLimitRef.current = true;
            setTimeLimit(newTimeLimit);
        }
        if (newPoints !== points) {
            pointsRef.current = true;
            setPoints(newPoints);
        }
    }, [questionItem]);

    React.useEffect(() => {
        if (!isGameRef.current) {
            onUpdate({
                type: "UPDATE_QUIZ",
                updatedQuiz: {
                    isGame: isGame
                }
            });
        }
        else {
            isGameRef.current = false;
        }
    }, [isGame]);

    React.useEffect(() => {
        if (!isShuffleRef.current) {
            onUpdate({
                type: "UPDATE_QUIZ",
                updatedQuiz: {
                    shuffleAnswers: isShuffle
                }
            });
        }
        else {
            isShuffleRef.current = false;
        }
    }, [isShuffle]);

    React.useEffect(() => {
        if (!showCorrectAnswerRef.current) {
            onUpdate({
                type: "UPDATE_QUIZ",
                updatedQuiz: {
                    showCorrectAnswer: showCorrectAnswer
                }
            });
        }
        else {
            showCorrectAnswerRef.current = false;
        }
    }, [showCorrectAnswer]);

    React.useEffect(() => {
        if (!timeLimitRef.current) {
            onUpdate({
                type: "UPDATE_QUESTION",
                updatedQuestion: {
                    questionId: questionItem.questionId,
                    time: timeLimit
                }
            });
        }
        else {
            timeLimitRef.current = false;
        }
    }, [timeLimit]);

    React.useEffect(() => {
        if (startTime === "") {
            startTimeRef.current = false;
            return;
        }
        
        if (!startTimeRef.current) {
            onUpdate({
                type: "UPDATE_QUIZ",
                updatedQuiz: {
                    startTime: new Date(startTime).toISOString()
                }
            });
        }
        else {
            startTimeRef.current = false;
        }
    }, [startTime]);

    React.useEffect(() => {
        if (endTime === "") {
            endTimeRef.current = false;
            return;
        }

        if (!endTimeRef.current) {
            onUpdate({
                type: "UPDATE_QUIZ",
                updatedQuiz: {
                    endTime: new Date(endTime).toISOString()
                }
            });
        }
        else {
            endTimeRef.current = false;
        }
    }, [endTime]);

    React.useEffect(() => {
        if (!totalTimeLimitRef.current) {
            onUpdate({
                type: "UPDATE_QUIZ",
                updatedQuiz: {
                    totalTime: totalTimeLimit
                }
            });
        }
        else {
            totalTimeLimitRef.current = false;
        }
    }, [totalTimeLimit]);

    React.useEffect(() => {
        if (!attemptsAllowedRef.current) {
            onUpdate({
                type: "UPDATE_QUIZ",
                updatedQuiz: {
                    allowedAttempts: attemptsAllowed
                }
            });
        }
        else {
            attemptsAllowedRef.current = false;
        }
    }, [attemptsAllowed]);

    React.useEffect(() => {
        if (!pointsRef.current) {
            onUpdate({
                type: "UPDATE_QUESTION",
                updatedQuestion: {
                    questionId: questionItem.questionId,
                    points: points
                }
            });
        }
        else {
            pointsRef.current = false;
        }
    }, [points]);

    const handleSaveDescription = (description) => {
        onUpdate({
            type: "UPDATE_QUIZ",
            updatedQuiz: {
                description: description
            }
        });
    };

    const handleDelete = () => {
        onUpdate({
            type: "DELETE_QUESTION",
            questionId: questionItem.questionId
        });
    };

    const handleDuplicate = () => {
        onUpdate({
            type: "INSERT_QUESTION",
            newQuestion: {
                question: {
                    ...questionItem,
                    questionId: Date.now()
                },
                position: questionItem.position + 1
            }
        });
    };

    const handleReset = () => {
        onReset();
    };

    const handleSave = () => {
        onSave();
    };

    const handleExit = () => {
        setIsOpenExit(true);
    };

    return (
        <div className="setting-frame">
            <div className="setting">
                <div className="setting-zone">
                    <div className="option">
                        {/* <div className="selection-box">
                            <div
                                className={`checkbox ${isGame ? "checkbox-checked" : ""}`}
                                onMouseUp={() => setIsGame(!isGame)}
                            >
                                {
                                    isGame &&
                                    <div className="checkmark"/>
                                }
                            </div>
                            <div className="text-wrapper-8">Is game</div>
                        </div>
                        <div className="text-wrapper-9">
                            Is competitive quiz or not?
                        </div> */}

                        <div className="selection-box">
                            <div
                                className={`checkbox ${isShuffle ? "checkbox-checked" : ""}`}
                                onMouseUp={() => setIsShuffle(!isShuffle)}
                            >
                                {
                                    isShuffle &&
                                    <div className="checkmark"/>
                                }
                            </div>
                            <div className="text-wrapper-8">Shuffle</div>
                        </div>
                        <div className="text-wrapper-9">
                            Shuffle the questions
                        </div>

                        <div className="selection-box">
                            <div
                                className={`checkbox ${showCorrectAnswer ? "checkbox-checked" : ""}`}
                                onMouseUp={() => setShowCorrectAnswer(!showCorrectAnswer)}
                            >
                                {
                                    showCorrectAnswer &&
                                    <div className="checkmark"/>
                                }
                            </div>
                            <div className="text-wrapper-8">Show correct answers</div>
                        </div>
                        <div className="text-wrapper-9">
                            Show correct answer at the end of the quiz
                        </div>
                    </div>

                    <div className="line"/>

                    <div className="div-4">
                        <div className="text-wrapper-10">Quiz settings</div>
                    </div>

                    {
                        !isGame &&
                        <>
                            <div className="div-5">
                                <div className="div-6">
                                    <img className="image-3" src={CalendarIcon}/>

                                    <div className="text-wrapper-8">Start time</div>
                                </div>

                                <div
                                    className="input-box"
                                >
                                    <input
                                        className="input-datetime"
                                        type="datetime-local"
                                        onChange={e => setStartTime(e.target.value)}
                                        value={startTime}
                                        min={new Date().toISOString().slice(0, 16)}
                                    />
                                </div>
                            </div>
                            <div className="div-5">
                                <div className="div-6">
                                    <img className="image-3" src={CalendarIcon}/>

                                    <div className="text-wrapper-8">End time</div>
                                </div>

                                <div
                                    className="input-box"
                                >
                                    <input
                                        className="input-datetime"
                                        type="datetime-local"
                                        onChange={e => setEndTime(e.target.value)}
                                        value={endTime}
                                        min={startTime || new Date().toISOString().slice(0, 16)}
                                    />
                                </div>
                            </div>
                        </>
                    }

                    <div className="div-5">
                        <div className="div-6">
                            <img className="image-3" src={ClockIcon}/>

                            <div className="text-wrapper-8">Total time limit (s)</div>
                        </div>

                        <div
                            className="input-box"
                        >
                            <input
                                className="input-text"
                                placeholder="Enter total quiz time limit"
                                onChange={e => handleInputNumber(e, setTotalTimeLimit)}
                                value={totalTimeLimit}
                            />
                        </div>
                    </div>

                    <div className="div-5">
                        <div className="div-6">
                            <img className="image-3" src={AttemptIcon}/>

                            <div className="text-wrapper-8">Attempts allowed</div>
                        </div>

                        <div
                            className="input-box"
                        >
                            <input
                                className="input-text"
                                placeholder="Enter number of attempts allowed"
                                onChange={e => handleInputNumber(e, setAttemptsAllowed)}
                                value={attemptsAllowed}
                            />
                        </div>
                    </div>

                    <div className="div-5">
                        <div className="div-6">
                            <img className="image-3" src={DescriptionIcon}/>

                            <div className="text-wrapper-8">Description</div>
                        </div>

                        <div className="input-box">
                            <div
                                className="input-description"
                                onClick={() => setIsOpenDescription(true)}
                            >
                                {description || "Click to enter description"}
                            </div>
                        </div>
                        <DescriptionPopup
                            isOpen={isOpenDescription}
                            setIsOpen={setIsOpenDescription}
                            currDescription={quizItem.description}
                            onUpdate={(description) => {
                                setDescription(description);
                                handleSaveDescription(description);
                            }}
                        />
                    </div>

                    <div className="line"/>

                    <div className="div-4">
                        <div className="text-wrapper-10">Question settings</div>
                    </div>

                    <div className="div-5">
                        <div className="div-6">
                            <img className="image-3" src={ClockIcon}/>

                            <div className="text-wrapper-8">Time limit (s)</div>
                        </div>

                        <div
                            className="input-box"
                        >
                            <input
                                className="input-text"
                                placeholder="Enter question time limit"
                                onChange={e => handleInputNumber(e, setTimeLimit)}
                                value={timeLimit}
                            />
                        </div>
                    </div>

                    <div className="div-5">
                        <div className="div-6">
                            <img className="image-3" src={MedalIcon}/>

                            <div className="text-wrapper-8">Points</div>
                        </div>

                        <div
                            className="input-box"
                        >
                            <input
                                className="input-text"
                                placeholder="Enter question points"
                                onChange={e => handleInputNumber(e, setPoints)}
                                value={points}
                            />
                        </div>
                    </div>

                    <div className="div-3">
                        <div
                            className="delete-button"
                            onClick={() => handleDelete()}
                        >
                            <img src={DeleteIcon} className="delete-1"/>
                            <div className="text-wrapper-15">Delete</div>
                        </div>

                        <div
                            className="duplicate-button"
                            onClick={() => handleDuplicate()}
                        >
                            <img className="img-2" src={DuplicateWhiteIcon}/>

                            <div className="text-wrapper-15">Duplicate</div>
                        </div>
                    </div>

                    <div className="line"/>
                </div>
                
                <div className="div-4">
                    <div className="text-wrapper-10">Start quiz</div>
                </div>

                <div className="div-7">
                    <div
                        className="save-button"
                        onClick={() => handleStartSession()}
                    >
                        <img className="img-2" src={GamesIcon}/>

                        <div className="text-wrapper-15">Games</div>
                    </div>

                    <div
                        className="save-button"
                        onClick={() => handleStartQuiz()}
                    >
                        <img className="img-2" src={StartQuizIcon}/>

                        <div className="text-wrapper-15">Normal</div>
                    </div>
                </div>

                <div className="line"/>

                <div className="div-7">
                    <div
                        className="reset-button"
                        onClick={() => handleReset()}
                    >
                        <img className="img-2" src={ResetBlackIcon}/>

                        <div className="text-wrapper-16">Reset</div>
                    </div>

                    <div
                        className="save-button"
                        onClick={() => handleSave()}
                    >
                        <img className="img-2" src={SaveWhiteIcon}/>

                        <div className="text-wrapper-15">Save</div>
                    </div>
 
                </div>

                <div className="div-7">
                    <div
                        className="exit-button"
                        onClick={() => handleExit()}
                    >
                        <img className="img-2" src={ExitBlackIcon}/>

                        <div className="text-wrapper-16">Exit</div>
                    </div>
                    <ExitPopup
                        isOpen={isOpenExit}
                        setIsOpen={setIsOpenExit}
                    />
                </div>
            </div>
        </div>
    );
}