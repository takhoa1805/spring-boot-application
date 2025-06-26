import React from "react";
import { Header } from "../../components/Header";
import { NavigationDrawer } from "../../components/NavigationDrawer";
import {
    GlobeIcon, PlusIcon, SearchIcon,
    DropDownIcon, FolderIcon,
    PdfIcon, VideoIcon, QuizIcon, DocumentIcon
} from '../../images/Icon';
import './styles/ContentPage.css';
import { ContentPopup } from './ContentPopup';
import {
    restoreResources, getResourcesByFolderAndScope, moveResources,
    getQuizInfo
} from "../../api";
import {
    MoreVert, DriveFileRenameOutline, Delete,
    AddCircleOutline, PersonAddAlt, Restore, DeleteForever,
    DriveFileMove, Edit, Games, OpenInNew, Download
} from '@mui/icons-material';
import { ErrorPopup } from "./ErrorPopup";
import { RenamePopup } from "./RenamePopup";
import { SharePopup } from "./SharePopup";
import { MoveTrashPopup } from "./MoveTrashPopup";
import { DeletePermanentPopup } from "./DeletePermanentPopup";
import { FolderSelectorPopup } from "./FolderSelectorPopup";
import { SuccessPopup } from "./SuccessPopup";
import { DownloadPopup } from "./DownloadPopup";
import { useNavigate } from "react-router-dom";

const Menu = ({ isOpen, content, menuPosition, menuRef, menu }) => {
    const handleClickMenu = (_, item) => {
        item.handleClick();
    };

    React.useEffect(() => {
        if (menuRef && menuRef.current) {
            const styleEl = menuRef.current.style;

            if (isOpen) {
                styleEl.height = "unset";

                setTimeout(() => {
                    styleEl.opacity = "1";
                    styleEl.visibility = "visible";
                }, 100);
            }
            else {
                styleEl.opacity = "0";
                styleEl.visibility = "hidden";

                setTimeout(() => {
                    styleEl.height = "100%";
                }, 200);
            }
        }
    }, [isOpen]);

    return (
        <div
            className="dropdown-menu"
            id={`dropdownMenuFolder${content.id}`}
            style={{
                top: `${menuPosition.top}px`,
                left: `${menuPosition.left}px`,
            }}
            ref={menuRef}
        >
            {
                menu?.map((item, index) => {
                    if (!item.role.includes(content.role.toLowerCase())) {
                        return null;
                    }

                    if (item.type.length > 0) {
                        if (content.isFolder) {
                            if (!item.type.includes("folder")) {
                                return null;
                            }
                        }
                        else if (content.isQuiz) {
                            if (!item.type.includes("quiz")) {
                                return null;
                            }
                        }
                        else if (content.fileType) {
                            if (!item.type.includes(content.fileType.toLowerCase())) {
                                return null;
                            }
                        }
                        else {
                            return null;
                        }
                    }

                    return (
                        <div
                            key={index}
                            className="dropdown-row"
                            onClick={() => {
                                handleClickMenu(content, item);
                            }}
                        >
                            {item.icon}
                            <div>{item.name}</div>
                        </div>
                    );
                })
            }
        </div>
    );
};

