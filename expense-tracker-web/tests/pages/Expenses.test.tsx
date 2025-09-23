import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { render, screen, fireEvent, waitFor, within } from "@testing-library/react";
import Expenses from "../../src/pages/Expenses";
import * as expSvc from "../../src/services/expense-service";
import * as catSvc from "../../src/services/category-service";
import * as subCatSvc from "../../src/services/sub-category-service";
import * as ptSvc from "../../src/services/payment-type-service";
import { MemoryRouter } from "react-router-dom";

describe("pages/Expenses", () => {
  const mockExpense = {
    expenseId: 1,
    amount: 10,
    date: "2024-01-01",
    comments: "",
    paymentType: { code: "CASH", label: "Cash" },
    subCategory: {
      subCategoryId: 2,
      label: "Lunch",
      category: { categoryId: 1, label: "Food" },
    },
  };

  beforeEach(() => {
    vi.spyOn(expSvc, "getFilteredExpenses").mockResolvedValue({
      content: [mockExpense],
      totalPages: 1,
    } as any);
    vi.spyOn(expSvc, "deleteExpense").mockResolvedValue();
    vi.spyOn(expSvc, "updateExpenseAmount").mockResolvedValue();
    vi.spyOn(expSvc, "exportExpenses").mockResolvedValue();

    vi.spyOn(catSvc, "getAllCategories").mockResolvedValue([
      { categoryId: 1, label: "Food" },
      { categoryId: 99, label: "Travel" },
    ] as any);

    vi.spyOn(subCatSvc, "getAllSubCategories").mockResolvedValue([
      { subCategoryId: 2, label: "Lunch", category: { categoryId: 1, label: "Food" } },
    ] as any);

    vi.spyOn(ptSvc, "getAllPaymentTypes").mockResolvedValue([
      { code: "CASH", label: "Cash" },
      { code: "CARD", label: "Card" },
    ] as any);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("renders table with fetched data", async () => {
    render(
      <MemoryRouter>
        <Expenses />
      </MemoryRouter>,
    );

    await screen.findByText("Lunch");
    expect(screen.getByText("Page 1 of 1")).toBeInTheDocument();
  });

  it("shows empty state when no data", async () => {
    (expSvc.getFilteredExpenses as vi.Mock).mockResolvedValueOnce({
      content: [],
      totalPages: 0,
    });

    render(
      <MemoryRouter>
        <Expenses />
      </MemoryRouter>,
    );

    await screen.findByText("No expenses found.");
  });

  it("handles delete confirmation", async () => {
    vi.spyOn(window, "confirm").mockReturnValue(true);

    render(
      <MemoryRouter>
        <Expenses />
      </MemoryRouter>,
    );

    await screen.findByText("Lunch");
    fireEvent.click(screen.getByLabelText("Delete"));

    await waitFor(() => expect(expSvc.deleteExpense).toHaveBeenCalledWith(1));
  });

  it("handles edit amount - valid, invalid, cancel", async () => {
    const promptSpy = vi.spyOn(window, "prompt");

    render(
      <MemoryRouter>
        <Expenses />
      </MemoryRouter>,
    );

    await screen.findByText("Lunch");

    // valid update
    promptSpy.mockReturnValue("20");
    fireEvent.click(screen.getByLabelText("Edit"));
    await waitFor(() => expect(expSvc.updateExpenseAmount).toHaveBeenCalledWith(1, 20));

    // invalid update
    promptSpy.mockReturnValue("not-a-number");
    fireEvent.click(screen.getByLabelText("Edit"));
    expect(expSvc.updateExpenseAmount).toHaveBeenCalledTimes(1); // still only 1 call

    // cancel
    promptSpy.mockReturnValue(null);
    fireEvent.click(screen.getByLabelText("Edit"));
    expect(expSvc.updateExpenseAmount).toHaveBeenCalledTimes(1);
  });

  it("handles export full and range", async () => {
    render(
      <MemoryRouter>
        <Expenses />
      </MemoryRouter>,
    );

    await screen.findByText("Lunch");
    fireEvent.click(screen.getByText("Export"));

    // full export
    fireEvent.click(screen.getByText("Export as CSV"));
    await waitFor(() => expect(expSvc.exportExpenses).toHaveBeenCalled());

    // switch to range
    fireEvent.click(screen.getByLabelText("Specific Date Range"));
    fireEvent.click(screen.getByText("Export as Excel"));
    await waitFor(() => expect(expSvc.exportExpenses).toHaveBeenCalledTimes(2));
  });

  it("filters by category, subcategory, payment type, and reset", async () => {
    render(
      <MemoryRouter>
        <Expenses />
      </MemoryRouter>,
    );

    // wait for table data to load
    await screen.findByText("Lunch");

    // --- select category ---
    fireEvent.click(screen.getByRole("combobox", { name: "Category" }));
    // get the dropdown itself
    const selectContent = await screen.findByRole("listbox");
    // select the "Food" option inside the listbox
    const foodOption = within(selectContent).getByText("Food");
    fireEvent.click(foodOption);
    await waitFor(() => expect(expSvc.getFilteredExpenses).toHaveBeenCalledTimes(2));

    // --- select subcategory ---
    fireEvent.click(screen.getByRole("combobox", { name: "Subcategory" }));
    const subCategoryContent = await screen.findByRole("listbox");
    const lunchOption = within(subCategoryContent).getByText("Lunch");
    fireEvent.click(lunchOption);
    await waitFor(() => expect(expSvc.getFilteredExpenses).toHaveBeenCalledTimes(3));

    // --- select payment type ---
    fireEvent.click(screen.getByRole("combobox", { name: "Payment Type" }));
    const paymentTypeContent = await screen.findByRole("listbox");
    const cashOption = within(paymentTypeContent).getByText("Cash");
    fireEvent.click(cashOption);
    await waitFor(() => expect(expSvc.getFilteredExpenses).toHaveBeenCalledTimes(4));

    // --- reset filters ---
    fireEvent.click(screen.getByText("Reset Filters"));
    await waitFor(() => expect(expSvc.getFilteredExpenses).toHaveBeenCalledTimes(5));
  });

  it("handles pagination next/prev buttons", async () => {
    // override default so every call returns totalPages=2
    (expSvc.getFilteredExpenses as vi.Mock).mockResolvedValue({
      content: [mockExpense],
      totalPages: 2,
    });

    render(
      <MemoryRouter>
        <Expenses />
      </MemoryRouter>,
    );

    await screen.findByText("Lunch");
    const nextBtn = screen.getByText("Next");
    const prevBtn = screen.getByText("Prev");

    expect(prevBtn).toBeDisabled();
    expect(nextBtn).not.toBeDisabled();

    fireEvent.click(nextBtn);
    await waitFor(() => expect(expSvc.getFilteredExpenses).toHaveBeenCalledTimes(2));
  });
});

