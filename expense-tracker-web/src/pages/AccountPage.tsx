import { useAuth } from "@/context/AuthContext";
import { useState, useEffect } from "react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Card, CardContent } from "@/components/ui/card";
import { updateBudget } from "@/services/api";

export default function AccountPage() {
  const { user, setUser } = useAuth();

  const [budget, setBudget] = useState<number | undefined>();
  const [editMode, setEditMode] = useState(false);
  const [originalBudget, setOriginalBudget] = useState<number | undefined>();

  useEffect(() => {
    if (user) {
      setBudget(user.budget);
      setOriginalBudget(user.budget);
    }
  }, [user]);

  const handleSave = async () => {
    if (budget === undefined || !user) return;
    try {
      const updatedUser = await updateBudget({ amount: budget });
      setUser(updatedUser);
      setOriginalBudget(budget);
      setEditMode(false);
    } catch (err) {
      console.error(err);
      alert("Failed to update budget.");
    }
  };

  const handleCancel = () => {
    setBudget(originalBudget);
    setEditMode(false);
  };

  if (!user) return null;

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
              type="number"
              value={budget}
              onChange={(e) => setBudget(Number(e.target.value))}
              disabled={!editMode}
            />
            {editMode ? (
              <div className="flex gap-2">
                <Button onClick={handleSave}>Save</Button>
                <Button variant="outline" onClick={handleCancel}>
                  Cancel
                </Button>
              </div>
            ) : (
              <Button variant="outline" onClick={() => setEditMode(true)}>
                Edit Budget
              </Button>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
