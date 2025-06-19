import API from "./api";

export interface MonthlyExpense {
  last30Days: number;
  currentMonth: number;
}

export const getMonthlyExpense = async (): Promise<MonthlyExpense> => {
  const response = await API.get("/expense/monthly");
  return response.data;
};
