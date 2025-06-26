import Popup from "reactjs-popup";
import "./styles/ImageMetadataPopup.css";
import React from "react";
import { CrossIcon } from "../../../images/Icon";

export function ImageMetadataPopup({
    isOpen, setIsOpen, getMetadata
}) {
    const [metadata, setMetadata] = React.useState({});

    React.useEffect(() => {
        setMetadata(getMetadata());
    }, [isOpen]);

    return (
        <Popup open={isOpen} nested>
            <div className="image-metadata-popup-overlay">
                <div className="popup-image">
                    <button
                        className="close-btn"
                        onClick={() => setIsOpen(false)}
                    >
                        <img src={CrossIcon}/>
                    </button>
                    <h3>Image Metadata</h3>
                    <ul className="metadata-list">
                        <li><strong>Filename:</strong> <span id="filename">{metadata.name}</span></li>
                        <li><strong>Size:</strong> <span id="size">{metadata.file_size}</span></li>
                        <li><strong>Type:</strong> <span id="dimensions">{metadata.type?.toLowerCase()}</span></li>
                        <li><strong>Dimensions:</strong> <span id="dimensions">{metadata.dimensions}</span></li>
                        <li><strong>Last Modified:</strong> <span id="format">{metadata.lastModified}</span></li>
                    </ul>
                </div>
            </div>
        </Popup>
    );
}