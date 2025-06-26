import Popup from "reactjs-popup";
import './styles/DeletePopup.css';

export function DeletePopup({
    isOpen, closePopup, description,
    handleSubmit
}) {
    return (
        <Popup open={isOpen} nested>
            <div className="delete-popup">
                <div className="popup">
                <p className="message">Are you sure?</p>
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