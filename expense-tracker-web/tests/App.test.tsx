import { render, screen } from "@testing-library/react";
import { describe, it, expect, vi } from "vitest";
import { BrowserRouter } from "react-router-dom";
import { AuthProvider } from "@/context/AuthContext";
import { GoogleOAuthProvider } from "@react-oauth/google";
import App from "@/App";

// Mock the routes to avoid complex routing tests
vi.mock("@/routes/AppRoutes", () => ({
  default: () => <div data-testid="app-routes">App Routes</div>,
}));

// Mock sonner toaster
vi.mock("sonner", () => ({
  Toaster: () => <div data-testid="toaster">Toaster</div>,
}));

describe("App", () => {
  it("renders with all providers and components", () => {
    render(
      <GoogleOAuthProvider clientId="test-client-id">
        <AuthProvider>
          <BrowserRouter>
            <App />
          </BrowserRouter>
        </AuthProvider>
      </GoogleOAuthProvider>
    );

    expect(screen.getByTestId("app-routes")).toBeInTheDocument();
    expect(screen.getByTestId("toaster")).toBeInTheDocument();
  });


});
