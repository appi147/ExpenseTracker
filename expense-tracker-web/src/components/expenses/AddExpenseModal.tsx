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
import { createExpense } from "@/services/expense-service";
import { getAllPaymentTypes, type PaymentType } from "@/services/payment-type-service";
import { getAllCategories, type Category } from "@/services/category-service";
import { getAllSubCategories, type SubCategory } from "@/services/sub-category-service";
import { Calendar as CalendarIcon } from "lucide-react";
import { cn } from "@/lib/utils";
import { Calendar } from "@/components/ui/calendar";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { format } from "date-fns";
import { Link } from "react-router-dom";
import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";

interface AddExpenseModalProps {
  isOpen: boolean;
  onClose: () => void;
  onExpenseAdded: () => void;
}

export function AddExpenseModal({ isOpen, onClose, onExpenseAdded }: AddExpenseModalProps) {
  const [amount, setAmount] = useState("");
  const [date, setDate] = useState(new Date().toISOString().split("T")[0]);
  const [comments, setComments] = useState("");
  const [categoryId, setCategoryId] = useState<number | undefined>(undefined);
  const [subCategoryId, setSubCategoryId] = useState<number | undefined>(undefined);
  const [paymentTypeCode, setPaymentTypeCode] = useState("");

  const [isAmortized, setIsAmortized] = useState(false);
  const [monthsToAmortize, setMonthsToAmortize] = useState("3");

  const [categories, setCategories] = useState<Category[]>([]);
  const [subCategories, setSubCategories] = useState<SubCategory[]>([]);
  const [paymentTypes, setPaymentTypes] = useState<PaymentType[]>([]);
  const [isDatePopoverOpen, setIsDatePopoverOpen] = useState(false);

  useEffect(() => {
    async function loadOptions() {
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
    }

    if (isOpen) loadOptions();
  }, [isOpen]);

  useEffect(() => {
    async function loadSubCategoryOptions() {
      if (!categoryId) {
        setSubCategories([]);
        return;
      }
      try {
        const data = await getAllSubCategories(categoryId);
        setSubCategories(data);
      } catch {
        toast.error("Failed to load subcategories");
      }
    }

    loadSubCategoryOptions();
  }, [categoryId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!amount || !date || !paymentTypeCode || !subCategoryId) {
      toast.error("Please fill in all required fields.");
      return;
    }

    try {
      await createExpense({
        amount: parseFloat(amount),
        date,
        comments,
        subCategoryId,
        paymentTypeCode,
        monthsToAmortize: isAmortized ? parseInt(monthsToAmortize) : 1,
      });
      toast.success("Expense added successfully");
      onExpenseAdded();
      onClose();
    } catch {
      toast.error("Failed to add expense");
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={(v) => !v && onClose()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Add Expense</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Label>Date</Label>
            <Popover open={isDatePopoverOpen} onOpenChange={setIsDatePopoverOpen}>
              <PopoverTrigger asChild>
                <Button
                  variant="outline"
                  className={cn(
                    "w-full justify-start text-left font-normal",
                    !date && "text-muted-foreground",
                  )}
                >
                  {date ? format(new Date(date), "PPP") : <span>Pick a date</span>}
                  <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                </Button>
              </PopoverTrigger>
              <PopoverContent className="p-0">
                <Calendar
                  mode="single"
                  selected={date ? new Date(date) : undefined}
                  onSelect={(selected) => {
                    if (!selected) return;
                    const today = new Date();
                    today.setHours(0, 0, 0, 0);
                    if (selected > today) return;
                    const year = selected.getFullYear();
                    const month = String(selected.getMonth() + 1).padStart(2, "0");
                    const day = String(selected.getDate()).padStart(2, "0");
                    setDate(`${year}-${month}-${day}`);
                    setIsDatePopoverOpen(false);
                  }}
                  toDate={new Date()}
                  initialFocus
                />
              </PopoverContent>
            </Popover>
          </div>

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
              <Select
                value={categoryId?.toString()}
                onValueChange={(val) => setCategoryId(parseInt(val))}
              >
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

          {categoryId &&
            (subCategories.length === 0 ? (
              <p className="text-sm text-muted-foreground">
                No subcategories found.{" "}
                <Link to="/sub-categories" className="text-blue-600 underline">
                  Add a subcategory
                </Link>{" "}
                under the selected category to continue.
              </p>
            ) : (
              <div>
                <Label>Subcategory</Label>
                <Select
                  value={subCategoryId?.toString()}
                  onValueChange={(val) => setSubCategoryId(parseInt(val))}
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

          <div>
            <Label>Amount</Label>
            <Input
              type="text"
              value={amount}
              onChange={(e) => {
                const val = e.target.value.replace(/^0+(?!\.|$)/, "");
                if (/^\d*\.?\d{0,2}$/.test(val)) {
                  setAmount(val);
                }
              }}
              required
            />
          </div>

          <div>
            <Label>Payment Type</Label>
            <Select value={paymentTypeCode} onValueChange={(val) => setPaymentTypeCode(val)}>
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

          <div>
            <Label>Comments</Label>
            <Input
              value={comments}
              onChange={(e) => setComments(e.target.value)}
              placeholder="Optional"
            />
          </div>

          <div className="flex items-center gap-2">
            <Switch checked={isAmortized} onCheckedChange={setIsAmortized} />
            <Label>Amortize this expense</Label>
          </div>

          {isAmortized && (
            <div>
              <Label>Amortize Over</Label>
              <Select value={monthsToAmortize} onValueChange={setMonthsToAmortize}>
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Select months" />
                </SelectTrigger>
                <SelectContent>
                  {[1, 2, 3, 6, 12].map((month) => (
                    <SelectItem key={month} value={month.toString()}>
                      {month} {month === 1 ? "month" : "months"}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          )}

          <DialogFooter>
            <Button variant="outline" type="button" onClick={onClose}>
              Cancel
            </Button>
            <Button
              type="submit"
              disabled={
                !amount ||
                !date ||
                !paymentTypeCode ||
                !subCategoryId ||
                categories.length === 0 ||
                subCategories.length === 0
              }
            >
              Add Expense
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
