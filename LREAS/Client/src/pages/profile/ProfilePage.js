import "./styles/ProfilePage.css";
import React from "react";
import { Header } from "../../components/Header";
import { NavigationDrawer } from "../../components/NavigationDrawer";
import {
    AvatarIcon, ResetBlackIcon, SaveWhiteIcon, EditWhiteIcon,
    PasswordHideIcon, PasswordShowIcon, LoadingIcon
} from "../../images/Icon";
import {
    getProfile, updateProfile, updatePassword
} from "../../api";
import { getToken, parseJwt } from "../../utils";

const Gender = ({
    item, setGender, gender, setOtherGender,
    otherGender
}) => {
    const handleSetOtherGender = (value) => {
        if (value.length <= 100) {
            setOtherGender(value);
        }
    };

    const handleSetGender = (item) => {
        setGender(item);
        setOtherGender("");
    };

    return (
        <div className="option">
            <div
                className={`checkbox ${gender === item ? "checkbox-checked" : ""}`}
                onClick={() => handleSetGender(item)}
            >
                {
                    gender === item &&
                    <div className="checkmark"/>
                }
            </div>

            <div className="text">
                {item}
            </div>

            {
                item === "Other" &&
                <div className="other-input">
                    <input
                        className="input-text"
                        placeholder="Enter your gender..."
                        disabled={gender !== item}
                        onChange={e => handleSetOtherGender(e.target.value)}
                        value={otherGender}
                    />
                </div>
            }
        </div>
    );
};

