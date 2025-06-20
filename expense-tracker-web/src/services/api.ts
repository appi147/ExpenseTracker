import axios from "axios";
import { getToken } from "../utils/auth";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

const API = axios.create({
  baseURL: API_URL,
});

API.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const getUserProfile = async () => {
  const response = await API.post("/user/login");
  return response.data;
};

export default API;
