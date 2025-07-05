import { useEffect, useMemo, useState } from "react";
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
import { columns } from "@/components/expenses/columns";
import { getFilteredExpenses } from "@/services/expense-service";

export default function Expenses() {
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);

  const [filters, setFilters] = useState({
    dateRange: null as { from: string; to: string } | null,
    categoryId: null as number | null,
    subCategoryId: null as number | null,
    paymentTypeCode: "",
  });

  const fetchExpenses = async () => {
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
  };

  useEffect(() => {
    fetchExpenses();
  }, [filters, page]);

  const categories = useMemo(() => {
    const map = new Map<number, string>();
    expenses.forEach((e) =>
      map.set(e.subCategory.category.categoryId, e.subCategory.category.label)
    );
    return Array.from(map.entries());
  }, [expenses]);

  const subCategories = useMemo(() => {
    const map = new Map<number, string>();
    expenses.forEach((e) =>
      map.set(e.subCategory.subCategoryId, e.subCategory.label)
    );
    return Array.from(map.entries());
  }, [expenses]);

  const paymentTypes = useMemo(() => {
    const map = new Map<string, string>();
    expenses.forEach((e) => map.set(e.paymentType.code, e.paymentType.label));
    return Array.from(map.entries());
  }, [expenses]);

  return (
    <div className="p-4 space-y-4">
      <Card>
        <CardContent className="p-4 grid grid-cols-1 md:grid-cols-5 gap-4">
          <DateRangePicker
            value={filters.dateRange}
            onChange={(range) =>
              setFilters((prev) => ({ ...prev, dateRange: range }))
            }
            className="col-span-1 md:col-span-2"
          />

          <Select
            value={filters.categoryId?.toString() ?? ""}
            onValueChange={(val) =>
              setFilters((prev) => ({ ...prev, categoryId: Number(val) }))
            }
          >
            <SelectTrigger>
              <SelectValue placeholder="Category" />
            </SelectTrigger>
            <SelectContent>
              {categories.map(([id, label]) => (
                <SelectItem key={id} value={id.toString()}>
                  {label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Select
            value={filters.subCategoryId?.toString() ?? ""}
            onValueChange={(val) =>
              setFilters((prev) => ({ ...prev, subCategoryId: Number(val) }))
            }
          >
            <SelectTrigger>
              <SelectValue placeholder="Subcategory" />
            </SelectTrigger>
            <SelectContent>
              {subCategories.map(([id, label]) => (
                <SelectItem key={id} value={id.toString()}>
                  {label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Select
            value={filters.paymentTypeCode}
            onValueChange={(val) =>
              setFilters((prev) => ({ ...prev, paymentTypeCode: val }))
            }
          >
            <SelectTrigger>
              <SelectValue placeholder="Payment Type" />
            </SelectTrigger>
            <SelectContent>
              {paymentTypes.map(([code, label]) => (
                <SelectItem key={code} value={code}>
                  {label}
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
      </Card>

      <DataTable columns={columns} data={expenses} />

      <div className="flex justify-between items-center py-2 px-4">
        <div className="text-sm">
          Page {page + 1} of {totalPages}
        </div>
        <div className="space-x-2">
          <button
            className="px-3 py-1 rounded bg-gray-100 hover:bg-gray-200 text-sm"
            onClick={() => setPage((p) => Math.max(p - 1, 0))}
            disabled={page === 0}
          >
            Prev
          </button>
          <button
            className="px-3 py-1 rounded bg-gray-100 hover:bg-gray-200 text-sm"
            onClick={() => setPage((p) => p + 1)}
            disabled={page + 1 >= totalPages}
          >
            Next
          </button>
        </div>
      </div>
    </div>
  );
}