export default function ProfilePage() {
    const JWT_loginToken = getToken();
    const userInfo = parseJwt(JWT_loginToken);
    const [avatarFile, setAvatarFile] = React.useState(null);
    const [username, setUsername] = React.useState("");
    const [birthday, setBirthday] = React.useState("");
    const genderList = ["Male", "Female", "Other"];
    const [gender, setGender] = React.useState("Male");
    const [otherGender, setOtherGender] = React.useState("");
    const [description, setDescription] = React.useState("");
    const [email, setEmail] = React.useState("");
    const [phone, setPhone] = React.useState("");
    const [address, setAddress] = React.useState("");
    const [currPassword, setCurrPassword] = React.useState("");
    const [showCurrPassword, setShowCurrPassword] = React.useState(false);
    const [newPassword, setNewPassword] = React.useState("");
    const [showNewPassword, setShowNewPassword] = React.useState(false);
    const [confirmPassword, setConfirmPassword] = React.useState("");
    const [showConfirmPassword, setShowConfirmPassword] = React.useState(false);
    const [institutionName, setInstitutionName] = React.useState("");
    const [invitationCode, setInvitationCode] = React.useState("");
    const [passCheckStat, setPassCheckStat] = React.useState("?");
    const [newPassStat, setNewPassStat] = React.useState("?");
    const [confirmPassStat, setConfirmPassStat] = React.useState("?");
    const [updatePassStat, setUpdatePassStat] = React.useState("");
    const [updateProfileStat, setUpdateProfileStat] = React.useState("");
    const [allowUpdatePass, setAllowUpdatePass] = React.useState(false);
    const [isLoading, setIsLoading] = React.useState(false);

    const convertToLocalISOString = (isoString) => {
        if (!isoString) {
            return ""; // Handle null or undefined values
        }

        const date = new Date(isoString);
        const localISO = new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString(); // Adjust for local time
        return localISO.slice(0, 10); // remove the milliseconds and 'Z'
    };

    const loadInitialData = (data) => {
        // load avatar
        const avatarPreview = document.getElementById("avatar-preview");
        avatarPreview.src = data.avtPath;

        // load info
        setUsername(data.username || "");

        if (data.birthday) {
            setBirthday(convertToLocalISOString(data.birthday) || "");
        }

        // gender
        if (data.gender && data.gender.length > 0) {
            const genderText = data.gender.charAt(0).toUpperCase() + data.gender.slice(1).toLowerCase();
            setGender(genderText);
            if (genderText === "Other") {
                setOtherGender(data.otherGender || "");
            }
        }

        setDescription(data.description || "");
        setEmail(data.email || "");
        setPhone(data.phone || "");
        setAddress(data.address || "");
        setInstitutionName(data.institutionName || "");
        setInvitationCode(data.invitationCode || "");
    };

    const fetchData = async () => {
        await getProfile().then(
            res => {
                loadInitialData(res.data);
            }
        ).catch(
            e => console.log(e)
        );
    };

    React.useEffect(() => {
        fetchData();
    }, []);

    const handleShowOrHidePassword = (
        passwordText, showPassword, setShowPassword,
        inputId
    ) => {
        if (!passwordText || passwordText === "") {
            return null;
        }

        const inputEl = document.getElementById(inputId);

        if (showPassword) {
            if (inputEl) {
                inputEl.type = "text";
            }
            
            return (
                <img
                    src={PasswordHideIcon}
                    onClick={() => setShowPassword(false)}
                />
            );
        }

        if (inputEl) {
            inputEl.type = "password";
        }

        return (
            <img
                src={PasswordShowIcon}
                onClick={() => setShowPassword(true)}
            />
        );
    };

    const loadFile = (e) => {
        if (!e.target.files || e.target.files.length === 0) {
            return;
        }

        const avatarPreview = document.getElementById("avatar-preview");
        const file = e.target.files[0];

        var reader = new FileReader();
        reader.onload = e => {
            setAvatarFile(e.target.result);
        };
        reader.readAsArrayBuffer(file);

        avatarPreview.src = URL.createObjectURL(file) || AvatarIcon;
        avatarPreview.onload = () => {
            URL.revokeObjectURL(avatarPreview.src);
        };
    };

    const handleUpdateProfile = async () => {
        setIsLoading(true);
        setUpdateProfileStat("");
        
        const updateData = JSON.stringify({
            username: username,
            birthday: birthday ? new Date(birthday).toISOString() : null,
            gender: gender.toUpperCase(),
            otherGender: otherGender,
            description: description,
            email: email,
            phone: phone,
            address: address
        });

        // update profile
        const statusText = document.getElementById("status-text");
        await updateProfile(avatarFile, updateData).then(
            res => {
                loadInitialData(res.data);
                statusText.style.color = "green";
                setUpdateProfileStat("Profile updated successfully");
            }
        ).catch(
            e => {
                console.log(e);
                statusText.style.color = "red";
                setUpdateProfileStat("Cannot save changes. Please try again later");
            }
        );

        setIsLoading(false);
    };

    const handleUpdatePassword = async () => {
        if (
            newPassword !== confirmPassword ||
            newPassword === "" ||
            currPassword === ""
        ) {
            if (currPassword === "") {
                setPassCheckStat("Please enter your current password");
            }
            else if (newPassword === "") {
                setNewPassStat("Please enter your new password");
            }
            else if (newPassword !== confirmPassword) {
                setConfirmPassStat("Passwords do not match");
            }
            
            setAllowUpdatePass(false);
            return;
        }
        setUpdatePassStat("");
        setPassCheckStat("?");
        setNewPassStat("?");
        setConfirmPassStat("?");

        // update password
        const updatePasswordData = {
            email: userInfo.email,
            currentPassword: currPassword,
            newPassword: newPassword
        };
        await updatePassword(updatePasswordData).then(
            res => {
                if (res.data.success) {
                    setUpdatePassStat(res.data.message);
                }
                else {
                    setPassCheckStat(res.data.message);
                }
            }
        )
    };

    const handleReset = () => {
        fetchData();
    };

    const handleSetDescription = (value) => {
        if (value.length <= 250) {
            setDescription(value);
        }
    };

    React.useEffect(() => {
        if (
            currPassword !== "" &&
            newPassword !== "" &&
            confirmPassword !== ""
        ) {
            setAllowUpdatePass(true);
        }
        else {
            setAllowUpdatePass(false);
        }
    }, [currPassword, newPassword, confirmPassword]);

    return (
        <div className="profile-page">
            <Header/>
            <div className="profile-page-container">
                <div className="container">
                    <NavigationDrawer/>

                    <div className="main-container">
                        <div className="info-frame">
                            <div className="title">
                                Personal profile
                            </div>

                            <div className="info">
                                <div className="info-title">
                                    Basic information
                                </div>

                                <div className="info-row">
                                    <div className="field-title">
                                        Your avatar
                                    </div>

                                    <div className="avatar-frame">
                                        <div className="avatar-image">
                                            <img src={AvatarIcon} id="avatar-preview"/>
                                        </div>

                                        <button
                                            className="edit-button"
                                            onClick={() => document.getElementById("avatar-file-input").click()}
                                        >
                                            <img src={EditWhiteIcon}/>

                                            <div className="text">
                                                Edit
                                            </div>

                                            <input
                                                type="file"
                                                id="avatar-file-input"
                                                onChange={loadFile}
                                            />
                                        </button>
                                    </div>
                                </div>

                                <div className="info-row">
                                    <div className="field-title">
                                        Username
                                    </div>

                                    <div className="input">
                                        <input
                                            className="input-text"
                                            placeholder="Your username"
                                            onChange={e => setUsername(e.target.value)}
                                            value={username}
                                        />
                                    </div>
                                </div>

                                <div className="info-row">
                                    <div className="field-title">
                                        Birthday
                                    </div>

                                    <div className="input">
                                        <input
                                            type="date"
                                            className="input-datetime"
                                            onChange={e => setBirthday(e.target.value)}
                                            value={birthday}
                                        />
                                    </div>
                                </div>

                                <div className="gender">
                                    <div className="field-title">
                                        Gender
                                    </div>

                                    <div className="selection-zone">
                                        {
                                            genderList.map((g, index) => {
                                                return (
                                                    <Gender
                                                        key={index}
                                                        item={g}
                                                        setGender={setGender}
                                                        gender={gender}
                                                        setOtherGender={setOtherGender}
                                                        otherGender={otherGender}
                                                    />
                                                );
                                            })
                                        }
                                    </div>
                                </div>

                                <div className="info-row">
                                    <div className="field-title">
                                        Description
                                    </div>

                                    <textarea
                                        className="description-input"
                                        placeholder="A brief introduction about yourself"
                                        onChange={e => handleSetDescription(e.target.value)}
                                        value={description}
                                    />

                                    <div className="limit-text">
                                        {`${description.length} / 250`}
                                    </div>
                                </div>
                            </div>

                            <div className="info">
                                <div className="info-title">
                                    Contact information
                                </div>

                                <div className="info-row">
                                    <div className="field-title">
                                        Email
                                    </div>

                                    <div className="input">
                                        <input
                                            className="input-text"
                                            placeholder="Your email"
                                            onChange={e => setEmail(e.target.value)}
                                            value={email}
                                        />
                                    </div>
                                </div>

                                <div className="info-row">
                                    <div className="field-title">
                                        Phone number
                                    </div>

                                    <div className="input">
                                        <input
                                            className="input-text"
                                            placeholder="Your phone number"
                                            onChange={e => setPhone(e.target.value)}
                                            value={phone}
                                        />
                                    </div>
                                </div>

                                <div className="info-row">
                                    <div className="field-title">
                                        Address
                                    </div>

                                    <div className="input">
                                        <input
                                            className="input-text"
                                            placeholder="Your address"
                                            onChange={e => setAddress(e.target.value)}
                                            value={address}
                                        />
                                    </div>
                                </div>

                                <div className="info-row">
                                    <div className="field-title">
                                        Institution name
                                    </div>

                                    <div className="input">
                                        <div
                                            className="input-text"
                                        >
                                            {institutionName}
                                        </div>
                                    </div>
                                </div>

                                <div className="info-row">
                                    <div className="field-title">
                                        Invitation code
                                    </div>

                                    <div className="input">
                                        <div
                                            className="input-text"
                                        >
                                            {invitationCode}
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="info">
                                <div className="info-title">
                                    Security
                                </div>

                                <div className="note">
                                    You can change your password here
                                </div>

                                <div className="info-row">
                                    <div className="field-title">
                                        Current password
                                    </div>

                                    <div className="password-input">
                                        <input
                                            className="input-text"
                                            type="password"
                                            placeholder="Current password"
                                            id="current-password"
                                            onChange={e => {
                                                setPassCheckStat("?");
                                                setCurrPassword(e.target.value);
                                            }}
                                        />

                                        {handleShowOrHidePassword(
                                            currPassword,
                                            showCurrPassword, setShowCurrPassword,
                                            "current-password"
                                        )}
                                    </div>

                                    <div
                                        className="status-text"
                                        style={{opacity: passCheckStat === "?" ? "0" : "1"}}
                                    >
                                        {passCheckStat}
                                    </div>
                                </div>

                                <div className="info-row">
                                    <div className="field-title">
                                        New password
                                    </div>

                                    <div className="password-input">
                                        <input
                                            className="input-text"
                                            type="password"
                                            placeholder="New password"
                                            id="new-password"
                                            onChange={e => {
                                                setNewPassStat("?");
                                                setNewPassword(e.target.value);
                                            }}
                                        />

                                        {handleShowOrHidePassword(
                                            newPassword,
                                            showNewPassword, setShowNewPassword,
                                            "new-password"
                                        )}
                                    </div>

                                    <div
                                        className="status-text"
                                        style={{opacity: newPassStat === "?" ? "0" : "1"}}
                                    >
                                        {newPassStat}
                                    </div>
                                </div>

                                <div className="info-row">
                                    <div className="field-title">
                                        Confirm password
                                    </div>

                                    <div className="password-input">
                                        <input
                                            className="input-text"
                                            type="password"
                                            placeholder="Confirm password"
                                            id="confirm-password"
                                            onChange={e => {
                                                setConfirmPassStat("?");
                                                setConfirmPassword(e.target.value);
                                            }}
                                        />

                                        {handleShowOrHidePassword(
                                            confirmPassword,
                                            showConfirmPassword, setShowConfirmPassword,
                                            "confirm-password"
                                        )}
                                    </div>

                                    <div
                                        className="status-text"
                                        style={{opacity: confirmPassStat === "?" ? "0" : "1"}}
                                    >
                                        {confirmPassStat}
                                    </div>
                                </div>

                                <div className="action-buttons">
                                    <div
                                        className="status-text"
                                        style={{opacity: updatePassStat === "" ? "0" : "1"}}
                                    >
                                        {updatePassStat}
                                    </div>

                                    <button
                                        className="update-button"
                                        onClick={() => handleUpdatePassword()}
                                        disabled={!allowUpdatePass}
                                    >
                                        <img src={SaveWhiteIcon}/>

                                        <div className="text">
                                            Update
                                        </div>
                                    </button>
                                </div>
                            </div>

                            <div className="action-buttons">
                                <div
                                    className="status-text"
                                    id="status-text"
                                    style={{opacity: updateProfileStat === "" ? "0" : "1"}}
                                >
                                    {updateProfileStat}
                                </div>

                                {
                                    isLoading &&
                                    <div
                                        className="loading"
                                    >
                                        <div className="text">
                                        Updating...
                                        </div>
                                        <img src={LoadingIcon}/>
                                    </div>
                                }

                                <div className="button-group">
                                    <button
                                        className="reset-button"
                                        onClick={() => handleReset()}
                                    >
                                        <img src={ResetBlackIcon}/>

                                        <div className="text">
                                            Reset
                                        </div>
                                    </button>

                                    <button
                                        className="save-button"
                                        onClick={() => handleUpdateProfile()}
                                    >
                                        <img src={SaveWhiteIcon}/>

                                        <div className="text">
                                            Save
                                        </div>
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