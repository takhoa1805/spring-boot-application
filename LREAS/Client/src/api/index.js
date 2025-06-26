import axios from "axios";
import { getToken } from "../utils/index";

const BASE_URL = process.env.REACT_APP_BASE_URL ? process.env.REACT_APP_BASE_URL : null;

//API ĐĂNG KÝ TÀI KHOẢN
// Đăng ký tài khoản
export const createAccount = async (account) => {
  try
  {  
      const response =  await axios({
      method: "post",
      data: account,
      url: `/api/authentication/signup`,
      baseURL: BASE_URL,
    });

    return response;
  } catch(error){

    return error.response;
  }

};

// Tạo mật khẩu
export const createPasswordFromInvitation = async (password, invitationId) => {
try {
    return await axios({
    method: "post",
    data: {"password":password},
    url: `/api/authentication/users/verify/${invitationId}`,
    baseURL: BASE_URL,
  });
  } catch(error){
    return error.response
  }

};

export const getInvitationInformation = async (invitationId) => {
  try {
  const response =  await axios({
    method: "get",
    url: `/api/authentication/users/verify/${invitationId}`,
    baseURL: BASE_URL,
  });

  return response.status === 200 ? response.data : null;
  } catch(error){

    return null;
  }

};
//Thay đổi mật khẩu
export const changePassword = async (password) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    data: password,
    url: `/auth/password`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

//Quên mật khẩu
export const forgotPassword = async (account) => {
  return await axios({
    method: "post",
    data: account,
    url: `/auth/password/recover`,
  });
};

//Reset mật khẩu
export const resetPassword = async (password, resetPasswordID) => {
  return await axios({
    method: "post",
    data: password,
    url: `/auth/password/reset?resetToken=${resetPasswordID}`,
  });
};

// Gửi thông tin đăng nhập
export const sendInfoLogin = async (loginInfo) => {
  try
  {
    return await axios({
    method: "post",
    data: loginInfo,
    url: `/api/authentication/login`,
    baseURL: BASE_URL,
  });
  } catch(error){
    return null;
  }



};


// Lấy thông tin tổ chức
export const getInfoAccount = async (accountID) => {
  const token = getToken();
  return await axios.get(`/account/${accountID}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
};

// API TỔ CHỨC
// Cập nhật thông tin tổ chức
export const updateInfoAccount = async (dataSend, accountID) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    data: dataSend,
    url: `/account/${accountID}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

// Thêm user vào tổ chức
export const inviteUser = async (user) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    data: user,
    url: `/api/authentication/users/invite`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
};

export const banUser = async (userId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    url: `/api/admin/users/${userId}/state/ban`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
};

export const unBanUser = async (userId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    url: `/api/admin/users/${userId}/state/unban`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
};

export const promoteUserToStudent = async (userId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    url: `/api/admin/users/${userId}/role/student`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
};

export const promoteUserToTeacher = async (userId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    url: `/api/admin/users/${userId}/role/teacher`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
};

export const promoteUserToAdmin = async (userId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    url: `/api/admin/users/${userId}/role/admin`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
};


export const sendInvitationEmail = async ({email,invitationLink}) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    url: `/api/email/invite`,
    data: {
      email: email,
      invitationLink: invitationLink
    },
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
};

export const getAllUser = async () => {
  const JWT_loginToken = getToken();
  return await axios.get(`/api/admin/users`, {
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });


};

export const deleteUser = async (userId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "delete",
    url: `/api/admin/users/${userId}/state`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
};

export const searchUser = async (queryParam) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    data: queryParam,
    url: `/clients/search`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

