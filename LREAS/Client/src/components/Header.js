import './styles/Header.css';
import {
    BrandIcon, SearchIcon, NotiIcon, AvatarIcon, DropDownIcon
  } from '../images/Icon';
import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getNotiInfo, markReadNoti, markUnreadNoti,
  deleteNoti, getProfile
} from '../api';

export function Header() {
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
      show: userInfo?.role?.toLowerCase() === 'admin',
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
  const [isLoggedIn, setIsLoggedIn] = React.useState(userInfo && userInfo.id ? true : false);
  const userActionRef = React.useRef(null);

  const useClickOutside = (ref) => {
    React.useEffect(() => {
        const handleClickOutside = (e) => {
        if (
          ref.current &&
          !ref.current.contains(e.target) &&
          userActionRef.current &&
          !userActionRef.current.contains(e.target)
        ) {
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

  const fetchNotifications = async () => {
    await getNotiInfo().then(res => {
      setNotifications(res.data.map(notification => {
        return {
          author: notification.senderName,
          message: notification.message,
          time: notification.createdTime,
          unread: !notification.isRead,
          readTime: notification.readTime,
        };
      }));
    })
    .catch(e => {
      console.log('Error fetching notifications: ', e);
    });
  };

  const fetchUserInfo = async () => {
    await getProfile().then(res => {
      setUserInfo(res.data);
    }).catch(e => {
      console.log(e);
    });
  };

  React.useEffect(() => {
    fetchUserInfo();
    fetchNotifications();
  }, []);

  React.useEffect(() => {
    if (!showNotifications) {
      return;
    }

    // Mark notifications as read when the notification menu is opened
    notifications.forEach(async notification => {
      if (notification.unread) {
        await markReadNoti(notification.id).then(
          res => {}
        ).catch(e => {
          console.log('Error marking notification as read: ', e);
        });
      }
    });

    fetchNotifications(); // Fetch notifications again to update the state
  }, [showNotifications]);

  const handleClearNotifications = () => {
    notifications.forEach(async notification => {
      await deleteNoti(notification.id).then(
        res => {}
      ).catch(e => {
        console.log('Error deleting notification: ', e);
      });
    });
    setNotifications([]);
  };

  return (
    <>
      <header className="header">
        <div className="brand-logo-header">
          <img
            loading="lazy"
            src={BrandIcon}
            className="brand-icon"
            alt="Company logo"
          />
          <div className="brand-text">LREAS</div>
        </div>
        {
          isLoggedIn &&
          <div className="search-container" role="search">
            <div className="search-main-content">
              <img
                loading="lazy"
                src={SearchIcon}
                className="search-icon"
                alt=""
              />
              <input
                type="search"
                className="search-input"
                placeholder="Search anything..."
                aria-label="Search"
              />
            </div>
          </div>
        }
        {
          isLoggedIn ? (
            <div className="user-profile" ref={wrapperRef}>
              <img
                loading="lazy"
                src={NotiIcon}
                className="notification-icon"
                alt="Notifications"
                onClick={() => setShowNotifications(!showNotifications)}
              />
              {showNotifications && (
                <div className="notification-menu">
                  <div className="notification-header">
                    <span>Notifications</span>
                    <button
                      className={`clear-button ${notifications.length == 0 ? "empty-clear-button" : "color-clear-button"}`}
                      onClick={() => handleClearNotifications()}
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
              <div className="user-name">{`Hi, ${userInfo.username || "John Lewis"}`}</div>
              <div className="user-actions" onClick={() => setShowUserMenu(!showUserMenu)} ref={userActionRef}>
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
          ) : (
            <div className="actions-group">
              <button
                className="sign-in-button"
                onClick={() => navigate("/login")}
              >
                Login
              </button>
              <button
                className="sign-up-button"
                onClick={() => navigate("/signup")}
              >
                Sign up
              </button>
            </div>
          )
        }
      </header>
    </>
  );
}