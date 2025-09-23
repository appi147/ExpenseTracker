import { useAuth } from "@/context/AuthContext";
import { useState, useEffect } from "react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Card, CardContent } from "@/components/ui/card";
import { updateBudget } from "@/services/api";
import { toast } from "sonner";

export default function AccountPage() {
  const { user, setUser } = useAuth();
  const [budget, setBudget] = useState<number | undefined>(undefined);
  const [editMode, setEditMode] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user?.budget !== undefined) {
      setBudget(user.budget);
    }
  }, [user]);

  const handleSave = async () => {
    if (budget === undefined || !user || budget === user.budget) {
      setEditMode(false);
      return;
    }
    try {
      setLoading(true);
      const updatedUser = await updateBudget({ amount: budget });
      setUser(updatedUser);
      setEditMode(false);
    } catch (err) {
      console.error(err);
      toast.error("Failed to update budget.");
    } finally {
      setLoading(false);
    }
  };

  if (!user) return <div className="p-6">Loading...</div>;

  return (
    <div className="max-w-xl mx-auto p-6 space-y-6">
      <h1 className="text-2xl font-semibold">Account</h1>

      <Card>
        <CardContent className="p-6 space-y-6">
          <div className="flex items-center gap-4">
            <Avatar className="h-16 w-16">
              <AvatarImage src={user.pictureUrl} />
              <AvatarFallback>
                {user.fullName
                  .split(" ")
                  .map((n) => n[0])
                  .join("")}
              </AvatarFallback>
            </Avatar>
            <div>
              <p className="text-lg font-medium">{user.fullName}</p>
              <p className="text-sm text-muted-foreground">{user.email}</p>
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="budget">Monthly Budget</Label>
            <Input
              id="budget"
              type="text"
              inputMode="numeric"
              pattern="[1-9]\\d*|0"
              value={budget === undefined ? "" : String(budget)}
              onChange={(e) => {
                const value = e.target.value;
                if (value === "") {
                  setBudget(undefined);
                } else if (/^(0|[1-9]\d*)$/.test(value)) {
                  setBudget(Number(value));
                }
              }}
              disabled={!editMode || loading}
            />
            {editMode ? (
              <div className="flex gap-2">
                <Button onClick={handleSave} disabled={loading}>
                  {loading ? "Saving..." : "Save"}
                </Button>
                <Button
                  variant="outline"
                  onClick={() => {
                    setBudget(user.budget);
                    setEditMode(false);
                  }}
                  disabled={loading}
                >
                  Cancel
                </Button>
              </div>
            ) : (
              <Button variant="outline" onClick={() => setEditMode(true)} disabled={loading}>
                Edit Budget
              </Button>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
