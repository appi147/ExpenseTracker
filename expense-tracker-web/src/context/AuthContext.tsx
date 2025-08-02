import { createContext, useContext, useEffect, useState } from "react";
import { getToken, setToken as storeToken, clearToken } from "../utils/auth";
import { getUserProfile, type ThemeType } from "../services/api";
import { useTheme } from "@/components/theme-provider";

interface User {
  fullName: string;
  email: string;
  pictureUrl: string;
  role: "USER" | "SUPER_USER";
  budget: number;
  preferredTheme: ThemeType;
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
  const [token, setToken] = useState<string | null>(getToken());
  const [user, setUser] = useState<User | null>(null);

  const { setTheme } = useTheme(); // ✅ always call hooks at top level

  const setAuthToken = (newToken: string) => {
    storeToken(newToken);
    setToken(newToken);
  };

  const logout = () => {
    clearToken();
    setToken(null);
    setUser(null);
  };

  // Fetch user profile when token is present
  useEffect(() => {
    if (token) {
      getUserProfile()
        .then(setUser)
        .catch((err) => {
          console.error("Failed to fetch user profile:", err);
          logout();
        });
    }
  }, [token]);

  // Sync theme when user preferredTheme changes
  useEffect(() => {
    const userTheme = user?.preferredTheme?.toLowerCase() as
      | "light"
      | "dark"
      | "system"
      | undefined;
    const currentStoredTheme = localStorage.getItem("vite-ui-theme");

    if (userTheme && userTheme !== currentStoredTheme) {
      setTheme(userTheme);
    }
  }, [user?.preferredTheme, setTheme]);

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
