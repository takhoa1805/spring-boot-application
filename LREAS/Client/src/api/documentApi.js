import axios from "axios";
import { getToken } from "../utils/index";

const API_BASE_URL = "/api/documents";
const BASE_URL = process.env.REACT_APP_BASE_URL ? process.env.REACT_APP_BASE_URL : null;

export async function getDocumentEditor(docId) {
    const JWT_loginToken = getToken();
  try {
    const response = await axios.get(`${API_BASE_URL}/${docId}/editor`, {
        headers: {
            Authorization: `Bearer ${JWT_loginToken}`
        },
        baseURL: BASE_URL,
    });


    return response.data;
  } catch (error) {
    console.error("Error fetching document:", error);
    return null;
  }
}

export async function getDocumentViewer(docId) {
  const JWT_loginToken = getToken();
try {

  const response = await axios.get(`${API_BASE_URL}/${docId}/viewer`, {
      headers: {
          Authorization: `Bearer ${JWT_loginToken}`
      },
      baseURL: BASE_URL,
  });

  return response.data;
} catch (error) {
  console.error("Error fetching document:", error);
  return null;
}
}


export async function saveDocument(docId, content) {
  try {
    const JWT_loginToken = getToken();

    const response = await axios.post(`${API_BASE_URL}/${docId}`, { content }, {
        headers: {
            Authorization: `Bearer ${JWT_loginToken}`
        },
        baseURL: BASE_URL,
    });
    return  response.data;

  } catch (error) {
    console.error("Error saving document:", error);
  }
}
