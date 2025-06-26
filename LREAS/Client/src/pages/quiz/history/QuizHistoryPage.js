import React from "react";
import { Header } from "../../../components/Header";
import "./styles/QuizHistoryPage.css";
import { VersionList } from "./VersionList";
import { QuizHistoryNav } from './QuizHistoryNav';
import { Content } from './Content';
import {
    getQuizByVersion, getAllQuizVersions
} from '../../../api';
import { useParams } from "react-router-dom";

export default function QuizHistoryPage() {
    const { resourceId } = useParams();
    const [questionItem, setQuestionItem] = React.useState(null);
    const [quiz, setQuiz] = React.useState(null);
    const [versions, setVersions] = React.useState([]);
    const [currVersion, setCurrVersion] = React.useState(null);

    const fetchQuizVersion = async (versionId) => {
        await getQuizByVersion(versionId).then(
            res => {
                setQuiz(res.data);
                setQuestionItem(res.data.questions[0]);
            }
        ).catch(
            e => {
                console.log(e);
            }
        );
    };

    const fetchData = async () => {
        await getAllQuizVersions(resourceId).then(
            async res => {
                setVersions(res.data);

                const currVer = res.data[0];
                setCurrVersion(currVer);
                await fetchQuizVersion(currVer.versionId);
            }
        ).catch(
            e => {
                console.log(e);
            }
        );
    };

    const handleSelectVersion = async (version) => {
        if (!version) {
            return;
        }
        setCurrVersion(version);
        await fetchQuizVersion(version.versionId);
    }

    React.useEffect(() => {
        fetchData();
    }, [resourceId]);

    return (
        <>
            <div className="history-quiz-page">
                <Header/>
                <div className="history-quiz">
                    <div className="div">
                        {
                            quiz && Object.keys(quiz).length > 0 &&
                            <>
                                <QuizHistoryNav
                                    quiz={quiz}
                                    questionItem={questionItem}
                                    setQuestionItem={setQuestionItem}
                                />

                                <Content
                                    questionItem={questionItem}
                                />

                                <VersionList
                                    versions={versions}
                                    handleSelectVersion={handleSelectVersion}
                                    currVersion={currVersion}
                                    resourceId={resourceId}
                                    quiz={quiz}
                                />
                            </>
                        }
                    </div>
                </div>
            </div>
        </>
    );
};