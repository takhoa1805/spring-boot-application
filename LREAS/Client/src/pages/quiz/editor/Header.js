import './styles/Header.css';
import {
    BrandIcon, NotiIcon, AvatarIcon, DropDownIcon,
    VersionHistoryIcon, MoreBlackIcon
} from '../../../images/Icon';
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { getProfile } from '../../../api';

const CollaboratorList = ({
  collaborators, moreRef, isOpen
}) => {
  const listRef = React.useRef(null);

  React.useEffect(() => {
    if (!moreRef || !listRef) {
      return;
    }

    if (moreRef.current && listRef.current) {
      const moreRect = moreRef.current.getBoundingClientRect();
      const listRect = listRef.current.getBoundingClientRect();
      
      listRef.current.style.top = `${moreRect.bottom}px`;
      listRef.current.style.left = `${moreRect.left - listRect.width}px`;
    }
  }, [isOpen, moreRef, listRef]);

  const getStatusClass = (activeTime) => {
    const active = new Date(activeTime);
    const now = new Date();

    if (now.getMinutes() - active.getMinutes() <= 30) {
      return "-online";
    } else {
      return "-away";
    }
  };

  return (
    <div
      className="collaborator-list"
      ref={listRef}
      style={{
        opacity: isOpen ? "1" : "0",
        visibility: isOpen ? "visible" : "hidden",
      }}
    >
      <div className="collaborator-title">
        <div className="collab-name">
          Collaborators
        </div>
      </div>
      {
        collaborators.map((collab, index) => {
          return (
            <div key={index} className="collaborator-row">
              <div className="info-container">
                <div className="collab-avatar">
                  <img
                    src={collab?.imageUrl || AvatarIcon}
                    className={`collab-icon collab-icon${getStatusClass(collab.lastActive)}`}
                    loading="lazy"
                  />
                </div>

                <div className="collab-name">
                  <div className="text">
                    {collab?.name || ""}
                  </div>
                </div>
              </div>

              <div className={`status status${getStatusClass(collab.lastActive)}`}/>
            </div>
          );
        })
      }
    </div>
  );
};

const CollaboratorItem = ({ collab }) => {
  const getStatusClass = (activeTime) => {
    const active = new Date(activeTime);
    const now = new Date();

    if (now.getMinutes() - active.getMinutes() <= 30) {
      return "-online";
    } else {
      return "-away";
    }
  };

  return (
    <div className="collab-item">
      <img
        src={collab?.imageUrl || AvatarIcon}
        className={`collab-icon${getStatusClass(collab.lastActive)}`}
        loading="lazy"
      />
    </div>
  );
};

