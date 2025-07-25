import axios from "axios";
import { getToken } from "../utils/auth";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

export const Theme = {
  SYSTEM: "SYSTEM",
  LIGHT: "LIGHT",
  DARK: "DARK",
} as const;

export type ThemeType = (typeof Theme)[keyof typeof Theme];

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

export async function updateBudget(payload: { amount: number }) {
  const res = await API.put("/user/budget", payload);
  return res.data;
}

export async function updateUserTheme(payload: { theme: ThemeType }) {
  const res = await API.put("/user/theme", payload);
  return res.data;
}

export default API;
