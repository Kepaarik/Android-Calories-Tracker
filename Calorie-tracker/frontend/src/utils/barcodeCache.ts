interface CachedProduct {
  data: any;
  timestamp: number;
  barcode: string;
}

interface CacheStore {
  [barcode: string]: CachedProduct;
}

const CACHE_KEY = "calorie_tracker_barcode_cache";
const CACHE_DURATION = 24 * 60 * 60 * 1000;
const MAX_CACHE_SIZE = 200;

const readCache = (): CacheStore => {
  try {
    const raw = localStorage.getItem(CACHE_KEY);
    if (!raw) return {};
    const parsed = JSON.parse(raw);
    return typeof parsed === "object" && parsed !== null ? parsed : {};
  } catch {
    return {};
  }
};

const writeCache = (cache: CacheStore): void => {
  try {
    localStorage.setItem(CACHE_KEY, JSON.stringify(cache));
  } catch {
    try {
      localStorage.removeItem(CACHE_KEY);
      localStorage.setItem(CACHE_KEY, JSON.stringify(cache));
    } catch {
      // Игнорируем
    }
  }
};

const purgeExpired = (cache: CacheStore): CacheStore => {
  const now = Date.now();
  const cleaned: CacheStore = {};
  for (const [barcode, entry] of Object.entries(cache)) {
    if (now - entry.timestamp < CACHE_DURATION) {
      cleaned[barcode] = entry;
    }
  }
  return cleaned;
};

const enforceMaxSize = (cache: CacheStore): CacheStore => {
  const entries = Object.values(cache);
  if (entries.length <= MAX_CACHE_SIZE) return cache;
  entries.sort((a, b) => a.timestamp - b.timestamp);
  const keep = entries.slice(entries.length - MAX_CACHE_SIZE);
  const trimmed: CacheStore = {};
  for (const entry of keep) {
    trimmed[entry.barcode] = entry;
  }
  return trimmed;
};

export const barcodeCache = {
  get(barcode: string): any | null {
    try {
      const cache = readCache();
      const entry = cache[barcode];
      if (!entry) return null;
      if (Date.now() - entry.timestamp > CACHE_DURATION) {
        delete cache[barcode];
        writeCache(cache);
        return null;
      }
      return entry.data;
    } catch {
      return null;
    }
  },

  set(barcode: string, data: any): void {
    try {
      let cache = readCache();
      cache = purgeExpired(cache);
      cache[barcode] = { data, timestamp: Date.now(), barcode };
      cache = enforceMaxSize(cache);
      writeCache(cache);
    } catch {
      // Игнорируем
    }
  },

  remove(barcode: string): void {
    try {
      const cache = readCache();
      if (cache[barcode]) {
        delete cache[barcode];
        writeCache(cache);
      }
    } catch {
      // Игнорируем
    }
  },

  clear(): void {
    try {
      localStorage.removeItem(CACHE_KEY);
    } catch {
      // Игнорируем
    }
  },
};
