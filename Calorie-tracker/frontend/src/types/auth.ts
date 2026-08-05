export interface User {
  id: number;
  email: string | null;
  telegram_id: number | null;
  first_name?: string;
  last_name?: string;
  username?: string;
  photo_url?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
}

export interface TokenResponse {
  access_token: string;
  token_type: string;
  user: User; // ← Добавлено для удобства
}