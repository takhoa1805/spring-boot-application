import React from "react";
import Popup from "reactjs-popup";
import "./styles/SharePopup.css";
import {
    getUserInfoFromEmail, addContributor, addViewer, deleteAccess
} from "../../api";
import { 
    DropDownIcon, TickIcon
} from "../../images/Icon";

import InputFileUpload from "./component/InputFileUpload";
import Papa from 'papaparse'; // For parsing CSV

import {
  Button
} from "@mui/material";

export function SharePopup({
    isOpen, closePopup, currContent = {id: null, collaborators: []}, setCollaborators, allowedSubmit = true,
    setIsOpenSuccessPopup, setSuccessMessage
}) {
    const [users, setUsers] = React.useState([]);
    const [email, setEmail] = React.useState("");
    const [roles, setRoles] = React.useState([
        {
            value: "VIEWER",
            name: "Viewer",
            description: "",
        },
        {
            value: "CONTRIBUTOR", 
            name: "Contributor", 
            description: "",
        },
    ]);
    const [selectedRow, setSelectedRow] = React.useState(0);
    const [isOpenMenu, setIsOpenMenu] = React.useState(false);
    const [isAllSuccess, setIsAllSuccess] = React.useState(true);
    const [status, setStatus] = React.useState("");
    const selectRoleRef = React.useRef(null);

    const [csvUsers, setCsvUsers] = React.useState([]);
    const [processing, setProcessing] = React.useState(false);
    const [progress, setProgress] = React.useState(0);
    const [currentUser, setCurrentUser] = React.useState('');
    const [userStatuses, setUserStatuses] = React.useState([]);

    React.useEffect(() => {
        setUsers(
            currContent.collaborators.map(c => {
                return {
                    id: c.id,
                    email: c.email,
                    role: c.role,
                    isAlreadyExisted: true,
                    isDeleted: false,
                }
            })
        );
    }, [isOpen]);

    const useClickOutside = (ref) => {
        React.useEffect(() => {
            const handleClickOutside = (e) => {
                if (
                    ref.current && !ref.current.contains(e.target)
                    && selectRoleRef.current && !selectRoleRef.current.contains(e.target)
                ) {
                    setIsOpenMenu(false);
                }
            };
            document.getElementsByClassName("share-popup")[0]?.addEventListener(
                "mouseup", handleClickOutside
            );
        }, [ref]);
    };
    const wrapperRef = React.useRef(null);
    useClickOutside(wrapperRef);

    const addUser = async () => {
        if (!email.includes("@") || !email.includes(".")) {
            setStatus("Invalid email address");
            return;
        }
        setStatus("");

        if (email) {
            await getUserInfoFromEmail(email).then(
                res => {
                    const updatedUsers = [...users];
                    const index = updatedUsers.findIndex(e => e.id === res.data.id);
                    if (index !== -1) {
                        updatedUsers[index].isDeleted = false;
                        setUsers(updatedUsers);
                    }
                    else {
                        setUsers([...users, {
                            id: res.data.id,
                            email: email,
                            role: "CONTRIBUTOR",
                            isAlreadyExisted: false,
                            isDeleted: false,
                        }]);
                    }

                    setEmail("");
                }
            ).catch(
                e => {
                    setStatus(e.response.data || e.message || e);
                }
            );
        }
    };

    const addUserWithEmail = async (userEmail,userRole) => {
        if (!userEmail.includes("@") || !userEmail.includes(".")) {
            setStatus("Invalid email address");
            return;
        }
        setStatus("");
        console.log("userEmail",userEmail,users);

        if (userEmail) {
            await getUserInfoFromEmail(userEmail).then(
                res => {
                    const updatedUsers = [...users];
                    const index = updatedUsers.findIndex(e => e.id === res.data.id);
                    if (index !== -1) {
                        updatedUsers[index].isDeleted = false;
                        setUsers(updatedUsers);
                    }
                    else {
                        console.log("userEmail",userEmail);
                        console.log("users before",users);
                        const newUser = {
                            id: res.data.id,
                            email: userEmail,
                            role: (userRole.toLowerCase() != 'viewer' && userRole.toLowerCase() != 'contributor') ? 'Viewer': userRole.toUpperCase(),
                            isAlreadyExisted: false,
                            isDeleted: false,
                        }
                        setUsers(prevUsers => [...prevUsers, newUser]);

                    }

                }
            ).catch(
                e => {
                    setStatus(e.response.data || e.message || e);
                }
            );
        }

    };

    const sleep = ms => new Promise(r => setTimeout(r, ms));
    
    const handleCSVUpload = (e) => {
    const file = e.target.files[0];
    if (!file || file.type !== 'text/csv') {
        alert('Please upload a valid CSV file.');
        return;
    }
    

    Papa.parse(file, {
        header: true,
        skipEmptyLines: true,
        complete: (results) => {
        const valid = results.meta.fields.length === 2 &&
            results.meta.fields.includes('email') &&
            results.meta.fields.includes('role');

        if (!valid) {
            alert('CSV format invalid. Must contain columns: email, role');
            return;
        }

        setCsvUsers(results.data);
        setUserStatuses(results.data.map(user => ({
            email: user.email,
            status: 'pending'
        })));
        },
    });
    };

    const handleBulkInvite = async () => {
        setProcessing(true);
        let completed = 0;
        console.log("csvUsers",csvUsers);

        for (const [index, user] of csvUsers.entries()) {
            const { email, role } = user;
            setCurrentUser(email);

            try {
                

                await addUserWithEmail(email,role);

                setUserStatuses(prev =>
                    prev.map(u =>
                    u.email === email ? { ...u, status: 'success' } : u
                    )
                );
            } catch (err) {
            setUserStatuses(prev =>
                prev.map(u =>
                u.email === email ? { ...u, status: 'fail' } : u
                )
            );
            }

            completed += 1;
            setProgress((completed / csvUsers.length) * 100);
        }

        setCurrentUser('');
    };

    const resetState = () => {
        // setEmailInput('');
        // setUsernameInput('');
        setRoles('');
        setCsvUsers([]);
        setProcessing(false);
        setProgress(0);
        setCurrentUser('');
        setUserStatuses([]);
    };

    const removeUser = async (index) => {
        const updatedUsers = [...users];
        updatedUsers[index].isDeleted = true;
        setUsers(updatedUsers);
    };

    const updateRole = async (index, role) => {
        const updatedUsers = [...users];
        updatedUsers[index].role = role;
        setUsers(updatedUsers);
    };

    const handleClose = () => {
        closePopup();
    };

    const handleSubmit = () => {
        if (currContent?.role?.toLowerCase() === "viewer") {
            handleClose();
            return;
        }

        if (!allowedSubmit) {
            setCollaborators(users);
            handleClose();
            return;
        }
        
        users.forEach(async (u, i) => {
            if (u.isAlreadyExisted && u.isDeleted) {
                await deleteAccess(currContent.id, u.id).then(
                    res => handleClose()
                ).catch(
                    e => {
                        const message = e.response.data.error.message.substring(
                            e.response.data.error.message.indexOf("\"") + 1,
                            e.response.data.error.message.length - 1
                        );
                        setStatus(message || e.message || e);
                        setIsAllSuccess(false);
                    }
                );
            }
            else if (!u.isDeleted) {
                await deleteAccess(currContent.id, u.id).then(
                    res => {}
                ).catch(
                    e => {}
                );

                if (u.role.toLowerCase() === "viewer") {
                    await addViewer(currContent.id, u.id).then(
                        res => handleClose()
                    ).catch(
                        e => {
                            const message = e.response.data.error.message.substring(
                                e.response.data.error.message.indexOf("\"") + 1,
                                e.response.data.error.message.length - 1
                            );
                            setStatus(message || e.message || e);
                            setIsAllSuccess(false);
                        }
                    );
                }
                else if (u.role.toLowerCase() === "contributor") {
                    await addContributor(currContent.id, u.id).then(
                        res => handleClose()
                    ).catch(
                        e => {
                            const message = e.response.data.error.message.substring(
                                e.response.data.error.message.indexOf("\"") + 1,
                                e.response.data.error.message.length - 1
                            );
                            setStatus(message || e.message || e);
                            setIsAllSuccess(false);
                        }
                    );
                }
            }
        });
        setSuccessMessage("Access list updated successfully!");
        setIsOpenSuccessPopup(true);
    };

    const toUpperCaseFirst = (text) => {
        return text.charAt(0).toUpperCase() + text.slice(1).toLowerCase();
    };

    return (
        <Popup open={isOpen} nested>
            <div className="share-popup">
                <div className="share-container">
                    <div className="header-share-popup">
                        <div className="share-title">
                            {`Share this content`}
                        </div>
                        <button
                            className="closeButton"
                            onClick={() => handleClose()}
                        >
                            x
                        </button>
                    </div>
                    {
                        currContent?.role?.toLowerCase() !== "viewer" &&
                        <div className="inviteSection">
                            <label className="inviteLabel">Invite by email</label>
                            <div className="inviteRow">
                                <input
                                    type="text"
                                    placeholder="Enter email address"
                                    value={email}
                                    onChange={(e) => {
                                        setEmail(e.target.value);
                                        setStatus("");
                                    }}
                                    className="input"
                                />
                                <button
                                    onClick={addUser}
                                    className="addButton"
                                >
                                    Add
                                </button>
                            </div>
                            <div className="status-text">
                                {status}
                            </div>
                        </div>
                    }
                    <div className="userList">
                        {users.filter(u => !u.isDeleted).map((user, index) => (
                            <div key={index} className="userRow">
                                <div className="userEmail">
                                    {user.email}
                                </div>
                                <div className="action-group">
                                    <div
                                        className={`
                                            role-select
                                            ${
                                                currContent?.role?.toLowerCase() === "viewer" || user?.role?.toLowerCase() === "owner" ?
                                                "role-select-disabled" : ""
                                            }
                                        `}
                                    >
                                        <div
                                            onClick={() => {
                                                setSelectedRow(index);
                                                setIsOpenMenu(!isOpenMenu);
                                            }}
                                            className="select-role-button"
                                            ref={selectRoleRef}
                                        >
                                            <div className="text">
                                                {toUpperCaseFirst(user?.role)}
                                            </div>
                                            {
                                                currContent?.role?.toLowerCase() !== "viewer" &&
                                                user?.role?.toLowerCase() !== "owner" &&
                                                <img src={DropDownIcon} className="icon"/>
                                            }
                                        </div>
                                        {
                                            selectedRow === index && isOpenMenu &&
                                            <div className="role-selector-menu" ref={wrapperRef}>
                                                <span className="role-title">ROLE</span>
                                                <div className="menu">
                                                    {roles.filter(r => !currContent.isFolder || r.value === "CONTRIBUTOR").map((role) => (
                                                        <div
                                                            key={role.name}
                                                            className="option"
                                                            onClick={() => {
                                                                updateRole(index, role.value);
                                                                setIsOpenMenu(false);
                                                            }}
                                                        >
                                                            <img
                                                                src={TickIcon}
                                                                style={{
                                                                    opacity: user?.role?.toLowerCase() === role?.name?.toLowerCase() ? 1 : 0,
                                                                }}
                                                            />
                                                            {user?.role === role.name && <span className="checkmark">✔</span>}
                                                            {role.name}
                                                            {role.description && (
                                                                <div className="description">{role.description}</div>
                                                            )}
                                                        </div>
                                                    ))}
                                                </div>
                                            </div>
                                        }
                                    </div>
                                    <button
                                        onClick={() => removeUser(index)}
                                        className="removeButton"
                                        style={{
                                            opacity: currContent?.role?.toLowerCase() === "viewer" || user?.role?.toLowerCase() === "owner" ? 0 : 1,
                                        }}
                                        disabled={currContent?.role?.toLowerCase() === "viewer" || user?.role?.toLowerCase() === "owner"}
                                    >
                                        x
                                    </button>
                                </div>
                            </div>
                        ))}
                            {csvUsers.length > 0 && !processing && (
                                <>
                                <Button variant="contained" color="primary" onClick={handleBulkInvite} 
                                sx={{ marginLeft: '10px' }}  
        
                                >
                                Invite {csvUsers.length} Users
                                </Button>
                                <Button variant="contained" color="dangerous" onClick={resetState} 
                                sx={{ marginLeft: '10px' }}  
        
                                >
                                Cancel
                                </Button>
                                </>
                            )}
        
                            {/* {processing && (
                                <Box sx={{ mt: 3 }}>
                                <Typography variant="body1">Inviting: {currentUser}</Typography>
                                <LinearProgress variant="determinate" value={progress} sx={{ mt: 1, mb: 2 }} />
                                <List dense>
                                    {userStatuses.map(({ email, status }) => (
                                    <ListItem key={email}>
                                        <ListItemText
                                        primary={email}
                                        secondary={status}
                                        secondaryTypographyProps={{
                                            color:
                                            status === 'success'
                                                ? 'green'
                                                : status === 'fail'
                                                ? 'red'
                                                : 'textSecondary',
                                        }}
                                        />
                                    </ListItem>
                                    ))}
                                </List>
                                </Box>
                            )} */}
                    </div>
                    <div className="footer">
                        {
                            currContent?.role?.toLowerCase() !== "viewer" &&
                            <>
                                <button
                                    className="cancelButton"
                                    onClick={() => handleClose()}
                                >
                                    Cancel
                                </button>
                                <InputFileUpload handleCSVUpload={handleCSVUpload} />
                            </>
                        }
                        <button
                            className="confirmButton"
                            onClick={() => handleSubmit()}
                        >
                            Confirm
                        </button>
                    </div>
                </div>
            </div>
        </Popup>
    );
};