// frontend/src/hooks/useAuth.ts
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../api/endpoints';
import { useAuthStore } from '../store/authStore';
import { LoginRequest, RegisterRequest } from '../types/auth';

export const useAuth = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const { setToken, setUser } = useAuthStore();

  const login = async (data: LoginRequest) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await authApi.login(data);
      setToken(response.data.access_token);
      
      const userResponse = await authApi.getMe();
      setUser(userResponse.data);
      
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.detail || 'Ошибка входа');
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (data: RegisterRequest) => {
    setIsLoading(true);
    setError(null);
    try {
      await authApi.register(data);
      await login({ email: data.email, password: data.password });
    } catch (err: any) {
      setError(err.response?.data?.detail || 'Ошибка регистрации');
      setIsLoading(false);
    }
  };

  return { login, register, isLoading, error };
};