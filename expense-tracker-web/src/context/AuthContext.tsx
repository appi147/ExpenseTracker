import { createContext, useContext, useEffect, useState } from "react";
import { getToken, setToken, clearToken } from "../utils/auth";
import { getUserProfile } from "../services/api";

interface User {
  fullName: string;
  email: string;
  pictureUrl: string;
}

interface AuthContextType {
  token: string | null;
  setAuthToken: (token: string) => void;
  logout: () => void;
  user: User | null;
  setUser: (user: User | null) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [token, setTokenState] = useState<string | null>(getToken());
  const [user, setUser] = useState<User | null>(null);

  const setAuthToken = (newToken: string) => {
    setToken(newToken);
    setTokenState(newToken);
  };

  const logout = () => {
    clearToken();
    setTokenState(null);
    setUser(null);
  };

  useEffect(() => {
    if (token) {
      getUserProfile()
        .then(setUser)
        .catch(() => logout());
    }
  }, [token]);

  return (
    <AuthContext.Provider value={{ token, setAuthToken, logout, user, setUser }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
};
