import { BrowserRouter as Router, Routes, Route, useLocation, useNavigate } from "react-router-dom";
import React, { useEffect } from "react";
import "@fontsource/lato"; // Ensure the font is loaded
import './App.css';
import routes from "./routes/index";
import UnauthorizedPage from './pages/UnauthorizedPage';
import NotFoundPage from './pages/NotFoundPage';
import InvitationPage from './pages/invitation/InvitationPage';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import './App.css';

const theme = createTheme({
  typography: {
    fontFamily: 'Lato, sans-serif', // Correct format
  },
});

function AuthGuard({ role, unauthorizedRoutes, setCurrNavTab, children }) {
  const location = useLocation();
  const navigate = useNavigate();
  const [timeoutAuth, setTimeoutAuth] = React.useState(null);
  
  const isUnauthorized = !unauthorizedRoutes.some(route => route.path === location.pathname);
  
  useEffect(() => {
    if (timeoutAuth) {
      clearTimeout(timeoutAuth);
    }

    setTimeoutAuth(
      setTimeout(() => {
        if (role === 'unauthorized' && isUnauthorized && !location.pathname.includes('/verify')) {
          navigate('/unauthorized');
        }
      }, 30000)
    );

    const r = unauthorizedRoutes.filter(route => route.path === location.pathname)[0];
    if (r && r.authorizedRoute && role!="unauthorized") {
      navigate(r.authorizedRoute);
    }
  }, [role, isUnauthorized, navigate]);

  useEffect(() => {
    const currUrl = "/" + window.location.href.split("/").slice(-1)[0];
    
    if (currUrl === "/") {
      return;
    }

    setCurrNavTab(
      routes.filter((item) => item.path === currUrl)[0]?.index
    );
  }, [window.location.href, location]);
  
  return children;
}

export const UserContext = React.createContext(null);

function App({ role = 'unauthorized', unauthorizedRoutes = [] }) {
  const [currNavTab, setCurrNavTab] = React.useState(2); // 0-indexed

  return (
    <Router>
      <ThemeProvider theme={theme}>
        <div className="App">
          <UserContext.Provider value={{ currNavTab: currNavTab, setCurrNavTab: setCurrNavTab }}>
            <AuthGuard role={role} unauthorizedRoutes={unauthorizedRoutes} setCurrNavTab={setCurrNavTab}>
              <Routes>
                {routes.map((route, index) => {
                  const Page = route.component;
                  return <Route key={index} path={route.path} element={<Page />} />;
                })}
                <Route path="/verify/:invitationId" element={<InvitationPage />} />
                <Route path="*" element={<NotFoundPage />} />
                <Route path="/unauthorized" element={<UnauthorizedPage />} />
              </Routes>
            </AuthGuard>
          </UserContext.Provider>
        </div>
      </ThemeProvider>
    </Router>
  );
}

export default App;
