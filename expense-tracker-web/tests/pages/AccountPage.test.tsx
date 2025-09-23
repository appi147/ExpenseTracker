import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { BrowserRouter } from "react-router-dom";
import { toast } from "sonner";
import React from "react";
import AccountPage from "../../src/pages/AccountPage";
import * as api from "../../src/services/api";

// Mock the API
vi.mock("@/services/api", () => ({
  updateBudget: vi.fn(),
}));

// Mock toasts and alert
vi.mock("sonner", () => ({
  toast: {
    error: vi.fn(),
  },
}));

// Mock the auth context
const mockUser = {
  id: "1",
  email: "test@example.com",
  fullName: "Test User",
  pictureUrl: "https://example.com/avatar.jpg",
  budget: 1000,
};

const mockSetUser = vi.fn();

vi.mock("@/context/AuthContext", () => ({
  useAuth: () => ({
    user: mockUser,
    setUser: mockSetUser,
  }),
}));

describe("AccountPage", () => {
  const mockUpdatedUser = {
    ...mockUser,
    budget: 1500,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders user information when authenticated", () => {
    render(
      <BrowserRouter>
        <AccountPage />
      </BrowserRouter>
    );

    expect(screen.getByText("Account")).toBeInTheDocument();
    expect(screen.getByText("Test User")).toBeInTheDocument();
    expect(screen.getByText("test@example.com")).toBeInTheDocument();
    expect(screen.getByDisplayValue("1000")).toBeInTheDocument();
  });

  it("shows edit budget button when not in edit mode", () => {
    render(
      <BrowserRouter>
        <AccountPage />
      </BrowserRouter>
    );

    expect(screen.getByText("Edit Budget")).toBeInTheDocument();
  });

  it("enters edit mode when edit button is clicked", () => {
    render(
      <BrowserRouter>
        <AccountPage />
      </BrowserRouter>
    );

    fireEvent.click(screen.getByText("Edit Budget"));

    expect(screen.getByDisplayValue("1000")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /save/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /cancel/i })).toBeInTheDocument();
  });

  it("updates budget successfully", async () => {
    vi.mocked(api.updateBudget).mockResolvedValue(mockUpdatedUser);

    render(
      <BrowserRouter>
        <AccountPage />
      </BrowserRouter>
    );

    fireEvent.click(screen.getByText("Edit Budget"));

    const budgetInput = screen.getByDisplayValue("1000");
    fireEvent.change(budgetInput, { target: { value: "1500" } });

    fireEvent.click(screen.getByRole("button", { name: /save/i }));

    await waitFor(() => {
      expect(api.updateBudget).toHaveBeenCalledWith({ amount: 1500 });
    });

    await waitFor(() => {
      expect(mockSetUser).toHaveBeenCalledWith(mockUpdatedUser);
    });
  });

  it("handles budget update error", async () => {
    vi.mocked(api.updateBudget).mockRejectedValue(new Error("Update failed"));

    render(
      <BrowserRouter>
        <AccountPage />
      </BrowserRouter>
    );

    fireEvent.click(screen.getByText("Edit Budget"));

    const budgetInput = screen.getByDisplayValue("1000");
    fireEvent.change(budgetInput, { target: { value: "1500" } });

    fireEvent.click(screen.getByRole("button", { name: /save/i }));

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith("Failed to update budget.");
    });
  });

  it("cancels edit mode and reverts budget", () => {
    render(
      <BrowserRouter>
        <AccountPage />
      </BrowserRouter>
    );

    fireEvent.click(screen.getByText("Edit Budget"));

    const budgetInput = screen.getByDisplayValue("1000");
    fireEvent.change(budgetInput, { target: { value: "2000" } });

    fireEvent.click(screen.getByRole("button", { name: /cancel/i }));

    expect(screen.getByDisplayValue("1000")).toBeInTheDocument();
    expect(screen.getByText("Edit Budget")).toBeInTheDocument();
  });

  it("validates budget input to only accept numbers", () => {
    render(
      <BrowserRouter>
        <AccountPage />
      </BrowserRouter>
    );

    fireEvent.click(screen.getByText("Edit Budget"));

    const budgetInput = screen.getByDisplayValue("1000");
    
    // Try to enter invalid characters
    fireEvent.change(budgetInput, { target: { value: "abc" } });
    expect(budgetInput).toHaveValue("1000"); // Should not change

    // Try to enter valid number
    fireEvent.change(budgetInput, { target: { value: "1500" } });
    expect(budgetInput).toHaveValue("1500");
  });


});
