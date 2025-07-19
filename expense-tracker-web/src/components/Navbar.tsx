import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
} from "@/components/ui/dropdown-menu";
import { ModeToggle } from "@/components/mode-toggle";
import { LogOut, User, BarChartBig } from "lucide-react";
import { updateUserTheme, type ThemeType } from "@/services/api";

const Navbar = () => {
  const { user, setUser, logout } = useAuth();

  const handleThemeChange = async (theme: ThemeType) => {
    try {
      await updateUserTheme({ theme });
      if (user) {
        setUser({ ...user, preferredTheme: theme });
      }
    } catch (error) {
      console.error("Failed to update theme:", error);
    }
  };

  return (
    <nav className="flex justify-between items-center px-6 py-4 border-b bg-background text-foreground">
      <Link to="/" className="text-lg font-bold">
        Expense Tracker
      </Link>

      <div className="flex items-center gap-4">
        {user && <ModeToggle value={user.preferredTheme} onChange={handleThemeChange} />}

        {user && (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <div className="flex items-center gap-2 cursor-pointer rounded-md px-2 py-1.5 hover:bg-muted">
                <div className="text-right">
                  <p className="text-sm font-medium">{user.fullName}</p>
                  <p className="text-xs text-muted-foreground">{user.email}</p>
                </div>
                <Avatar>
                  <AvatarImage src={user.pictureUrl} alt={user.fullName} />
                  <AvatarFallback>{user.fullName[0]}</AvatarFallback>
                </Avatar>
              </div>
            </DropdownMenuTrigger>

            <DropdownMenuContent align="end" className="w-56">
              <DropdownMenuLabel className="font-medium leading-tight">
                {user.fullName}
                <br />
                <span className="text-xs text-muted-foreground">{user.email}</span>
              </DropdownMenuLabel>

              <DropdownMenuSeparator />

              <DropdownMenuItem asChild>
                <Link to="/profile" className="flex items-center gap-2 cursor-pointer">
                  <User className="w-4 h-4" />
                  Profile
                </Link>
              </DropdownMenuItem>

              <DropdownMenuSeparator />

              {user.role === "SUPER_USER" && (
                <>
                  <DropdownMenuItem asChild>
                    <Link to="/insights" className="flex items-center gap-2 cursor-pointer">
                      <BarChartBig className="w-4 h-4" />
                      Site Insights
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                </>
              )}

              <DropdownMenuItem
                onClick={logout}
                className="flex items-center gap-2 text-red-600 cursor-pointer"
              >
                <LogOut className="w-4 h-4" />
                Logout
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
