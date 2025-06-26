// import axios from "axios";
// import { BASE_URL, routeConstants } from "../constants";
// import { common } from "../utils/common";

// const axiosClient = axios.create({
//   baseURL: BASE_URL,
//   headers: {
//     "Content-Type": "application/json",
//     Accept: "application/json",
//   },
//   // timeout: 30000,
//   // responseEncoding: "utf8",
//   // withCredentials: false,
// });

// const apiRequest = (method, url, data, config) => {
//   return axiosClient({
//     method,
//     url,
//     ...(method === "get" ? { params: data } : { data }), // Sử dụng params hoặc data tùy thuộc vào phương thức
//     ...config,
//   });
// };

// axiosClient.interceptors.request.use(
//   (config) => {
//     // const { JWT_loginToken } = common.getToken();
//     const JWT_loginToken = common.getToken();
//     if (JWT_loginToken) {
//       config.headers.Authorization = `Bearer ${JWT_loginToken}`;
//     }

//     return config;
//   },
//   (error) => {
//     return Promise.reject(error);
//   }
// );

// axiosClient.interceptors.response.use(
//   (response) => response,
//   (error) => {
//     const originalRequest = error.config;

//     // check Unauthorized error
//     if (error.response.status === 401 && !originalRequest._retry) {
//       originalRequest._retry = true;

//       // return axios
//       //   .post("/auth/refresh-token", {
//       //     refresh_token: localStorage.getItem("refresh_token"),
//       //   })
//       //   .then((response) => {
//       //     if (response.data.access_token) {
//       //       localStorage.setItem("access_token", response.jwt);

//       //       originalRequest.headers.Authorization = `Bearer ${response.jwt}`;

//       //       return axiosClient(originalRequest);
//       //     }
//       //   });
//     }

//     const { response } = error;

//     // if (response && response.status === 401) {
//     //   console.error('Unauthorized:', response.data);
//     //   navigate(routeConstants.LOGIN);
//     // }

//     // check Bad Request
//     if (response && response.status === 400) {
//       console.error("Bad Request:", response.data);
//     }

//     // 500 Internal Server Error
//     if (response && response.status >= 500) {
//       console.error("Server Error:", response.data);
      
//     }

//     return Promise.reject(error);
//   }
// );

// export default axiosClient;

// export const get = (url, params, config) =>
//   apiRequest("get", url, params, config);
// export const remove = (url, config) => apiRequest("delete", url, null, config);
// export const post = (url, data, config) =>
//   apiRequest("post", url, data, config);
// export const put = (url, data, config) => apiRequest("put", url, data, config);
// export const patch = (url, data, config) =>
//   apiRequest("patch", url, data, config);
