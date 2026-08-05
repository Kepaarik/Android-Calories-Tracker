import { useState, useEffect } from "react";
import { createPortal } from "react-dom";
import { useNavigate } from "react-router-dom";
import { productsApi } from "../api/endpoints";
import { Product } from "../types/api";
import PageHeader from "../components/ui/PageHeader";
import GlassCard from "../components/ui/GlassCard";
import GlassButton from "../components/ui/GlassButton";
import Icon from "../components/ui/Icon";
import Modal from "../components/ui/Modal";
import AddProductModal from "../components/products/AddProductModal";
import EditProductModal from "../components/products/EditProductModal";
import "./ProductsPage.css";
import Toast from "../components/ui/Toast";
import BuildDishModal from "../components/products/BuildDishModal";
import { useToast } from "../context/ToastContext";

type SortField = "name" | "calories" | "proteins" | "fats" | "carbs";
type SortOrder = "asc" | "desc";

interface DeletedProduct {
  product: Product;
  timeoutId: ReturnType<typeof setTimeout>;
}

const SORT_OPTIONS: { value: SortField; label: string }[] = [
  { value: "name", label: "Название" },
  { value: "calories", label: "Ккал" },
  { value: "proteins", label: "Белки" },
  { value: "fats", label: "Жиры" },
  { value: "carbs", label: "Углеводы" },
];

