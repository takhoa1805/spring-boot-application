import Popup from "reactjs-popup";
import './styles/CancelPopup.css';
import {
    deleteGeneratingResource, stopGeneratingResource
} from "../../api";
import React from "react";

export function CancelPopup({
    isOpen, closePopup, currContent,
    message, description, setFlag
}) {
    const handleClose = () => {
        setFlag();
        closePopup();
    };

    const handleSubmit = async () => {
        if (!currContent) {
            handleClose();
            return;
        }

        if (currContent.state === "generating") {
            await stopGeneratingResource(currContent.id).then(
                async res => {
                    await deleteGeneratingResource(currContent.id).then(
                        res => handleClose()
                    ).catch(
                        e => {}
                    );
                }
            ).catch(
                e => {}
            );
        }
        else {
            await deleteGeneratingResource(currContent.id).then(
                res => handleClose()
            ).catch(
                e => {}
            );
        }
    };

    return (
        <Popup open={isOpen} nested>
            <div className="generator-cancel-popup">
                <div className="popup">
                <p className="message">{message}</p>
                <p className="description">
                    {description}
                </p>
                <div className="btn-group">
                    <button
                        className="cancel-button"
                        onClick={closePopup}
                    >
                        No
                    </button>
                    <button
                        className="ok-button"
                        onClick={() => handleSubmit()}
                    >
                        Yes
                    </button>
                </div>
                </div>
            </div>
        </Popup>
    );
}