export interface Expense {
  expenseId: number;
  amount: number;
  date: string;
  comments?: string;
  paymentType: {
    code: string;
    label: string;
  };
  subCategory: {
    subCategoryId: number;
    label: string;
    category: {
      categoryId: number;
      label: string;
    };
  };
}
