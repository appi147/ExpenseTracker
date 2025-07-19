export const setToken = (token: string) => {
  localStorage.setItem("auth_token", token);
};

export const getToken = () => localStorage.getItem("auth_token");

export const clearToken = () => localStorage.removeItem("auth_token");
