import { Header } from "../../components/Header";
import { NavigationDrawer } from "../../components/NavigationDrawer";
import "./styles/ForumPage.css";
import {
    getThread, getAllCommentsInThread,
    getAllTopicsInInstitution, getAllThreadsInTopic,
    createComment,
    updateThread, updateTopic , updateComment,
    deleteComment, deleteThread, deleteTopic
} from "../../api";
import React from "react";
import {
    ForumIcon, DropDownIcon, AvatarIcon,
    EditBlackIcon, ReplyBlackIcon, DeleteBlackIcon,
    CheckBlackIcon, CrossBlackIcon, ResetBlackIcon
} from "../../images/Icon";
import CreateNewPopup from "./CreateNewPopup";
import { EditorState } from "prosemirror-state";
import { EditorView } from "prosemirror-view";
import { Schema, DOMParser } from "prosemirror-model";
import { schema } from "prosemirror-schema-basic";
import { addListNodes } from "prosemirror-schema-list";
import { exampleSetup } from "prosemirror-example-setup";
import "./styles/DocumentEditorPage.css";
import { DeletePopup } from "./DeletePopup";

const Item = ({
    item, navigateTo,
    setFlag
}) => {
    const [isOpenDeletePopup, setIsOpenDeletePopup] = React.useState(false);
    const [isEdit, setIsEdit] = React.useState(false);
    const [currSubject, setCurrSubject] = React.useState(item?.subject || "");
    const [missingSubject, setMissingSubject] = React.useState(false);

    const getDescription = (content) => {
        if (content.type === "topic") {
            return "This will delete the topic and all its replies. Are you sure?";
        }
        else if (content.type === "thread") {
            return "This will delete the thread and all its comments. Are you sure?";
        }
        else if (content.type === "comment") {
            return "This will delete the comment and all its replies. Are you sure?";
        }
    };

    const handleDeleteContent = async () => {
        const deleteFunc = item.type === "topic" ? deleteTopic : deleteThread;
        await deleteFunc(item.id).then(
            res => {
                setIsOpenDeletePopup(false);
                setFlag();
            }
        ).catch(
            e => console.log(e)
        );
    };

    const handleUpdateItem = async () => {
        if (currSubject.length === 0) {
            setMissingSubject(true);
            return;
        }

        const updateFunc = item.type === "topic" ? updateTopic : updateThread;
        let data = null;
        if (item.type === "topic") {
            data = {
                topicId: item.id,
                topicName: currSubject
            };
        }
        else {
            data = {
                threadId: item.id,
                topicId: item.topicId,
                content: item.content,
                subject: currSubject
            };
        }

        await updateFunc(item.id, data).then(
            res => {
                setIsEdit(false);
                setFlag();
            }
        ).catch(
            e => console.log(e)
        );
    };

    const formatTime = (time) => {
        return new Date(time).toLocaleString("en-GB");
    };

    const handleSetSubject = (value) => {
        if (value.length <= 255) {
            setCurrSubject(value);
        }
    };

    React.useEffect(() => {
        setCurrSubject(item.subject);
    }, [item]);

    const handleCancelEdit = () => {
        setIsEdit(false);
        setFlag();
        setMissingSubject(false);
    };

    return (
        <div
            className="item"
        >
            <div className="info-frame">
                <div className="left">
                    <img src={ForumIcon}/>
                </div>

                <div className="right">
                    <div
                        className="item-title"
                    >
                        <div className="text">
                            {
                                isEdit ?
                                <>
                                    <input
                                        className="input-box"
                                        value={currSubject}
                                        onChange={e => handleSetSubject(e.target.value)}
                                        onClick={() => setIsEdit(true)}
                                    />
                                    <div className="info-text">
                                        <div
                                            className="error-text"
                                            style={{
                                                opacity: `${missingSubject ? 1 : 0}`
                                            }}
                                        >
                                            Subject cannot be empty
                                        </div>
                                        <div className="limit-text">
                                            {`${currSubject.length} / 255`}
                                        </div>
                                    </div>
                                </> :
                                <div
                                    className="show-box"
                                    onClick={() => navigateTo(item)}
                                >
                                    {currSubject}
                                </div>
                            }
                        </div>

                        {
                            item.canEdit &&
                            <div className="action-buttons">
                                {
                                    isEdit ?
                                    <>
                                        <img
                                            src={CheckBlackIcon}
                                            onClick={() => handleUpdateItem()}
                                        />
                                        <img
                                            src={CrossBlackIcon}
                                            onClick={() => handleCancelEdit()}
                                        />
                                    </> :
                                    <img
                                        src={EditBlackIcon}
                                        onClick={() => setIsEdit(true)}
                                    />
                                }
                            </div>
                        }
                    </div>

                    <div className="info">
                        Created by {item.authorName} on {formatTime(item.createdTime)}
                    </div>
                </div>
            </div>

            {
                item.canEdit &&
                <div className="action-group">
                    <img
                        src={DeleteBlackIcon}
                        onClick={() => setIsOpenDeletePopup(true)}
                    />
                    <DeletePopup
                        isOpen={isOpenDeletePopup}
                        closePopup={() => setIsOpenDeletePopup(false)}
                        description={getDescription(item)}
                        handleSubmit={() => handleDeleteContent()}
                    />
                </div>
            }
        </div>
    );
};

