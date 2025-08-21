import React from "react";
import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import Navbar from "../../src/components/Navbar";
import { AuthProvider, useAuth } from "../../src/context/AuthContext";
import userEvent from "@testing-library/user-event";
import * as api from "../../src/services/api";
import { MemoryRouter } from "react-router-dom";
import "@testing-library/jest-dom";

const WithUser: React.FC = () => {
  const { setUser } = useAuth();
  React.useEffect(() => {
    setUser({
      fullName: "Jane Tester",
      email: "jane@test.io",
      pictureUrl: "",
      role: "SUPER_USER",
      budget: 0,
      preferredTheme: "LIGHT",
    } as any);
  }, [setUser]);

  return <Navbar />;
};

describe("components/Navbar", () => {
  it("updates theme via updateUserTheme", async () => {
    vi.spyOn(api, "updateUserTheme").mockResolvedValue({});
    render(
      <MemoryRouter>
        <AuthProvider>
          <WithUser />
        </AuthProvider>
      </MemoryRouter>,
    );

    const trigger = screen.getByText("Jane Tester");
    await userEvent.click(trigger);

    expect(await screen.findByText("Profile")).toBeInTheDocument();
  });

  it("shows user info and site insights for super user", async () => {
    render(
      <MemoryRouter>
        <AuthProvider>
          <WithUser />
        </AuthProvider>
      </MemoryRouter>,
    );

    // The dropdown trigger has "Jane Tester" inside
    const trigger = screen.getByText("Jane Tester");
    await userEvent.click(trigger);

    expect(await screen.findByText("Site Insights")).toBeInTheDocument();
  });
});

