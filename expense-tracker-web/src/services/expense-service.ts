import API from "./api";

export interface MonthlyExpense {
  last30Days: number;
  currentMonth: number;
}

export interface CreateExpenseRequest {
  amount: number;
  date: string;           // "YYYY-MM-DD"
  comments?: string;
  subCategoryId: number;
  paymentTypeCode: string;
}

export const createExpense = async (data: CreateExpenseRequest) => {
  const response = await API.post("/expense/create", data);
  return response.data;
};

export const getMonthlyExpense = async (): Promise<MonthlyExpense> => {
  const response = await API.get("/expense/monthly");
  return response.data;
};
