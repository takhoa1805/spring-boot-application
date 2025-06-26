import Popup from "reactjs-popup";
import './styles/MoveTrashPopup.css';
import {
    moveResourceToTrash 
} from "../../api";

export function MoveTrashPopup({ isOpen, closePopup, currContent }) {
    const handleSubmit = async () => {
        await moveResourceToTrash(currContent.id).then(
            res => closePopup()
        ).catch(
            e => {}
        );
    };

    return (
        <Popup open={isOpen} nested>
            <div className="trash-popup">
                <div className="popup">
                <p className="message">Move to trash?</p>
                <p className="description">
                    Are you sure you want to move this resource to trash?
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