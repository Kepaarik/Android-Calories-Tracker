import { useState } from "react";
import { useNavigate } from "react-router-dom";
import WebApp from "@twa-dev/sdk";
import GlassCard from "../components/ui/GlassCard";
import GlassButton from "../components/ui/GlassButton";
import { useAuth } from "../hooks/useAuth";
import { useToast } from "../context/ToastContext";
import "./LoginPage.css";

export default function LoginPage() {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const { login, register, isLoading, error } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();

  const isInTelegram = !!(WebApp.initData && WebApp.initData.length > 0);

  // EDGE-CASE: если открыли страницу в Telegram — перенаправляем на главную
  // (авто-вход должен был сработать в App.tsx)
  if (isInTelegram) {
    return (
      <div className="login-page-telegram-wrapper">
        <div className="login-page-telegram-message">
          <div className="login-page-telegram-spinner" />
          Авторизация через Telegram...
        </div>
      </div>
    );
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim() || !password) {
      toast.warning("Заполните все поля");
      return;
    }
    if (isLogin) {
      await login({ email, password });
    } else {
      await register({ email, password });
    }
  };

  return (
    <div className="login-page-container">
      <GlassCard className="login-page-card" padding="32px">
        <h1 className="login-page-title">
          {isLogin ? "Вход" : "Регистрация"}
        </h1>
        <p className="login-page-subtitle">
          {isLogin ? "Войдите в свой аккаунт" : "Создайте новый аккаунт"}
        </p>

        <form onSubmit={handleSubmit}>
          <div className="login-page-field">
            <label className="input-label">Email</label>
            <input
              type="email"
              className="glass-input login-page-input"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="your@email.com"
              required
            />
          </div>

          <div className="login-page-field-password">
            <label className="input-label">Пароль</label>
            <input
              type="password"
              className="glass-input login-page-input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
              minLength={6}
            />
          </div>

          {error && (
            <div className="login-page-error">
              {error}
            </div>
          )}

          <GlassButton
            type="submit"
            variant="success"
            fullWidth
            disabled={isLoading}
            className="login-page-submit-btn"
          >
            {isLoading ? "..." : isLogin ? "Войти" : "Зарегистрироваться"}
          </GlassButton>

          <button
            type="button"
            onClick={() => {
              setIsLogin(!isLogin);
              setEmail("");
              setPassword("");
            }}
            className="login-page-toggle-btn"
          >
            {isLogin
              ? "Нет аккаунта? Зарегистрироваться"
              : "Уже есть аккаунт? Войти"}
          </button>
        </form>
      </GlassCard>
    </div>
  );
}
