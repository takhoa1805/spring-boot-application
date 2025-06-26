import Popup from "reactjs-popup";
import './styles/DeletePermanentPopup.css';
import {
    deleteResourcePermanent 
} from "../../api";

export function DeletePermanentPopup({
    isOpen, closePopup, currContent, contents,
    isEmptyTrash = false,
    setIsOpenErrorPopup, setErrorMessage
}) {
    const handleSubmit = async () => {
        if (isEmptyTrash) {
            let count = 0;
            const contentsLength = contents.length;
            for (let content of contents) {
                await deleteResourcePermanent(content.id).then(
                    _ => {
                        count++;
                    }
                ).catch(
                    e => console.log(e, content.id)
                );
            }

            if (count !== contentsLength) {
                setErrorMessage("Some items cannot be deleted. Please try again later.");
                setIsOpenErrorPopup(true);
            }
            closePopup();
        }
        else {
            await deleteResourcePermanent(currContent.id).then(
                _ => closePopup()
            ).catch(
                e => console.log(e)
            );
        }
    };

    const handleDescription = () => {
        if (isEmptyTrash) {
            return "All items in trash will be deleted forever. This can't be undone."
        }
        return `"${currContent ? currContent.contentName : "Undefined"}" will be deleted forever. This can't be undone.`;
    };

    return (
        <Popup open={isOpen} nested>
            <div className="trash-popup">
                <div className="popup">
                    <p className="message">Delete forever?</p>
                    <p className="description">
                        {handleDescription()}
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