import { render, screen } from "@testing-library/react";
import { describe, it, expect } from "vitest";
import { Separator } from "@/components/ui/separator";

describe("Separator", () => {
  it("renders horizontal separator by default", () => {
    render(<Separator data-testid="separator" />);
    
    const separator = screen.getByTestId("separator");
    expect(separator).toBeInTheDocument();
    expect(separator).toHaveAttribute("data-slot", "separator");
    expect(separator).toHaveClass("bg-border", "shrink-0", "h-px", "w-full");
  });

  it("renders vertical separator when orientation is vertical", () => {
    render(<Separator orientation="vertical" data-testid="separator" />);
    
    const separator = screen.getByTestId("separator");
    expect(separator).toHaveClass("bg-border", "shrink-0", "w-px", "h-full");
  });

  it("applies custom className", () => {
    render(<Separator className="custom-class" data-testid="separator" />);
    
    const separator = screen.getByTestId("separator");
    expect(separator).toHaveClass("custom-class");
  });

  it("passes through additional props", () => {
    render(
      <Separator 
        data-testid="separator" 
        aria-label="section separator"
        role="separator"
      />
    );
    
    const separator = screen.getByTestId("separator");
    expect(separator).toHaveAttribute("aria-label", "section separator");
    expect(separator).toHaveAttribute("role", "separator");
  });

  it("combines orientation and decorative props correctly", () => {
    render(
      <Separator 
        orientation="vertical" 
        decorative={false} 
        data-testid="separator" 
      />
    );
    
    const separator = screen.getByTestId("separator");
    expect(separator).toHaveClass("w-px", "h-full");
  });
});
