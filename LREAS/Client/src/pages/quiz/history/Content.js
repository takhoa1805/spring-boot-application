import './styles/Content.css';
import React from 'react';

const Answer = ({
  answerItem
}) => {
  return (
    <>
      <div
        className="answer"
        id={answerItem.choiceId}
      >
        <div className="text-wrapper-2">
          {answerItem.answer}
        </div>
        
        <div
          className={`circle-checkbox ${answerItem.correct ? "circle-checkbox-checked" : ""}`}
        >
          {
            answerItem.correct &&
            <div className="checkmark"/>
          }
        </div>
      </div>
    </>
  );
};

export function Content({
  questionItem
}) {
  return (
    <div className="content-quiz-container">
      <div className="content-quiz">
        <div className="question">
          <div className="text-wrapper">
            {questionItem?.question}
          </div>
        </div>
        
        <div className="image-frame">
          <div className="image">
            <img
              src={questionItem?.imageUrl}
              className="upload-image"
            />
          </div>
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
                  />
                );
              })
            }
          </div>
        </div>
      </div>
    </div>
  );
};