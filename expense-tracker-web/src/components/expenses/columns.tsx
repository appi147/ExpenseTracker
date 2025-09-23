import { type ColumnDef } from "@tanstack/react-table";
import { type Expense } from "./types";
import { Pencil, X } from "lucide-react";

export const getExpenseColumns = (
  handleEditAmount: (expense: Expense) => void,
  handleDelete: (id: number) => void,
): ColumnDef<Expense>[] => [
  {
    accessorKey: "date",
    header: "Date",
    cell: ({ row }) => new Date(row.original.date).toLocaleDateString("en-IN"),
  },
  {
    accessorKey: "category",
    header: "Category",
    cell: ({ row }) => row.original.subCategory.category.label,
  },
  {
    accessorKey: "subCategory",
    header: "Subcategory",
    cell: ({ row }) => row.original.subCategory.label,
  },
  {
    accessorKey: "amount",
    header: "Amount",
    cell: ({ row }) => (
      <div className="flex items-center gap-2">
        ₹{row.original.amount}
        <button
          onClick={() => handleEditAmount(row.original)}
          className="text-blue-500 hover:text-blue-700"
          aria-label="Edit"
        >
          <Pencil size={14} />
        </button>
      </div>
    ),
  },
  {
    accessorKey: "paymentType",
    header: "Payment Type",
    cell: ({ row }) => row.original.paymentType.label,
  },
  {
    accessorKey: "comments",
    header: "Comments",
  },
  {
    id: "actions",
    header: "",
    cell: ({ row }) => (
      <button
        onClick={() => handleDelete(row.original.expenseId)}
        className="text-red-500 hover:text-red-700"
        aria-label="Delete"
      >
        <X size={16} />
      </button>
    ),
  },
];
