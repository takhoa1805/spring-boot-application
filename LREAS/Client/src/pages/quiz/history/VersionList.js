import './styles/VersionList.css';
import React from 'react';
import { 
    ExitBlackIcon, RestoreWhiteIcon
} from '../../../images/Icon';
import { useNavigate } from 'react-router-dom';
import {
    restoreQuiz
} from '../../../api';

const VersionRow = ({
    version, index, currVersion,
    handleSelectVersion,
    quiz
}) => {
    const handleTime = (time) => {
        return new Date(time).toLocaleString("en-GB");
    };

    return (
        <div
            className={`version-row ${currVersion && currVersion.versionId === version.versionId ? "version-row-selected" : ""}`}
            onClick={() => handleSelectVersion(version)}
        >
            <div className="version-title">
                {version.versionName || `Version ${index + 1}`} {quiz?.quizVersionId === version.versionId ? "(current version)" : ""}
            </div>

            <div className="info">
                Time: {handleTime(version.updatedTime)}
            </div>
        </div>
    );
};

export function VersionList({
    versions, handleSelectVersion,
    currVersion, resourceId, quiz
}) {
    const navigate = useNavigate();

    const handleNavigate = () => {
        navigate(`/quiz/editor/${resourceId}`);
    };

    const handleRestoreVersion = async (versionId) => {
        if (!versionId) {
            return;
        }

        const data = {
            resourceId: resourceId,
            versionId: versionId
        }

        await restoreQuiz(data).then(
            _ => {
                handleNavigate();
            }
        ).catch(
            e => console.log(e)
        );
    };

    return (
        <div className="version-list-frame">
            <div className="version-list">
                <div className="history-title">
                    Quiz versions
                </div>

                <div className="version-list-container">
                    {
                        versions && versions.map((ver, index) => {
                            return (
                                <VersionRow
                                    key={index}
                                    version={ver}
                                    index={index}
                                    currVersion={currVersion}
                                    handleSelectVersion={handleSelectVersion}
                                    quiz={quiz}
                                />
                            );
                        })
                    }
                </div>

                <div className="action-buttons">
                    <button
                        className="exit-button"
                        onClick={() => handleNavigate()}
                    >
                        <img src={ExitBlackIcon}/>

                        <div className="text">
                            Exit
                        </div>
                    </button>

                    <button
                        className="restore-button"
                        onClick={() => handleRestoreVersion(currVersion?.versionId)}
                        disabled={quiz?.quizVersionId === currVersion?.versionId}
                    >
                        <img src={RestoreWhiteIcon}/>

                        <div className="text">
                            Restore
                        </div>
                    </button>
                </div>
            </div>
        </div>
    );
}