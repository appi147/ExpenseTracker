import React from "react";
import { GoogleLogin } from "@react-oauth/google";
import type { CredentialResponse } from "@react-oauth/google";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

const Login: React.FC = () => {
  const { setAuthToken } = useAuth();
  const navigate = useNavigate();

  const handleSuccess = (credentialResponse: CredentialResponse) => {
    if (credentialResponse.credential) {
      setAuthToken(credentialResponse.credential);
      navigate("/");
    } else {
      console.error("Credential missing in response");
    }
  };

  const handleError = () => {
    console.log("Login Failed");
  };

  return (
    <div className="login-page">
      <h2>Welcome to Expense Tracker</h2>
      <GoogleLogin onSuccess={handleSuccess} onError={handleError} />
    </div>
  );
};

export default Login;