const ItemList = ({
    items, navigateTo, setFlag
}) => {
    return (
        <div className="item-list">
            {
                items?.map((item, index) => {
                    return (
                        <Item
                            key={index}
                            item={item}
                            navigateTo={navigateTo}
                            setFlag={setFlag}
                        />
                    );
                })
            }

            {
                (!items || items.length === 0) &&
                <div className="empty-list">
                    Nothing to show at the moment.
                </div>
            }
        </div>
    );
};

const Content = ({
    content, setFlag,
    goBackToThread
}) => {
    const [isEdit, setIsEdit] = React.useState(false);
    const [isReply, setIsReply] = React.useState(false);
    const [currContent, setCurrContent] = React.useState(content?.content?.trim() || "");
    const [currSubject, setCurrSubject] = React.useState(content?.subject || "");
    const [currNewComment, setCurrNewComment] = React.useState("");
    const [isOpenDeletePopup, setIsOpenDeletePopup] = React.useState(false);
    const [missingSubject, setMissingSubject] = React.useState(false);
    const [missingContent, setMissingContent] = React.useState(false);
    const [missingNewComment, setMissingNewComment] = React.useState(false);
    const contentRef = React.useRef(null);
    const contentViewRef = React.useRef(null);
    const newCommentRef = React.useRef(null);
    const newCommentViewRef = React.useRef(null);
    const showRef = React.useRef(null);
    const regX = /(<([^>]+)>)/ig;

    const mySchema = new Schema({
        nodes: addListNodes(schema.spec.nodes, "paragraph block*", "block"),
        marks: schema.spec.marks,
    });

    React.useEffect(() => {
        showRef.current.innerHTML = content?.content?.trim() || "";
    }, [content]);

    React.useEffect(() => {
        if (!contentRef.current) {
            return;
        }

        const contentElement = document.createElement("div");
        contentElement.innerHTML = currContent;
        const doc = DOMParser.fromSchema(mySchema).parse(contentElement);
        
        const state = EditorState.create({
            doc,
            plugins: exampleSetup({ schema: mySchema })
        });
        
        contentViewRef.current = new EditorView(contentRef.current, {
            state,
            dispatchTransaction(tr) {
                const newState = contentViewRef.current.state.apply(tr);
                contentViewRef.current.updateState(newState);

                setCurrContent(contentViewRef.current.dom.innerHTML);
                setMissingContent(false);
            }
        });

        return () => {
            if (contentViewRef.current) {
                contentViewRef.current.destroy();
            }
        }
    }, [contentRef, isEdit]);

    React.useEffect(() => {
        if (!newCommentRef.current) {
            return;
        }

        const commentElement = document.createElement("div");
        commentElement.innerHTML = "";
        const doc = DOMParser.fromSchema(mySchema).parse(commentElement);
        
        const state = EditorState.create({
            doc,
            plugins: exampleSetup({ schema: mySchema })
        });
        
        newCommentViewRef.current = new EditorView(newCommentRef.current, {
            state,
            dispatchTransaction(tr) {
                const newState = newCommentViewRef.current.state.apply(tr);
                newCommentViewRef.current.updateState(newState);

                setCurrNewComment(newCommentViewRef.current.dom.innerHTML);
                setMissingNewComment(false);
            }
        });

        return () => {
            if (newCommentViewRef.current) {
                newCommentViewRef.current.destroy();
            }
        }
    }, [newCommentRef, isReply]);

    const formatTime = (time) => {
        return new Date(time).toLocaleString("en-GB");
    };

    const handleSubmitReply = async (parentContenId) => {
        if (currNewComment.replace(regX, "").length === 0) {
            setMissingNewComment(true);
            return;
        }

        let parentId = parentContenId;
        if (content.type !== "comment") {
            parentId = null;
        }
        
        const data = {
            threadId: content.threadId,
            parentCommentId: parentId,
            content: currNewComment
        };

        await createComment(data).then(
            res => {
                setIsReply(false);
                setFlag();
            }
        ).catch(
            e => console.log(e)
        );

        setCurrNewComment("");
        setMissingContent(false);
        setMissingSubject(false);
        setMissingNewComment(false);
    };

    const handleUpdateContent = async () => {
        if (currSubject.length === 0) {
            setMissingSubject(true);
        }
        if (currContent.replace(regX, "").length === 0) {
            setMissingContent(true);
        }
        if (currSubject.length === 0 || currContent.replace(regX, "").length === 0) {
            return;
        }

        let data = null;
        const updateFunc = content.type === "comment" ? updateComment : updateThread;

        if (content.type === "comment") {
            data = {
                content: currContent,
            };
        }
        else {
            data = {
                threadId: content.id,
                content: currContent,
                topicId: content.topicId,
                subject: currSubject
            };
        }

        await updateFunc(content.id, data).then(
            res => {
                setIsEdit(false);
                setFlag();
            }
        ).catch(
            e => console.log(e)
        );

        setMissingContent(false);
        setMissingSubject(false);
        setMissingNewComment(false);
    };

    const handleDeleteContent = async () => {
        const deleteFunc = content.type === "comment" ? deleteComment : deleteThread;
        await deleteFunc(content.id).then(
            res => {
                if (content.type === "thread") {
                    goBackToThread();
                }
                else {
                    setFlag();
                }
            }
        ).catch(
            e => console.log(e)
        );
        setIsOpenDeletePopup(false);
    };

    const getDescription = (content) => {
        if (content.type === "topic") {
            return "This will delete the topic and all its replies. Are you sure?";
        }
        else if (content.type === "thread") {
            return "This will delete the thread and all its comments. Are you sure?";
        }
        else if (content.type === "comment") {
            return "This will delete the comment and all its replies. Are you sure?";
        }
    };

    const handleSetSubject = (value) => {
        setMissingSubject(false);
        if (value.length <= 255) {
            setCurrSubject(value);
        }
    };

    const handleCancelEdit = () => {
        setIsEdit(false);
        setFlag();
        setMissingContent(false);
        setMissingSubject(false);
    };

    const handleToggleEdit = () => {
        setIsEdit(!isEdit);
        
        setCurrContent(content?.content?.trim() || "");
        setCurrSubject(content?.subject);

        setMissingContent(false);
        setMissingSubject(false);

        if (!isEdit) {
            setIsReply(false);
        }
    };

    const handleToggleReply = () => {
        setIsReply(!isReply);
        setCurrNewComment("");
        setMissingNewComment(false);

        if (!isReply) {
            setIsEdit(false);
        }
    };

    return (
        <>
            <div className="content-heading">
                <div className="left">
                    <div className="avatar">
                        <img src={content?.authorAvt || AvatarIcon}/>
                    </div>

                    <div className="info">
                        <div className="topic-title">
                            <input
                                value={currSubject}
                                disabled={!isEdit || content.type === "comment"}
                                onChange={e => handleSetSubject(e.target.value)}
                            />
                            {
                                isEdit && content.type !== "comment" &&
                                <div className="info-text">
                                    <div
                                        className="error-text"
                                        style={{
                                            opacity: `${missingSubject ? 1 : 0}`
                                        }}
                                    >
                                        Subject cannot be empty
                                    </div>
                                    <div className="limit-text">
                                        {`${currSubject.length} / 255`}
                                    </div>
                                </div>
                            }
                        </div>

                        <div className="agent-info">
                            By {content.authorName} - {formatTime(content.createdTime)}
                        </div>
                    </div>
                </div>

                <div className="right">
                    {
                        content.canEdit &&
                        <>
                            <img
                                src={DeleteBlackIcon}
                                onClick={() => setIsOpenDeletePopup(true)}
                            />
                            <DeletePopup
                                isOpen={isOpenDeletePopup}
                                closePopup={() => setIsOpenDeletePopup(false)}
                                description={getDescription(content)}
                                handleSubmit={() => handleDeleteContent()}
                            />
                        </>
                    }

                    {
                        content.canEdit &&
                        <img
                            src={EditBlackIcon}
                            onClick={() => handleToggleEdit()}
                        />
                    }

                    <img
                        src={ReplyBlackIcon}
                        onClick={() => handleToggleReply()}
                    />
                </div>
            </div>

            <div className="content">
                <div className="blank"/>

                <div
                    className="info"
                >
                    <div className="edit-region">
                        <div
                            className="text"
                            ref={contentRef}
                            style={{
                                display: `${isEdit ? "" : "none"}`
                            }}
                        />

                        {
                            missingContent &&
                            <div className="error-text">
                                Content cannot be empty
                            </div>
                        }

                        <div
                            className="action-buttons"
                            style={{
                                display: `${isEdit ? "" : "none"}`
                            }}
                        >
                            <button
                                className="cancel-button"
                                onClick={() => handleCancelEdit()}
                            >
                                <div className="text-button">
                                    Cancel
                                </div>
                            </button>

                            <button
                                className="post-button"
                                onClick={() => handleUpdateContent()}
                            >
                                <div className="text-button">
                                    Submit
                                </div>
                            </button>
                        </div>
                    </div>

                    <div
                        className="show-region"
                        style={{
                            display: `${isEdit ? "none" : ""}`
                        }}
                        ref={showRef}
                    >
                    </div>
                </div>
            </div>

            <div
                className="comment-content-container"
                style={{
                    display: `${isReply ? "" : "none"}`
                }}
            >
                <div
                    className="comment-text"
                    ref={newCommentRef}
                />

                <div
                    className="error-text"
                    style={{
                        display: `${missingNewComment ? "" : "none"}`
                    }}
                >
                    Comment cannot be empty
                </div>

                <div className="action-buttons">
                    <button
                        className="cancel-button"
                        onClick={() => setIsReply(false)}
                    >
                        <div className="text">
                            Cancel
                        </div>
                    </button>

                    <button
                        className="post-button"
                        onClick={() => handleSubmitReply(content.id)}
                    >
                        <div className="text">
                            Post
                        </div>
                    </button>
                </div>
            </div>
        </>
    );
};