export function Header({ resourceId, collaborators }) {
  const navigate = useNavigate();
  const [userInfo, setUserInfo] = React.useState(JSON.parse(localStorage.getItem("user_info")));
  const [showUserMenu, setShowUserMenu] = React.useState(false);
  const [userMenu, setUserMenu] = React.useState([
    {
      name: 'Profile',
      handleClick: () => {
        navigate('/profile');
      },
      show: true,
    },
    {
      name: 'Settings',
      handleClick: () => {
        navigate('/admin');
      },
      show: true,
    },
    {
      name: 'Logout',
      handleClick: () => {
        localStorage.removeItem("access_token");
        localStorage.removeItem("user_info");
        navigate('/login');
      },
      show: true,
    },
  ]);
  const [notifications, setNotifications] = React.useState([
    {author: 'Julian Paul', message: 'marked your order.', time: '2h ago', unread: true},
    {author: 'Owen Michael', message: 'left a 5-star review.', time: '14h ago', unread: false}
  ]);
  const [showNotifications, setShowNotifications] = React.useState(false);
  const [showCollaboratorList, setShowCollaboratorList] = React.useState(false);
  const moreRef = React.useRef(null);

  const useClickOutside = (ref) => {
    React.useEffect(() => {
        const handleClickOutside = (e) => {
        if (ref.current && !ref.current.contains(e.target)) {
          setShowUserMenu(false);
          setShowNotifications(false);
        }
        };
        document.querySelectorAll("div.App")[0]?.addEventListener(
            "mousedown", handleClickOutside
        );
    }, [ref, showUserMenu]);
  };
  const wrapperRef = React.useRef(null);
  useClickOutside(wrapperRef);

  const fetchUserInfo = async () => {
    await getProfile().then(res => {
      setUserInfo(res.data);
    }).catch(e => {
      console.log(e);
    });
  };

  React.useEffect(() => {
    fetchUserInfo();
  }, []);

  React.useEffect(() => {
    window.addEventListener(
      "resize",
      () => setShowCollaboratorList(false),
      false
    );
  }, []);

  return (
    <>
      <header className="header-quiz-editor">
        <div className="brand-logo-header">
          <img
            loading="lazy"
            src={BrandIcon}
            className="brand-icon"
            alt="Company logo"
          />
          <div className="brand-text">LREAS</div>
        </div>
        <div className="user-profile" ref={wrapperRef}>
          <div className="collaborator-header">
            <div
              className="collab-item"
              ref={moreRef}
              onClick={() => setShowCollaboratorList(!showCollaboratorList)}
            >
              <img
                src={MoreBlackIcon}
                className="more-icon"
                loading="lazy"
              />
            </div>

            {
              collaborators.map((collab, index) => (
                <CollaboratorItem key={index} collab={collab} />
              ))
            }

            <div className="collaborator-list-header">
              <CollaboratorList
                collaborators={collaborators}
                moreRef={moreRef}
                isOpen={showCollaboratorList}
              />
            </div>
          </div>

          <img
            src={VersionHistoryIcon}
            className="icon"
            onClick={() => navigate(`/quiz/history/${resourceId}`)}
          />

          <img
            src={NotiIcon}
            className="icon"
            onClick={() => setShowNotifications(!showNotifications)}
          />

          {showNotifications && (
            <div className="notification-menu">
              <div className="notification-header">
                <span>Notifications</span>
                <button
                  className={`clear-button ${notifications.length == 0 ? "empty-clear-button" : "color-clear-button"}`}
                  onClick={() => setNotifications([])}
                >
                  Clear All
                </button>
              </div>
              <div className="notification-list">
                {
                  notifications.map((notification, index) => (
                    <div
                      key={index}
                      className={`notification-item${notification.unread ? '-unread' : ''}`}
                    >
                      <img src={AvatarIcon} alt="User" className="user-icon" />
                      <div>
                        <strong>{notification.author}</strong> {notification.message}
                      </div>
                      <span className="timestamp">{notification.time}</span>
                    </div>
                ))}
              </div>
              {
                notifications.length > 0 &&
                <button className="view-all-button">View All</button>
              }
              {
                notifications.length === 0 &&
                <div>
                  You have no notification right now!
                </div>
              }
            </div>
          )}

          <div className="vertical-divider" role="separator" />

          <div className="user-name">{`Hi, ${userInfo?.username || "John Lewis"}`}</div>

          <div className="user-actions" onClick={() => setShowUserMenu(!showUserMenu)}>
            <img
              loading="lazy"
              src={userInfo?.avtPath || AvatarIcon}
              className="user-icon"
              alt=""
              tabIndex="0"
              role="button"
            />
            <img
              loading="lazy"
              src={DropDownIcon}
              className="drop-down-icon"
              alt=""
              tabIndex="0"
              role="button"
            />
            {showUserMenu && (
              <div className="user-menu" ref={wrapperRef}>
                {
                  userMenu.map((item, index) => {
                    if (!item.show) {
                      return null;
                    }
                    
                    return (
                      <div
                        key={index}
                        onClick={() => item.handleClick()}
                      >
                        {item.name}
                      </div>
                    );
                  })
                }
              </div>
            )}
          </div>

        </div>
      </header>
    </>
  );
}