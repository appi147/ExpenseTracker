import { Routes, Route } from "react-router-dom";
import Dashboard from "@/pages/Dashboard";
import Login from "@/pages/Login";
import NotFound from "@/pages/NotFound";
import Profile from "@/pages/AccountPage";
import Categories from "@/pages/Categories";
import Layout from "@/components/Layout";
import Expenses from "@/pages/Expenses";
import MonthlyInsights from "@/pages/MonthlyInsights";
import SiteWideInsights from "@/pages/SiteWideInsights";
import ProtectedRoute from "@/components/ProtectedRoute";

const AppRoutes = () => {
  return (
    <main className="min-h-screen w-full bg-background">
      <Routes>
        <Route path="/login" element={<Login />} />

        <Route
          path="/"
          element={
            <ProtectedRoute>
              <Layout>
                <Dashboard />
              </Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <Layout>
                <Profile />
              </Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/categories"
          element={
            <ProtectedRoute>
              <Layout>
                <Categories />
              </Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/expenses/list"
          element={
            <ProtectedRoute>
              <Layout>
                <Expenses />
              </Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/expenses/insights"
          element={
            <ProtectedRoute>
              <Layout>
                <MonthlyInsights />
              </Layout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/insights"
          element={
            <ProtectedRoute>
              <Layout>
                <SiteWideInsights />
              </Layout>
            </ProtectedRoute>
          }
        />

        <Route
          path="*"
          element={
            <Layout>
              <NotFound />
            </Layout>
          }
        />
      </Routes>
    </main>
  );
};

export default AppRoutes;
