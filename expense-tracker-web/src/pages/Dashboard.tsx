import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import Navbar from '@/components/Navbar';

const Dashboard = () => {
  const { token, user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!token) {
      navigate("/login");
    }
  }, [token]);

  return (
    <div>
      <Navbar />
      <div style={{ padding: "2rem" }}>
        <h2>Dashboard</h2>
        {user ? (
          <div style={{ display: "flex", alignItems: "center", gap: "1rem" }}>
            <img
              src={user.pictureUrl}
              alt={user.fullName}
              style={{
                width: "60px",
                height: "60px",
                borderRadius: "50%",
                objectFit: "cover",
              }}
            />
            <div>
              <p>Welcome, {user.fullName}</p>
              <p>{user.email}</p>
            </div>
          </div>
        ) : (
          <p>Loading...</p>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
