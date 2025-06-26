import Popup from "reactjs-popup";
import './styles/SuccessPopup.css';

export function SuccessPopup({ isOpen, setIsOpen, message }) {
  return (
      <Popup open={isOpen} nested>
        <div className="success-popup">
          <div className="popup">
            <div className="success-icon"></div>
            <p className="message">Success</p>
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