const FolderItem = ({
    content, menu, mainContainerRef,
    selectedContent, setSelectedContent,
    navigateTo
}) => {
    const [isOpenMenu, setIsOpenMenu] = React.useState(false);
    const [menuPosition, setMenuPosition] = React.useState({top: 0, left: 0});
    const [mousePosition, setMousePosition] = React.useState(null);
    const menuRef = React.useRef(null);
    const folderRef = React.useRef(null);
    const moreIconRef = React.useRef(null);

    React.useEffect(() => {
        const handleClickOutside = (e) => {
            if (
                folderRef &&
                folderRef.current &&
                !folderRef.current.contains(e.target)
            ) {
                setIsOpenMenu(false);
            }
        };
        document.getElementsByClassName("main-container")[0]?.addEventListener(
            "mouseup", handleClickOutside
        );
    }, []);

    React.useEffect(() => {
        if (!isOpenMenu || !menuRef.current || !folderRef.current) {
            return;
        }
        
        const folderRect = folderRef.current.getBoundingClientRect();
        const menuRect = menuRef.current.getBoundingClientRect();
        const moreIconRect = moreIconRef.current.getBoundingClientRect();
        const mainContainerRect = mainContainerRef.current.getBoundingClientRect();
        const viewportWidth = window.innerWidth;
        const viewportHeight = window.innerHeight;

        let top = moreIconRect.top - folderRect.top + moreIconRect.height + 4;
        let left = moreIconRect.left - folderRect.left;

        if (mousePosition) {
            top = mousePosition.top - folderRect.top;
            left = mousePosition.left - folderRect.left;
            
            if (mousePosition.top + menuRect.height > viewportHeight) {
                top -= menuRect.height;
            }
            
            if (mousePosition.left + menuRect.width > viewportWidth) {
                left -= menuRect.width;
            }
        }
        else {
            if (
                moreIconRect.top + moreIconRect.height + 4 + menuRect.height > viewportHeight
            ) {
                top -= (moreIconRect.height + menuRect.height + 12);
            }

            if (
                moreIconRect.left + menuRect.width > viewportWidth
            ) {
                left -= menuRect.width;
            }
        }
        
        if (folderRect.top + top < mainContainerRect.top) {
            top = mainContainerRect.top - folderRect.top;
        }
        
        if (left < 0) {
            left = 0;
        }

        setMenuPosition({top: top, left: left});
        setMousePosition(null);
    }, [isOpenMenu]);

    const handleResize = () => {
        setIsOpenMenu(false);
    };

    React.useEffect(() => {
        window.addEventListener("resize", handleResize, false);
    }, []);

    const handleSetMousePosAndOpenMenu = (e) => {
        setMousePosition({top: e.clientY, left: e.clientX});
        setIsOpenMenu(true);
    };

    const handleClick = (e) => {
        e.preventDefault();
        if (isOpenMenu) {
            setIsOpenMenu(false);
            setTimeout(() => handleSetMousePosAndOpenMenu(e), 100);
            return;
        }

        handleSetMousePosAndOpenMenu(e);
    };

    const handleItemIcon = (content) => {
        let src = "";
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

        return (
            <img
                loading="lazy"
                src={src}
                alt=""
            />
        );
    };

    const checkActualMenuLengthByRole = () => {
        let actualMenuLength = 0;
        for (let item of menu) {
            if (item.role.includes(content.role.toLowerCase())) {
                actualMenuLength++;
            }
        }
        return actualMenuLength;
    };

    return (
        <div
            key={content.id}
            className={`folder-item
                ${selectedContent?.id === content.id ? "folder-item-selected" : "folder-item-not-selected"}
            `}
            onContextMenu={e => handleClick(e)}
            onMouseDown={() => setSelectedContent(content)}
            onDoubleClick={() => navigateTo(content)}
            ref={folderRef}
        >
            <div className="folder-item-content padding-content">
                {handleItemIcon(content)}
                <div>{content.contentName}</div>
            </div>
            <div className="menu-container">
                {
                    checkActualMenuLengthByRole() > 0 &&
                    <MoreVert
                        className="icon-content"
                        onMouseUp={() => {
                            setIsOpenMenu(!isOpenMenu);
                        }}
                        ref={moreIconRef}
                    />
                }
                <Menu
                    isOpen={isOpenMenu}
                    content={content}
                    menuPosition={menuPosition}
                    menuRef={menuRef}
                    menu={menu}
                />
            </div>
        </div>
    );
};

