import { parseJwt } from './'; 

const setToken = (jwt) => {
  if (!jwt) {
    return;
  }
  localStorage.setItem("access_token", jwt);
  const json = parseJwt(jwt);
  if (Object.keys(json).length === 0) {
    return;
  }
  localStorage.setItem("user_info", JSON.stringify(json));
}

const getToken = () => {
  let token = localStorage.getItem("access_token");
  return token;
};



export {
  getToken,
  setToken
};
