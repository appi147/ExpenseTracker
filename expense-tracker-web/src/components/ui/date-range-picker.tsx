import * as React from "react";
import { DateRange } from "react-date-range";
import { format, parseISO } from "date-fns";
import { CalendarIcon } from "lucide-react";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Button } from "@/components/ui/button";

import "react-date-range/dist/styles.css";
import "react-date-range/dist/theme/default.css";

export interface DateRangeValue {
  from: string;
  to: string;
}

interface DateRangePickerProps {
  value: DateRangeValue | null;
  onChange: (val: DateRangeValue | null) => void;
  className?: string;
}

export function DateRangePicker({
  value,
  onChange,
  className,
}: DateRangePickerProps) {
  const [open, setOpen] = React.useState(false);

  const selectionRange = {
    startDate: value?.from ? parseISO(value.from) : new Date(),
    endDate: value?.to ? parseISO(value.to) : new Date(),
    key: "selection",
  };

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          className={`w-full justify-start text-left font-normal ${className}`}
        >
          <CalendarIcon className="mr-2 h-4 w-4" />
          {value?.from && value?.to ? (
            `${format(parseISO(value.from), "dd MMM yyyy")} - ${format(
              parseISO(value.to),
              "dd MMM yyyy"
            )}`
          ) : (
            <span>Pick a date range</span>
          )}
        </Button>
      </PopoverTrigger>
      <PopoverContent align="start" className="p-0">
        <DateRange
          ranges={[selectionRange]}
          onChange={({ selection }) => {
            if (selection.startDate && selection.endDate) {
              onChange({
                from: format(selection.startDate, "yyyy-MM-dd"),
                to: format(selection.endDate, "yyyy-MM-dd"),
              });
            } else {
              onChange(null);
            }
          }}
          moveRangeOnFirstSelection={false}
          rangeColors={["#3b82f6"]}
          showDateDisplay={false}
        />
      </PopoverContent>
    </Popover>
  );
}
