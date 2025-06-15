import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";

const Navbar = () => {
  const { user, logout } = useAuth();

  return (
    <nav className="flex justify-between items-center px-6 py-4 border-b shadow-sm bg-white">
      <Link to="/" className="text-lg font-bold">Expense Tracker</Link>

      {user && (
        <div className="flex items-center gap-4">
          <div className="text-right">
            <p className="text-sm font-medium">{user.fullName}</p>
            <p className="text-xs text-gray-500">{user.email}</p>
          </div>
          <Avatar>
            <AvatarImage src={user.pictureUrl} alt={user.fullName} />
            <AvatarFallback>{user.fullName[0]}</AvatarFallback>
          </Avatar>
          <Button variant="outline" size="sm" onClick={logout}>
            Logout
          </Button>
        </div>
      )}
    </nav>
  );
};

export default Navbar;
