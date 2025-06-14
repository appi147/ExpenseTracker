import { useEffect, useState } from "react";
import { getUserProfile } from "../services/api";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

const Dashboard = () => {
  const { token, logout } = useAuth();
  const navigate = useNavigate();
  const [user, setUser] = useState<any>(null);

  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    getUserProfile()
      .then(setUser)
      .catch(() => {
        logout();
        navigate("/login");
      });
  }, [token]);

  return (
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
  );
};

export default Dashboard;
