import axios from "axios";
import { getToken } from "../utils/index";

const API_BASE_URL = "/api/competitive-quiz";
const BASE_URL = process.env.REACT_APP_BASE_URL ? process.env.REACT_APP_BASE_URL : null;

export async function startNewQuizSession({quizVersionId}) {
    try {
        const JWT_loginToken = getToken();
    
        const response = await axios.post(`${API_BASE_URL}/sessions/new`, { quizVersionId:quizVersionId }, {
            headers: {
                Authorization: `Bearer ${JWT_loginToken}`
            },
            baseURL: BASE_URL,
        });
        return  response.data.sessionCode;
    
      } catch (error) {
        console.error("Error saving document:", error);
      }
}

export async function joinSession({sessionCode}){
  try{
      const JWT_loginToken = getToken();
      const response = await axios.get(`${API_BASE_URL}/sessions/join/${sessionCode}`, {
          headers: {
              Authorization: `Bearer ${JWT_loginToken}`
          },
          baseURL: BASE_URL,
      });
      // if status = 200 then return true
      if(response.status === 200){
          return true;
      } else return false;
      // if status = 404 then return false
  } catch (error) {
    console.error("Error joining session:", error);
      return false;
  }
}
