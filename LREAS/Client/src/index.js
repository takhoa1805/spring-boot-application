import React, {useEffect,useState} from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import {parseJwt,getToken,setToken} from './utils'; 
import routes from './routes';




function AuthChecker() {
  const [userInfo, setUserInfo] = useState(
    {
      "role": null,
      "name": null,
      "email": null,
      "user_id": null,
      "account_id": null,
      "language": null
    }
  );

  useEffect(() => {
    const JWT_loginToken = getToken();
    const json = parseJwt(JWT_loginToken);
    
    if (Object.keys(json).length == 0) {
      return;
    }

    setUserInfo(json);
  }, []);

  const unauthorizedRoutes = routes.filter((route) => route.unauthorized == true);

  return (
    <App role={userInfo?.role || 'unauthorized'} unauthorizedRoutes={unauthorizedRoutes} />
  );
}



const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  // <React.StrictMode>
  <AuthChecker />
  // </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
