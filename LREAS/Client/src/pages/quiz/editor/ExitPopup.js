import Popup from "reactjs-popup";
import './styles/ExitPopup.css';
import { useNavigate } from 'react-router-dom';
import {
    Logout
} from '@mui/icons-material';

export function ExitPopup({ isOpen, setIsOpen }) {
    const navigate = useNavigate();

    const handleExit = () => {
        navigate('/content');
        setIsOpen(false);
    }

    return (
        <Popup open={isOpen} nested>
            <div className="exit-popup">
                <div className="popup">
                    <div className="exit-icon">
                        <Logout/>
                    </div>
                    <p className="message">Are you sure?</p>
                    <p className="description">
                        Data you have entered may not be saved.
                    </p>
                    <div className="action-button">
                        <button
                            className="cancel-button"
                            onClick={() => setIsOpen(false)}
                        >
                            Cancel
                        </button>
                        <button
                            className="ok-button"
                            onClick={() => handleExit()}
                        >
                            Exit
                        </button>
                    </div>
                </div>
            </div>
        </Popup>
    );
}