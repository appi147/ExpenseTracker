import { Moon, Sun } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useTheme } from "@/components/theme-provider";
import type { ThemeType } from "@/services/api";

interface ModeToggleProps {
  value: ThemeType;
  onChange: (theme: ThemeType) => void;
}

export function ModeToggle({ value, onChange }: ModeToggleProps) {
  const { setTheme } = useTheme();

  const handleSelect = (theme: ThemeType) => {
    setTheme(theme.toLowerCase() as "light" | "dark" | "system");
    onChange(theme);
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="outline" size="icon">
          <Sun className="h-[1.2rem] w-[1.2rem] scale-100 rotate-0 transition-all dark:scale-0 dark:-rotate-90" />
          <Moon className="absolute h-[1.2rem] w-[1.2rem] scale-0 rotate-90 transition-all dark:scale-100 dark:rotate-0" />
          <span className="sr-only">Toggle theme</span>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuItem
          onClick={() => handleSelect("LIGHT")}
          className={value === "LIGHT" ? "font-bold" : ""}
        >
          Light
        </DropdownMenuItem>
        <DropdownMenuItem
          onClick={() => handleSelect("DARK")}
          className={value === "DARK" ? "font-bold" : ""}
        >
          Dark
        </DropdownMenuItem>
        <DropdownMenuItem
          onClick={() => handleSelect("SYSTEM")}
          className={value === "SYSTEM" ? "font-bold" : ""}
        >
          System
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
