import React from 'react';
import Popup from "reactjs-popup";
import './styles/ContentPopup.css';
import { FolderSelectorPopup } from './FolderSelectorPopup';
import { SharePopup } from "./SharePopup";
import {
  generateFromResources 
} from '../../api';
import {
  LoadingIcon
} from '../../images/Icon';

export function ContentPopup({
  isOpen, closePopup, currItem, setFlag
}) {
  const [collaborators, setCollaborators] = React.useState([]);
  const [contentType, setContentType] = React.useState(['Quiz', 'Notebook']);
  const [currContentType, setCurrContentType] = React.useState('Quiz');
  const [currContentName, setCurrContentName] = React.useState('');
  const [currParentFolder, setCurrParentFolder] = React.useState(null);
  const [isOpenFolderSelector, closePopupFolderSelector] = React.useState(false);
  const [isAllowedToSubmit, setIsAllowedToSubmit] = React.useState(false);
  const [isOpenAddUser, setIsOpenAddUser] = React.useState(false);
  const [numberOfQuestions, setNumberOfQuestions] = React.useState(0);
  const [timeLimit, setTimeLimit] = React.useState(0.0);
  const [isLoading, setIsLoading] = React.useState(false);

  React.useEffect(() => {
    if (currContentName !== '') {
      setIsAllowedToSubmit(true);
    }
    else {
      setIsAllowedToSubmit(false);
    }
  }, [currContentName, isOpen]);

  const handleInputNumber = (e, setFunc) => {
    const numericValue = Number(e.target.value.replace(/[^0-9]/g, ""));
    setFunc(numericValue);
};
  
  const removeCollaborator = (index) => {
    setCollaborators(collaborators.filter((_, i) => i !== index));
  };

  const handleSelectFolder = (folder) => {
    setCurrParentFolder(folder);
    closePopupFolderSelector(false);
  };

  const getArgs = (contentType) => {
    if (contentType.toLowerCase() === "quiz") {
      return [numberOfQuestions, timeLimit];
    }
    else if (contentType.toLowerCase() === "notebook") {
      return [];
    }
    else if (contentType.toLowerCase() === "slides") {
      return [];
    }
  };

  const handleSubmit = async () => {
    setIsLoading(true);
    const metadata = {
      resourceId: currItem?.id,
      newResourceName: currContentName,
      type: currContentType.toUpperCase(),
      collaborators: collaborators.map(c => {
        return {
          id: c.id,
          role: c.role,
        }
      }),
      outputFolderId: currParentFolder?.id ? currParentFolder.id : null,
      args: getArgs(currContentType)
    };

    await generateFromResources(metadata).then(
      res => {
        handleClose();
      }
    ).catch(
      e => {
        console.log(e);
        setIsLoading(false);
      }
    );
  };

  const handleClose = () => {
    setIsLoading(false);
    setFlag(); // re-render
    closePopup();
    setCurrParentFolder(null);
  };

  return (
    <Popup open={isOpen} nested>
      <div className="content-popup">
        <div className="popup-container">
          <div className="popup-header">
            <h2>New Content</h2>
            <button
              className="close-button"
              onClick={() => handleClose()}
            >
              &times;
            </button>
          </div>
          <div className="input-group">
            <label>Content name</label>
            <input
              type="text"
              placeholder="Enter content name"
              className="content-info"
              onChange={e => setCurrContentName(e.target.value)}
            />
          </div>
          <div className="input-group">
            <label>Collaborators</label>
            {collaborators.map((colab, index) => (
              <div key={index} className="collaborator-item">
                <span>{colab.email}</span>
                <button onClick={() => removeCollaborator(index)} className="remove-button">✕</button>
              </div>
            ))}
            <button
              className="add-user-button"
              onClick={() => setIsOpenAddUser(true)}
            >
              Add user
            </button>
            <SharePopup
              isOpen={isOpenAddUser}
              closePopup={() => setIsOpenAddUser(false)}
              currContent={{id: null, collaborators: [], isFolder: currContentType === "Folder"}}
              setCollaborators={setCollaborators}
              allowedSubmit={false}
            />
          </div>
          <div className="input-group">
            <label>Parent folder</label>
            <div
              className="folder-selection"
              onClick={() => closePopupFolderSelector(!isOpenFolderSelector)}
            >
              📁 {currParentFolder ? currParentFolder.contentName : "Home"}
            </div>
            <FolderSelectorPopup
              isOpen={isOpenFolderSelector}
              closePopup={() => closePopupFolderSelector(false)}
              handleSubmit={handleSelectFolder}
              currFolder={currParentFolder}
              omitCurrFolder={false}
            />
          </div>
          <div className="input-group">
            <label>Content type</label>
            <select
              value={currContentType}
              onChange={(e) => setCurrContentType(e.target.value)}
              className="dropdown-group"
            >
              {contentType.map((type, i) => (
                <option key={i} value={type}>{type}</option>
              ))}
            </select>
          </div>
          {
            currContentType === "Quiz" &&
            <>
              <div className="input-group">
              <label>Number of question(s)</label>
              <input
                type="text"
                placeholder="e.g. 10"
                className="content-info"
                onChange={e => handleInputNumber(e, setNumberOfQuestions)}
                value={numberOfQuestions}
              />
              </div>
              <div className="input-group">
                <label>Time limit (s)</label>
                <input
                  type="text"
                  placeholder="e.g. 300"
                  className="content-info"
                  onChange={e => handleInputNumber(e, setTimeLimit)}
                  value={timeLimit}
                />
              </div>
            </>
          }
          <div className="bottom">
            <div
              className="loading"
              style={{
                opacity: `${isLoading ? "1" : "0"}`
              }}
            >
              <div className="text">
                Generating {currContentType.toLowerCase()}
              </div>
              <img src={LoadingIcon}/>
            </div>
            <div className="button-action-group">
              <button
                className="cancel-button"
                onClick={() => handleClose()}
              >
                Cancel
              </button>
              <button
                className={`create-button ${isAllowedToSubmit ? "" : "create-button-disabled"}`}
                onClick={() => {
                  handleSubmit();
                }}
                disabled={!isAllowedToSubmit}
              >
                Generate
              </button>
            </div>
          </div>
        </div>
      </div>
    </Popup>
  );
}