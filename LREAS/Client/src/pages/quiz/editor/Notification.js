import React from "react";
import "./styles/Notification.css";

export function Notification({
    isOpen, message, notiIcon,
    isLoading = false
}) {
    const noti = document.getElementById("noti");

    React.useEffect(() => {
        if (!noti) {
            return;
        }

        if (isOpen) {
            noti.style.width = "250px";
            noti.style.padding = "1rem";
        }
        else {
            noti.style.width = "0";
            noti.style.padding = "0";
        }
    }, [isOpen]);

    return (
        <div className="notification-overlay" id="noti">
            <div className="notification-bar">
                {
                    isOpen &&
                    <>
                        <div className="text">
                            {message || "Nothing to show here"}
                        </div>
                        <div className="icon">
                            <img
                                className={`${isLoading ? "loading-spinner" : ""}`}
                                src={notiIcon}
                            />
                        </div>
                    </>
                }
            </div>
        </div>
    );
};