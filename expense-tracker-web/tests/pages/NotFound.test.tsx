import { render, screen } from "@testing-library/react";
import { describe, it, expect } from "vitest";
import { BrowserRouter } from "react-router-dom";
import NotFound from "@/pages/NotFound";

describe("NotFound", () => {
  const renderNotFound = () => {
    return render(
      <BrowserRouter>
        <NotFound />
      </BrowserRouter>
    );
  };

  it("renders 404 error message", () => {
    renderNotFound();
    
    expect(screen.getByText("404")).toBeInTheDocument();
    expect(screen.getByText("Oops! Page not found.")).toBeInTheDocument();
  });

  it("renders link to dashboard", () => {
    renderNotFound();
    
    const link = screen.getByRole("link", { name: /go to dashboard/i });
    expect(link).toBeInTheDocument();
    expect(link).toHaveAttribute("href", "/");
  });

  it("applies correct styles to elements", () => {
    renderNotFound();
    
    const container = screen.getByText("404").parentElement;
    expect(container).toHaveStyle({
      textAlign: "center",
      marginTop: "100px",
    });

    const codeElement = screen.getByText("404");
    expect(codeElement).toHaveStyle({
      fontSize: "72px",
      margin: "0",
    });

    const messageElement = screen.getByText("Oops! Page not found.");
    expect(messageElement).toHaveStyle({
      fontSize: "20px",
      color: "#666",
    });

    const linkElement = screen.getByRole("link");
    expect(linkElement).toHaveStyle({
      marginTop: "20px",
      display: "inline-block",
      color: "#007bff",
      textDecoration: "none",
    });
  });

  it("has proper heading structure", () => {
    renderNotFound();
    
    const heading = screen.getByRole("heading", { level: 1 });
    expect(heading).toBeInTheDocument();
    expect(heading).toHaveTextContent("404");
  });
});
