import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";
import { type CategoryWiseExpense } from "@/services/expense-service";

type Props = {
  data: CategoryWiseExpense[];
};

export default function CategoryAccordion({ data }: Props) {
  return (
    <Accordion type="multiple" className="w-full">
      {data.map((cat) => (
        <AccordionItem key={cat.category} value={cat.category}>
          <AccordionTrigger className="no-underline hover:no-underline focus:no-underline cursor-pointer">
            <div className="flex justify-between w-full">
              <span>{cat.category}</span>
              <span className="font-medium text-muted-foreground">₹{cat.amount.toFixed(2)}</span>
            </div>
          </AccordionTrigger>
          <AccordionContent>
            <ul className="pl-4 space-y-1">
              {cat.subCategoryWiseExpenses.map((sub) => (
                <li key={sub.subCategory} className="flex justify-between">
                  <span>{sub.subCategory}</span>
                  <span className="text-sm text-muted-foreground">₹{sub.amount.toFixed(2)}</span>
                </li>
              ))}
            </ul>
          </AccordionContent>
        </AccordionItem>
      ))}
    </Accordion>
  );
}