const ChildContent = ({
    content, setFlag,
    goBackToThread
}) => {
    return (
        <div className="content-container">
            <div className="content-box">
                <Content
                    content={content}
                    setFlag={setFlag}
                    goBackToThread={goBackToThread}
                />
            </div>

            {
                content &&
                content.childrenComments?.length > 0 &&
                <div className="comments-box">
                    {content.childrenComments.map((comment, index) => {
                        return (
                            <ChildContent
                                key={index}
                                content={comment}
                                parentContent={content}
                                setFlag={setFlag}
                                goBackToThread={goBackToThread}
                            />
                        );
                    })}
                </div>
            }
        </div>
    );
};

const ThreadContent = ({
    thread, setFlag,
    comments, goBackToThread
}) => {
    return (
        <div className="thread-content-container">
            <div className="content-container">
                <div className="content-box">
                    <Content
                        content={thread}
                        setFlag={setFlag}
                        goBackToThread={goBackToThread}
                    />
                </div>

                {
                    comments &&
                    comments.length > 0 &&
                    <div className="comments-box">
                        {comments?.map((comment, index) => {
                            return (
                                <ChildContent
                                    key={index}
                                    content={comment}
                                    setFlag={setFlag}
                                    goBackToThread={goBackToThread}
                                />
                            );
                        })}
                    </div>
                }
            </div>
        </div>
    );
};

