import "./App.css";
import AppRoutes from "./routes/AppRoutes";
import { ThemeProvider } from "@/components/theme-provider";
import { Toaster } from "sonner";

const App = () => {
  return (
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
      <AppRoutes />
      <Toaster richColors position="top-right" />
    </ThemeProvider>
  );
};

export default App;
