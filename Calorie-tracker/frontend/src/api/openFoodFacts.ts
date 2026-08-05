import { barcodeCache } from "../utils/barcodeCache";

export interface OpenFoodFactsProduct {
  product_name?: string;
  product_name_ru?: string;
  generic_name?: string;
  generic_name_ru?: string;
  brands?: string;
  categories?: string;
  _keywords?: string[];
  ingredients_text?: string;
  ingredients_text_ru?: string;
  nutriments?: {
    "energy-kcal_100g"?: number;
    "energy-kcal_value"?: number;
    energy_100g?: number;
    energy_value?: number;
    proteins_100g?: number;
    proteins_value?: number;
    fat_100g?: number;
    fat_value?: number;
    carbohydrates_100g?: number;
    carbohydrates_value?: number;
  };
}

export interface OpenFoodFactsResponse {
  status: number;
  product?: OpenFoodFactsProduct;
}

const REQUEST_TIMEOUT = 10000; // 10 секунд
const MAX_RETRIES = 2;

const extractProductName = (
  product: OpenFoodFactsProduct,
  barcode: string
): string => {
  if (product.product_name_ru?.trim()) return product.product_name_ru.trim();
  if (product.product_name?.trim()) return product.product_name.trim();
  if (product.generic_name_ru?.trim()) return product.generic_name_ru.trim();
  if (product.generic_name?.trim()) return product.generic_name.trim();

  const ingredientsText =
    product.ingredients_text_ru || product.ingredients_text || "";
  if (ingredientsText) {
    const match = ingredientsText.match(/^([^:]+):/);
    if (match && match[1].trim().length > 3 && match[1].trim().length < 100) {
      let name = match[1].trim();
      name = name.replace(/средние значения.*$/i, "").trim();
      if (name.length > 3) return name;
    }
  }

  if (product._keywords && product._keywords.length > 0) {
    const keyword = product._keywords[0];
    if (keyword && keyword.length > 2) {
      return keyword.charAt(0).toUpperCase() + keyword.slice(1);
    }
  }

  if (product.categories?.trim()) {
    return product.categories.trim();
  }

  return `Продукт ${barcode}`;
};

const extractCaloriesFromText = (text: string): number | null => {
  if (!text) return null;

  const kcalMatch = text.match(
    /(\d+(?:[.,]\d+)?)\s*(?:ккал|ккал\.|kkal|kcal)/i
  );
  if (kcalMatch) {
    const value = parseFloat(kcalMatch[1].replace(",", "."));
    if (!isNaN(value) && value > 0 && value < 10000) {
      return value;
    }
  }

  const kjMatch = text.match(/(\d+(?:[.,]\d+)?)\s*(?:кдж|кДж|кДж\.|kj)/i);
  if (kjMatch) {
    const kjValue = parseFloat(kjMatch[1].replace(",", "."));
    if (!isNaN(kjValue) && kjValue > 0 && kjValue < 50000) {
      return Math.round(kjValue * 0.239);
    }
  }

  return null;
};

/**
 * Один запрос к API с таймаутом
 */
const fetchWithTimeout = async (
  url: string,
  timeoutMs: number
): Promise<Response> => {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), timeoutMs);

  try {
    const response = await fetch(url, { signal: controller.signal });
    clearTimeout(timeoutId);
    return response;
  } catch (err) {
    clearTimeout(timeoutId);
    throw err;
  }
};

/**
 * Получение данных о продукте по штрихкоду с кэшем и retry
 */
export const getProductByBarcode = async (
  barcode: string
): Promise<OpenFoodFactsProduct | null> => {
  // 1. Проверяем кэш
  const cached = barcodeCache.get(barcode);
  if (cached) {
    console.log(`✓ Cache hit for barcode ${barcode}`);
    return cached;
  }

  // 2. Пробуем получить из API с retry
  for (let attempt = 0; attempt <= MAX_RETRIES; attempt++) {
    try {
      console.log(
        `Fetching barcode ${barcode} (attempt ${attempt + 1}/${
          MAX_RETRIES + 1
        })`
      );

      const response = await fetchWithTimeout(
        `https://world.openfoodfacts.org/api/v2/product/${barcode}.json`,
        REQUEST_TIMEOUT
      );

      if (!response.ok) {
        if (response.status === 404) {
          return null; // Продукта нет, retry не нужен
        }
        throw new Error(`API error: ${response.status}`);
      }

      const data: OpenFoodFactsResponse = await response.json();

      if (data.status === 1 && data.product) {
        // Сохраняем в кэш
        barcodeCache.set(barcode, data.product);
        return data.product;
      }

      return null;
    } catch (error: any) {
      console.error(
        `Attempt ${attempt + 1} failed for ${barcode}:`,
        error.message
      );

      if (attempt === MAX_RETRIES) {
        console.error("All retry attempts failed");
        return null;
      }

      // Exponential backoff: 1с, 2с
      await new Promise((resolve) => setTimeout(resolve, 1000 * (attempt + 1)));
    }
  }

  return null;
};

/**
 * Конвертация данных из Open Food Facts в формат приложения
 */
export const convertToProductData = (
  product: OpenFoodFactsProduct,
  barcode: string
) => {
  const productName = extractProductName(product, barcode);
  const brands = product.brands?.trim() || "";
  const fullName =
    brands && !productName.toLowerCase().includes(brands.toLowerCase())
      ? `${productName} (${brands})`
      : productName;

  const nutriments = product.nutriments || {};

  let calories =
    nutriments["energy-kcal_100g"] ?? nutriments["energy-kcal_value"];
  if (!calories && nutriments["energy_100g"]) {
    calories = Math.round(nutriments["energy_100g"] * 0.239);
  } else if (!calories && nutriments["energy_value"]) {
    calories = Math.round(nutriments["energy_value"] * 0.239);
  }

  if (!calories) {
    const text = product.ingredients_text_ru || product.ingredients_text || "";
    const extracted = extractCaloriesFromText(text);
    if (extracted) {
      calories = extracted;
    }
  }

  const proteins = nutriments.proteins_100g ?? nutriments.proteins_value ?? 0;
  const fats = nutriments.fat_100g ?? nutriments.fat_value ?? 0;
  const carbs =
    nutriments.carbohydrates_100g ?? nutriments.carbohydrates_value ?? 0;

  return {
    name: fullName,
    calories: calories ? Math.round(calories * 10) / 10 : 0,
    proteins: Math.round(proteins * 10) / 10,
    fats: Math.round(fats * 10) / 10,
    carbs: Math.round(carbs * 10) / 10,
    barcode,
  };
};
