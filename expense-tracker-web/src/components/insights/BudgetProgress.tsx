import { Progress } from "@/components/ui/progress";

type Props = {
  totalExpense: number;
  monthlyBudget: number;
};

export default function BudgetProgress({ totalExpense, monthlyBudget }: Props) {
  const safeExpense = totalExpense ?? 0;
  const safeBudget = monthlyBudget ?? 0;

  const percentUsed = safeBudget > 0 ? Math.min((safeExpense / safeBudget) * 100, 100) : 0;
  const overBudget = safeBudget > 0 && safeExpense > safeBudget;
  const remaining = safeBudget - safeExpense;

  return (
    <div className="space-y-3">
      <div className="flex justify-between items-center text-lg font-semibold">
        <span>💰 Total Expense</span>
        <span>₹{safeExpense.toFixed(2)}</span>
      </div>

      <div className="flex justify-between text-sm font-medium">
        <span className={overBudget ? "text-red-600" : "text-green-600"}>
          {overBudget ? "Over Budget" : "Remaining Budget"}
        </span>
        <span>₹{Math.abs(remaining).toFixed(2)}</span>
      </div>

      <Progress value={percentUsed} indicatorColor={overBudget ? "bg-red-500" : "bg-green-500"} />

      <p className="text-center text-xs text-muted-foreground">
        {safeBudget > 0
          ? `${percentUsed.toFixed(1)}% of ₹${safeBudget.toFixed(2)} used`
          : "No budget defined"}
      </p>
    </div>
  );
}
