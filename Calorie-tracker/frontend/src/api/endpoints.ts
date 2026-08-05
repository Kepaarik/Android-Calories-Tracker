// frontend/src/api/endpoints.ts
import { apiClient } from "./client";
import {
  LoginRequest,
  RegisterRequest,
  TokenResponse,
  User,
} from "../types/auth";
import {
  Product,
  ProductCreate,
  DiaryEntry,
  DiaryEntryCreate,
  DailySummary,
  UserProfileCreate,
  UserProfileUpdate,
  UserProfileResponse,
  WeightEntry, // ← Добавлено
  WeightEntryCreate, // ← Добавлено
  WeightStats, // ← Добавлено
  StatisticsSettings,
  ThemeSettings,
} from "../types/api";

// ==================== Auth ====================
export const authApi = {
  register: (data: RegisterRequest) =>
    apiClient.post<User>("/api/auth/register", data),

  login: (data: LoginRequest) =>
    apiClient.post<TokenResponse>("/api/auth/login", data),

  getMe: () => apiClient.get<User>("/api/auth/me"),
};

// ==================== Products ====================
export const productsApi = {
  getAll: (params?: { search?: string; sort_by?: string; sort_order?: string }) =>
    apiClient.get<Product[]>("/api/products/", { params }),

  getById: (id: number) =>
    apiClient.get<Product>(`/api/products/${id}`),

  create: (data: ProductCreate) =>
    apiClient.post<Product>("/api/products/", data),

  createComposite: (data: any) =>
    apiClient.post<Product>("/api/products/composite", data),

  // ← ДОБАВЬ ЭТИ ДВА МЕТОДА:
  update: (id: number, data: Partial<ProductCreate>) =>
    apiClient.put<Product>(`/api/products/${id}`, data),

  delete: (id: number) =>
    apiClient.delete(`/api/products/${id}`),
};

// ==================== Diary ====================
export const diaryApi = {
  getEntries: (date?: string) =>
    apiClient.get<DiaryEntry[]>("/api/diary/", {
      params: date ? { date } : {},
    }),

  addEntry: (data: DiaryEntryCreate & { date?: string }) =>
    apiClient.post<DiaryEntry>("/api/diary/", data),

  updateEntry: (
    entryId: number,
    data: { weight_grams?: number; meal_type?: string; consumed_at?: string }
  ) => apiClient.put<DiaryEntry>(`/api/diary/${entryId}`, data),

  deleteEntry: (entryId: number) => apiClient.delete(`/api/diary/${entryId}`),

  restoreEntry: (entryId: number) =>
    apiClient.post(`/api/diary/${entryId}/restore`),

  getSummary: (date?: string) =>
    apiClient.get<DailySummary>("/api/diary/summary", {
      params: date ? { date } : {},
    }),
};

// ==================== Profile ====================
export const profileApi = {
  getProfile: () => apiClient.get<UserProfileResponse>("/api/profile/"), // ← Было /profile/

  calculatePreview: (data: UserProfileCreate) =>
    apiClient.post<{
      calories: number;
      proteins: number;
      fats: number;
      carbs: number;
    }>("/api/profile/calculate-preview", data),

  createProfile: (data: UserProfileCreate) =>
    apiClient.post<UserProfileResponse>("/api/profile/", data),

  updateProfile: (data: UserProfileUpdate) =>
    apiClient.put<UserProfileResponse>("/api/profile/", data),
};

// ==================== Statistics Widgets Order ====================
export const statisticsSettingsApi = {
  getSettings: () =>
    apiClient.get<StatisticsSettings>("/api/profile/dashboard-settings"),

  saveSettings: (settings: StatisticsSettings) =>
    apiClient.put<StatisticsSettings>(
      "/api/profile/dashboard-settings",
      settings
    ),
};

// ==================== Theme Settings ====================
export const themeSettingsApi = {
  getSettings: () =>
    apiClient.get<ThemeSettings>("/api/profile/theme-settings"),

  saveSettings: (settings: ThemeSettings) =>
    apiClient.put<ThemeSettings>("/api/profile/theme-settings", settings),
};

// ==================== Weight ====================
export const weightApi = {
  getEntries: (limit = 30) =>
    apiClient.get<WeightEntry[]>("/api/weight/", { params: { limit } }),

  addEntry: (data: WeightEntryCreate) =>
    apiClient.post<WeightEntry>("/api/weight/", data),

  deleteEntry: (entryId: number) => apiClient.delete(`/api/weight/${entryId}`), // ← Было /weight/

  getStats: () => apiClient.get<WeightStats>("/api/weight/stats"),
};
