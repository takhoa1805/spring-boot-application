import './styles/NavigationDrawer.css';
import {
  ProfileIcon, GeneratorIcon, ContentIcon, ForumIcon, ManageIcon,
  CollapseIcon
} from '../images/Icon';
import { useNavigate } from "react-router-dom";
import React from "react";
import { UserContext } from '../App';
import routes from '../routes/index';

const NavigationTab = ({
  icon, text, index, setCurrNavTab, currNavTab, isCollapsed
}) => {
  const navigate = useNavigate();
  const handleClick = () => {
    setCurrNavTab(index);
    navigate(routes.filter(route => route.navText === text)[0].path);
  };

  return (
    <div 
      className={`nav-tab ${currNavTab ? 'active' : ''}`}
      tabIndex="0"
      role="button"
      onClick={handleClick}
    >
      <img
        loading="lazy"
        src={icon}
        className="nav-icon"
        alt={`${text} icon`}
        id={index}
      />
      <div
        className="nav-text"
        style={{
          opacity: `${isCollapsed ? "0" : "1"}`,
          visibility: `${isCollapsed ? "hidden" : "visible"}`,
        }}
      >
        {text}
      </div>
    </div>
  );
};

export function NavigationDrawer() {
  const {currNavTab, setCurrNavTab} = React.useContext(UserContext);
  const userInfo = JSON.parse(localStorage.getItem("user_info"));

  const navigationItems = [
    {
      icon: ProfileIcon,
      text: "Profile",
      show: true,
    },
    {
      icon: GeneratorIcon,
      text: "Resource Generator",
      show: userInfo?.role?.toLowerCase() !== "student",
    },
    {
      icon: ContentIcon,
      text: "Content",
      show: true,
    },
    {
      icon: ForumIcon,
      text: "Forum",
      show: true,
    },
    {
      icon: ManageIcon,
      text: "Organization Management",
      show: userInfo?.role?.toLowerCase() === "admin",
    }
  ];
  const [nav, setNav] = React.useState(null);
  const [collapse, setCollapse] = React.useState(null);
  const [isCollapsed, setIsCollapsed] = React.useState(false);
  const [isHovered, setIsHovered] = React.useState(true);
  const [timeoutHover, setTimeoutHover] = React.useState(null);

  const handleResize = (isCollapsed) => {
    if (!nav) {
      return;
    }

    if (isCollapsed) {
      nav.style.width = "64px";
      collapse.style.transform = "rotate(0deg)";
    }
    else {
      nav.style.width = "254.475px";
      collapse.style.transform = "rotate(180deg)";
    }
  };

  const handleCollapse = () => {
    handleResize(!isCollapsed);
    setIsCollapsed(!isCollapsed);
  };

  React.useEffect(() => {
    window.addEventListener("resize", handleResize, false);
  }, []);

  const setTimeoutForHover = () => {
    if (timeoutHover) {
      clearTimeout(timeoutHover);
    }
    setTimeoutHover(setTimeout(() => {
      setIsHovered(false);
    }, 2000));
  };

  React.useEffect(() => {
    if (isHovered) {
      setNav(document.getElementById("navigation-drawer"));
      setCollapse(document.getElementById("collapse-nav-icon"));

      setTimeoutForHover();
    }
  }, [isHovered]);

  React.useEffect(() => {
    handleResize(isCollapsed);
  }, [collapse]);

  return (
    <div
      className="navigation-drawer"
      id="navigation-drawer"
      onMouseMove={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <div className="navigation">
        {
          navigationItems.map((item, index) => {
            if (!item.show) {
              return null;
            }
            return (
              <NavigationTab
                key={index}
                icon={item.icon}
                text={item.text}
                index={index}
                setCurrNavTab={setCurrNavTab}
                currNavTab={index === currNavTab}
                isCollapsed={isCollapsed}
              />
            );
          })
        }
      </div>
      <div
        className="collapse"
        onClick={() => handleCollapse()}
        onMouseEnter={() => {
          if (timeoutHover) {
            clearTimeout(timeoutHover);
          }
          setIsHovered(true);
        }}
        onMouseLeave={() => setTimeoutForHover()}
        style={{
          opacity: `${isHovered ? "1" : "0"}`,
          visibility: `${isHovered ? "visible" : "hidden"}`,
        }}
      >
        <img src={CollapseIcon} id="collapse-nav-icon"/>
      </div>
    </div>
  );
}