import { useEffect, useMemo, useState, useCallback } from "react";
import { DataTable } from "@/components/ui/data-table";
import { Card, CardContent } from "@/components/ui/card";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";
import type { Expense } from "@/components/expenses/types";
import { DateRangePicker } from "@/components/ui/date-range-picker";
import { getExpenseColumns } from "@/components/expenses/columns";
import {
  getFilteredExpenses,
  deleteExpense,
  updateExpenseAmount,
  exportExpenses,
} from "@/services/expense-service";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { getAllCategories, type Category } from "@/services/category-service";
import { getAllSubCategories, type SubCategory } from "@/services/sub-category-service";
import { getAllPaymentTypes, type PaymentType } from "@/services/payment-type-service";

export default function Expenses() {
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  const [categories, setCategories] = useState<Category[]>([]);
  const [subCategories, setSubCategories] = useState<SubCategory[]>([]);
  const [paymentTypes, setPaymentTypes] = useState<PaymentType[]>([]);

  const [exportOpen, setExportOpen] = useState(false);
  const [exportType, setExportType] = useState<"full" | "range">("full");
  const [exportDateRange, setExportDateRange] = useState<{ from: string; to: string } | null>(null);

  type Filters = {
    dateRange: { from: string; to: string } | null;
    categoryId: number | null;
    subCategoryId: number | null;
    paymentTypeCode: string;
  };

  const [filters, setFilters] = useState<Filters>({
    dateRange: null,
    categoryId: null,
    subCategoryId: null,
    paymentTypeCode: "",
  });

  useEffect(() => {
    getAllCategories().then(setCategories);
    getAllPaymentTypes().then(setPaymentTypes);
  }, []);

  useEffect(() => {
    if (filters.categoryId) {
      getAllSubCategories(filters.categoryId).then(setSubCategories);
    } else {
      setSubCategories([]);
    }
  }, [filters.categoryId]);

  const fetchExpenses = useCallback(async () => {
    const response = await getFilteredExpenses({
      categoryId: filters.categoryId ?? undefined,
      subCategoryId: filters.subCategoryId ?? undefined,
      paymentTypeCode: filters.paymentTypeCode,
      dateFrom: filters.dateRange?.from,
      dateTo: filters.dateRange?.to,
      page,
      size: pageSize,
    });
    setExpenses(response.content);
    setTotalPages(response.totalPages);
  }, [filters, page, pageSize]);

  useEffect(() => {
    fetchExpenses();
  }, [filters, page, pageSize, fetchExpenses]);

  useEffect(() => {
    setPage(0);
  }, [filters, pageSize]);

  const handleDelete = useCallback(
    async (id: number) => {
      if (confirm("Are you sure you want to delete this expense?")) {
        await deleteExpense(id);
        fetchExpenses();
      }
    },
    [fetchExpenses],
  );

  const handleEditAmount = useCallback(
    async (expense: Expense) => {
      const newAmount = prompt("Enter new amount", expense.amount.toString());
      if (newAmount) {
        const amount = parseFloat(newAmount);
        if (!isNaN(amount)) {
          await updateExpenseAmount(expense.expenseId, amount);
          fetchExpenses();
        }
      }
    },
    [fetchExpenses],
  );

  const handleExport = (format: "csv" | "excel") => {
    exportExpenses({
      format,
      dateFrom: exportType === "range" ? exportDateRange?.from : undefined,
      dateTo: exportType === "range" ? exportDateRange?.to : undefined,
    });
  };

  const columns = useMemo(
    () => getExpenseColumns(handleEditAmount, handleDelete),
    [handleEditAmount, handleDelete],
  );

  return (
    <div className="p-4 space-y-4">
      <Card>
        <CardContent className="p-4 grid grid-cols-1 md:grid-cols-5 gap-4">
          <DateRangePicker
            value={filters.dateRange}
            onChange={(range) => setFilters((prev) => ({ ...prev, dateRange: range }))}
            className="col-span-1 md:col-span-2"
          />

          <Select
            aria-label="Category"
            value={filters.categoryId?.toString() ?? ""}
            onValueChange={(val) =>
              setFilters((prev) => ({
                ...prev,
                categoryId: val === "" ? null : Number(val),
                subCategoryId: null,
              }))
            }
          >
            <SelectTrigger aria-label="Category">
              <SelectValue placeholder="Category" />
            </SelectTrigger>
            <SelectContent>
              {categories.map((cat) => (
                <SelectItem key={cat.categoryId} value={cat.categoryId.toString()}>
                  {cat.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Select
            aria-label="Subcategory"
            value={filters.subCategoryId?.toString() ?? ""}
            onValueChange={(val) =>
              setFilters((prev) => ({
                ...prev,
                subCategoryId: val === "" ? null : Number(val),
              }))
            }
          >
            <SelectTrigger aria-label="Subcategory">
              <SelectValue placeholder="Subcategory" />
            </SelectTrigger>
            <SelectContent>
              {subCategories.map((sub) => (
                <SelectItem key={sub.subCategoryId} value={sub.subCategoryId.toString()}>
                  {sub.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Select
            aria-label="Payment Type"
            value={filters.paymentTypeCode}
            onValueChange={(val) => setFilters((prev) => ({ ...prev, paymentTypeCode: val }))}
          >
            <SelectTrigger aria-label="Payment Type">
              <SelectValue placeholder="Payment Type" />
            </SelectTrigger>
            <SelectContent>
              {paymentTypes.map((pt) => (
                <SelectItem key={pt.code} value={pt.code}>
                  {pt.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </CardContent>
        <CardContent className="px-4 pb-4">
          <button
            onClick={() =>
              setFilters({
                dateRange: null,
                categoryId: null,
                subCategoryId: null,
                paymentTypeCode: "",
              })
            }
            className="text-sm px-3 py-1 bg-red-100 hover:bg-red-200 text-red-800 rounded"
          >
            Reset Filters
          </button>
        </CardContent>
        <CardContent className="px-4 pb-4">
          <Button variant="outline" className="text-sm" onClick={() => setExportOpen(true)}>
            Export
          </Button>
        </CardContent>

        <Dialog open={exportOpen} onOpenChange={setExportOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Export Expenses</DialogTitle>
            </DialogHeader>

            <RadioGroup
              value={exportType}
              onValueChange={(val) => setExportType(val as "full" | "range")}
            >
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="full" id="full" />
                <Label htmlFor="full">Full Export</Label>
              </div>
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="range" id="range" />
                <Label htmlFor="range">Specific Date Range</Label>
              </div>
            </RadioGroup>

            {exportType === "range" && (
              <div className="pt-4">
                <Label className="mb-1 block">Select Date Range</Label>
                <DateRangePicker value={exportDateRange} onChange={setExportDateRange} />
              </div>
            )}

            <DialogFooter className="gap-2 pt-4">
              <Button onClick={() => handleExport("csv")}>Export as CSV</Button>
              <Button onClick={() => handleExport("excel")}>Export as Excel</Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </Card>

      {expenses.length === 0 ? (
        <div className="text-center text-muted-foreground py-10">No expenses found.</div>
      ) : (
        <DataTable columns={columns} data={expenses} />
      )}

      <div className="flex justify-between items-center py-2 px-4">
        <div className="text-sm">
          Page {page + 1} of {totalPages}
        </div>
        <div className="flex items-center space-x-2">
          <Label className="text-sm text-muted-foreground">Rows per page:</Label>
          <Select
            value={pageSize.toString()}
            onValueChange={(val) => {
              setPageSize(Number(val));
              setPage(0);
            }}
          >
            <SelectTrigger className="w-20">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {[10, 25, 50, 100].map((size) => (
                <SelectItem key={size} value={size.toString()}>
                  {size}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        <div className="space-x-2">
          <button
            className="px-3 py-1 rounded text-sm 
                        bg-gray-100 hover:bg-gray-200 
                        dark:bg-gray-800 dark:hover:bg-gray-700 
                        dark:text-gray-100 text-gray-900 
                        disabled:opacity-50"
            onClick={() => setPage((p) => Math.max(p - 1, 0))}
            disabled={page <= 0}
          >
            Prev
          </button>

          <button
            className="px-3 py-1 rounded text-sm 
                        bg-gray-100 hover:bg-gray-200 
                        dark:bg-gray-800 dark:hover:bg-gray-700 
                        dark:text-gray-100 text-gray-900 
                        disabled:opacity-50"
            onClick={() => setPage((p) => Math.min(p + 1, totalPages - 1))}
            disabled={page >= totalPages - 1}
          >
            Next
          </button>
        </div>
      </div>
    </div>
  );
}
