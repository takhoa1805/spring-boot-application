import Popup from "reactjs-popup";
import './styles/ErrorPopup.css';

export function ErrorPopup({ isOpen, setIsOpen, message }) {
  return (
      <Popup open={isOpen} nested>
        <div className="error-popup">
          <div className="popup">
            <div className="error-icon"></div>
            <p className="message">Error!</p>
            <p className="description">{message}</p>
            <button
              className="ok-button"
              onClick={() => setIsOpen(false)}
            >
              OK
            </button>
          </div>
        </div>
      </Popup>
  );
}