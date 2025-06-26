import {
    DuplicateBlackIcon,
    PlusBlackIcon,
    UploadIcon2, DeleteBlackIcon,
    MoreBlackIcon, InfoIcon
} from '../../../images/Icon';
import './styles/Editor.css';
import React from 'react';
import { useDropzone } from 'react-dropzone';
import { ImageMetadataPopup } from './ImageMetadataPopup';

const BoxSetting = ({
  isOpen, answerItem, setIsOpenSetting, refPopup, popupPosition,
  questionItem, onUpdate
}) => {
  const handleDeleteAnswer = () => {
    onUpdate({
      type: "DELETE_ANSWER",
      questionId: questionItem.questionId,
      choiceId: answerItem.choiceId
    });
  };

  const handleAddAnswer = () => {
    onUpdate({
      type: "INSERT_ANSWER",
      questionId: questionItem.questionId,
      answer: {
        choiceId: Date.now(),
        answer: "",
        correct: false
      },
      position: questionItem.choices.length
    });
  };

  const handleDuplicateAnswer = () => {
    onUpdate({
      type: "INSERT_ANSWER",
      questionId: questionItem.questionId,
      answer: {
        choiceId: Date.now(),
        answer: answerItem.answer,
        correct: answerItem.correct
      },
      position: questionItem.choices.length
    });
  };

  return (
    <div
      className="box-settings"
      style={{
        top: `${popupPosition.top}px`,
        left: `${popupPosition.left}px`,
        opacity: `${isOpen ? "1" : "0"}`,
        visibility: `${isOpen ? "visible" : "hidden"}`,
      }}
      ref={refPopup}
    >
      <div
        className="row-setting"
        onClick={() => {
          handleAddAnswer();
          setIsOpenSetting(false);
        }}
      >
        <div className="command">
          <img className="icon" src={PlusBlackIcon}/>
          <div className="text-wrapper-box-setting">
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
        onClick={() => {
          handleDuplicateAnswer();
          setIsOpenSetting(false);
        }}
      >
        <div className="command">
          <img className="icon" src={DuplicateBlackIcon}/>
          <div className="text-wrapper-box-setting">
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
        onClick={() => {
          handleDeleteAnswer();
          setIsOpenSetting(false);
        }}
      >
        <div className="command">
          <img className="icon" src={DeleteBlackIcon}/>
          <div className="text-wrapper-box-setting">
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

const Answer = ({
  answerItem, questionItem, onUpdate,
  isAllowedUnSelect
}) => {
  const [isOpenSetting, setIsOpenSetting] = React.useState(false);
  const popupRef = React.useRef(null);
  const answerRef = React.useRef(null);
  const moreRef = React.useRef(null);
  const [popupPosition, setPopupPosition] = React.useState({top: 0, left: 0});
  const [hover, setHover] = React.useState(false);
  const [answerText, setAnswerText] = React.useState(answerItem?.answer ? answerItem.answer : "");
  const [isCorrect, setIsCorrect] = React.useState(answerItem?.correct ? answerItem.correct : false);
  const header = document.getElementsByTagName("header")[0];
  const isMountingAnswerRef = React.useRef(false);
  const isMountingCorrectRef = React.useRef(false);

  React.useEffect(() => {
    window.addEventListener("resize", handleResize, false);
    document.getElementsByClassName("editor-quiz-container")[0]?.addEventListener(
      "scroll", () => {
          setIsOpenSetting(false);
      },
      false
    );

    isMountingAnswerRef.current = true;
    isMountingCorrectRef.current = true;
  }, []);

  React.useEffect(() => {
    const newAnswerText = answerItem?.answer ? answerItem.answer : "";
    const newIsCorrect = answerItem?.correct ? answerItem.correct : false;

    if (newAnswerText !== answerText) {
      isMountingAnswerRef.current = true;
      setAnswerText(newAnswerText);
    }

    if (newIsCorrect !== isCorrect) {
      isMountingCorrectRef.current = true;
      setIsCorrect(newIsCorrect);
    }
  }, [answerItem]);

  React.useEffect(() => {
    if (!isMountingAnswerRef.current) {
      onUpdate({
        type: "UPDATE_ANSWER",
        questionId: questionItem.questionId,
        choiceId: answerItem.choiceId,
        updatedAnswer: {
          answer: answerText
        }
      });
    }
    else {
      isMountingAnswerRef.current = false;
    }
  }, [answerText]);

  React.useEffect(() => {
    if (!isMountingCorrectRef.current) {
      onUpdate({
        type: "UPDATE_ANSWER",
        questionId: questionItem.questionId,
        choiceId: answerItem.choiceId,
        updatedAnswer: {
          correct: isCorrect
        }
      });
    }
    else {
      isMountingCorrectRef.current = false;
    }
  }, [isCorrect]);

  const useClickOutside = (ref) => {
      React.useEffect(() => {
          const handleClickOutside = (e) => {
            if (ref.current && !ref.current.contains(e.target)) {
              setIsOpenSetting(false);
            }
          };
          document.getElementsByClassName("editor")[0]?.addEventListener(
            "mousedown", handleClickOutside
          );
      }, [ref]);
  };
  useClickOutside(answerRef);

  React.useEffect(() => {
    if (!popupRef.current || !answerRef.current || isOpenSetting) {
      return;
    }

    let top = 0;
    let left = 0;
    const popupRect = popupRef.current.getBoundingClientRect();
    const viewportWidth = window.innerWidth;

    if (!moreRef.current) {
      top = 0;
      left = 0;
    }
    else {
      const moreRect = moreRef.current.getBoundingClientRect();

      top = moreRect.top - popupRect.height - header.offsetHeight - 4;
      left = moreRect.right + 4;

      if (top + popupRect.height < 0) {
        top = moreRect.top + moreRect.height + 4;
      }

      if (left + popupRect.width > viewportWidth) {
        left = moreRect.left - popupRect.width - 4;
      }
    }

    setPopupPosition({top: top, left: left});
  }, [isOpenSetting, hover]);

  const handleResize = () => {
    setIsOpenSetting(false);
  };

  React.useEffect(() => {
    if (popupRef.current) {
      document.getElementsByClassName("create-edit-quiz")[0]?.addEventListener(
        "mouseup", (e) => {
          if (
            popupRef.current &&
            !popupRef.current.contains(e.target) &&
            (!moreRef.current || !moreRef.current.contains(e.target))
          ) {
            setIsOpenSetting(false);
          }
        }
      );
    }
  }, [popupRef]);

  return (
    <>
      <div
        className="answer"
        id={answerItem.choiceId}
        ref={answerRef}
        onMouseOver={() => setHover(true)}
        onMouseLeave={() => setHover(false)}
      >
        <textarea
          className="text-wrapper-2"
          placeholder="Enter your answer"
          onChange={e => setAnswerText(e.target.value)}
          onInput={(e) => {
            e.target.style.height = "auto"; // Reset height
            e.target.style.height = e.target.scrollHeight + "px"; // Auto-expand
          }}
          value={answerText}
        />
        
        <div
          className={`circle-checkbox ${isCorrect ? "circle-checkbox-checked" : ""}`}
          onMouseUp={() => {
            if (isCorrect && !isAllowedUnSelect) {
              return;
            }

            setIsCorrect(!isCorrect);
          }}
        >
          {
            isCorrect &&
            <div className="checkmark"/>
          }
        </div>

        {
          hover &&
          <div
            className="more"
            onClick={() => setIsOpenSetting(!isOpenSetting)}
            ref={moreRef}
          >
            <img
              src={MoreBlackIcon}
              className="more-icon"
            />
          </div>
        }
      </div>
      <BoxSetting
        isOpen={isOpenSetting}
        answerItem={answerItem}
        setIsOpenSetting={setIsOpenSetting}
        refPopup={popupRef}
        popupPosition={popupPosition}
        questionItem={questionItem}
        onUpdate={onUpdate}
      />
    </>
  );
};

export function Editor({
  questionItem, updateMedia, onUpdate,
  currQuestionMedia
}) {
  const [file, setFile] = React.useState(null);
  const [image, setImage] = React.useState(null);
  const [rejectDrop, setRejectDrop] = React.useState(false);
  const [lastAnswerItem, setLastAnswerItem] = React.useState(null);
  const [isOpenImageMetadataPopup, setIsOpenImageMetadataPopup] = React.useState(false);
  const [questionText, setQuestionText] = React.useState(questionItem.question);
  const [isAllowedUnSelect, setIsAllowedUnSelect] = React.useState(true);
  const isMountingRef = React.useRef(false);

  const onDrop = React.useCallback((acceptedFiles) => {
    acceptedFiles.forEach((f) => {
      var reader = new FileReader();
      reader.onload = e => {
        const image = new Image();
        image.src = URL.createObjectURL(f);

        image.onload = () => {
          setImage(image);
        };
        setFile([f, URL.createObjectURL(f), e.target.result]);
      };
      reader.readAsArrayBuffer(f);
      setRejectDrop(false);
    });
  }, []);
  const onDropRejected = React.useCallback((_) => {
    setRejectDrop(true);
    setFile([]);
  }, []);

  React.useEffect(() => {
    if (file && file.length > 0 && image) {
      const mainCreateEditQuiz = document.getElementById("create-edit-quiz");
      mainCreateEditQuiz.style.pointerEvents = "none";
      mainCreateEditQuiz.style.opacity = "0.5";
      
      updateMedia("UPDATE", file[2], file, {
        width: image.width,
        height: image.height
      });
    }
  }, [file, image]);

  const accept = {
    "image/jpeg": [".jpeg", ".jpg"],
    "image/png": [".png"],
    "image/webp": [".webp"],
  };
  const {getRootProps, getInputProps} = useDropzone({
    onDrop,
    onDropRejected,
    "accept": accept,
    maxSize: 104857600,
    maxFiles: 1
  });

  React.useEffect(() => {
    isMountingRef.current = true;
  }, []);

  React.useEffect(() => {
    let tempData = questionItem.choices[questionItem.choices.length - 1];
    if (questionItem.choices.length % 2 === 0) {
      tempData = questionItem.choices[questionItem.choices.length - 2];
    }
    setLastAnswerItem(tempData);
    setFile(currQuestionMedia.file);

    const newQuestionText = questionItem?.question ? questionItem.question : "";
    if (newQuestionText !== questionText) {
      isMountingRef.current = true;
      setQuestionText(newQuestionText);
    }
  }, [questionItem]);

  React.useEffect(() => {
    if (!isMountingRef.current) {
      onUpdate({
        type: "UPDATE_QUESTION",
        updatedQuestion: {
          questionId: questionItem.questionId,
          question: questionText
        }
      });
    }
    else {
      isMountingRef.current = false;
    }
  }, [questionText]);

  React.useEffect(() => {
    let count = 0;
    for (let i = 0; i < questionItem.choices.length; i++) {
      if (questionItem.choices[i].correct) {
        if (++count > 1) {
          setIsAllowedUnSelect(true);
          return;
        }
      }
    }
    setIsAllowedUnSelect(false);
  }, [questionItem?.choices]);

  const Action = () => {
    const handleAddAnswer = () => {
      onUpdate({
        type: "INSERT_ANSWER",
        questionId: questionItem.questionId,
        answer: {
          choiceId: Date.now(),
          answer: "",
          correct: false
        },
        position: questionItem.choices.length
      });
    };

    const handleDuplicateAnswer = () => {
      onUpdate({
        type: "INSERT_ANSWER",
        questionId: questionItem.questionId,
        answer: {
          choiceId: Date.now(),
          answer: lastAnswerItem.answer,
          correct: lastAnswerItem.correct
        },
        position: questionItem.choices.length
      });
    };

    const isLeft = questionItem.choices.length % 2 === 0;

    return (
      <div
        className={`action ${isLeft ? "action-left" : "action-right"}`}
      >
          <img
            src={PlusBlackIcon}
            className="add-circle"
            onMouseDown={() => handleAddAnswer()}
          />
          <img
            src={DuplicateBlackIcon}
            className="duplicate"
            onMouseDown={() => handleDuplicateAnswer()}
          />
      </div>
    );
  };

  const handleDeleteMedia = () => {
    updateMedia("DELETE", null, null);
    setFile(null);
    setImage(null);
    onUpdate({
      type: "UPDATE_QUESTION",
      updatedQuestion: {
        questionId: questionItem.questionId,
        imageObjectName: null,
        imageUrl: null,
        imageMetadata: {}
      }
    });
  };

  const formatDate = (timestamp) => {
    try {
      timestamp = parseInt(timestamp);
    }
    catch {
      return "undefined";
    }

    const date = new Date(timestamp);
    
    if (!date.getTime()) {
      return "undefined";
    }

    // get formated date and time
    const formattedDateTime = date.toLocaleString(
        "en-GB",
        {
            hour12: false
        }
    );

    // get timezone name
    const timeZone = Intl.DateTimeFormat(
        undefined,
        {
            timeZoneName: "short"
        }
    ).formatToParts(date).find(
        part => part.type === "timeZoneName"
    )?.value;

    return `${formattedDateTime} (${timeZone})`; // 13/03/2025 14:30:45 (GMT+7)
  };

  const getImageMetadata = () => {
    if (file && file.length !== 0) {
      const fileMetadata = file[0];
      return {
        name: fileMetadata.name || "",
        file_size: (fileMetadata.size / 1024).toFixed(2) + " KB",
        type: fileMetadata.type.split("/")[1].toUpperCase(),
        lastModified: formatDate(fileMetadata.lastModified),
        dimensions: `${image.width} x ${image.height}`,
      };
    }
    
    if (questionItem.imageUrl) {
      const metadata = questionItem.imageMetadata;

      if (Object.keys(metadata).length === 0) {
        return {};
      }

      return {
        name: metadata.name || "",
        file_size: (metadata.size / 1024).toFixed(2) + " KB",
        type: metadata.type.split("/")[1].toUpperCase(),
        lastModified: formatDate(metadata.last_modified),
        dimensions: `${metadata.width} x ${metadata.height}`,
      };
    }

    return {};
  };

  return (
    <div className="editor-quiz-container">
      <div className="editor-quiz">
        <div className="question">
          <textarea
            className="text-wrapper"
            placeholder="Enter your question"
            onChange={e => setQuestionText(e.target.value)}
            onInput={(e) => {
              e.target.style.height = "auto"; // Reset height
              e.target.style.height = e.target.scrollHeight + "px"; // Auto-expand
            }}
            value={questionText}
          />
        </div>
        
        <div {...getRootProps({className: "image-frame"})}>
          <input {...getInputProps()} />
          <div className="image">
            {
              ((!file || file.length === 0) && !questionItem.imageUrl) &&
              <img
                className="upload-icon"
                alt="Upload icon"
                src={UploadIcon2}
                loading="lazy"
              />
            }
            {
              (!file || file.length === 0) && !questionItem.imageUrl ? (
                <>
                  <div className="text">Upload file or drop file here</div>
                  <div className="note">
                    <div className="text-note">Max file size: 100 MB</div>
                    <div className="text-note">Format: jpeg, jpg, png, webp</div>
                  </div>
                </>
              ) : (
                <img
                  src={questionItem.imageUrl || file[1]}
                  className="upload-image"
                  loading="lazy"
                />
              )
            }
          </div>
          {
            (file && file.length !== 0 || questionItem.imageUrl) &&
            <div className="action-image-buttons">
              <div
                className="btn"
                onClick={e => {
                  e.stopPropagation();
                  setIsOpenImageMetadataPopup(true);
                }}
              >
                <img
                  src={InfoIcon}
                />
              </div>
              <div
                className="btn"
                onClick={e => {
                  e.stopPropagation();
                  handleDeleteMedia();
                }}
              >
                <img
                  src={DeleteBlackIcon}
                />
              </div>
            </div>
          }
        </div>

        <div className="answers">
          <div className="div-2">
            {
              questionItem &&
              questionItem.choices.map((a, i) => {
                return (
                  <Answer
                    key={a.choiceId}
                    answerItem={a}
                    questionItem={questionItem}
                    onUpdate={onUpdate}
                    isAllowedUnSelect={isAllowedUnSelect}
                  />
                );
              })
            }
            <Action/>
          </div>
        </div>
      </div>

      <ImageMetadataPopup
        isOpen={isOpenImageMetadataPopup}
        setIsOpen={setIsOpenImageMetadataPopup}
        getMetadata={getImageMetadata}
      />
    </div>
  );
};