const ContentList = ({
    filteredContents, menu, mainContainerRef,
    selectedContent, setSelectedContent,
    navigateTo
}) => {
    const folderItems = document.getElementsByClassName("folder-item");

    React.useEffect(() => {
        const handleClickOutside = (e) => {
            for (let fi of folderItems) {
                if (fi.contains(e.target)) {
                    return;
                }
            }
            setSelectedContent(null);
        };
        document.getElementsByClassName("main-container")[0]?.addEventListener(
            "mouseup", handleClickOutside
        );
    }, []);

    return filteredContents?.map((content, i) => (
        <FolderItem
            key={content.id}
            content={content}
            menu={menu}
            mainContainerRef={mainContainerRef}
            selectedContent={selectedContent}
            setSelectedContent={setSelectedContent}
            navigateTo={navigateTo}
        />
    ));
};

export default function ContentPage() {
    const navigate = useNavigate();
    const [filterOpen, setFilterOpen] = React.useState(false);
    const [isOpenErrorPopup, setIsOpenErrorPopup] = React.useState(false);
    const [errorMessage, setErrorMessage] = React.useState('Some error has occured!');
    const [isOpenSuccessPopup, setIsOpenSuccessPopup] = React.useState(false);
    const [successMessage, setSuccessMessage] = React.useState('Success');
    const [contents, setContents] = React.useState([]);
    const [currPath, setCurrPath] = React.useState([]);
    const [filterItems, setFilterItems] = React.useState([
        {
            name: "My resources",
            scope: "owned",
        },
        {
            name: "Shared with me",
            scope: "shared",
        },
        {
            name: "Trash",
            scope: "trashed",
        },
    ]);
    const [selectedContent, setSelectedContent] = React.useState(null);
    const [currParentContent, setCurrParentContent] = React.useState(null);
    const [isOpenPopup, setIsOpenPopup] = React.useState(null);
    const [menu, setMenu] = React.useState([]);
    const [searchText, setSearchText] = React.useState('');
    const [currFilterItemIdx, setCurrFilterItemIdx] = React.useState(0);
    const [isOpenEmptyTrashPopup, setIsOpenEmptyTrashPopup] = React.useState(false);
    const [flag, setFlag] = React.useState(false); // react hook to force re-render
    const isMountingRef = React.useRef(false);
    const mainContainerRef = React.useRef(null);

    const useClickOutside = (ref) => {
        React.useEffect(() => {
            const handleClickOutside = (e) => {
                if (ref.current && !ref.current.contains(e.target)) {
                    setSelectedContent(null);
                    setFilterOpen(false);
                }
            };
            document.getElementsByClassName("main-container")[0]?.addEventListener(
                "mousedown", handleClickOutside
            );
        }, [ref, contents]);
    };
    const wrapperRef = React.useRef(null);
    useClickOutside(wrapperRef);

    const filteredContents = contents?.filter(c => c?.contentName?.includes(searchText));

    React.useEffect(() => {
        isMountingRef.current = true;
    }, []);

    React.useEffect(() => {
        async function fetchData() {
            if (isOpenPopup) {
                return;
            }

            let currPathId = null;
            if (currPath && currPath.length > 0) {
                currPathId = currPath[currPath.length - 1].id;
            }

            let tempContents = null;

            await getResourcesByFolderAndScope(
                currPathId, filterItems[currFilterItemIdx].scope
            ).then(
                res => {
                    tempContents = res.data;
                }
            ).catch(
                e => {
                    setIsOpenErrorPopup(true);
                    setErrorMessage(e.message || e.response.data || e);
                }
            );

            if (currPathId) {
                let otherScope = null;
                if (filterItems[currFilterItemIdx].scope === "owned") {
                    otherScope = "shared";
                }
                else if (filterItems[currFilterItemIdx].scope === "shared") {
                    otherScope = "owned";
                }
                else {
                    return;
                }

                await getResourcesByFolderAndScope(
                    currPathId, otherScope
                ).then(
                    res => {
                        tempContents.push(...res.data);
                    }
                ).catch(
                    e => {
                        setIsOpenErrorPopup(true);
                        setErrorMessage(e.message || e.response.data || e);
                    }
                );
            }
            
            setContents(tempContents);
        }
        fetchData();
    }, [currPath, isOpenPopup, flag]);

    React.useEffect(() => {
        if (filterItems[currFilterItemIdx].name === "Trash") {
            setMenu([
                {
                    icon: <Restore/>,
                    name: "Restore",
                    handleClick: () => {
                        setIsOpenPopup({type: "Restore"});
                    },
                    type: [],
                    role: ["owner", "contributor"],
                },
                {
                    icon: <DeleteForever/>,
                    name: "Delete forever",
                    handleClick: () => {
                        setIsOpenPopup({type: "Delete forever"});
                    },
                    type: [],
                    role: ["owner", "contributor"],
                },
            ]);
        }
        else {
            setMenu([
                {
                    icon: <OpenInNew/>,
                    name: "Open",
                    handleClick: () => {
                        setIsOpenPopup({type: "Open"});
                    },
                    type: ["quiz", "document", "folder"],
                    role: ["owner", "contributor", "viewer"],
                },
                {
                    icon: <DriveFileRenameOutline/>,
                    name: "Rename",
                    handleClick: () => {
                        setIsOpenPopup({type: "Rename"});
                    },
                    type: [],
                    role: ["owner", "contributor"],
                },
                {
                    icon: <Delete/>,
                    name: "Move to trash",
                    handleClick: async () => {
                        setIsOpenPopup({type: "Trash"});
                        setCurrPath(currPath.slice(0, currPath.length));
                    },
                    type: [],
                    role: ["owner", "contributor"],
                },
                {
                    icon: <AddCircleOutline/>,
                    name: "New",
                    handleClick: async (_) => {
                        setIsOpenPopup({type: "New"});
                    },
                    type: [],
                    role: ["owner", "contributor"],
                },
                {
                    icon: <PersonAddAlt/>,
                    name: "Share",
                    handleClick: async () => {
                        setIsOpenPopup({type: "Share"});
                    },
                    type: [],
                    role: ["owner", "contributor", "viewer"],
                },
                {
                    icon: <DriveFileMove/>,
                    name: "Move",
                    handleClick: async () => {
                        setIsOpenPopup({type: "Move"});
                    },
                    type: [],
                    role: ["owner", "contributor"],
                },
                {
                    icon: <Edit/>,
                    name: "Edit",
                    handleClick: async () => {
                        setIsOpenPopup({type: "Edit"});
                    },
                    type: ["quiz", "document"],
                    role: ["owner", "contributor"],
                },
                                {
                    icon: <Games/>,
                    name: "Start game",
                    handleClick: async () => {
                        setIsOpenPopup({type: "Competitive"});
                    },
                    type: ["quiz"],
                    role: ["owner", "contributor"],
                },
                {
                    icon: <Download/>,
                    name: "Download",
                    handleClick: async () => {
                        setIsOpenPopup({type: "Download"});
                    },
                    type: ["pdf", "docx", "doc", "mp4", "mpeg", "webm"],
                    role: ["owner", "contributor"],
                },
            ]);
        }
    }, [currFilterItemIdx, isOpenPopup]);

    React.useEffect(() => {
        if (!isMountingRef.current) {
            setCurrPath([]);
            setCurrParentContent(null);
        }
        else {
            isMountingRef.current = false;
        }
    }, [currFilterItemIdx]);

    const navigateTo = async (content) => {
        if (filterItems[currFilterItemIdx].name === "Trash") {
            setErrorMessage("To view this resource, you will need to restore it from your trash.");
            setIsOpenErrorPopup(true);
            return;
        }
        if (content.isFolder) {
            setCurrParentContent(content);
            setCurrPath([...currPath, content]);
        }
        else if (content.isQuiz) {
            await getQuizInfo(selectedContent.id).then(
                res => {
                    // if (res.data.isGame) {
                    //     navigate(`/quiz/competitive/enter/${selectedContent.id}`);
                    // }
                    // else {
                    //     navigate(`/quiz/enter/${content.id}`);
                    // }
                    navigate(`/quiz/enter/${content.id}`);

                }
            ).catch(
                e => console.log(e)
            );
        }
        else if (content.fileType && content.fileType.toLowerCase() === "document") {
            // documents case
            navigate(`/documents/${content.mongoId}/view`);
        }
    };

    const getPopup = () => {
        if (!isOpenPopup) {
            return null;
        }

        const closePopup = () => setIsOpenPopup(null);

        if (isOpenPopup.type === "Open") {
            closePopup();
            navigateTo(selectedContent);
        }
        else if (isOpenPopup.type === "Rename") {
            return (
                <RenamePopup
                    isOpen={true}
                    closePopup={closePopup}
                    currContent={selectedContent}
                />
            );
        }
        else if (isOpenPopup.type === "Share") {
            return (
                <SharePopup
                    isOpen={true}
                    closePopup={closePopup}
                    currContent={selectedContent}
                    allowedSubmit={true}
                    setIsOpenSuccessPopup={setIsOpenSuccessPopup}
                    setSuccessMessage={setSuccessMessage}
                />
            );
        }
        else if (isOpenPopup.type === "New") {
            return (
                <ContentPopup
                    isOpen={true}
                    closePopup={closePopup}
                    maxSize="100MB"
                    currParent={currParentContent}
                />
            );
        }
        else if (isOpenPopup.type === "Trash") {
            return (
                <MoveTrashPopup
                    isOpen={true}
                    closePopup={closePopup}
                    currContent={selectedContent}
                />
            );
        }
        else if (isOpenPopup.type === "Restore") {
            closePopup();
            const handleRestore = async () => {
                if (!selectedContent) {
                    return;
                }

                await restoreResources(selectedContent.id).then(
                    _ => setFlag(!flag)
                ).catch(
                    e => console.log(e)
                );
            };
            handleRestore();
        }
        else if (isOpenPopup.type === "Delete forever") {
            return (
                <DeletePermanentPopup
                    isOpen={true}
                    closePopup={closePopup}
                    currContent={selectedContent}
                    contents={contents}
                    isEmptyTrash={false}
                    setIsOpenErrorPopup={setIsOpenErrorPopup}
                    setErrorMessage={setErrorMessage}
                />
            );
        }
        else if (isOpenPopup.type === "Move") {
            return (
                <FolderSelectorPopup
                    isOpen={true}
                    closePopup={closePopup}
                    handleSubmit={async folder => {
                        let parentId = folder === null ? null : folder.id;
                        await moveResources(selectedContent.id, parentId).then(
                            _ => closePopup()
                        ).catch(
                            e => console.log(e)
                        );
                    }}
                    currFolder={selectedContent}
                    omitCurrFolder={true}
                />
            );
        }
        else if (isOpenPopup.type === "Edit") {
            closePopup();
            if (selectedContent.isQuiz) {
                navigate(`/quiz/editor/${selectedContent.id}`);
            }
            else if (selectedContent.fileType.toLowerCase() === "document") {
                navigate(`/documents/${selectedContent.mongoId}/edit`);
            }
        }
        else if (isOpenPopup.type === "Download") {
            return (
                <DownloadPopup
                    isOpen={true}
                    closePopup={closePopup}
                    currContent={selectedContent}
                />
            );
        }
        else if (isOpenPopup.type === "Competitive") {
            closePopup();
            navigate(`/quiz/competitive/enter/${selectedContent.id}`);
        }
        return null;
    };

    return (
        <div className="content-page">
            <Header />
            <div className="content-page-container">
                <div className="container">
                    <NavigationDrawer />

                    <div
                        className="main-container"
                        ref={mainContainerRef}
                    >
                        <div className="heading-container">
                            <div className="left-heading">
                                <div>
                                    <button
                                        onClick={() => setFilterOpen(!filterOpen)}
                                        className="filter-button"
                                    >
                                        <img
                                            loading="lazy"
                                            src={GlobeIcon}
                                            alt=""
                                            className="icon-filter"
                                        />
                                    </button>
                                    {filterOpen && (
                                        <div className="dropdown-container" ref={wrapperRef}>
                                            {filterItems.map((item, index) => (
                                                <div
                                                    key={index}
                                                    onClick={() => {
                                                        setCurrFilterItemIdx(index);
                                                        setFilterOpen(false);
                                                    }}
                                                    className={`${currFilterItemIdx === index ? "div-selected" : ""}`}
                                                >
                                                    {item.name}
                                                </div>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="search-item-container">
                                    <input
                                        type="search"
                                        placeholder="Search"
                                        onChange={e => setSearchText(e.target.value)}
                                    />
                                    <img loading="lazy" src={SearchIcon} alt="" />
                                </div>
                            </div>

                            <div className="action-button-group">
                                <button
                                    className="new-button"
                                    onClick={() => {
                                        navigate("/quiz/competitive/");
                                    }}
                                >
                                    <Games className="icon-content" />
                                    Join Competitive Quiz
                                </button>
                                <button
                                    className="new-button"
                                    onClick={() => setIsOpenPopup({type: "New"})}
                                >
                                    <img loading="lazy" src={PlusIcon} alt="" />
                                    New Content
                                </button>

                            </div>
                        </div>
                        <div className="heading-title">
                            {filterItems[currFilterItemIdx].name}
                        </div>
                        {
                            filterItems[currFilterItemIdx].name === "Trash" ?
                            (
                                <>
                                        <button
                                            className="empty-trash"
                                            onClick={() => setIsOpenEmptyTrashPopup(true)}
                                            disabled={contents.length === 0}
                                        >
                                            Empty trash
                                        </button>
                                        <DeletePermanentPopup
                                            isOpen={isOpenEmptyTrashPopup}
                                            closePopup={() => {
                                                setIsOpenEmptyTrashPopup(false);
                                                setFlag(!flag); // re-render the page
                                            }}
                                            currContent={selectedContent}
                                            contents={contents}
                                            isEmptyTrash={true}
                                            setIsOpenErrorPopup={setIsOpenErrorPopup}
                                            setErrorMessage={setErrorMessage}
                                        />
                                </>
                            ) : (
                                <div className="folder-navigation-tab">
                                    <div key={-1} className="folder-tab-container">
                                        <div
                                            className="folder-tab"
                                            onClick={() => {
                                                setCurrParentContent(null);
                                                setCurrPath([]);
                                            }}
                                        >
                                            Home
                                        </div>
                                    </div>
                                    {currPath.map((folder, index) => (
                                        <div key={index} className="folder-tab-container">
                                            <img
                                                loading="lazy"
                                                src={DropDownIcon}
                                                alt=""
                                                className="folder-tab-icon"
                                            />
                                            <div
                                                className={`folder-tab ${currPath.slice(-1)[0].contentName === folder.contentName ? "folder-tab-bold" : ""}`}
                                                onClick={() => {
                                                    setCurrParentContent(folder);
                                                    setCurrPath(currPath.slice(0, index + 1));
                                                }}
                                            >
                                                {folder.contentName}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )
                        }
                        {
                            !contents || contents.length == 0 ? (
                                <div className="blank-list">
                                    <div className="illustration">
                                        <div className="paper"></div>
                                        <div className="pen"></div>
                                        <div className="magnifier"></div>
                                    </div>
                                    <h2 className="title">Empty list!</h2>
                                    <p className="subtitle">You have no contents at this moment.</p>
                                </div>
                            ) : (
                                <>
                                    <div className="folder-grid" id="folder-grid-content-page">
                                        <ContentList
                                            filteredContents={filteredContents}
                                            menu={menu}
                                            mainContainerRef={mainContainerRef}
                                            selectedContent={selectedContent}
                                            setSelectedContent={setSelectedContent}
                                            navigateTo={navigateTo}
                                        />
                                    </div>
                                </>
                            )
                        }
                    </div>
                </div>
                {getPopup()}
            </div>
            <ErrorPopup
                isOpen={isOpenErrorPopup}
                setIsOpen={setIsOpenErrorPopup}
                message={errorMessage}
            />
            <SuccessPopup
                isOpen={!isOpenErrorPopup && isOpenSuccessPopup}
                setIsOpen={setIsOpenSuccessPopup}
                message={successMessage}
            />
        </div>
    );
};
