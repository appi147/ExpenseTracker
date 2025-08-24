import { render, screen } from "@testing-library/react";
import { describe, it, expect, vi } from "vitest";
import { MemoryRouter } from "react-router-dom";
import { AuthProvider } from "@/context/AuthContext";
import AppRoutes from "@/routes/AppRoutes";

// Mock all page components
vi.mock("@/pages/Dashboard", () => ({
  default: () => <div data-testid="dashboard">Dashboard</div>,
}));

vi.mock("@/pages/Login", () => ({
  default: () => <div data-testid="login">Login</div>,
}));

vi.mock("@/pages/NotFound", () => ({
  default: () => <div data-testid="not-found">NotFound</div>,
}));

vi.mock("@/pages/AccountPage", () => ({
  default: () => <div data-testid="account">Account</div>,
}));

vi.mock("@/pages/Categories", () => ({
  default: () => <div data-testid="categories">Categories</div>,
}));

vi.mock("@/pages/Expenses", () => ({
  default: () => <div data-testid="expenses">Expenses</div>,
}));

vi.mock("@/pages/MonthlyInsights", () => ({
  default: () => <div data-testid="monthly-insights">MonthlyInsights</div>,
}));

vi.mock("@/pages/SiteWideInsights", () => ({
  default: () => <div data-testid="site-wide-insights">SiteWideInsights</div>,
}));

vi.mock("@/pages/MonthlyTrendsPage", () => ({
  default: () => <div data-testid="monthly-trends">MonthlyTrendsPage</div>,
}));

// Mock Layout component
vi.mock("@/components/Layout", () => ({
  default: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="layout">{children}</div>
  ),
}));

// Mock ProtectedRoute component
vi.mock("@/components/ProtectedRoute", () => ({
  default: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="protected-route">{children}</div>
  ),
}));

describe("AppRoutes", () => {
  const renderWithRouter = (initialEntries: string[] = ["/"]) => {
    return render(
      <AuthProvider>
        <MemoryRouter initialEntries={initialEntries}>
          <AppRoutes />
        </MemoryRouter>
      </AuthProvider>
    );
  };

  it("renders login page at /login", () => {
    renderWithRouter(["/login"]);
    expect(screen.getByTestId("login")).toBeInTheDocument();
  });

  it("renders dashboard at root path", () => {
    renderWithRouter(["/"]);
    expect(screen.getByTestId("protected-route")).toBeInTheDocument();
    expect(screen.getByTestId("layout")).toBeInTheDocument();
    expect(screen.getByTestId("dashboard")).toBeInTheDocument();
  });

  it("renders profile page at /profile", () => {
    renderWithRouter(["/profile"]);
    expect(screen.getByTestId("protected-route")).toBeInTheDocument();
    expect(screen.getByTestId("layout")).toBeInTheDocument();
    expect(screen.getByTestId("account")).toBeInTheDocument();
  });

  it("renders categories page at /categories", () => {
    renderWithRouter(["/categories"]);
    expect(screen.getByTestId("protected-route")).toBeInTheDocument();
    expect(screen.getByTestId("layout")).toBeInTheDocument();
    expect(screen.getByTestId("categories")).toBeInTheDocument();
  });

  it("renders expenses page at /expenses/list", () => {
    renderWithRouter(["/expenses/list"]);
    expect(screen.getByTestId("protected-route")).toBeInTheDocument();
    expect(screen.getByTestId("layout")).toBeInTheDocument();
    expect(screen.getByTestId("expenses")).toBeInTheDocument();
  });

  it("renders monthly insights page at /expenses/insights", () => {
    renderWithRouter(["/expenses/insights"]);
    expect(screen.getByTestId("protected-route")).toBeInTheDocument();
    expect(screen.getByTestId("layout")).toBeInTheDocument();
    expect(screen.getByTestId("monthly-insights")).toBeInTheDocument();
  });

  it("renders site wide insights page at /insights", () => {
    renderWithRouter(["/insights"]);
    expect(screen.getByTestId("protected-route")).toBeInTheDocument();
    expect(screen.getByTestId("layout")).toBeInTheDocument();
    expect(screen.getByTestId("site-wide-insights")).toBeInTheDocument();
  });

  it("renders monthly trends page at /expenses/trends", () => {
    renderWithRouter(["/expenses/trends"]);
    expect(screen.getByTestId("protected-route")).toBeInTheDocument();
    expect(screen.getByTestId("layout")).toBeInTheDocument();
    expect(screen.getByTestId("monthly-trends")).toBeInTheDocument();
  });

  it("renders not found page for unknown routes", () => {
    renderWithRouter(["/unknown-route"]);
    expect(screen.getByTestId("layout")).toBeInTheDocument();
    expect(screen.getByTestId("not-found")).toBeInTheDocument();
  });

  it("renders main container with correct classes", () => {
    renderWithRouter(["/"]);
    const mainElement = screen.getByRole("main");
    expect(mainElement).toHaveClass("min-h-screen", "w-full", "bg-background");
  });
});
