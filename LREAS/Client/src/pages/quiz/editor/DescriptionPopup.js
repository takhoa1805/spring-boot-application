import "./styles/DescriptionPopup.css";
import Popup from "reactjs-popup";
import React from "react";

export default function DescriptionPopup({
    isOpen, setIsOpen, 
    currDescription, onUpdate
}) {
    const [description, setDescription] = React.useState(currDescription || "");

    const handleSubmit = () => {
        onUpdate(description);
        setIsOpen(false);
    };

    const handleSetDescription = (value) => {
        if (value.length <= 200) {
            setDescription(value);
        }
    };

    return (
        <Popup open={isOpen} nested>
            <div className="description-popup">
                <div className="popup-container">
                    <div className="quiz-description-title">
                        Enter Description
                    </div>

                    <textarea
                        className="textarea"
                        placeholder="Type your description here..."
                        value={description}
                        onChange={(e) => handleSetDescription(e.target.value)}
                    />

                    <div className="limit-text">
                        {`${description.length} / 200`}
                    </div>
                    
                    <div className="footer">
                        <button
                            className="cancel-button"
                            onClick={() => setIsOpen(false)}
                        >
                            Cancel
                        </button>
                        <button 
                            className="submit-button"
                            onClick={() => handleSubmit()}
                        >
                            Submit
                        </button>
                    </div>
                </div>
            </div>
        </Popup>
    );
};