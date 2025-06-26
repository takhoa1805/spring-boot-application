import Popup from "reactjs-popup";
import './styles/DownloadPopup.css';
import { downloadFile, getFileSize } from "../../api";
import React from "react";
import { LoadingIcon } from "../../images/Icon";

export function DownloadPopup({
    isOpen, closePopup, currContent
}) {
    const [fileSize, setFileSize] = React.useState(0);
    const [downloadProgress, setDownloadProgress] = React.useState(0);
    const controllerRef = React.useRef(null);

    const onDownloadProgress = (progressEvent) => {
        const { loaded, _ } = progressEvent;
        let percentCompleted = 0;
        if (fileSize) {
            percentCompleted = Math.round((loaded * 100) / fileSize);
        }
        setDownloadProgress(percentCompleted);
    };

    React.useEffect(() => {
        controllerRef.current = new AbortController();

        async function fetchFileSize() {
            if (currContent) {
                await getFileSize(currContent.id, controllerRef.current.signal).then(
                    res => {
                        setFileSize(res.data);
                    }
                ).catch(
                    e => console.log(e)
                );
            }
        }
        if (isOpen) {
            fetchFileSize();
        }
    }, []);

    React.useEffect(() => {
        async function download() {
            await downloadFile(currContent.id, onDownloadProgress, controllerRef.current.signal).then(
                res => {
                    const match = res.headers["content-disposition"]?.match(/filename="?(.+?)"?$/);
                    const filename = match ? decodeURIComponent(match[1]) : currContent.contentName + "." + currContent.fileType;

                    const blob = new Blob([res.data], {type: res.headers["content-type"]});
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement("a");
                    a.href = url;
                    a.download = filename;
                    document.body.appendChild(a);
                    a.click();
                    a.remove();

                    window.URL.revokeObjectURL(url);
                }
            ).catch(
                e => console.log(e)
            );
            
            setTimeout(() => {
                closePopup();
            }, 1000);
        }
        if (isOpen && fileSize > 0) {
            download();
        }
        else if (!isOpen) {
            closePopup();
        }
    }, [fileSize]);

    const handleCancel = () => {
        if (controllerRef.current) {
            controllerRef.current.abort();
        }
        closePopup();
    };

    return (
        <Popup open={isOpen} nested>
            <div className="download-popup">
                <div className="popup">
                    <div className="message">
                        <img src={LoadingIcon}/>
                        <div className="description">
                            {`Downloading "${currContent ? currContent.contentName : ""}"`}
                        </div>
                    </div>

                    <div className="progress-bar">
                        <div className="progress-bar-container">
                            <div
                                className="progress-bar-fill"
                                style={{ width: `${downloadProgress}%` }}
                            />
                        </div>
                        <div className="progress-text">{downloadProgress}%</div>
                    </div>

                    <div className="btn-group">
                        <button
                            className="cancel-button"
                            onClick={handleCancel}
                        >
                            Cancel
                        </button>
                    </div>
                </div>
            </div>
        </Popup>
    );
}