import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { getMonthlyExpense } from "@/services/expense-service";
import { AddExpenseModal } from "@/components/expenses/AddExpenseModal";
import ToggleDuration from "@/components/insights/ToggleDuration";

const Dashboard = () => {
  const navigate = useNavigate();
  const [monthlyTotal, setMonthlyTotal] = useState<number | null>(null);
  const [last30DaysTotal, setLast30DaysTotal] = useState<number | null>(null);
  const [showLast30Days, setShowLast30Days] = useState(false);
  const [isAddExpenseOpen, setIsAddExpenseOpen] = useState(false);

  const loadExpenses = async () => {
    try {
      const { last30Days, currentMonth } = await getMonthlyExpense();
      setMonthlyTotal(currentMonth);
      setLast30DaysTotal(last30Days);
    } catch (err) {
      console.error("Failed to fetch monthly total:", err);
    }
  };

  useEffect(() => {
    loadExpenses();
  }, []);

  const options = [
    {
      label: "Add Expense",
      description: "Record a new expense quickly",
      onClick: () => setIsAddExpenseOpen(true),
    },
    {
      label: "Manage Categories",
      description: "Create, edit, or delete categories and subcategories",
      onClick: () => navigate("/categories"),
    },
    {
      label: "View Expenses",
      description: "Browse your expense list with filters",
      onClick: () => navigate("/expenses/list"),
    },
    {
      label: "Expense Trends",
      description: "Visualize spending patterns via charts",
      onClick: () => navigate("/expenses/chart"),
    },
  ];

  return (
    <div className="min-h-screen bg-background text-foreground">
      <div className="p-6 max-w-5xl mx-auto">
        <h2 className="text-2xl font-semibold mb-6">Dashboard</h2>

        <Card className="mb-6">
          <CardContent className="p-4 flex items-center justify-between">
            <div>
              <p className="text-muted-foreground text-sm">
                {showLast30Days
                  ? "Last 30 Days Total Expense"
                  : "This Month’s Total Expense"}
              </p>
              <h3 className="text-2xl font-bold">
                ₹
                {showLast30Days
                  ? last30DaysTotal !== null
                    ? last30DaysTotal.toFixed(2)
                    : "Loading..."
                  : monthlyTotal !== null
                  ? monthlyTotal.toFixed(2)
                  : "Loading..."}
              </h3>
            </div>

            <div className="flex flex-col gap-2 items-end">
              <ToggleDuration
                monthly={!showLast30Days}
                onChange={(val: boolean) => setShowLast30Days(!val)}
              />
              <Button
                size="sm"
                onClick={() =>
                  navigate(`/expenses/insights?monthly=${!showLast30Days}`)
                }
              >
                View Details
              </Button>
            </div>
          </CardContent>
        </Card>

        <div className="grid gap-4 md:grid-cols-2">
          {options.map((opt) => (
            <Card key={opt.label} className="hover:shadow-md transition">
              <CardContent className="p-4">
                <h3 className="text-lg font-semibold">{opt.label}</h3>
                <p className="text-sm text-muted-foreground">
                  {opt.description}
                </p>
                <Button onClick={opt.onClick} className="mt-3">
                  Go
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>

      <AddExpenseModal
        isOpen={isAddExpenseOpen}
        onClose={() => setIsAddExpenseOpen(false)}
        onExpenseAdded={loadExpenses}
      />
    </div>
  );
};

export default Dashboard;
