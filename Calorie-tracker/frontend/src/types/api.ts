export type Gender = "male" | "female";
export type ActivityLevel =
  | "sedentary"
  | "light"
  | "moderate"
  | "active"
  | "very_active";
export type FitnessGoal = "lose" | "maintain" | "gain";
export type CalculationFormula =
  | "mifflin_st_jeor"
  | "harris_benedict"
  | "katch_mcardle";

export interface UserProfileCreate {
  gender: Gender;
  age: number;
  weight_kg: number;
  height_cm: number;
  activity_level: ActivityLevel;
  fitness_goal: FitnessGoal;
  calculation_formula: CalculationFormula;
  calorie_adjustment: number;
  custom_calorie_goal?: number | null;
  custom_protein_goal?: number | null;
  custom_fat_goal?: number | null;
  custom_carb_goal?: number | null;
}

export interface UserProfileUpdate {
  gender?: Gender;
  age?: number;
  weight_kg?: number;
  height_cm?: number;
  activity_level?: ActivityLevel;
  fitness_goal?: FitnessGoal;
  calculation_formula?: CalculationFormula;
  calorie_adjustment?: number;
  custom_calorie_goal?: number | null;
  custom_protein_goal?: number | null;
  custom_fat_goal?: number | null;
  custom_carb_goal?: number | null;
}

export interface UserProfileResponse {
  id: number;
  user_id: number;
  gender: Gender;
  age: number;
  weight_kg: number;
  height_cm: number;
  activity_level: ActivityLevel;
  fitness_goal: FitnessGoal;
  calculation_formula: CalculationFormula;
  calorie_adjustment: number;
  custom_calorie_goal: number | null;
  custom_protein_goal: number | null;
  custom_fat_goal: number | null;
  custom_carb_goal: number | null;
  calculated_calories: number;
  calculated_proteins: number;
  calculated_fats: number;
  calculated_carbs: number;
  created_at: string;
  updated_at: string;
}

export interface WeightEntry {
  id: number;
  weight_kg: number;
  recorded_at: string;
}

export interface WeightEntryCreate {
  weight_kg: number;
  date?: string;
}

export interface WeightStats {
  current_weight: number | null;
  previous_weight: number | null;
  change: number | null;
  min_weight: number | null;
  max_weight: number | null;
  entries_count: number;
}

export interface Product {
  id: number;
  user_id?: number | null;
  barcode?: string | null; // ← ДОБАВЛЕНО
  name: string;
  calories: number;
  proteins: number;
  fats: number;
  carbs: number;
  is_composite?: boolean;
}

export interface ProductCreate {
  name: string;
  calories: number;
  proteins: number;
  fats: number;
  carbs: number;
  barcode?: string | null; // ← ДОБАВЛЕНО
}

export interface DailySummary {
  date: string;
  total_calories: number;
  total_proteins: number;
  total_fats: number;
  total_carbs: number;
  entries_count: number;
  meals: {
    breakfast: { calories: number; count: number };
    lunch: { calories: number; count: number };
    dinner: { calories: number; count: number };
    snack: { calories: number; count: number };
  };
  // ← Алиасы для совместимости с компонентами
  calories?: number;
  proteins?: number;
  fats?: number;
  carbs?: number;
}

// ← ДОБАВЬ В КОНЕЦ ФАЙЛА

export interface DiaryEntry {
  id: number;
  user_id: number;
  product_id: number;
  product?: Product;
  weight_grams: number;
  meal_type: string;
  consumed_at: string;
  created_at?: string;
  is_deleted?: boolean;
}

export interface DiaryEntryCreate {
  product_id: number;
  weight_grams: number;
  meal_type: string;
  date: string;
}

export interface StatWidgetConfig {
  id: string;
  visible: boolean;
  order: number;
}

export interface StatisticsSettings {
  widgets: StatWidgetConfig[];
}

export interface ThemeSettings {
  theme: string | null;
  accent_color: string | null;
}
