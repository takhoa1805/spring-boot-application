import React from "react";
import "./styles/FolderSelectorPopup.css";
import Popup from "reactjs-popup";
import {
  SearchIconBold, DropDownIcon
} from '../../images/Icon';
import { getAllResourcesAvailableInFolder } from "../../api";

export function FolderSelectorPopup({ isOpen, closePopup, handleSubmit, currFolder, omitCurrFolder = false }) {
  const [selectedTab, setSelectedTab] = React.useState("Folder");
  const [searchTerm, setSearchTerm] = React.useState("");
  const [tabs, setTabs] = React.useState(["Folder"]);
  const [currentPath, setCurrentPath] = React.useState([]);
  const [folders, setFolders] = React.useState([]);
  const [selectedFolder, setSelectedFolder] = React.useState(null);

  const filteredFolders = folders.filter(folder =>
    folder.contentName?.toLowerCase().includes(searchTerm.toLowerCase())
    &&
    folder.isFolder
    &&
    (!omitCurrFolder || !currFolder || currFolder.id !== folder.id)
  );

  React.useEffect(() => {
      async function fetchData() {
        if (!isOpen) {
          return;
        }

        let currPathId = null;
        if (currentPath && currentPath.length > 0) {
            currPathId = currentPath[currentPath.length - 1].id;
        }
        const res = await getAllResourcesAvailableInFolder(currPathId);
        setFolders(res.data);
      }
      fetchData();
  }, [currentPath, isOpen]);

  const navigateTo = (folder) => {
    setCurrentPath([...currentPath, folder]);
  };

  const goBack = () => {
    if (currentPath.length > 0) {
      setCurrentPath(currentPath.slice(0, -1));
    }
  };

  const handleClickOutside = (e) => {
    const folderItems = document.getElementsByClassName("folder-item");
    for (let fi of folderItems) {
      if (fi.contains(e.target)) {
        return;
      }
    }
    
    const confirmButton = document.getElementsByClassName("confirm-button");
    if (confirmButton[0].contains(e.target)) {
      return;
    }
    
    const cancelButton = document.getElementsByClassName("cancel-button");
    if (cancelButton[0].contains(e.target)) {
      return;
    }

    setSelectedFolder(null);
  };

  const handleClose = () => {
    closePopup();
    setCurrentPath([]);
    setSelectedFolder(null);
  };

  return (
    <Popup
      open={isOpen}
      position="center center"
      nested
    >
      <div className="folder-selector-container">
        <div
          className="folder-selector-popup"
          onMouseUp={handleClickOutside}
        >
          <h2>Select folder</h2>
          <div className="tabs">
            {tabs.map(tab => (
              <button
                key={tab}
                onClick={() => setSelectedTab(tab)}
                className={`tab 
                  ${selectedTab === tab ? "tab-border-bottom" : "tab-no-border-bottom"}
                `}
              >
                {tab}
              </button>
            ))}
          </div>
          <div className="search-bar">
            <div className="search-region">
              <input
                type="text"
                placeholder="Folder"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="search-input"
              />
              <button className="search-button">
                <img
                  loading="lazy"
                  src={SearchIconBold}
                  alt=""
                />
              </button>
            </div>
            <div className="folder-navigation-tab">
              <div key={-1} className="folder-tab-container">
                <div
                  className="folder-tab"
                  onClick={() => setCurrentPath([])}
                >
                  Home
                </div>
              </div>
              {currentPath.map((folder, index) => (
                <div key={index} className="folder-tab-container">
                  <img
                    loading="lazy"
                    src={DropDownIcon}
                    alt=""
                    className="folder-tab-icon"
                  />
                  <div
                    className={`folder-tab ${currentPath.slice(-1)[0].contentName === folder.contentName ? "folder-tab-bold" : ""}`}
                    onClick={() => setCurrentPath(currentPath.slice(0, index + 1))}
                  >
                    {folder.contentName}
                  </div>
                </div>
              ))}
            </div>
          </div>
          <div className="folder-grid" id="folder-grid-folder-selector">
            {filteredFolders.map(folder => (
              <div
                key={folder.id}
                className={`folder-item
                  ${selectedFolder?.id === folder.id ? "folder-item-selected" : "folder-item-not-selected"}
                `}
                onClick={() => setSelectedFolder(folder)}
                onDoubleClick={() => navigateTo(folder)}
              >
                <div className="folder-icon">📁 {folder.contentName}</div>
              </div>
            ))}
          </div>
          <div className="footer">
            <button
              className="confirm-button"
              onClick={() => handleSubmit(selectedFolder)}
            >
              Select
            </button>
            <button
              className="cancel-button"
              onClick={() => handleClose()}
            >
              Cancel
            </button>
          </div>
        </div>
      </div>
    </Popup>
  );
}