export default function ProductsPage() {
  const toast = useToast();
  const navigate = useNavigate();
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [sortBy, setSortBy] = useState<SortField>("name");
  const [sortOrder, setSortOrder] = useState<SortOrder>("asc");
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [deletingProduct, setDeletingProduct] = useState<Product | null>(null);
  const [deletedProduct, setDeletedProduct] = useState<DeletedProduct | null>(
    null
  );
  const [isBuildModalOpen, setIsBuildModalOpen] = useState(false);

  useEffect(() => {
    loadProducts();
  }, [searchQuery, sortBy, sortOrder]);

  useEffect(() => {
    return () => {
      if (deletedProduct?.timeoutId) {
        clearTimeout(deletedProduct.timeoutId);
      }
    };
  }, [deletedProduct]);

const loadProducts = async () => {
  setIsLoading(true);
  try {
    const res = await productsApi.getAll({
      search: searchQuery,
      sort_by: sortBy,
      sort_order: sortOrder,
    });
    setProducts(res.data);
  } catch (err) {
    console.error("Ошибка загрузки продуктов:", err);
  } finally {
    setIsLoading(false);
  }
};
  const handleSort = (field: SortField) => {
    if (sortBy === field) {
      setSortOrder((prev) => (prev === "asc" ? "desc" : "asc"));
    } else {
      setSortBy(field);
      setSortOrder("asc");
    }
  };

  const confirmDelete = (product: Product) => {
    setDeletingProduct(product);
  };

  const handleDelete = async () => {
    if (!deletingProduct) return;

    try {
      await productsApi.delete(deletingProduct.id);

      // Удаляем из списка сразу
      setProducts((prev) => prev.filter((p) => p.id !== deletingProduct.id));

      // Показываем toast через 100ms чтобы модалка успела закрыться
      setTimeout(() => {
        const timeoutId = setTimeout(() => {
          setDeletedProduct(null);
        }, 5000);

        setDeletedProduct({
          product: deletingProduct,
          timeoutId,
        });
      }, 100);

      setDeletingProduct(null);
    } catch (err: any) {
      toast.error(err.response?.data?.detail || "Ошибка удаления");
      setDeletingProduct(null);
    }
  };

  const handleUndoDelete = async () => {
    if (!deletedProduct) return;

    clearTimeout(deletedProduct.timeoutId);

    try {
      await productsApi.create({
        name: deletedProduct.product.name,
        calories: deletedProduct.product.calories,
        proteins: deletedProduct.product.proteins,
        fats: deletedProduct.product.fats,
        carbs: deletedProduct.product.carbs,
      });

      loadProducts();
      setDeletedProduct(null);
    } catch (err) {
      console.error("Ошибка восстановления:", err);
      loadProducts();
      setDeletedProduct(null);
    }
  };

  const closeDeleteModal = () => {
    setDeletingProduct(null);
  };

  return (
    <div className="container products-page-container">
      <PageHeader
        title="База продуктов"
        subtitle={
          searchQuery.trim()
            ? `Найдено: ${products.length}`
            : `Всего: ${products.length}`
        }
        onBack={() => navigate(-1)}
        actions={
          <GlassButton
            variant="success"
            onClick={() => setIsAddModalOpen(true)}
          >
            <Icon name="plus" size={16} />
            Добавить
          </GlassButton>
        }
      />

      {/* Поиск */}
      {/* Поиск + Кнопка сборки блюда */}
      <div className="products-page-search-row">
        <input
          type="text"
          className="glass-input products-page-search-input"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Поиск по названию..."
        />
        <GlassButton
          onClick={() => setIsBuildModalOpen(true)}
          className="products-page-build-dish-btn"
        >
          <Icon name="mix" size={18} />
          Собрать блюдо
        </GlassButton>
      </div>

      {/* Сортировка */}
      <div className="sort-container">
        {SORT_OPTIONS.map((option) => {
          const isActive = sortBy === option.value;
          return (
            <button
              key={option.value}
              onClick={() => handleSort(option.value)}
              className={`sort-btn ${isActive ? "active" : ""}`}
            >
              {option.label}
              {isActive && (
                <Icon
                  name={sortOrder === "asc" ? "arrow-up" : "arrow-down"}
                  size={14}
                />
              )}
            </button>
          );
        })}
      </div>

      {/* Список продуктов */}
      {isLoading ? (
        <GlassCard className="products-page-state-card" padding="40px">
          Загрузка...
        </GlassCard>
      ) : products.length === 0 ? (
        <GlassCard className="products-page-state-card" padding="40px">
          <p className="products-page-empty-message">
            {searchQuery ? "Ничего не найдено" : "Нет продуктов"}
          </p>
        </GlassCard>
      ) : (
        <div className="products-grid">
          {products.map((product) => (
            <div key={product.id} className="product-card">
              <div
                className="product-content"
                onClick={() => setEditingProduct(product)}
              >
                <div className="product-name">{product.name}</div>
                <div className="product-nutrients">
                  <span className="nutrient calories">
                    <span className="nutrient-value">
                      {Math.round(product.calories)}
                    </span>
                    <span className="nutrient-label">ккал</span>
                  </span>
                  <span className="nutrient">
                    <span className="nutrient-value">
                      {product.proteins.toFixed(1)}
                    </span>
                    <span className="nutrient-label">Б</span>
                  </span>
                  <span className="nutrient">
                    <span className="nutrient-value">
                      {product.fats.toFixed(1)}
                    </span>
                    <span className="nutrient-label">Ж</span>
                  </span>
                  <span className="nutrient">
                    <span className="nutrient-value">
                      {product.carbs.toFixed(1)}
                    </span>
                    <span className="nutrient-label">У</span>
                  </span>
                </div>
              </div>
              <div className="product-actions">
                <button
                  className="action-btn edit"
                  onClick={() => setEditingProduct(product)}
                  title="Редактировать"
                >
                  <Icon name="edit" size={16} />
                </button>
                <button
                  className="action-btn delete"
                  onClick={() => confirmDelete(product)}
                  title="Удалить"
                >
                  <Icon name="trash" size={16} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Модалка подтверждения удаления */}
      <Modal
        isOpen={!!deletingProduct}
        onClose={closeDeleteModal}
        title="Подтвердите действие"
      >
        <div className="products-page-delete-modal-body">
          <p
            className="products-page-delete-modal-text"
          >
            Удалить продукт <strong>"{deletingProduct?.name}"</strong>?
          </p>
          <div className="products-page-delete-modal-actions">
            <GlassButton
              type="button"
              onClick={closeDeleteModal}
              fullWidth
              className="products-page-delete-modal-btn"
            >
              Отмена
            </GlassButton>
            <GlassButton
              type="button"
              variant="danger"
              onClick={handleDelete}
              fullWidth
              className="products-page-delete-modal-btn"
            >
              Удалить
            </GlassButton>
          </div>
        </div>
      </Modal>

      {/* Модалки */}
      <AddProductModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onAdded={loadProducts}
      />

      <BuildDishModal
        isOpen={isBuildModalOpen}
        onClose={() => setIsBuildModalOpen(false)}
        onCreated={() => {
          setIsBuildModalOpen(false);
          loadProducts();
        }}
      />

      <EditProductModal
        product={editingProduct}
        isOpen={!!editingProduct}
        onClose={() => setEditingProduct(null)}
        onUpdated={loadProducts}
      />

      {/* Toast с отменой удаления - рендерится через портал в body */}
      {deletedProduct && (
        <Toast
          message="Продукт удалён"
          subtitle={`"${deletedProduct.product.name}"`}
          onUndo={handleUndoDelete}
          onClose={() => {
            clearTimeout(deletedProduct.timeoutId);
            setDeletedProduct(null);
          }}
        />
      )}
    </div>
  );
}
