import React from 'react';
import Popup from "reactjs-popup";
import './styles/ContentPopup.css';
import { FolderSelectorPopup } from './FolderSelectorPopup';
import { useDropzone } from 'react-dropzone';
import {
  uploadFile, createFolder, createQuiz,
  createDocument
} from '../../api';
import { SharePopup } from "./SharePopup";
import { LoadingIcon } from '../../images/Icon';

export function ContentPopup({ isOpen, closePopup, maxSize, currParent }) {
  const [collaborators, setCollaborators] = React.useState([]);
  const [contentType, setContentType] = React.useState([
    'Folder', 'File', 'Quiz', 'Document'
  ]);
  const [currContentType, setCurrContentType] = React.useState('Folder');
  const [currContentName, setCurrContentName] = React.useState('');
  const [rejectDrop, setRejectDrop] = React.useState(false);
  const [currParentFolder, setCurrParentFolder] = React.useState(currParent);
  const [isOpenFolderSelector, closePopupFolderSelector] = React.useState(false);
  const [isPublicContent, setIsPublicContent] = React.useState(false);
  const [isAllowedToSubmit, setIsAllowedToSubmit] = React.useState(false);
  const [file, setFile] = React.useState(null);
  const [isOpenAddUser, setIsOpenAddUser] = React.useState(false);
  const [isLoading, setIsLoading] = React.useState(false);

  const onDrop = React.useCallback((acceptedFiles) => {
    acceptedFiles.forEach((file) => {
      var reader = new FileReader();
      reader.onload = function(e) {
          setFile([file, e.target.result]);
      }
      reader.readAsArrayBuffer(file);
      setRejectDrop(false);
    });
  }, []);
  const onDropRejected = React.useCallback((fileRejections) => {
    setRejectDrop(true);
    setFile([]);
    setCurrContentType('');
  }, []);

  const accept = {
    "application/pdf": [".pdf"],
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
      [".docx"],
    "application/msword": [".doc"],
    "video/mp4": [".mp4"],
    "video/mpeg": [".mpeg"],
    "video/webm": [".webm"],
  };
  const {getRootProps, getInputProps} = useDropzone({
    onDrop,
    onDropRejected,
    "accept": accept,
    maxSize: maxSize === "100MB" ? 104857600 : 2147483648,
    maxFiles: 1
  });

  React.useEffect(() => {
    if (currContentName !== '') {
      setIsAllowedToSubmit(true);
    }
    else {
      setIsAllowedToSubmit(false);
    }
  }, [currContentName, isOpen]);
  
  const removeCollaborator = (index) => {
    setCollaborators(collaborators.filter((_, i) => i !== index));
  };

  const handleSelectFolder = (folder) => {
    setCurrParentFolder(folder);
    closePopupFolderSelector(false);
  };

  const handleSubmit = async () => {
    setIsLoading(true);
    const metadata = {
      contentName: currContentName,
      collaborators: collaborators.map(c => {
        return {
          id: c.id,
          role: c.role,
        }
      }),
      parentResourceId: currParentFolder?.id ? currParentFolder.id : null,
      contentType: currContentType,
      isFolder: currContentType === "Folder",
      isQuiz: currContentType === "Quiz",
      isPublic: isPublicContent,
      fileType: currContentType === "File" ? file[0].path.split(".").pop() : null,
    };

    if (currContentType === "Folder") {
      await createFolder(metadata).then(
        res => {
          setIsLoading(false);
          handleClose();
        }
      ).catch(
        e => {
          console.log(e);
          setIsLoading(false);
        }
      );
    }
    else if (currContentType === "File") {
      await uploadFile(file, JSON.stringify(metadata)).then(
        res => {
          setIsLoading(false);
          handleClose();
        }
      ).catch(
        e => {
          console.log(e);
          setIsLoading(false);
        }
      );
    }
    else if (currContentType === "Quiz") {
      await createQuiz(metadata).then(
        res => {
          setIsLoading(false);
          handleClose();
        }
      ).catch(
        e => {
          console.log(e);
          setIsLoading(false);
        }
      );
    }
    else if (currContentType === "Document") {
      // Create document
      const body = {
        "type":"document",
        "name":currContentName,
        "parentId":currParentFolder?.id ? currParentFolder.id : null
      }
      await createDocument (body).then(
        res => {
          setIsLoading(false);
          handleClose();
        }
      ).catch(
        e => {
          console.log(e);
          setIsLoading(false);
        }
      );
    }
  };

  const handleClose = () => {
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
              className="content-name"
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
            currContentType === "File" &&
            <div className="input-group">
              <label>Import a content</label>
              <div {...getRootProps({className: "file-upload-box"})}>
                <input {...getInputProps()} />
                {
                  !file || file.length === 0 ?
                  (
                    <>
                      <p className="upload-link">Link or drag and drop</p>
                      <p className="file-info">DOCX, PPTX, PDF, MP4, MKV (max. 100MB)</p>
                    </>
                  ) : (
                    <p className="file-info">{file[0].name}</p>
                  )
                }
                
              </div>
              {
                rejectDrop &&
                <div className='warning-text'>Please upload only 1 file!</div>
              }
            </div>
          }
          <div className="bottom">
            <div
              className="loading"
              style={{
                opacity: `${isLoading ? "1" : "0"}`
              }}
            >
              <div className="text">
                {currContentType.toLowerCase() === "file" ? "Uploading" : "Creating"}
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
                Create
              </button>
            </div>
          </div>
        </div>
      </div>
    </Popup>
  );
}