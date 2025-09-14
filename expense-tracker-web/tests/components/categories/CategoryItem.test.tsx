import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { Accordion } from "@/components/ui/accordion";
import CategoryItem from "@/components/categories/CategoryItem";
import { toast } from "sonner";
import {
  deleteCategory,
  updateCategory,
  type Category,
} from "@/services/category-service";
import {
  getAllSubCategories,
  createSubCategory,
  updateSubCategory,
  deleteSubCategory,
} from "@/services/sub-category-service";

vi.mock("sonner", () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

vi.mock("@/services/category-service", () => ({
  deleteCategory: vi.fn(),
  updateCategory: vi.fn(),
}));

vi.mock("@/services/sub-category-service", () => ({
  getAllSubCategories: vi.fn(),
  createSubCategory: vi.fn(),
  updateSubCategory: vi.fn(),
  deleteSubCategory: vi.fn(),
}));

// mock SubCategoryItem to simplify
vi.mock("@/components/categories/SubCategoryItem", () => ({
  default: ({ sub, onUpdate, onDelete }: any) => (
    <div data-testid="subitem">
      <span>{sub.label}</span>
      <button onClick={() => onUpdate(sub.subCategoryId, "Updated")}>Update</button>
      <button onClick={() => onDelete(sub.subCategoryId)}>Delete</button>
    </div>
  ),
}));

function renderWithAccordion(ui: React.ReactNode) {
  return render(<Accordion type="single" collapsible>{ui}</Accordion>);
}

describe("CategoryItem", () => {
  const category: Category = {
    categoryId: 1,
    label: "Food",
    deletable: true,
  };

  const reloadCategories = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders category label", () => {
    renderWithAccordion(
      <CategoryItem category={category} reloadCategories={reloadCategories} />
    );
    expect(screen.getByText("Food")).toBeInTheDocument();
  });

  it("edits category label and saves", async () => {
    renderWithAccordion(
      <CategoryItem category={category} reloadCategories={reloadCategories} />
    );
    fireEvent.click(screen.getByRole("button", { name: /edit category/i }));

    const input = screen.getByDisplayValue("Food");
    fireEvent.change(input, { target: { value: "Groceries" } });

    fireEvent.click(screen.getByRole("button", { name: /save category label/i }));

    await waitFor(() => {
      expect(updateCategory).toHaveBeenCalledWith(1, { label: "Groceries" });
      expect(toast.success).toHaveBeenCalledWith("Category updated.");
      expect(reloadCategories).toHaveBeenCalled();
    });
  });

  it("deletes category", async () => {
    renderWithAccordion(
      <CategoryItem category={category} reloadCategories={reloadCategories} />
    );
    fireEvent.click(screen.getByRole("button", { name: /delete category/i }));

    await waitFor(() => {
      expect(deleteCategory).toHaveBeenCalledWith(1);
      expect(toast.success).toHaveBeenCalledWith("Deleted category");
      expect(reloadCategories).toHaveBeenCalled();
    });
  });

  it("loads subcategories on accordion open", async () => {
    (getAllSubCategories as vi.Mock).mockResolvedValue([
      { subCategoryId: 101, label: "Fruits", deletable: true },
    ]);

    renderWithAccordion(
      <CategoryItem category={category} reloadCategories={reloadCategories} />
    );

    fireEvent.click(screen.getByText("Food")); // accordion trigger

    await waitFor(() => {
      expect(getAllSubCategories).toHaveBeenCalledWith(1);
      expect(screen.getByText("Fruits")).toBeInTheDocument();
    });
  });

  it("creates subcategory", async () => {
    renderWithAccordion(
      <CategoryItem category={category} reloadCategories={reloadCategories} />
    );
    fireEvent.click(screen.getByText("Food")); // open accordion

    const input = screen.getByPlaceholderText("New subcategory");
    fireEvent.change(input, { target: { value: "Snacks" } });
    fireEvent.click(screen.getByRole("button", { name: /add/i }));

    await waitFor(() => {
      expect(createSubCategory).toHaveBeenCalledWith({ label: "Snacks", categoryId: 1 });
      expect(toast.success).toHaveBeenCalledWith("Subcategory created.");
    });
  });

  it("updates and deletes subcategory via SubCategoryItem", async () => {
    (getAllSubCategories as vi.Mock).mockResolvedValue([
      { subCategoryId: 101, label: "Fruits", deletable: true },
    ]);

    renderWithAccordion(
      <CategoryItem category={category} reloadCategories={reloadCategories} />
    );
    fireEvent.click(screen.getByText("Food")); // expand

    await waitFor(() => {
      expect(screen.getByText("Fruits")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("Update"));
    await waitFor(() => {
      expect(updateSubCategory).toHaveBeenCalledWith(101, { label: "Updated" });
      expect(toast.success).toHaveBeenCalledWith("Subcategory updated");
    });

    fireEvent.click(screen.getByText("Delete"));
    await waitFor(() => {
      expect(deleteSubCategory).toHaveBeenCalledWith(101);
      expect(toast.success).toHaveBeenCalledWith("Deleted subcategory");
    });
  });
});
