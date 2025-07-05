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

  if (filters.categoryId)
    params.append("categoryId", String(filters.categoryId));
  if (filters.subCategoryId)
    params.append("subCategoryId", String(filters.subCategoryId));
  if (filters.paymentTypeCode)
    params.append("paymentTypeCode", filters.paymentTypeCode);
  if (filters.dateFrom) params.append("dateFrom", filters.dateFrom);
  if (filters.dateTo) params.append("dateTo", filters.dateTo);

  params.append("page", String(filters.page ?? 0));
  params.append("size", String(filters.size ?? 10));

  const response = await API.get(`/expense/list?${params.toString()}`);
  return response.data;
};
