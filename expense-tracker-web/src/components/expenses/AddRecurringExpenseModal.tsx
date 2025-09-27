import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogHeader,
  DialogContent,
  DialogFooter,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";
import { toast } from "sonner";
import { Label } from "@/components/ui/label";
import {
  createRecurringExpense,
  type CreateRecurringExpenseRequest,
} from "@/services/recurring-expense-service";
import { getAllCategories, type Category } from "@/services/category-service";
import { getAllSubCategories, type SubCategory } from "@/services/sub-category-service";
import { getAllPaymentTypes, type PaymentType } from "@/services/payment-type-service";
import { Link } from "react-router-dom";

interface AddRecurringExpenseModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly onExpenseAdded: () => void;
}

export function AddRecurringExpenseModal({
  isOpen,
  onClose,
  onExpenseAdded,
}: AddRecurringExpenseModalProps) {
  const [amount, setAmount] = useState("");
  const [dayOfMonth, setDayOfMonth] = useState("1");
  const [comments, setComments] = useState("");
  const [categoryId, setCategoryId] = useState<number>();
  const [subCategoryId, setSubCategoryId] = useState<number>();
  const [paymentTypeCode, setPaymentTypeCode] = useState("");

  const [categories, setCategories] = useState<Category[]>([]);
  const [subCategories, setSubCategories] = useState<SubCategory[]>([]);
  const [paymentTypes, setPaymentTypes] = useState<PaymentType[]>([]);

  useEffect(() => {
    if (!isOpen) return;

    setAmount("");
    setDayOfMonth("1");
    setComments("");
    setCategoryId(undefined);
    setSubCategoryId(undefined);
    setPaymentTypeCode("");

    (async () => {
      try {
        const [paymentData, categoryData] = await Promise.all([
          getAllPaymentTypes(),
          getAllCategories(),
        ]);
        setPaymentTypes(paymentData);
        setCategories(categoryData);
      } catch {
        toast.error("Failed to load options");
      }
    })();
  }, [isOpen]);

  useEffect(() => {
    if (!categoryId) {
      setSubCategories([]);
      setSubCategoryId(undefined);
      return;
    }
    (async () => {
      try {
        const data = await getAllSubCategories(categoryId);
        setSubCategories(data);
      } catch {
        toast.error("Failed to load subcategories");
      }
    })();
  }, [categoryId]);

  const parseFloatSafe = (value: string) => {
    const parsed = parseFloat(value);
    return isNaN(parsed) ? 0 : parsed;
  };

  const parseIntSafe = (value: string) => {
    const parsed = parseInt(value, 10);
    return isNaN(parsed) ? 1 : parsed;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!amount || !paymentTypeCode || !subCategoryId) {
      toast.error("Please fill in all required fields.");
      return;
    }

    const payload: CreateRecurringExpenseRequest = {
      amount: parseFloatSafe(amount),
      dayOfMonth: parseIntSafe(dayOfMonth),
      comments,
      subCategoryId,
      paymentTypeCode,
    };

    try {
      await createRecurringExpense(payload);
      toast.success("Recurring expense added successfully");
      onExpenseAdded();
      onClose();
    } catch {
      toast.error("Failed to add recurring expense");
    }
  };

  const isSubmitDisabled = !amount || !paymentTypeCode || !subCategoryId || categories.length === 0;

  return (
    <Dialog open={isOpen} onOpenChange={(v) => !v && onClose()}>
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle>Add Recurring Expense</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="grid grid-cols-1 gap-4">
          {/* Category */}
          <div>
            <Label>Category</Label>
            {categories.length === 0 ? (
              <p className="text-sm text-muted-foreground">
                No categories found.{" "}
                <Link to="/categories" className="text-blue-600 underline">
                  Add a category
                </Link>{" "}
                to continue.
              </p>
            ) : (
              <Select value={categoryId?.toString()} onValueChange={(val) => setCategoryId(+val)}>
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Select a category" />
                </SelectTrigger>
                <SelectContent>
                  {categories.map((cat) => (
                    <SelectItem key={cat.categoryId} value={cat.categoryId.toString()}>
                      {cat.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            )}
          </div>

          {/* Subcategory */}
          {categoryId &&
            (subCategories.length === 0 ? (
              <p className="text-sm text-muted-foreground">
                No subcategories found.{" "}
                <Link to="/sub-categories" className="text-blue-600 underline">
                  Add a subcategory
                </Link>{" "}
                to continue.
              </p>
            ) : (
              <div>
                <Label>Subcategory</Label>
                <Select
                  value={subCategoryId?.toString()}
                  onValueChange={(val) => setSubCategoryId(+val)}
                >
                  <SelectTrigger className="w-full">
                    <SelectValue placeholder="Select a subcategory" />
                  </SelectTrigger>
                  <SelectContent>
                    {subCategories.map((sub) => (
                      <SelectItem key={sub.subCategoryId} value={sub.subCategoryId.toString()}>
                        {sub.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            ))}

          {/* Amount */}
          <div>
            <Label>Amount</Label>
            <Input
              type="text"
              value={amount}
              onChange={(e) => {
                const val = e.target.value.replace(/^0+(?!\.|$)/, "");
                if (/^\d*\.?\d{0,2}$/.test(val)) setAmount(val);
              }}
              required
            />
          </div>

          {/* Day of Month */}
          <div>
            <Label>Day of Month (1-28)</Label>
            <Input
              type="number"
              min={1}
              max={28}
              value={dayOfMonth}
              onChange={(e) => setDayOfMonth(e.target.value)}
              required
            />
          </div>

          {/* Payment Type */}
          <div>
            <Label>Payment Type</Label>
            <Select value={paymentTypeCode} onValueChange={setPaymentTypeCode}>
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Select a payment method" />
              </SelectTrigger>
              <SelectContent>
                {paymentTypes.map((pt) => (
                  <SelectItem key={pt.code} value={pt.code}>
                    {pt.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          {/* Comments */}
          <div>
            <Label>Comments</Label>
            <Input
              value={comments}
              onChange={(e) => setComments(e.target.value)}
              placeholder="Optional"
            />
          </div>

          {/* Actions */}
          <DialogFooter className="flex justify-end gap-2">
            <Button variant="outline" type="button" onClick={onClose}>
              Cancel
            </Button>
            <Button type="submit" disabled={isSubmitDisabled}>
              Add Recurring Expense
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
