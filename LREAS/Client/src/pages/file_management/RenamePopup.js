import React from "react";
import "./styles/RenamePopup.css";
import Popup from "reactjs-popup";
import {
    updateResourceName
} from '../../api';

export function RenamePopup({ isOpen, closePopup, currContent }) {
    const [name, setName] = React.useState(currContent?.contentName);

    const handleClose = () => {
        closePopup();
    };

    const handleSubmit = async () => {
        if (name === currContent?.contentName) {
            return;
        }
        await updateResourceName(currContent?.id, name).then(
            res => handleClose()
        ).catch(
            e => console.log(e)
        );
    };

    return (
        <Popup open={isOpen} nested>
            <div className="rename-popup-overlay">
                <div className="rename-popup-container">
                    <h2 className="popup-title">Rename</h2>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="popup-input"
                    />
                    <div className="popup-actions">
                        <button
                            onClick={() => handleClose()}
                            className="btn-cancel"
                        >
                            Cancel
                        </button>
                        <button
                            className="btn-ok"
                            onClick={() => handleSubmit()}
                        >
                            OK
                        </button>
                    </div>
                </div>
            </div>
        </Popup>
    );
}