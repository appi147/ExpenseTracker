import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import Navbar from "@/components/Navbar";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { getMonthlyExpense } from "@/services/expense-service";

const Dashboard = () => {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [monthlyTotal, setMonthlyTotal] = useState<number | null>(null);
  const [last30DaysTotal, setLast30DaysTotal] = useState<number | null>(null);
  const [showLast30Days, setShowLast30Days] = useState(false);

  useEffect(() => {
    if (!token) {
      navigate("/login");
    }
  }, [token]);

  useEffect(() => {
    const fetchMonthlyTotal = async () => {
      try {
        const { last30Days, currentMonth } = await getMonthlyExpense();
        setMonthlyTotal(currentMonth);
        setLast30DaysTotal(last30Days);
      } catch (err) {
        console.error("Failed to fetch monthly total:", err);
      }
    };

    if (token) {
      fetchMonthlyTotal();
    }
  }, [token]);

  const options = [
    {
      label: "Manage Categories",
      description: "Create, edit, or delete categories and subcategories",
      onClick: () => navigate("/categories"),
    },
    {
      label: "Add Expense",
      description: "Record a new expense quickly",
      onClick: () => navigate("/expenses/new"),
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
      <Navbar />
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
              <Button
                variant="outline"
                size="sm"
                onClick={() => setShowLast30Days((prev) => !prev)}
              >
                {showLast30Days ? "Show This Month" : "Show Last 30 Days"}
              </Button>
              <Button size="sm" onClick={() => navigate("/expenses/list")}>
                View Details
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* Navigation */}
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
    </div>
  );
};

export default Dashboard;
