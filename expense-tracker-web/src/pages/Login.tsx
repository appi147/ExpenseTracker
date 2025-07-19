import React from "react";
import { GoogleLogin, useGoogleOneTapLogin, type CredentialResponse } from "@react-oauth/google";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

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

  useGoogleOneTapLogin({
    onSuccess: handleSuccess,
    onError: handleError,
    cancel_on_tap_outside: false,
  });

  return (
    <div className="flex items-center justify-center min-h-screen w-full bg-background">
      <Card className="w-full max-w-sm p-6 rounded-xl shadow-lg bg-card">
        <CardHeader>
          <CardTitle className="text-center text-xl font-semibold">
            Welcome to Expense Tracker
          </CardTitle>
        </CardHeader>
        <CardContent className="flex justify-center">
          <GoogleLogin onSuccess={handleSuccess} onError={handleError} />
        </CardContent>
      </Card>
    </div>
  );
};

export default Login;
