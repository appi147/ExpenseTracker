import { createContext, useContext, useEffect, useMemo, useState } from "react";
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
  readonly token: string | null;
  readonly setAuthToken: (token: string) => void;
  readonly logout: () => void;
  readonly user: User | null;
  readonly setUser: (user: User | null) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { readonly children: React.ReactNode }) => {
  const [token, setToken] = useState<string | null>(getToken());
  const [user, setUser] = useState<User | null>(null);

  const { setTheme } = useTheme();

  const setAuthToken = (newToken: string) => {
    storeToken(newToken);
    setToken(newToken);
  };

  const logout = () => {
    clearToken();
    setToken(null);
    setUser(null);
  };

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

  const value = useMemo<AuthContextType>(
    () => ({
      token,
      setAuthToken,
      logout,
      user,
      setUser,
    }),
    [token, user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = (): AuthContextType => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
};
