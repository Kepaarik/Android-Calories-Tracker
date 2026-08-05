export const ACTIVITY_LEVELS = [
  { value: "sedentary", label: "Минимальная", description: "Сидячий образ жизни" },
  { value: "light", label: "Лёгкая", description: "1-3 тренировки в неделю" },
  { value: "moderate", label: "Умеренная", description: "3-5 тренировок в неделю" },
  { value: "active", label: "Высокая", description: "6-7 тренировок в неделю" },
  { value: "very_active", label: "Очень высокая", description: "2 тренировки в день" },
] as const;

export const FITNESS_GOALS = [
  { value: "lose", label: "Похудеть", description: "Снизить вес", icon: "arrow-down" },
  { value: "maintain", label: "Поддержать", description: "Сохранить вес", icon: "minus" },
  { value: "gain", label: "Набрать", description: "Увеличить вес", icon: "arrow-up" },
] as const;

export const CALCULATION_FORMULAS = [
  { value: "mifflin_st_jeor", label: "Миффлина-Сан Жеора", description: "Самая точная современная формула" },
  { value: "harris_benedict", label: "Харриса-Бенедикта", description: "Классическая формула 1919 года" },
  { value: "katch_mcardle", label: "Кэтча-МакАрдла", description: "На основе сухой массы тела" },
] as const;