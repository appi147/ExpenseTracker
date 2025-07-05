// columns.ts
import { type ColumnDef } from "@tanstack/react-table";
import { type Expense } from "./types";

export const columns: ColumnDef<Expense>[] = [
  {
    accessorKey: "date",
    header: "Date",
    cell: ({ row }) => new Date(row.original.date).toLocaleDateString("en-IN"),
  },
  {
    accessorKey: "amount",
    header: "Amount",
    cell: ({ row }) => `₹${row.original.amount}`,
  },
  {
    accessorKey: "subCategory",
    header: "Subcategory",
    cell: ({ row }) => row.original.subCategory.label,
  },
  {
    accessorKey: "category",
    header: "Category",
    cell: ({ row }) => row.original.subCategory.category.label,
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
];
