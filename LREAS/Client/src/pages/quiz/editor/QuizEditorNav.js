import React from 'react';
import './styles/QuizEditorNav.css';
import {
    MoreHoriz
} from '@mui/icons-material';
import {
    DuplicateBlackIcon, PlusBlackIcon, DeleteBlackIcon,
    PlusIcon
} from '../../../images/Icon';

export function QuizEditorNav ({
    quiz, questionItem, setQuestionItem, onUpdate,
    questionMedia
}) {
    const [currQuestionList, setCurrQuestionList] = React.useState(quiz ? quiz.questions : []);
    const [isOrderingQuestions, setIsOrderingQuestions] = React.useState(false);

    React.useEffect(() => {
        if (!isOrderingQuestions && quiz) {
            const tempList = [...quiz.questions];
            tempList.sort((a, b) => a.position - b.position);
            setCurrQuestionList(tempList);
        }
    }, [quiz?.questions]);

    React.useEffect(() => {
        if (isOrderingQuestions) {
            onUpdate({
                type: "UPDATE_QUESTION_LIST",
                newQuestionList: currQuestionList
            });
            setIsOrderingQuestions(false);
        }
    }, [currQuestionList]);

    const handleAddQuestion = (index) => {
        const randomId = Date.now().toString();
        onUpdate({
            type: "INSERT_QUESTION",
            newQuestion: {
                question: {
                    ...questionItem,
                    questionId: randomId,
                    question: "",
                    time: 10,
                    points: 1,
                    position: index,
                    imageObjectName: null,
                    imageUrl: null,
                    imageMetadata: {},
                    choices: [
                        {
                            choiceId: randomId,
                            answer: "",
                            correct: true
                        },
                        {
                            choiceId: randomId + 1,
                            answer: "",
                            correct: false
                        }
                    ]
                },
                position: index
            }
        });
    };

    const handleDuplicateQuestion = (index) => {
        onUpdate({
            type: "INSERT_QUESTION",
            newQuestion: {
                question: {
                    ...questionItem,
                    questionId: Date.now()
                },
                position: index
            }
        });
    };

    const handleDeleteQuestion = () => {
        onUpdate({
            type: "DELETE_QUESTION",
            questionId: questionItem.questionId
        });
    };

    const BoxSetting = ({ index, refPopup, popupPosition, isOpen }) => {
        return (
          <div
            className="box-settings"
            style={{
                top: popupPosition.top,
                left: popupPosition.left,
                opacity: `${isOpen ? "1" : "0"}`,
                visibility: `${isOpen ? "visible" : "hidden"}`,
            }}
            ref={refPopup}
          >
            <div
                className="row-setting"
                onClick={() => handleAddQuestion(index + 1)}
            >
              <div className="command">
                <img className="icon" src={PlusBlackIcon}/>
                <div className="text-wrapper">
                    Add new below
                </div>
              </div>
    
              <div className="shortcut">
                <div className="text-shorcut">
                    Ctrl+C
                </div>
              </div>
            </div>
    
            <div
                className="row-setting"
                onClick={() => handleDuplicateQuestion(index + 1)}
            >
              <div className="command">
                <img className="icon" src={DuplicateBlackIcon}/>
                <div className="text-wrapper">
                    Duplicate
                </div>
              </div>
    
              <div className="shortcut">
                <div className="text-shorcut">
                    Ctrl+D
                </div>
              </div>
            </div>
    
            <div
              className="row-setting"
              onClick={() => handleDeleteQuestion()}
            >
              <div className="command">
                <img className="icon" src={DeleteBlackIcon}/>
                <div className="text-wrapper">
                    Delete
                </div>
              </div>
    
              <div className="shortcut">
                <div className="text-shorcut">
                    Del
                </div>
              </div>
            </div>
          </div>
        );
    };

    const Item = ({ item, index }) => {
        const refPopup = React.useRef(null);
        const refItem = React.useRef(null);
        const refItemFrame = React.useRef(null);
        const [isOpenPopup, setIsOpenPopup] = React.useState(false);
        const [popupPosition, setPopupPosition] = React.useState({ top: 0, left: 0 });
        const [hover, setHover] = React.useState(false);
        const [media, setMedia] = React.useState(null);
        const header = document.getElementsByTagName("header")[0];
        const [isMouseDown, setIsMouseDown] = React.useState(false);
        const [initialMousePos, setInitialMousePos] = React.useState({ top: 0, left: 0 });
        const [currOverlayLine, setCurrOverlayLine] = React.useState(null);
        const containerRef = React.useRef(null);

        React.useEffect(() => {
            const m = questionMedia.filter(qm => qm.questionId === item.questionId);
            if (m.length > 0) {
                setMedia(m[0].file);
            }
        }, [item]);

        React.useEffect(() => {
            if (refItem.current && refPopup.current) {
                const rectItem = refItem.current.getBoundingClientRect();
                const rectPopup = refPopup.current.getBoundingClientRect();
                const viewportHeight = window.innerHeight;
                
                let top = rectItem.top + rectItem.height + 4 - header.offsetHeight;
                let left = rectItem.left + rectItem.width;

                if (top + header.offsetHeight + rectPopup.height > viewportHeight) {
                    top -= (rectPopup.height + 8 + rectItem.height);
                }

                setPopupPosition({
                    top: top,
                    left: left
                });
            }
        }, [isOpenPopup]);

        const handleResize = () => {
            setIsOpenPopup(false);
        };
    
        React.useEffect(() => {
            window.addEventListener("resize", handleResize, false);
        }, []);

        const handleKeepItemSize = (itemFrame, containerRect) => {
            itemFrame.style.position = "fixed";
            itemFrame.style.width = "unset";
            itemFrame.style.height = "unset";
            itemFrame.style.right = (window.innerWidth - containerRect.right) + "px";
            itemFrame.style.bottom = (window.innerHeight - containerRect.bottom) + "px";
            itemFrame.style.top = containerRect.top + "px";
            itemFrame.style.left = containerRect.left + "px";
        };

        const isOverlapping = (el1, el2) => {
            if (!el1 || !el2) {
                return false;
            }

            const rect1 = el1.getBoundingClientRect();
            const rect2 = el2.getBoundingClientRect();

            return !(
                rect1.right < rect2.left ||
                rect1.left > rect2.right ||
                rect1.bottom < rect2.top ||
                rect1.top > rect2.bottom
            );
        };

        const handleMouseDownAndMove = (e) => {
            if (!questionItem || questionItem.questionId !== item.questionId || !refItemFrame.current) {
                return;
            }

            if (isMouseDown && containerRef.current) {
                const itemFrame = refItemFrame.current;
                const containerRect = containerRef.current.getBoundingClientRect();

                const top = e.clientY - initialMousePos.top;
                const left = e.clientX - initialMousePos.left;

                handleKeepItemSize(itemFrame, containerRect);

                itemFrame.style.transform = `translate(${left}px, ${top}px)`;
                itemFrame.style.zIndex = "100";

                // handle lines effect
                const lines = document.getElementsByClassName("separator-line");
                for (let line of lines) {
                    if (isOverlapping(itemFrame, line)) {
                        line.classList.add("separator-line-overlapping");
                        setCurrOverlayLine(line);
                    }
                    else {
                        line.classList.remove("separator-line-overlapping");
                    }
                }
            }
        };

        const handleMouseUpAndDrop = (e) => {
            if (!refItemFrame.current) {
                return;
            }

            setIsMouseDown(false);
            
            if (currOverlayLine) {
                // update html
                const parentNode = currOverlayLine.parentNode;

                if (index > Number(currOverlayLine.id)) {
                    const sourceElement = containerRef.current;

                    // add animations for source element
                    const animateDisY = document.getElementById(
                        Number(currOverlayLine.id) + 1 + "container"
                    ).getBoundingClientRect().top - sourceElement.getBoundingClientRect().top;
                    
                    // position source element to destination position
                    refItemFrame.current.style.transition = "transform 0.3s";
                    refItemFrame.current.style.transform = `translateY(${animateDisY}px)`;
                    refItemFrame.current.ontransitionend = () => {
                        refItemFrame.current.style.transition = "";
                        refItemFrame.current.style.position = "absolute";
                        refItemFrame.current.style.inset = "0";
                        refItemFrame.current.style.transform = `translate(0, 0)`;
                        refItemFrame.current.style.zIndex = "1";
                    };

                    const currLineIdx = Number(currOverlayLine.id);
                    let prevElementTop = sourceElement.getBoundingClientRect().top;
                    for (let i = index - 1; i > currLineIdx; i--) {
                        const element = document.getElementById(i).previousSibling;

                        const disY = prevElementTop - element.getBoundingClientRect().top;
                        prevElementTop = element.getBoundingClientRect().top;
                        
                        element.style.transform = `translateY(${disY}px)`;
                        element.ontransitionend = () => {
                            element.style.transform = "";
                            parentNode.insertBefore(element, document.getElementById(i + 1));
                            
                            if (i === currLineIdx + 1) {
                                sourceElement.style.transform = "";
                                parentNode.insertBefore(sourceElement, currOverlayLine.nextSibling);

                                setIsOrderingQuestions(true);

                                // update current question list first
                                setCurrQuestionList(prev => {
                                    let source = index;
                                    let destination = Number(currOverlayLine.id);

                                    if (source > destination) {
                                        destination = Math.min(prev.length - 1, destination + 1);
                                    }

                                    const updatedList = [...prev];
                                    const [questionTemp] = updatedList.splice(source, 1);
                                    updatedList.splice(destination, 0, questionTemp);
                                    return updatedList;
                                });
                            }
                        };
                    }
                }
                else {
                    const sourceElement = containerRef.current;

                    // add animations for source element
                    const animateDisY = currOverlayLine.previousSibling.getBoundingClientRect().top - sourceElement.getBoundingClientRect().top;
                        
                    // position source element to destination position
                    refItemFrame.current.style.transition = "transform 0.3s";
                    refItemFrame.current.style.transform = `translateY(${animateDisY}px)`;
                    refItemFrame.current.ontransitionend = () => {
                        refItemFrame.current.style.transition = "";
                        refItemFrame.current.style.position = "absolute";
                        refItemFrame.current.style.inset = "0";
                        refItemFrame.current.style.transform = `translate(0, 0)`;
                        refItemFrame.current.style.zIndex = "1";
                    };

                    const currLineIdx = Number(currOverlayLine.id);
                    let prevElementTop = sourceElement.getBoundingClientRect().top;
                    for (let i = index + 1; i <= currLineIdx; i++) {
                        const element = document.getElementById(i).previousSibling;

                        const disY = prevElementTop - element.getBoundingClientRect().top;
                        prevElementTop = element.getBoundingClientRect().top;
                        
                        element.style.transform = `translateY(${disY}px)`;
                        element.ontransitionend = () => {
                            element.style.transform = "";
                            parentNode.insertBefore(element, document.getElementById(i - 1));
                            
                            if (i === currLineIdx) {
                                sourceElement.style.transform = "";
                                parentNode.insertBefore(sourceElement, currOverlayLine);

                                setIsOrderingQuestions(true);

                                // update current question list first
                                setCurrQuestionList(prev => {
                                    let source = index;
                                    let destination = Number(currOverlayLine.id);

                                    if (source > destination) {
                                        destination = Math.min(prev.length - 1, destination + 1);
                                    }

                                    const updatedList = [...prev];
                                    const [questionTemp] = updatedList.splice(source, 1);
                                    updatedList.splice(destination, 0, questionTemp);
                                    return updatedList;
                                });
                            }
                        };
                    }
                }
            }
            else {
                const itemFrame = refItemFrame.current;
                itemFrame.style.position = "absolute";
                itemFrame.style.inset = "0";
                itemFrame.style.transform = `translate(0, 0)`;
                itemFrame.style.zIndex = "1";
            }

            // remove line effect
            const lines = document.getElementsByClassName("separator-line");
            for (let line of lines) {
                line.classList.remove("separator-line-overlapping");
            }

            if (refItem.current && refItem.current.contains(e.target)) {
                setIsOpenPopup(!isOpenPopup);
            }
            else {
                if (questionItem.questionId !== item.questionId) {
                    // this will cause re-render of QuizEditorNav
                    setQuestionItem(item);
                }
            }
        };

        const handleMouseDownOnly = (e) => {
            if (questionItem.questionId !== item.questionId) {
                return;
            }

            setIsMouseDown(true);
            setInitialMousePos({ top: e.clientY, left: e.clientX });
        };

        React.useEffect(() => {
            if (refPopup.current) {
                document.getElementsByClassName("create-edit-quiz")[0]?.addEventListener(
                    "mouseup", (e) => {
                      if (
                        refPopup.current &&
                        !refPopup.current.contains(e.target) &&
                        (!refItem.current || !refItem.current.contains(e.target))
                      ) {
                        setIsOpenPopup(false);
                      }
                    }
                );
            }
        }, [refPopup]);

        return (
            <>
                <div
                    id={index + "container"}
                    className="item-frame-container"
                    ref={containerRef}
                >
                    <div
                        ref={refItemFrame}
                        className={`
                            item-frame item-frame-selected-invisible 
                            ${questionItem && questionItem.position === item.position ? "item-frame-selected-visible" : ""}
                        `}
                        onMouseOver={() => {
                            setHover(true);
                        }}
                        onMouseLeave={() => {
                            if (isOpenPopup) {
                                return;
                            }
                            setHover(false);
                        }}
                        onMouseDown={handleMouseDownOnly}
                        onMouseUp={handleMouseUpAndDrop}
                        onMouseMove={handleMouseDownAndMove}
                    >
                        <div className="overlap-group">
                            <div className="content-frame">
                                <div className="question">
                                    <div className="question-content">
                                        {item.question || "Question"}
                                    </div>
                                </div>
                                <div className="image">
                                    <img
                                        className="img"
                                        alt=""
                                        src={item.imageUrl || (media && media[1]) || ""}
                                    />
                                </div>
                                <div className="answers">
                                    {
                                        item &&
                                        item.choices.map((c, i) => {
                                            return (
                                                <div key={i} className="answer">
                                                    {
                                                        c.correct &&
                                                        <span className="circle"></span>
                                                    }
                                                </div>
                                            );
                                        })
                                    }
                                </div>
                            </div>

                            {
                                hover &&
                                <>
                                    <div className="text-wrapper">{index + 1}</div>
                                    {
                                        questionItem.position === item.position &&
                                        <div
                                            className="more"
                                            ref={refItem}
                                        >
                                            <MoreHoriz
                                                className="more-horiz"
                                            />
                                        </div>
                                    }
                                </>
                            }
                        </div>
                    </div>
                </div>
                <div className="separator-line" id={index}/>
                <BoxSetting
                    index={index}
                    refPopup={refPopup}
                    popupPosition={popupPosition}
                    isOpen={isOpenPopup}
                />
            </>
        );
    };

    return (
        <div className="quiz-editor-mini-drawer">
            <div className="sliding-container">
                <div className="items">
                    {
                        currQuestionList.map((question, index) => (
                            <Item
                                key={index}
                                index={index}
                                item={question}
                            />
                        ))
                    }
                </div>
            </div>
            <div className="action-buttons">
                <div
                    className="new-button"
                    onClick={() => handleAddQuestion(currQuestionList.length)}
                >
                    <img className="img-2" src={PlusIcon}/>

                    <div className="text-wrapper-15">Add new</div>
                </div>
            </div>
        </div>
    );
};