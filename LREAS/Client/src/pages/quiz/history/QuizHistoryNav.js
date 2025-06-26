import React from 'react';
import './styles/QuizHistoryNav.css';

export function QuizHistoryNav ({
    quiz, questionItem, setQuestionItem
}) {
    const Item = ({ item, index }) => {
        const [hover, setHover] = React.useState(false);

        return (
            <>
                <div
                    className="item-frame-container"
                    onClick={() => setQuestionItem(item)}
                >
                    <div
                        className={`
                            item-frame item-frame-selected-invisible 
                            ${questionItem && questionItem.position === item.position ? "item-frame-selected-visible" : ""}
                        `}
                        onMouseOver={() => setHover(true)}
                        onMouseLeave={() => setHover(false)}
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
                                        src={item.imageUrl}
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
                                <div className="text-wrapper">{index + 1}</div>
                            }
                        </div>
                    </div>
                </div>
            </>
        );
    };

    return (
        <div className="quiz-history-mini-drawer">
            <div className="sliding-container">
                <div className="items">
                    {
                        quiz.questions.map((question, index) => (
                            <Item
                                key={index}
                                index={index}
                                item={question}
                            />
                        ))
                    }
                </div>
            </div>
        </div>
    );
};