export default function ForumPage() {
    const userInfo = JSON.parse(localStorage.getItem("user_info"));
    const [currThread, setCurrThread] = React.useState(null);
    const [items, setItems] = React.useState(null);
    const [paths, setPaths] = React.useState([]);
    const [openNewPopup, setOpenNewPopup] = React.useState(false);
    const [comments, setComments] = React.useState(null);
    const [flag, setFlag] = React.useState(false);
    const [currPage, setCurrPage] = React.useState(1);
    const [totalPage, setTotalPage] = React.useState(1);
    const [pageSize, setPageSize] = React.useState(5);
    const [isRefreshing, setIsRefreshing] = React.useState(false);
    const newButtonText = ["New topic", "New thread"];

    const getAllTopics = async () => {
        await getAllTopicsInInstitution(currPage - 1, pageSize).then(
            res => {
                const data = res.data;
                setItems(data.map(topic => {
                    return {
                        id: topic.topicId,
                        subject: topic.topicName,
                        authorName: topic.authorName,
                        createdTime: topic.createdTime,
                        page: topic.page,
                        pageSize: topic.pageSize,
                        totalPages: topic.totalPages,
                        canEdit: topic.canEdit,
                        type: "topic",
                    };
                }));

                if (data.length > 0) {
                    setTotalPage(data[0].paginationInfo.totalPages);
                }
                else {
                    setTotalPage(1);
                }

                setTimeout(() => {
                    setIsRefreshing(false);
                }, 800);
            }
        ).catch(
            e => console.log(e)
        );
    };

    const getAllThreads = async (topicId) => {
        await getAllThreadsInTopic(topicId, currPage - 1, pageSize).then(
            res => {
                const data = res.data;

                setItems(data.map(thread => {
                    return {
                        id: thread.threadId,
                        threadId: thread.threadId,
                        topicId: thread.topicId,
                        subject: thread.subject,
                        authorName: thread.authorName,
                        createdTime: thread.createdTime,
                        page: thread.page,
                        pageSize: thread.pageSize,
                        totalPages: thread.totalPages,
                        canEdit: thread.canEdit,
                        type: "thread",
                    };
                }));

                if (data.length > 0) {
                    setTotalPage(data[0].paginationInfo.totalPages);
                }
                else {
                    setTotalPage(1);
                }

                setTimeout(() => {
                    setIsRefreshing(false);
                }, 800);
            }
        ).catch(
            e => console.log(e)
        );
    };

    const getComments = async (thread) => {
        const formatCommentData = (comment, parentSubject) => {
            const subject = "Re: " + parentSubject;
            return {
                id: comment.commentId,
                threadId: comment.threadId,
                parentId: comment.parentCommentId,
                authorName: comment.authorName,
                authorAvt: comment.authorAvtPath,
                createdTime: comment.dateCreated,
                updatedTime: comment.dateModified,
                content: comment.content,
                childrenComments: comment.childrenComments.map(child => {
                    return formatCommentData(child, subject);
                }),
                subject: subject,
                page: comment.page,
                pageSize: comment.pageSize,
                totalPages: comment.totalPages,
                canEdit: comment.canEdit,
                type: "comment",
            };
        }

        await getAllCommentsInThread(thread.id, currPage - 1, pageSize).then(
            res => {
                const data = res.data;

                setComments(data.map(comment => {
                    return formatCommentData(comment, thread.subject);
                }));

                if (data.length > 0) {
                    setTotalPage(data[0].paginationInfo.totalPages);
                }
                else {
                    setTotalPage(1);
                }

                setTimeout(() => {
                    setIsRefreshing(false);
                }, 800);
            }
        ).catch(
            e => console.log(e)
        );
    };

    const getCurrThread = async (threadId) => {
        await getThread(threadId).then(
            res => {
                const thread = res.data;
                const threadTemp = {
                    id: thread.threadId,
                    content: thread.content,
                    topicId: thread.topicId,
                    threadId: thread.threadId,
                    subject: thread.subject,
                    authorName: thread.authorName,
                    authorAvt: thread.authorAvtPath,
                    createdTime: thread.createdTime,
                    updatedTime: thread.updatedTime,
                    canEdit: thread.canEdit,
                    type: "thread",
                };
                setCurrThread(threadTemp);
                getComments(threadTemp);
            }
        ).catch(
            e => console.log(e)
        );
    };

    const goBack = (index) => {
        setCurrPage(1);

        if (index < 2) {
            setCurrThread(null);
        }

        if (index < 0) {
            setPaths([]);
        }
        else {
            setPaths(paths.slice(0, index + 1));
        }
    };

    const navigateTo = (item) => {
        setCurrPage(1);
        setPaths([...paths, item]);
    };

    const goBackToThread = () => {
        console.log("goBackToThread");
        goBack(0);
    };

    const handleRefresh = (setRefreshState = true) => {
        if (isRefreshing) {
            return;
        }

        if (setRefreshState) {
            setIsRefreshing(true);
        }
        
        if (paths.length === 0) {
            getAllTopics();
        }
        else if (paths.length === 1) {
            getAllThreads(paths[0].id);
        }
        else if (paths.length === 2) {
            getCurrThread(paths[1].id);
        }
    };

    React.useEffect(() => {
        handleRefresh(false);
    }, [paths, openNewPopup, currPage, pageSize, flag]);

    const getNewButtonText = (length) => {
        if (length >= newButtonText.length) {
            return "Some text";
        }

        return newButtonText[length];
    };

    const handlePageChange = (page) => {
        if (page < 1 || page > totalPage) {
            return;
        }

        setCurrPage(page);
    };

    const handleChangeDisplay = (e) => {
        setPageSize(e.target.value);
        setCurrPage(1);
    };

    const isShowActionButtons = (pathLength) => {
        return (
            pathLength < 2 &&
            (pathLength > 0 || userInfo?.role?.toLowerCase() === "admin")
        );
    };

    return (
        <div className="forum-page">
            <Header />
            <div className="forum-page-container">
                <div className="container">
                    <NavigationDrawer />
        
                    <div className="main-container">
                        <div className="heading-title">
                            Forum
                        </div>

                        <div className="forum-content">
                            <div className="heading">
                                <div className="navigation-forum">
                                    <div className="nav">
                                        <div
                                            className="text"
                                            onClick={() => goBack(-1)}
                                        >
                                            Forums
                                        </div>
                                    </div>

                                    {
                                        paths.map((path, index) => {
                                            return (
                                                <div
                                                    key={index}
                                                    className="nav"
                                                >
                                                    <img
                                                        src={DropDownIcon}
                                                        className="symbol"
                                                    />

                                                    <div
                                                        className="text"
                                                        onClick={() => goBack(index)}
                                                    >
                                                        {path.subject}
                                                    </div>
                                                </div>
                                            );
                                        })
                                    }
                                </div>

                                <div className="action-group">
                                    <div
                                        className="refresh-button"
                                        onClick={() => handleRefresh(true)}
                                    >
                                        <img
                                            className={`${isRefreshing ? "refresh-button-animate" : ""}`}
                                            src={ResetBlackIcon}
                                        />
                                    </div>

                                    <select onChange={handleChangeDisplay}>
                                        <option value="5">5</option>
                                        <option value="10">10</option>
                                        <option value="15">15</option>
                                        <option value="20">20</option>
                                    </select>

                                    {
                                        isShowActionButtons(paths.length) &&
                                        <div
                                            className="action-buttons"
                                            onClick={() => setOpenNewPopup(true)}
                                        >
                                            <button className="new-button">
                                                {getNewButtonText(paths.length)}
                                            </button>
                                        </div>
                                    }
                                    <CreateNewPopup
                                        isOpen={openNewPopup}
                                        setIsOpen={setOpenNewPopup}
                                        isTopic={paths.length === 0}
                                        topic={paths[0]}
                                    />
                                </div>
                            </div>

                            {
                                currThread ?
                                <ThreadContent
                                    thread={currThread}
                                    setFlag={() => setFlag(!flag)}
                                    comments={comments}
                                    goBackToThread={goBackToThread}
                                /> :
                                <ItemList
                                    items={items}
                                    navigateTo={navigateTo}
                                    setFlag={() => setFlag(!flag)}
                                />
                            }

                            <div className="navigate-buttons">
                                <div className="pages">
                                    {`${currPage} of ${totalPage}`}
                                </div>

                                <div className="buttons">
                                    <button
                                        className="prev-button"
                                        onClick={() => handlePageChange(currPage - 1)}
                                    >
                                        <img src={DropDownIcon}/>
                                    </button>

                                    <button
                                        className="next-button"
                                        onClick={() => handlePageChange(currPage + 1)}
                                    >
                                        <img src={DropDownIcon}/>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};