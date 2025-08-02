import API from "./api";

export interface MonthlyExpense {
  last30Days: number;
  currentMonth: number;
}

export interface CreateExpenseRequest {
  amount: number;
  date: string;
  comments?: string;
  subCategoryId: number;
  paymentTypeCode: string;
  monthsToAmortize: number;
}

interface FilterParams {
  categoryId?: number;
  subCategoryId?: number;
  paymentTypeCode?: string;
  dateFrom?: string;
  dateTo?: string;
  page?: number;
  size?: number;
}

export interface SubCategoryWiseExpense {
  subCategory: string;
  amount: number;
}

export interface CategoryWiseExpense {
  category: string;
  amount: number;
  subCategoryWiseExpenses: SubCategoryWiseExpense[];
}

export interface MonthlyExpenseInsight {
  categoryWiseExpenses: CategoryWiseExpense[];
  totalExpense: number;
  monthlyBudget: number;
}

export type ExportFormat = "csv" | "excel";

interface ExportParams {
  dateFrom?: string;
  dateTo?: string;
  format: ExportFormat;
}

export const createExpense = async (data: CreateExpenseRequest) => {
  const response = await API.post("/expense/create", data);
  return response.data;
};

export const getMonthlyExpense = async (): Promise<MonthlyExpense> => {
  const response = await API.get("/expense/monthly");
  return response.data;
};

export const getFilteredExpenses = async (filters: FilterParams) => {
  const params = new URLSearchParams();

  if (filters.categoryId) params.append("categoryId", String(filters.categoryId));
  if (filters.subCategoryId) params.append("subCategoryId", String(filters.subCategoryId));
  if (filters.paymentTypeCode) params.append("paymentTypeCode", filters.paymentTypeCode);
  if (filters.dateFrom) params.append("dateFrom", filters.dateFrom);
  if (filters.dateTo) params.append("dateTo", filters.dateTo);

  params.append("page", String(filters.page ?? 0));
  params.append("size", String(filters.size ?? 10));

  const response = await API.get(`/expense/list?${params.toString()}`);
  return response.data;
};

export const deleteExpense = async (expenseId: number): Promise<void> => {
  await API.delete(`/expense/${expenseId}`);
};

export async function updateExpenseAmount(expenseId: number, amount: number) {
  return API.put(`/expense/${expenseId}/amount`, { amount });
}

/**
 * Fetches detailed category and sub-category wise expense insights.
 * @param monthly - true for current month, false for last 30 days
 */
export const getMonthlyInsight = async (
  monthly: boolean = true,
): Promise<MonthlyExpenseInsight> => {
  const response = await API.get(`/expense/insight?monthly=${monthly}`);
  return response.data;
};

export const exportExpenses = async ({ dateFrom, dateTo, format }: ExportParams) => {
  const params = new URLSearchParams();
  if (dateFrom) params.set("dateFrom", dateFrom);
  if (dateTo) params.set("dateTo", dateTo);
  params.set("format", format);

  const response = await API.get(`/expense/export?${params.toString()}`, {
    responseType: "blob",
  });

  const ext = format === "csv" ? "csv" : "xlsx";
  const fileName = `expenses-${Date.now()}.${ext}`;

  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement("a");
  link.href = url;
  link.setAttribute("download", fileName);
  document.body.appendChild(link);
  link.click();
  link.remove();
};
