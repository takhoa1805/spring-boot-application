import React from 'react';
import { useDropzone } from 'react-dropzone';
import {
    ExclamationIcon, UploadIcon, LoadingIcon,
    FolderIcon, QuizIcon, VideoIcon, PdfIcon,
    DocumentIcon, CrossIcon, FailedGenerateIcon
} from '../../images/Icon';
import { Header } from '../../components/Header';
import { NavigationDrawer } from "../../components/NavigationDrawer";
import './styles/GeneratorPage.css';
import { ContentPopup } from './ContentPopup';
import {
    uploadFile,
    getAllFilesAvailableInFolder,
    getAllGeneratingResources
} from '../../api';
import { FolderSelectorPopup } from './FolderSelectorPopup';
import { CancelPopup } from './CancelPopup';

export const InfoBanner = () => {
    return (
        <div className="info-banner">
            <img loading="lazy" src={ExclamationIcon} alt="" className="info-icon" />
            <p className="info-text">
            What is <strong>Resource Generator</strong> and how does it work?{' '}
            <span className="info-link">More info</span>
            </p>
        </div>
    );
};

export const FileUploader = ({ title, acceptedFormats, maxSize, setFlag }) => {
    const [file, setFile] = React.useState(null);
    const [errorText, setErrorText] = React.useState("");
    const [isLoading, setIsLoading] = React.useState(false);

    const onDrop = React.useCallback((acceptedFiles) => {
        acceptedFiles.forEach((file) => {
            var reader = new FileReader();
            reader.onload = function(e) {
                setFile([file, e.target.result]);
            }
            reader.readAsArrayBuffer(file);
            setErrorText("");
        });
    }, []);
    const onDropRejected = React.useCallback((fileRejections) => {
        console.log(fileRejections[0].errors[0].message);
    }, []);

    const accept = acceptedFormats === ".doc, .docx, .pdf" ? {
        "application/pdf": [".pdf"],
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
          [".docx"],
        "application/msword": [".doc"],
    } : {
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

    const handleImport = async () => {
        if (!file || file.length <= 0) {
            setErrorText("Please upload file");
            return;
        }
        setIsLoading(true);
        setErrorText("");
        
        const metadata = JSON.stringify({
            contentName: file[0].name.split(".")[0],
            collaborators: [],
            parentResourceId: null,
            contentType: "File",
            isFolder: false,
            isQuiz: false,
            isPublic: null,
            fileType: file[0].path.split(".").pop()
        });

        await uploadFile(file, metadata).then(
            res => {
                setFlag();
                setIsLoading(false);
                setFile(null);
            }
        ).catch(
            e => {
                console.log(e);
                setErrorText("Some error occur. Please try again later");
                setIsLoading(false);
            }
        );
    };

    return (
        <div className="uploader-container">
            <div className="uploader-title">{title}</div>
            <div {...getRootProps({className: "uploader-box"})}>
                <input {...getInputProps()} />
                {
                    (!file || file.length === 0) ?
                    <>
                        <img loading="lazy" src={UploadIcon} alt="" className="upload-icon" />
                        <div className="upload-text">
                            <span className="upload-drag">Choose a file or drag it here</span>
                        </div>
                        <div className="upload-format">{acceptedFormats} (max. {maxSize})</div>
                    </> :
                    <div>
                        {file && file.length > 0 ? file[0].name : ""}
                    </div>
                }
            </div>
            <div className="uploader-bottom">
                <div
                    className="loading"
                    style={{
                        opacity: `${isLoading ? "1" : "0"}`
                    }}
                >
                    Importing
                    <img src={LoadingIcon}/>
                </div>
                <div className="left">
                    <div className="error-text">
                        {errorText}
                    </div>
                    <button
                        className="import-button"
                        onClick={() => handleImport()}
                    >
                        Import
                    </button>
                </div>
            </div>
        </div>
    );
};

export const ImportItem = ({
    setIsOpenSettings,
    currItem, setCurrItem,
    currTab,
    setIsOpenCancel
}) => {
    const [isHovered, setIsHovered] = React.useState(false);

    return (
        <div className="import-item">
            <img loading="lazy" src={currItem.icon} alt="" className="import-icon" />
            <div className="import-content">
                <div className="import-box">
                    <div className="import-details">
                        <div className="import-title">
                            {currItem.title}
                        </div>
                        {
                            currTab === 0 ?
                            <div className="import-status">
                                Author: {currItem.author}
                            </div> :
                            <div className="import-status">
                                Updated: {currItem.updatedTime}
                            </div>
                        }
                        <div className="import-status">
                            Status: {currItem.state}
                        </div>
                    </div>
                    {
                        currTab === 0 ?
                        <div className="import-action">
                            <button
                                className="action-button"
                                aria-label={`Actions for ${currItem.title}`}
                                onClick={() => {
                                    setCurrItem(currItem);
                                    setIsOpenSettings(true);
                                }}
                                onMouseEnter={() => setIsHovered(true)}
                                onMouseDown={() => setIsHovered(false)}
                                onMouseLeave={() => setIsHovered(false)}
                            >
                                {
                                    isHovered &&
                                    <div className="tooltip">
                                        Generate new content from this
                                    </div>
                                }
                                <div>
                                    Generate content
                                </div>
                            </button>
                        </div> :
                        <div
                            className="action-button"
                            onClick={() => {
                                setIsOpenCancel(true);
                                setCurrItem(currItem);
                            }}
                        >
                            <img src={CrossIcon} loading="lazy"/>
                        </div>
                    }
                </div>
                <div className="import-divider" />
            </div>
        </div>
    );
};

export default function GeneratorPage() {
    const [importItems, setImportItems] = React.useState([]);
    const [isOpenSettings, setIsOpenSettings] = React.useState(false);
    const [isOpenCancel, setIsOpenCancel] = React.useState(false);
    const [currFolder, setCurrFolder] = React.useState(null);
    const [isOpenFolderSelector, setIsOpenFolderSelector] = React.useState(false);
    const [currItem, setCurrItem] = React.useState(null);
    const [flag, setFlag] = React.useState(false); // flag to force re-render
    const tabs = ["Your files", "Generating contents"];
    const [currTab, setCurrTab] = React.useState(0);
    const [intervalGenerating, setIntervalGenerating] = React.useState(null);

    const handleItemIcon = (content) => {
        let src = FailedGenerateIcon;
        if (content.isFolder) {
            src = FolderIcon;
        }
        else if (content.isQuiz) {
            src = QuizIcon;
        }
        else if (content.fileType) {
            const fileType = content.fileType.toLowerCase();

            if (fileType === "pdf") {
                src = PdfIcon;
            }
            else if (fileType === "mp4") {
                src = VideoIcon;
            }
            else if (fileType === "document") {
                src = DocumentIcon;
            }
        }

        return src;
    };

    React.useEffect(() => {
        async function fetchData() {
            const parentId = currFolder ? currFolder.id : null;
            const fetchFunction = currTab === 0 ? getAllFilesAvailableInFolder : getAllGeneratingResources;

            setImportItems([]);
            setCurrItem(null);

            await fetchFunction(parentId).then(
                res => {
                    let data = res.data.map(r => {
                        return {
                            id: r.id,
                            icon: handleItemIcon(r),
                            title: r.contentName,
                            author: r.author,
                            state: r.state.toLowerCase(),
                            updatedTime: r.updatedTime
                        };
                    });

                    data.sort((a, b) => new Date(b.updatedTime) - new Date(a.updatedTime));
                    data = data.map(d => {
                        d.updatedTime = new Date(d.updatedTime).toLocaleString("en-GB");
                        return d;
                    })

                    setImportItems(data);
                }
            ).catch(
                e => console.log(e)
            );
        }
        fetchData();
    }, [currFolder, currTab, flag]);

    React.useEffect(() => {
        if (currTab === 1) {
            const interval = setInterval(() => {
                setFlag(!flag);
            }, 10000);
            setIntervalGenerating(interval);
        }
        else {
            if (intervalGenerating) {
                clearInterval(intervalGenerating);
            }
        }

        return () => {
            if (intervalGenerating) {
                clearInterval(intervalGenerating);
            }
        };
    }, [currTab, flag]);

    return (
        <div className="generator-page">
            <Header />
            <div className="generator-page-container">
                <div className="resource-layout">
                    <NavigationDrawer />
                    <div className="main-content">
                        <InfoBanner />
                        
                        <div className="upload-section">
                            <div className="upload-grid">
                                <FileUploader
                                    title="Import from file"
                                    acceptedFormats=".doc, .docx, .pdf" 
                                    maxSize="100MB"
                                    setFlag={() => setFlag(!flag)}
                                />
                                <FileUploader
                                    title="Import media"
                                    acceptedFormats=".mp4, .webm, .mpeg"
                                    maxSize="2GB"
                                    setFlag={() => setFlag(!flag)}
                                />
                            </div>
                        </div>

                        <div className="imports-heading">
                            <div className="imports-tab">
                                {
                                    tabs.map((tab, index) => {
                                        return (
                                            <div
                                                key={index}
                                                className={`imports-title ${currTab === index ? "imports-title-selected" : ""}`}
                                                onClick={() => setCurrTab(index)}
                                            >
                                                <div>
                                                    {tab}
                                                </div>
                                            </div>
                                        );
                                    })
                                }
                            </div>
                            <div
                                className="folder-selector"
                                onClick={() => setIsOpenFolderSelector(true)}
                            >
                                📁 {currFolder ? currFolder.contentName : "Home"}
                            </div>
                            <FolderSelectorPopup
                                isOpen={isOpenFolderSelector}
                                closePopup={() => setIsOpenFolderSelector(false)}
                                handleSubmit={folder => {
                                    setCurrFolder(folder);
                                    setIsOpenFolderSelector(false);
                                }}
                                currFolder={currFolder}
                                omitCurrFolder={false}
                            />
                        </div>
                                                
                        <div className="imports-list">
                            {importItems.map((item, index) => (
                                <ImportItem
                                    key={index}
                                    setIsOpenSettings={setIsOpenSettings}
                                    currItem={item}
                                    setCurrItem={setCurrItem}
                                    currTab={currTab}
                                    setIsOpenCancel={setIsOpenCancel}
                                />
                            ))}
                        </div>
                    </div>
                </div>
                <ContentPopup
                    isOpen={isOpenSettings}
                    closePopup={() => setIsOpenSettings(false)}
                    currItem={currItem}
                    setFlag={() => setFlag(!flag)}
                />
                <CancelPopup
                    isOpen={isOpenCancel}
                    closePopup={() => setIsOpenCancel(false)}
                    currContent={currItem}
                    message={`${currItem?.state === "generating" ? "Cancel generate " : "Delete "} ${currItem?.title}?`}
                    description="This action cannot be undone."
                    setFlag={() => setFlag(!flag)}
                />
            </div>
        </div>
    );
}