//domain
export const getDomain = async (accountID) => {
  const JWT_loginToken = getToken();
  return await axios.get(`/account/${accountID}/domains`, {
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

export const addNewDomain = async (domain, accountID) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    data: domain,
    url: `/accounts/${accountID}/domain`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

export const updateDomain = async (domain, domain_id) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    data: domain,
    url: `/domain/update/${domain_id}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

//check domain exist
export const checkDomain = async (host) => {
  return await axios({
    method: "post",
    data: host,
    url: `/domains/valid`,
  });
};

//my_account
export const getMyAccount = async (id) => {
  const JWT_loginToken = getToken();
  return await axios.get(`/client/${id}`, {
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

export const updateMyAccount = async (account, id) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    data: account,
    url: `/client/${id}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

// Content
export const getContentClient = async (id) => {
  const JWT_loginToken = getToken();
  return await axios.get(`/clients/${id}/contents`, {
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

//share content
export const shareContent = async (email, contentId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    data: email,
    url: `/contents/${contentId}/sharing`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

//H5P
export const getH5P = async () => {
  const JWT_loginToken = getToken();
  return await axios.get(`/h5p/new`, {
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

export const createContent = async (requestBody,parent) => {
  const JWT_loginToken = getToken();

  if (parent){
    return await axios({
      method: "post",
      data: requestBody,
      url: `/h5p/new?parent=${parent}`,
      headers: {
        Authorization: `Bearer ${JWT_loginToken}`,
      },
    });
  } else return await axios({
    method: "post",
    data: requestBody,
    url: `/h5p/new`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });


};

export const viewContent = async (content_id) => {
  const JWT_loginToken = getToken();
  return await axios.get(`/h5p/play/${content_id}`, {
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

export const getContentEdit = async (content_id) => {
  const JWT_loginToken = getToken();
  return await axios.get(`/h5p/edit/${content_id}`, {
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

export const editContent = async (requestBody, contentId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    data: requestBody,
    url: `/h5p/edit/${contentId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

export const deleteContent = async (contentId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    url: `/h5p/contents/${contentId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

//getRole
export const getRoles = async () => {
  const JWT_loginToken = getToken();
  return await axios.get(`/users-permissions/roles`, {
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
  });
};

export const generateFromResources = async (data) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    data: data,
    url: `/api/generator/resources/generate`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getAllGeneratingResources = async (parentId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/file-management/folder/resources/generating`,
    params: {
      folderId: parentId
    },
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const deleteGeneratingResource = async (resourceId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "delete",
    url: `/api/file-management/folder/resources/generating/${resourceId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const stopGeneratingResource = async (resourceId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    url: `/api/generator/resources/generate/stop/${resourceId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const createFolder = async (metadata) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    data: metadata,
    url: `/api/file-management/resources/folder`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const uploadFile = async (file, metadata) => {
  const formData = new FormData();

  if (file) {
    const blob = new Blob([file[1]]);
    formData.append("file", blob);
  }
  formData.append("metadata", metadata);
  
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    data: formData,
    url: `/api/file-management/resources/file`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
      "Content-Type": undefined,
    },
    baseURL: BASE_URL,
  });
}

export const getAllResourcesAvailableInFolder = async (parentId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/file-management/folder/resources/available`,
    params: {
      folderId: parentId
    },
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getAllFilesAvailableInFolder = async (parentId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/file-management/folder/files/available`,
    params: {
      folderId: parentId,
    },
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const moveResourceToTrash = async (resourceId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "delete",
    url: `/api/file-management/resources/${resourceId}/trash`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const updateResourceName = async (resourceId, newName) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    url: `/api/file-management/resources/${resourceId}/name`,
    data: {
      name: newName
    },
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getUserInfoFromEmail = async (email) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/profile/by-email`,
    params: {
      email: email
    },
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const addContributor = async (resourceId, contributorId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    url: `/api/file-management/resources/${resourceId}/contributor`,
    data: {
      userId: contributorId
    },
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const addViewer = async (resourceId, viewerId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    url: `/api/file-management/resources/${resourceId}/viewer`,
    data: {
      userId: viewerId
    },
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const deleteAccess = async (resourceId, userId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "delete",
    url: `/api/file-management/resources/${resourceId}/access`,
    data: {
      userId: userId
    },
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getResourcesByScope = async (scope) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/file-management/resources/scope`,
    params: {
      scope: scope
    },
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const restoreResources = async (resourceId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    url: `/api/file-management/resources/${resourceId}/restore`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const deleteResourcePermanent = async (resourceId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "delete",
    url: `/api/file-management/resources/${resourceId}/permanent`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getResourcesByFolderAndScope = async (folderId, scope) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/file-management/folders/${scope}/all`,
    params: {
      folderId: folderId,
    },
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const moveResources = async (resourceId, parentId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    url: `/api/file-management/resources/${resourceId}/parent`,
    data: {
      parentId: parentId,
    },
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const createQuiz = async (data) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    data: data,
    url: `/api/quiz/new`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const createDocument = async (data) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    data: data,
    url: `/api/file-management/resources/documents`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getQuiz = async (resourceId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/quiz/${resourceId}/latest`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getQuizForEdit = async (resourceId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/quiz/${resourceId}/edit`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getQuizInfo = async (resourceId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/quiz/${resourceId}/info`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const updateQuiz = async (updatedQuiz) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    data: updatedQuiz,
    url: `/api/quiz/${updatedQuiz.resourceId}/update`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const uploadMediaQuiz = async (resourceId, files, metadata, quiz) => {
  const formData = new FormData();

  if (files) {
    files.map(file => {
      const blob = new Blob([file]);
      formData.append("files", blob);
    });
  }
  formData.append("metadata", metadata);
  formData.append("quiz", quiz);
  
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    data: formData,
    url: `/api/quiz/${resourceId}/media/upload`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
      "Content-Type": undefined,
    },
    baseURL: BASE_URL,
  });
}

export const startTraditionalQuiz = async (resourceId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    url: `/api/quiz/${resourceId}/start`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getQuizByAttempt = async (attemptId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/quiz/attempt/${attemptId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const submitTraditionalQuiz = async (attemptId, data) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    data: data,
    url: `/api/quiz/submission/${attemptId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getAllQuizAttempts = async (resourceId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/quiz/${resourceId}/submission/all`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getQuizResult = async (attemptId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/quiz/result/${attemptId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getAllQuizVersions = async (resourceId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/quiz/versions/${resourceId}/all`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getQuizByVersion = async (versionId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/quiz/version/${versionId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const restoreQuiz = async (data) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    data: data,
    url: `/api/quiz/restore`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const updatePassword = async (data) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    data: data,
    url: `/api/authentication/users/password`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const updateProfile = async (file, data) => {
  const formData = new FormData();

  if (file) {
    const blob = new Blob([file]);
    formData.append("avatar", blob);
  }
  formData.append("update_info", data);

  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    data: formData,
    url: `/api/profile/`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
      "Content-Type": undefined,
    },
    baseURL: BASE_URL,
  });
}


export const updateProfileById = async (file, data, userId) => {
  const formData = new FormData();

  if (file) {
    const blob = new Blob([file]);
    formData.append("avatar", blob);
  }
  formData.append("update_info", data);

  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    data: formData,
    url: `/api/profile/${userId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
      "Content-Type": undefined,
    },
    baseURL: BASE_URL,
  });
}

export const getProfile = async () => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/profile/`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getProfileById = async (userId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/profile/${userId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const createThread = async (data) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    data: data,
    url: `/api/forum/thread/new`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getThread = async (threadId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/forum/thread/${threadId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const updateThread = async (threadId, data) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    data: data,
    url: `/api/forum/thread/${threadId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const deleteThread = async (threadId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "delete",
    url: `/api/forum/thread/${threadId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const createComment = async (data) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    data: data,
    url: `/api/forum/comment/new`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getComment = async (commentId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/forum/comment/${commentId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const updateComment = async (commentId, data) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    data: data,
    url: `/api/forum/comment/${commentId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const deleteComment = async (commentId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "delete",
    url: `/api/forum/comment/${commentId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const createTopic = async (data) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "post",
    data: data,
    url: `/api/forum/topic/new`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getTopic = async (topicId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/forum/topic/${topicId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const updateTopic = async (topicId, data) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    data: data,
    url: `/api/forum/topic/${topicId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const deleteTopic = async (topicId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "delete",
    url: `/api/forum/topic/${topicId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getAllTopicsInInstitution = async (
  page = null, pageSize = null
) => { 
  const JWT_loginToken = getToken();
  let params = null;
  let url = '/api/forum/topics/all';
  if (page !== null && pageSize !== null) {
    params = {
      page: page,
      pageSize: pageSize,
    };
    url = '/api/forum/topics';
  }

  return await axios({
    method: "get",
    url: url,
    params: params,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getAllThreadsInTopic = async (
  topicId, page = null, pageSize = null
) => {
  const JWT_loginToken = getToken();
  let params = null;
  let url = `/api/forum/topic/${topicId}/threads/all`;
  if (page !== null && pageSize !== null) {
    params = {
      page: page,
      pageSize: pageSize,
    };
    url = `/api/forum/topic/${topicId}/threads`;
  }

  return await axios({
    method: "get",
    url: url,
    params: params,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getAllCommentsInThread = async (
  threadId, page = null, pageSize = null
) => {
  const JWT_loginToken = getToken();
  let params = null;
  let url = `/api/forum/thread/${threadId}/comments/all`;
  if (page !== null && pageSize !== null) {
    params = {
      page: page,
      pageSize: pageSize,
    };
    url = `/api/forum/thread/${threadId}/comments`;
  }

  return await axios({
    method: "get",
    url: url,
    params: params,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const getNotiInfo = async () => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/profile/notifications`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const markReadNoti = async (notificationId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    url: `/api/profile/notification/${notificationId}/read`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const markUnreadNoti = async (notificationId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "put",
    url: `/api/profile/notification/${notificationId}/unread`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const deleteNoti = async (notificationId) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "delete",
    url: `/api/profile/notification/${notificationId}`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    baseURL: BASE_URL,
  });
}

export const downloadFile = async (resourceId, onDownloadProgress, signal) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/file-management/file/${resourceId}/download`,
    responseType: "blob",
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    signal: signal,
    onDownloadProgress: onDownloadProgress,
    baseURL: BASE_URL,
  });
}

export const getFileSize = async (resourceId, signal) => {
  const JWT_loginToken = getToken();
  return await axios({
    method: "get",
    url: `/api/file-management/file/${resourceId}/size`,
    headers: {
      Authorization: `Bearer ${JWT_loginToken}`,
    },
    signal: signal,
    baseURL: BASE_URL,
  });
}