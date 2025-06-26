import React from "react";
import { Header } from "./Header";
import "./styles/QuizEditorPage.css";
import { Setting } from "./Setting";
import { QuizEditorNav } from './QuizEditorNav';
import { Editor } from './Editor';
import {
    getQuizForEdit, updateQuiz, uploadMediaQuiz
} from '../../../api';
import { useParams } from "react-router-dom";
import { Notification } from "./Notification";
import {
    SuccessIcon, FailedIcon, LoadingIcon, AvatarIcon
} from "../../../images/Icon";
import { getToken } from "../../../utils";

export default function QuizEditorPage() {
    const { resourceId } = useParams();
    const JWT_loginToken = getToken();
    const [questionItem, setQuestionItem] = React.useState(null);
    const [currQuestionMedia, setCurrQuestionMedia] = React.useState(null);
    const [questionMedia, setQuestionMedia] = React.useState([]);
    const [currAction, setCurrAction] = React.useState("");
    const [showNoti, setShowNoti] = React.useState(false);
    const [notiIcon, setNotiIcon] = React.useState("");
    const [isLoading, setIsLoading] = React.useState(false);
    const [notiMessage, setNotiMessage] = React.useState("");
    const [socket, setSocket] = React.useState(null);
    const [socketTimeout, setSocketTimeout] = React.useState(null);
    const [updateTimeout, setUpdateTimeout] = React.useState(null);
    const [collaborators, setCollaborators] = React.useState([]);
    const websocketProtocol = window.location.protocol === "https:" ? "wss" : "ws";

    const quizReducer = (quiz, action) => {
        switch(action.type) {
            case "UPDATE_QUIZ":
                return {
                    ...quiz,
                    ...action.updatedQuiz
                };
            case "INSERT_QUESTION":
                const {question, position} = action.newQuestion;
                const updatedQuestions = [...quiz.questions];

                // sort question list first
                updatedQuestions.sort((a, b) => a.position - b.position);

                updatedQuestions.splice(position, 0, question); // insert new question
                
                // update position for all questions to maintain order
                const reorderedQuestions = updatedQuestions.map((q, index) => {
                    return {
                        ...q,
                        position: index
                    };
                });

                return {
                    ...quiz,
                    questions: reorderedQuestions
                };
            case "INSERT_ANSWER":
                const questionId = action.questionId;
                const answer = action.answer;
                const positionA = action.position;
                const updatedQ = quiz.questions.map(q => {
                    if (q.questionId === questionId) {
                        const updatedAnswers = [...q.choices];

                        updatedAnswers.splice(positionA, 0, answer);

                        return {
                            ...q,
                            choices: updatedAnswers
                        };
                    }
                    return q;
                });

                return {
                    ...quiz,
                    questions: updatedQ
                };
            case "UPDATE_QUESTION":
                return {
                    ...quiz,
                    questions: quiz.questions.map(q => {
                        if (q.questionId === action.updatedQuestion.questionId) {
                            return {
                                ...q,
                                ...action.updatedQuestion
                            };
                        }
                        return q;
                    })
                };
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
                                            ...action.updatedAnswer
                                        };
                                    }
                                    return a;
                                })
                            };
                        }
                        return q;
                    })
                };
            case "DELETE_QUESTION":
                let currQuestionPos = questionItem.position;

                // delete question
                const newQuestions = quiz.questions.filter(
                    q => q.questionId !== action.questionId
                );

                // update position for all questions to maintain order
                const orderedQuestions = newQuestions.map((q, index) => {
                    return {
                        ...q,
                        position: index
                    };
                });

                // update question item
                if (currQuestionPos >= orderedQuestions.length) {
                    currQuestionPos--;
                }
                setQuestionItem(orderedQuestions[currQuestionPos]);

                return {
                    ...quiz,
                    questions: orderedQuestions
                };
            case "DELETE_ANSWER":
                return {
                    ...quiz,
                    questions: quiz.questions.map(q => {
                        if (q.questionId === action.questionId) {
                            return {
                                ...q,
                                choices: q.choices.filter(
                                    c => c.choiceId !== action.choiceId
                                )
                            };
                        }
                        return q;
                    })
                };
            case "UPDATE_QUESTION_LIST":
                // update position for all questions to maintain order
                const newList = action.newQuestionList.map((q, index) => {
                    return {
                        ...q,
                        position: index
                    };
                });

                return {
                    ...quiz,
                    questions: newList
                };
            case "INITIAL":
                return {
                    ...action.initial
                };
            default:
                return quiz;
        }
    };
    const [quiz, dispatch] = React.useReducer(quizReducer, {});

    const fetchData = async () => {
        await getQuizForEdit(resourceId).then(
            res => {
                handleDispatch({
                    type: "INITIAL",
                    initial: res.data
                });
            }
        ).catch(
            e => {
                console.log(e);
            }
        );
    };

    const isEmpty = (obj) => {
        return !obj || (
                Object.keys(obj).length === 0 &&
                obj.constructor === Object
        );
    };

    const deepEqual = (obj1, obj2) => {
        if (isEmpty(obj1) && isEmpty(obj2)) {
            return true;
        }
        if (isEmpty(obj1) || isEmpty(obj2)) {
            return false;
        }

        if (obj1 === obj2) {
            return true;
        }
        
        if (
            typeof obj1 !== 'object' ||
            typeof obj2 !== 'object' ||
            obj1 === null ||
            obj2 === null
        ) {
            return false;
        }
        
        const keys1 = Object.keys(obj1);
        const keys2 = Object.keys(obj2);
        
        if (keys1.length !== keys2.length) {
            return false;
        }
        
        for (let key of keys1) {
            if (!keys2.includes(key)) {
                return false;
            }
            if (!deepEqual(obj1[key], obj2[key])) {
                return false;
            }
        }
        
        return true;
    };

    const clearTimeouts = () => {
        if (updateTimeout) {
            clearTimeout(updateTimeout);
        }
        if (socketTimeout) {
            clearTimeout(socketTimeout);
        }
    };

    const setUpWebSocket = () => {
        // listen for real-time quiz updates
        let newSocket = socket;
        if (!socket || socket.readyState === WebSocket.CLOSED) {
            newSocket = new WebSocket(`${websocketProtocol}://${window.location.hostname}:80/api/quiz/quiz-edit-collab?token=${JWT_loginToken}`);
        }
        
        newSocket.onmessage = e => {
            clearTimeouts();

            const data = JSON.parse(e.data);

            if (data.type === "UPDATED") {
                // console.log("Received UPDATED from server");

                handleDispatch({
                    type: "INITIAL",
                    initial: data.updatedQuiz
                });
                
                setCollaborators(data.collaborators.map(
                    c => {
                        return {
                            name: c.username,
                            imageUrl: c.avtPath,
                            lastActive: c.lastActive,
                        };
                    }
                ));
            }
            else if (data.type === "PING") {
                // console.log("Received PING from server");

                newSocket.send(JSON.stringify({
                    type: "PONG",
                }));
            }
            else if (data.type === "INFO") {
                // console.log("Received INFO from server");

                setCollaborators(data.collaborators.map(
                    c => {
                        return {
                            name: c.username,
                            imageUrl: c.avtPath,
                            lastActive: c.lastActive,
                        };
                    }
                ));
            }
        };
        
        newSocket.onopen = () => {
            // console.log("Connected to Quiz WebSocket server");

            newSocket.send(JSON.stringify({
                type: "REGISTER",
                updatedQuiz: {
                    resourceId: resourceId,
                }
            }));
        };

        newSocket.onerror = (error) => {
            console.error("Quiz WebSocket Error:", error);
        };

        setSocket(newSocket);
    };

    React.useEffect(() => {
        fetchData();
        setUpWebSocket();

        return () => {
            if (socket) {
                socket.close();
                setSocket(null);
            }
        };
    }, [resourceId]);

    const updateMedia = (type, fileBuffer, file, imageDim) => {
        if (type === "UPDATE") {
            const newMedia = {
                file: file,
                fileBuffer: fileBuffer,
                imageDim: imageDim,
                questionId: questionItem.questionId,
            };
            setQuestionMedia([...questionMedia, newMedia]);
            setCurrQuestionMedia(newMedia);
            setCurrAction("UPDATE_MEDIA");
        }
        else if (type === "DELETE") {
            setQuestionMedia(
                questionMedia.filter(
                    qm => qm.questionId !== questionItem.questionId
                )
            );
        }
    };

    const handleSetQuestionItem = (item = null) => {
        let questionI = null;
        if (item) {
            questionI = item;
            setQuestionItem(item);
        }
        else {
            if (
                Object.keys(quiz).length > 0 &&
                quiz.questions.length > 0
            ) {
                if (!questionItem) {
                    questionI = quiz.questions[0];
                    setQuestionItem(questionI);
                }
                else {
                    questionI = quiz.questions.filter(
                        q => q.position === questionItem.position
                    )[0];
                    setQuestionItem(questionI);
                }
            }
            else {
                return;
            }
        }

        if (!questionI) {
            return;
        }

        const qm = questionMedia.filter(
            qm => qm.questionId === questionI.questionId
        );
        if (qm.length > 0) {
            setCurrQuestionMedia(qm[0]);
        }
        else {
            setCurrQuestionMedia({
                fileUrl: null,
                file: null,
                imageDim: null,
                questionId: questionI.questionId,
            });
        }
    };

    const handleNoti = (loading, isSuccess, isReset) => {
        setShowNoti(true);
        if (loading) {
            setNotiMessage("Your changes is being saved...");
            setNotiIcon(LoadingIcon);
            setIsLoading(true);
        }
        else {
            if (isSuccess) {
                if (isReset) {
                    setNotiMessage("Your changes have been reset");
                }
                else {
                    setNotiMessage("Your changes have been saved");
                }
                setNotiIcon(SuccessIcon);
            }
            else {
                setNotiMessage("Cannot save your data");
                setNotiIcon(FailedIcon);
            }
            
            setIsLoading(false);
            setTimeout(() => setShowNoti(false), 5000);
        }
    };

    const handleSave = async (showPopup = true) => {
        const mainCreateEditQuiz = document.getElementById("create-edit-quiz");
        mainCreateEditQuiz.style.pointerEvents = "none";
        mainCreateEditQuiz.style.opacity = "0.5";

        if (showPopup) {
            handleNoti(true, false, false);
        }
        
        if (questionMedia.length > 0) {
            const {files, questionsId, widths, heights} = questionMedia.reduce(
                (acc, qm) => {
                    acc.files.push(qm.fileBuffer);
                    acc.questionsId.push(qm.questionId);
                    acc.widths.push(qm.imageDim.width);
                    acc.heights.push(qm.imageDim.height);
                    return acc;
                },
                {
                    files: [],
                    questionsId: [],
                    widths: [],
                    heights: []
                }
            );
            const metadata = {
                resourceId: quiz.resourceId,
                questionsId: questionsId,
                widths: widths,
                heights: heights
            };
    
            await uploadMediaQuiz(
                quiz.resourceId, files,
                JSON.stringify(metadata),
                JSON.stringify(quiz)
            ).then(
                async res => {
                    const updatedQuiz = res.data;

                    handleDispatch({
                        type: "INITIAL",
                        initial: updatedQuiz
                    });

                    setQuestionMedia([]);
                    setCurrQuestionMedia(null);

                    if (showPopup) {
                        handleNoti(false, true, false);
                    }

                    if (socket && socket.readyState === WebSocket.OPEN) {
                        socket.send(JSON.stringify({
                            type: "UPDATED",
                            updatedQuiz: updatedQuiz,
                        }));
                    }

                    mainCreateEditQuiz.style.pointerEvents = "auto";
                    mainCreateEditQuiz.style.opacity = "1";
                }
            ).catch(
                e => {
                    console.log(e);
                    handleNoti(false, false, false);
                }
            );
        }        
        else {
            await updateQuiz(quiz).then(
                res => {
                    const updatedQuiz = res.data;

                    handleDispatch({
                        type: "INITIAL",
                        initial: updatedQuiz
                    });
    
                    if (showPopup) {
                        handleNoti(false, true, false);
                    }

                    if (socket && socket.readyState === WebSocket.OPEN) {
                        socket.send(JSON.stringify({
                            type: "UPDATED",
                            updatedQuiz: updatedQuiz,
                        }));
                    }

                    mainCreateEditQuiz.style.pointerEvents = "auto";
                    mainCreateEditQuiz.style.opacity = "1";
                }
            ).catch(
                e => {
                    console.log(e);
                    handleNoti(false, false, false);
                }
            ); 
        }
        setQuestionMedia([]);
        setCurrQuestionMedia(null);
    };

    const handleReset = () => {
        handleNoti(true, false, false);
        fetchData();
        handleNoti(false, true, true);
    };

    React.useEffect(() => {
        handleSetQuestionItem();
    }, [quiz]);

    React.useEffect(() => {
        if (currAction !== "INITIAL" && currAction !== "") {
            if (socket && socket.readyState === WebSocket.OPEN) {
                clearTimeouts();

                setSocketTimeout(
                    setTimeout(() => {
                        if (questionMedia.length > 0) {
                            handleSave(false);
                        }
                        else {
                            socket.send(JSON.stringify({
                                type: "UPDATED",
                                updatedQuiz: quiz,
                            }));
                        }
                    }, 1000)
                );
            }
            else {
                setUpWebSocket();
            }
        }
        setCurrAction("");
    }, [currAction]);

    const handleDispatch = (action) => {
        clearTimeouts();

        setUpdateTimeout(
            setTimeout(() => {
                dispatch(action);
                setCurrAction(action.type);
            }, 50)
        );
    };

    return (
        <>
            <div className="edit-quiz-page">
                <Header
                    resourceId={resourceId}
                    collaborators={collaborators}
                />
                <div className="create-edit-quiz" id="create-edit-quiz">
                    <div className="div">
                        {
                            Object.keys(quiz).length > 0 &&
                            <>
                                <QuizEditorNav
                                    quiz={quiz}
                                    questionItem={questionItem}
                                    setQuestionItem={item => {
                                        handleSetQuestionItem(item);
                                    }}
                                    onUpdate={action => handleDispatch(action)}
                                    questionMedia={questionMedia}
                                />

                                {
                                    questionItem &&
                                    <>
                                        <Editor
                                            questionItem={questionItem}
                                            updateMedia={(type, fileBuffer, file, imageDim) => {
                                                updateMedia(type, fileBuffer, file, imageDim);
                                            }}
                                            onUpdate={action => handleDispatch(action)}
                                            currQuestionMedia={currQuestionMedia}
                                        />

                                        <Setting
                                            questionItem={questionItem}
                                            quizItem={quiz}
                                            onUpdate={action => handleDispatch(action)}
                                            onSave={() => handleSave(true)}
                                            onReset={() => handleReset()}
                                        />
                                    </>
                                }
                            </>
                        }
                    </div>
                </div>
            </div>
            <Notification
                isOpen={showNoti}
                message={notiMessage}
                notiIcon={notiIcon}
                isLoading={isLoading}
            />
        </>
    );
};