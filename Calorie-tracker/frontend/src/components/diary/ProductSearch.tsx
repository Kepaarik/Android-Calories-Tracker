// frontend/src/components/products/ProductSearch.tsx
import { useState, useEffect, useRef } from "react";
import { productsApi } from "../../api/endpoints";
import { Product } from "../../types/api";
import Skeleton from "../ui/Skeleton";
import DropdownPortal from "../ui/DropdownPortal";
import "./ProductSearch.css";

interface ProductSearchProps {
  onSelect: (product: Product) => void;
}

export default function ProductSearch({ onSelect }: ProductSearchProps) {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (query.length < 2) {
      setResults([]);
      setError(null);
      return;
    }

    const timer = setTimeout(async () => {
      setIsLoading(true);
      setError(null);
      try {
        const res = await productsApi.getAll({ search: query });
        setResults(res.data);
        if (res.data.length === 0) {
          setError("Ничего не найдено");
        }
      } catch (err: any) {
        const message = err.response?.data?.detail || "Ошибка поиска";
        setError(message);
        console.error("Search error:", err);
      } finally {
        setIsLoading(false);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [query]);

  return (
    <div className="product-search-wrapper">
      <input
        ref={inputRef}
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        className="glass-input product-search-input"
        placeholder="Поиск продукта..."
      />

      {isLoading && (
        <div className="product-search-loading">
          <Skeleton variant="rect" height="40px" />
        </div>
      )}

      {error && !isLoading && (
        <div
          className={`product-search-error ${
            error === "Ничего не найдено"
              ? "product-search-error-not-found"
              : "product-search-error-message"
          }`}
        >
          {error}
        </div>
      )}

      <DropdownPortal isOpen={results.length > 0} anchorRef={inputRef}>
        {results.map((product) => (
          <div
            key={product.id}
            onClick={() => {
              onSelect(product);
              setQuery("");
              setResults([]);
            }}
            className="product-search-result-item"
          >
            <div
              className="product-search-result-name"
            >
              {product.name}
              {product.is_composite && (
                <span
                  className="product-search-composite-badge"
                >
                  🍽️ блюдо
                </span>
              )}
            </div>
            <div
              className="product-search-result-macros"
            >
              {Math.round(product.calories)} ккал • Б:{product.proteins} Ж:
              {product.fats} У:{product.carbs}
            </div>
          </div>
        ))}
      </DropdownPortal>
    </div>
  );
}
