import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { useAuthStore } from "./store/authStore";
import { useThemeStore } from "./store/themeStore";
import { useTelegramAuth } from "./hooks/useTelegramAuth";
import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";
import ProductsPage from "./pages/ProductsPage";
import ProfilePage from "./pages/ProfilePage";
import TelegramThemeSync from "./components/telegram/TelegramThemeSync";
import StatisticsPage from "./pages/StatisticsPage";
import StatisticsSettingsPage from "./pages/StatisticsSettingsPage";
import Layout from "./components/layout/Layout";
import { useEffect } from "react";
import { useToast } from "./context/ToastContext";
import WebApp from "@twa-dev/sdk";
import "./App.css";

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuthStore();
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
}

function App() {
  const { isAuthenticating, authError } = useTelegramAuth();
  const { isAuthenticated } = useAuthStore();
  const toast = useToast();
  const isInTelegram = !!(WebApp.initData && WebApp.initData.length > 0);

  useEffect(() => {
    const handler = (e: CustomEvent) => {
      const { message, type } = e.detail;
      if (type === "error") toast.error(message);
      else if (type === "success") toast.success(message);
      else if (type === "warning") toast.warning(message);
      else toast.info(message);
    };
    window.addEventListener("global-toast", handler as EventListener);
    return () =>
      window.removeEventListener("global-toast", handler as EventListener);
  }, [toast]);

  useEffect(() => {
    if (isAuthenticated) {
      useThemeStore.getState().syncFromProfile();
    }
  }, [isAuthenticated]);

  // EDGE-CASE: В Telegram и идёт авто-вход — показываем loader
  if (isInTelegram && isAuthenticating) {
    return (
      <div className="app-telegram-loader">
        <div className="app-telegram-spinner" />
        <div className="app-telegram-loader-text">
          Авторизация через Telegram...
        </div>
      </div>
    );
  }

  // EDGE-CASE: В Telegram, авто-вход провалился — показываем ошибку
  if (isInTelegram && authError && !isAuthenticated) {
    return (
      <div className="app-telegram-error">
        <svg
          width="64"
          height="64"
          viewBox="0 0 24 24"
          fill="none"
          stroke="var(--danger-color)"
          strokeWidth="2"
        >
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
        <div className="app-telegram-error-title">
          Ошибка авторизации
        </div>
        <div className="app-telegram-error-message">
          {authError}
        </div>
        <button
          onClick={() => window.location.reload()}
          className="app-telegram-retry-btn"
        >
          Попробовать снова
        </button>
      </div>
    );
  }

  return (
    <>
      <TelegramThemeSync />
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/"
            element={
              <PrivateRoute>
                <Layout>
                  <DashboardPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/products"
            element={
              <PrivateRoute>
                <Layout>
                  <ProductsPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <PrivateRoute>
                <Layout>
                  <ProfilePage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/statistics"
            element={
              <PrivateRoute>
                <Layout>
                  <StatisticsPage />
                </Layout>
              </PrivateRoute>
            }
          />
          <Route
            path="/statistics-settings"
            element={
              <PrivateRoute>
                <Layout>
                  <StatisticsSettingsPage />
                </Layout>
              </PrivateRoute>
            }
          />
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
