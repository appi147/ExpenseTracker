import { getToken } from '../utils/auth';
import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:8080/api',
});

API.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const getUserProfile = async () => {
  const response = await API.post('/user/login');
  return response.data;
};

export default API;
