# backend/seed_products.py
import asyncio
from sqlalchemy import select

# Импорты для создания таблиц
from app.core.database import engine, Base, async_session_maker

# Импорты всех моделей, чтобы Base.metadata знал о них
from app.db.models.user import User
from app.db.models.product import Product
from app.db.models.diary import DiaryEntry

# Попробуем разные варианты импорта weight модели
try:
    from app.db.models.weight_entry import WeightEntry
except ImportError:
    try:
        from app.db.models.weight import WeightEntry
    except ImportError:
        pass  # Если модели нет - пропускаем

PRODUCTS = [
    # Мясо и птица
    {"name": "Куриная грудка варёная", "calories": 113, "proteins": 23.6, "fats": 1.9, "carbs": 0.4},
    {"name": "Говядина варёная", "calories": 254, "proteins": 25.8, "fats": 16.8, "carbs": 0},
    {"name": "Свинина нежирная", "calories": 160, "proteins": 19.4, "fats": 9.1, "carbs": 0},
    {"name": "Индейка грудка", "calories": 104, "proteins": 23.5, "fats": 1.0, "carbs": 0},
    
    # Рыба
    {"name": "Лосось запечённый", "calories": 208, "proteins": 22.5, "fats": 12.8, "carbs": 0},
    {"name": "Тунец консервированный", "calories": 96, "proteins": 21.5, "fats": 1.0, "carbs": 0},
    {"name": "Креветки варёные", "calories": 95, "proteins": 18.9, "fats": 2.2, "carbs": 0},
    
    # Молочка и яйца
    {"name": "Яйцо куриное варёное", "calories": 155, "proteins": 12.6, "fats": 10.6, "carbs": 0.7},
    {"name": "Творог 5%", "calories": 121, "proteins": 17.2, "fats": 5.0, "carbs": 1.8},
    {"name": "Йогурт натуральный", "calories": 60, "proteins": 4.0, "fats": 1.5, "carbs": 7.0},
    {"name": "Сыр твёрдый", "calories": 360, "proteins": 25.0, "fats": 28.0, "carbs": 2.0},
    {"name": "Молоко 2.5%", "calories": 52, "proteins": 2.8, "fats": 2.5, "carbs": 4.7},
    
    # Крупы
    {"name": "Гречка варёная", "calories": 110, "proteins": 4.5, "fats": 1.1, "carbs": 21.3},
    {"name": "Рис белый варёный", "calories": 130, "proteins": 2.7, "fats": 0.3, "carbs": 28.0},
    {"name": "Овсянка на воде", "calories": 88, "proteins": 3.0, "fats": 1.7, "carbs": 15.0},
    {"name": "Макароны варёные", "calories": 157, "proteins": 5.8, "fats": 1.5, "carbs": 29.7},
    
    # Овощи
    {"name": "Огурец свежий", "calories": 15, "proteins": 0.8, "fats": 0.1, "carbs": 2.8},
    {"name": "Томат свежий", "calories": 18, "proteins": 1.1, "fats": 0.2, "carbs": 3.7},
    {"name": "Картофель варёный", "calories": 82, "proteins": 2.0, "fats": 0.4, "carbs": 16.7},
    {"name": "Брокколи варёная", "calories": 35, "proteins": 3.0, "fats": 0.4, "carbs": 6.0},
    
    # Фрукты
    {"name": "Яблоко", "calories": 47, "proteins": 0.4, "fats": 0.4, "carbs": 9.8},
    {"name": "Банан", "calories": 95, "proteins": 1.5, "fats": 0.5, "carbs": 21.0},
    {"name": "Апельсин", "calories": 43, "proteins": 0.9, "fats": 0.2, "carbs": 8.1},
    
    # Хлеб
    {"name": "Хлеб белый", "calories": 266, "proteins": 8.1, "fats": 1.0, "carbs": 52.0},
    {"name": "Хлеб чёрный", "calories": 214, "proteins": 6.6, "fats": 1.2, "carbs": 43.0},
    
    # Готовые блюда
    {"name": "Борщ", "calories": 49, "proteins": 1.1, "fats": 2.2, "carbs": 6.7},
    {"name": "Плов с курицей", "calories": 150, "proteins": 7.0, "fats": 5.0, "carbs": 20.0},
    {"name": "Оливье", "calories": 198, "proteins": 5.5, "fats": 14.0, "carbs": 12.0},
    {"name": "Пельмени варёные", "calories": 275, "proteins": 11.9, "fats": 12.4, "carbs": 29.0},
    
    # Напитки
    {"name": "Сок апельсиновый", "calories": 45, "proteins": 0.9, "fats": 0.2, "carbs": 10.0},
    {"name": "Кола", "calories": 42, "proteins": 0, "fats": 0, "carbs": 10.6},
    
    # Сладости и орехи
    {"name": "Шоколад молочный", "calories": 550, "proteins": 6.9, "fats": 35.7, "carbs": 54.4},
    {"name": "Мёд", "calories": 329, "proteins": 0.8, "fats": 0, "carbs": 81.5},
    {"name": "Миндаль", "calories": 579, "proteins": 21.2, "fats": 49.9, "carbs": 9.5},
]


async def seed():
    # 1. Создаем все таблицы в БД, если их нет
    print("🔧 Проверяем и создаем таблицы...")
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    print("✅ Таблицы готовы")

    # 2. Заполняем базу продуктами (не трогая уже существующие, включая добавленные пользователями)
    async with async_session_maker() as session:
        result = await session.execute(select(Product.name))
        existing_names = {name for (name,) in result.all()}

        print("➕  Добавляем недостающие продукты...")
        added = 0
        for data in PRODUCTS:
            if data["name"] in existing_names:
                continue
            product = Product(
                user_id=None,          # None = общий продукт для всех пользователей
                is_composite=False,    # Обычный продукт, не составной
                **data
            )
            session.add(product)
            added += 1

        await session.commit()
        print(f"✅ Добавлено {added} новых продуктов (пропущено уже существующих: {len(PRODUCTS) - added})")


if __name__ == "__main__":
    asyncio.run(seed())