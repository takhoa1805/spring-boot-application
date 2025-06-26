import "./styles/CreateNewPopup.css";
import Popup from "reactjs-popup";
import React from "react";
import {
    createThread, createTopic
} from "../../api";
import { EditorState } from "prosemirror-state";
import { EditorView } from "prosemirror-view";
import { Schema, DOMParser } from "prosemirror-model";
import { schema } from "prosemirror-schema-basic";
import { addListNodes } from "prosemirror-schema-list";
import { exampleSetup } from "prosemirror-example-setup";

export default function CreateNewPopup({
    isOpen, setIsOpen,
    isTopic, topic
}) {
    const [subject, setSubject] = React.useState("");
    const [content, setContent] = React.useState("");
    const contentRef = React.useRef(null);
    const contentViewRef = React.useRef(null);
    const [count, setCount] = React.useState(0);
    const [missingSubject, setMissingSubject] = React.useState(false);
    const [missingContent, setMissingContent] = React.useState(false);

    const handleSubmit = async () => {
        if (content.length === 0) {
            setMissingContent(true);
        }
        if (subject.length === 0) {
            setMissingSubject(true);
        }
        if (subject.length === 0 || (content.length === 0 && !isTopic)) {
            return;
        }

        let data = null;
        const fetchFunc = isTopic ? createTopic : createThread;

        if (isTopic) {
            data = {
                title: subject
            };
        }
        else {
            data = {
                content: content,
                topicId: topic.id,
                subject: subject,
            };
        }

        await fetchFunc(data).then(
            res => {
                setIsOpen(false);
                setSubject("");
                setContent("");
            }
        ).catch(
            e => console.log(e)
        );

        setMissingContent(false);
        setMissingSubject(false);
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            handleSubmit();
        }
    };

    const handleSetSubject = (value) => {
        setMissingSubject(false);
        if (value.length <= 255) {
            setSubject(value);
        }
    };

    const mySchema = new Schema({
        nodes: addListNodes(schema.spec.nodes, "paragraph block*", "block"),
        marks: schema.spec.marks,
    });

    React.useEffect(() => {
        if (isOpen && count < 1) {
            setCount(count + 1);
        }
        else if (!isOpen) {
            setCount(0);
        }
        if (!contentRef.current) {
            return;
        }
        
        const contentElement = document.createElement("div");
        contentElement.innerHTML = content;
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

                setContent(contentViewRef.current.dom.innerHTML);
                setMissingContent(false);
            }
        });

        return () => {
            if (contentViewRef.current) {
                contentViewRef.current.destroy();
            }
        }
    }, [contentRef, isOpen, count]);

    return (
        <Popup open={isOpen} nested>
            <div
                className="new-popup"
                onKeyDown={handleKeyDown}
            >
                <div className="popup-container">
                    <div className="thread-title">Subject</div>
                    <input
                        className="input"
                        placeholder="Type your subject here..."
                        value={subject}
                        onChange={(e) => handleSetSubject(e.target.value)}
                    />
                    <div className="info-text">
                        <div
                            className="error-text"
                            style={{
                                opacity: missingSubject ? 1 : 0,
                            }}
                        >
                            Please enter a subject
                        </div>
                        <div className="limit-text">
                            {`${subject.length} / 255`}
                        </div>
                    </div>

                    {
                        !isTopic &&
                        <>
                            <div className="thread-title">Content</div>
                            <div
                                className="textarea"
                                ref={contentRef}
                            />
                            <div
                                className="error-text"
                                style={{
                                    display: missingContent ? "" : "none",
                                }}
                            >
                                Please enter content
                            </div>
                        </>
                    }

                    <div className="footer">
                        <button
                            className="cancel-button"
                            onClick={() => setIsOpen(false)}
                        >
                            Cancel
                        </button>
                        <button 
                            className="submit-button"
                            onClick={() => handleSubmit()}
                        >
                            Submit
                        </button>
                    </div>
                </div>
            </div>
        </Popup>
    );
};