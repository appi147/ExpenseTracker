import { Routes, Route } from "react-router-dom";
import Dashboard from "@/pages/Dashboard";
import Login from "@/pages/Login";
import NotFound from "@/pages/NotFound";
import Profile from "@/pages/Profile";
import Settings from "@/pages/Settings";
import Categories from "@/pages/Categories";
import Layout from "@/components/Layout";
import Expenses from "@/pages/Expenses";
import MonthlyInsights from "@/pages/MonthlyInsights";

const AppRoutes = () => {
  return (
    <main className="min-h-screen w-full bg-background">
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route
          path="/"
          element={
            <Layout>
              <Dashboard />
            </Layout>
          }
        />
        <Route
          path="/profile"
          element={
            <Layout>
              <Profile />
            </Layout>
          }
        />
        <Route
          path="/settings"
          element={
            <Layout>
              <Settings />
            </Layout>
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
        <Route
          path="/"
          element={
            <Layout>
              <Dashboard />
            </Layout>
          }
        />
        <Route
          path="/categories"
          element={
            <Layout>
              <Categories />
            </Layout>
          }
        />
        <Route
          path="/expenses/list"
          element={
            <Layout>
              <Expenses />
            </Layout>
          }
        />
        <Route
          path="/expenses/insights"
          element={
            <Layout>
              <MonthlyInsights />
            </Layout>
          }
        />
      </Routes>
    </main>
  );
};

export default AppRoutes;
