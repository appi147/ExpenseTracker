import { useEffect, useState } from "react";
import { DataTable } from "@/components/ui/data-table";
import type { ColumnDef } from "@tanstack/react-table";
import { X } from "lucide-react";
import {
  getRecurringExpenses,
  deleteRecurringExpense,
  type RecurringExpense,
} from "@/services/recurring-expense-service";

export default function RecurringExpensePage() {
  const [expenses, setExpenses] = useState<RecurringExpense[]>([]);

  useEffect(() => {
    loadExpenses();
  }, []);

  const loadExpenses = async () => {
    const data = await getRecurringExpenses();
    setExpenses(data);
  };

  const handleDelete = async (id: number) => {
    await deleteRecurringExpense(id);
    await loadExpenses();
  };

  const columns: ColumnDef<RecurringExpense>[] = [
    {
      accessorKey: "subCategory.category.label",
      header: "Category",
      cell: ({ row }) => row.original.subCategory.category.label,
    },
    {
      accessorKey: "subCategory.label",
      header: "Sub Category",
      cell: ({ row }) => row.original.subCategory.label,
    },
    {
      accessorKey: "amount",
      header: "Amount",
      cell: ({ row }) => `₹${row.original.amount.toFixed(2)}`,
    },
    {
      accessorKey: "dayOfMonth",
      header: "Day",
    },
    {
      accessorKey: "paymentType.label",
      header: "Payment Type",
      cell: ({ row }) => row.original.paymentType.label,
    },
    {
      accessorKey: "comments",
      header: "Comments",
      cell: ({ row }) => row.original.comments || "-",
    },
    {
      id: "actions",
      header: "Actions",
      cell: ({ row }) => (
        <button
          onClick={() => handleDelete(row.original.recurringExpenseId)}
          className="text-red-500"
        >
          <X className="h-4 w-4" />
        </button>
      ),
    },
  ];

  return (
    <div className="p-6">
      <DataTable columns={columns} data={expenses} />
    </div>
  );
}
