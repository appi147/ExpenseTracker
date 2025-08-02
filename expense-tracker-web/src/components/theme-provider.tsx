import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import type { Theme, ThemeProviderProps, ThemeProviderState } from "./theme-types";

const initialState: ThemeProviderState = {
  theme: "system",
  setTheme: () => null,
};

const ThemeProviderContext = createContext<ThemeProviderState>(initialState);

export function ThemeProvider({
  children,
  defaultTheme = "system",
  storageKey = "vite-ui-theme",
  ...props
}: ThemeProviderProps) {
  const getInitialTheme = () => {
    const stored = localStorage.getItem(storageKey);
    if (stored === "light" || stored === "dark" || stored === "system") return stored;
    return defaultTheme;
  };

  const [theme, setTheme] = useState<Theme>(getInitialTheme);

  useEffect(() => {
    const root = window.document.documentElement;

    root.classList.remove("light", "dark");

    if (theme === "system") {
      const systemTheme = window.matchMedia("(prefers-color-scheme: dark)").matches
        ? "dark"
        : "light";

      root.classList.add(systemTheme);
      return;
    }

    root.classList.add(theme);
  }, [theme]);

  useEffect(() => {
    const handleStorage = () => {
      const storedTheme = localStorage.getItem(storageKey) as Theme;
      if (storedTheme && storedTheme !== theme) {
        setTheme(storedTheme);
      }
    };

    window.addEventListener("storage", handleStorage);
    handleStorage(); // initial sync

    return () => window.removeEventListener("storage", handleStorage);
  }, [storageKey, theme]);

  const setAndStoreTheme = useCallback(
    (theme: Theme) => {
      localStorage.setItem(storageKey, theme);
      setTheme(theme);
    },
    [storageKey],
  );

  const value = useMemo(
    () => ({
      theme,
      setTheme: setAndStoreTheme,
    }),
    [theme, setAndStoreTheme],
  );

  return (
    <ThemeProviderContext.Provider {...props} value={value}>
      {children}
    </ThemeProviderContext.Provider>
  );
}

export const useTheme = () => {
  const context = useContext(ThemeProviderContext);
  if (context === undefined) throw new Error("useTheme must be used within a ThemeProvider");
  return context;
};
