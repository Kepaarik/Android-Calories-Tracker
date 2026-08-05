# 🍽️ Calorie Tracker

Веб - приложение для подсчёта калорий с поддержкой Telegram Web App.

## 📸 Возможности

- 📊 Дневник питания с группировкой по приёмам пищи
- 🔍 Умный поиск продуктов (регистронезависимый, с поддержкой кириллицы)
- 📈 Статистика за день и неделю с графиками
- 💧 Трекер потребления воды
- 🎯 Персональный расчёт нормы КБЖУ по формуле Миффлина-Сан Жеора
- 🔄 Undo для удалённых записей (5 секунд на отмену)
- 📱 Адаптивный дизайн с liquid glass эффектом
- 🌙 Светлая/тёмная тема
- 🤖 Интеграция с Telegram Web App (MainButton, BackButton, Haptic Feedback)
- 📦 База продуктов с автокатегоризацией (белковый/жировой/углеводный)

## 🛠️ Стек технологий

### Backend

- Python 3.12
- FastAPI — веб-фреймворк
- SQLAlchemy 2.0 (async) — ORM
- Alembic — миграции БД
- SQLite — база данных (для разработки)
- Pydantic — валидация данных
- python-jose — JWT токены
- passlib + bcrypt — хэширование паролей

### Frontend

- React 18 + TypeScript
- Vite — сборщик
- React Router — маршрутизация
- Zustand — state management
- Axios — HTTP клиент
- Recharts — графики
- date-fns — работа с датами
- @twa-dev/sdk — Telegram Web App SDK

### Bot

- aiogram 3.x — Telegram Bot API
- asyncio — асинхронность

## 📁 Структура проекта

calorie-tracker/ ├── backend/ # FastAPI сервер │ ├── app/ │ │ ├── api/ # Роутеры и схемы │ │ ├── core/ # Конфигурация, БД, auth │ │ ├── db/ # Модели и CRUD │ │ ├── services/ # Бизнес-логика │ │ └── main.py # Точка входа │ ├── migrations/ # Alembic миграции │ ├── requirements.txt │ └── seed_products.py # Сиды продуктов ├── frontend/ # React приложение │ ├── src/ │ │ ├── api/ # API клиент │ │ ├── components/ # React компоненты │ │ ├── hooks/ # Кастомные хуки │ │ ├── pages/ # Страницы │ │ ├── store/ # Zustand store │ │ ├── types/ # TypeScript типы │ │ └── App.tsx │ └── package.json └── bot/ # Telegram бот ├── handlers/ # Обработчики команд ├── keyboards/ # Клавиатуры ├── main.py └── .env

## ⚙️ Требования

- Python 3.12 (обязательно, 3.14 не поддерживается)
- Node.js 18+ и npm
- Git (опционально)

Проверка версий:
bash python --version # Должно быть 3.12.x node --version # Должно быть 18+ npm --version

## 🚀 Установка и запуск

### 1. Клонирование репозитория

bash git clone <your-repo-url> cd calorie-tracker

### 2. Backend

cd backend 
# Создать виртуальное окружение (ОБЯЗАТЕЛЬНО Python 3.12) py -3.12 -m venv venv 
# Активировать venv # Windows PowerShell: .\venv\Scripts\Activate.ps1 
# Windows CMD: .\venv\Scripts\activate.bat 
# Linux/Mac: source venv/bin/activate 
# Установить зависимости pip install -r requirements.txt 
# Инициализировать миграции (если ещё не сделано) alembic init migrations 
# Заменить содержимое migrations/env.py на async версию 
# (см. раздел "Настройка миграций" ниже) 
# Применить миграции alembic revision --autogenerate -m "initial migration" alembic upgrade head 
# (Опционально) Заполнить БД тестовыми продуктами python seed_products.py 
# Запустить сервер uvicorn app.main:app --reload --host 0.0.0.0 --port 8000

Backend будет доступен по адресу:

- API: http://localhost:8000
- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

### 3. Frontend

Откройте новый терминал:

bash cd frontend 
# Установить зависимости npm install 
# Запустить dev-сервер npm run dev

Frontend будет доступен по адресу: http://localhost:3000

### 4. Telegram Bot (опционально)

Откройте новый терминал:

bash cd bot # Создать venv py -3.12 -m venv venv .\venv\Scripts\Activate.ps1 # Установить зависимости pip install aiogram==3.15.0 python-dotenv==1.0.1 pydantic-settings # Создать .env файл # BOT_TOKEN=<токен от @BotFather> # WEBAPP_URL=<URL вашего фронтенда> # Запустить бота python main.py

## 🔧 Настройка миграций

Файл backend/migrations/env.py должен быть настроен для async работы:

python import sys import os BACKEND_DIR = os.path.dirname(os.path.dirname(os.path.abspath(**file**))) sys.path.insert(0, BACKEND_DIR) import asyncio from logging.config import fileConfig from sqlalchemy import pool from sqlalchemy.engine import Connection from sqlalchemy.ext.asyncio import async_engine_from_config from alembic import context from app.core.database import Base from app.db.models import User, Product, DiaryEntry, UserProfile config = context.config from app.core.config import settings config.set_main_option("sqlalchemy.url", settings.DATABASE_URL) if config.config_file_name is not None: fileConfig(config.config_file_name) target_metadata = Base.metadata def run_migrations_offline() -> None: url = config.get_main_option("sqlalchemy.url") context.configure(url=url, target_metadata=target_metadata, literal_binds=True) with context.begin_transaction(): context.run_migrations() def do_run_migrations(connection: Connection) -> None: context.configure(connection=connection, target_metadata=target_metadata) with context.begin_transaction(): context.run_migrations() async def run_async_migrations() -> None: connectable = async_engine_from_config( config.get_section(config.config_ini_section, {}), prefix="sqlalchemy.", poolclass=pool.NullPool, ) async with connectable.connect() as connection: await connection.run_sync(do_run_migrations) await connectable.dispose() def run_migrations_online() -> None: asyncio.run(run_async_migrations()) if context.is_offline_mode(): run_migrations_offline() else: run_migrations_online()

## 🔐 Переменные окружения

### Backend (backend/.env)

env DATABASE_URL=sqlite+aiosqlite:///./calorie_tracker.db SECRET_KEY=your-super-secret-key-change-this ALGORITHM=HS256 ACCESS_TOKEN_EXPIRE_MINUTES=1440

### Bot (bot/.env)

env BOT_TOKEN=1234567890:ABCdefGHIjklMNOpqrsTUVwxyz WEBAPP_URL=https://your-domain.com

Получить токен бота: @BotFather → /newbot

## 🐛 Решение типичных проблем

### ❌ bcrypt ошибка при установке

Проблема: AttributeError: module 'bcrypt' has no attribute '**about**'

Решение: понизить версию bcrypt
bash pip uninstall bcrypt -y pip install bcrypt==4.0.1

### ❌ Can't locate revision identified by 'xxx'

Проблема: рассинхронизация миграций

Решение: полная очистка и пересоздание
powershell cd backend Remove-Item calorie_tracker.db -Force Remove-Item -Recurse -Force migrations alembic init migrations # Заменить env.py (см. раздел "Настройка миграций") alembic revision --autogenerate -m "initial" alembic upgrade head

### ❌ pydantic-core не компилируется

Проблема: используется Python 3.14

Решение: использовать Python 3.12
powershell Remove-Item -Recurse -Force venv py -3.12 -m venv venv .\venv\Scripts\Activate.ps1 pip install -r requirements.txt

### ❌ Старая версия фронтенда в браузере

Решение:
powershell cd frontend Remove-Item -Recurse -Force node_modules\.vite npm run dev
В браузере: Ctrl+Shift+R (жёсткая перезагрузка)

### ❌ ModuleNotFoundError: No module named 'bot'

Решение: запускать из корня проекта
powershell cd calorie-tracker python -m bot.main

### ❌ Поиск не находит кириллицу

Решение: в backend/app/core/database.py должна быть регистрация кастомной функции lower():
python @event.listens_for(engine.sync_engine, "connect") def set_sqlite_pragma(dbapi_connection, connection_record): if settings.DATABASE_URL.startswith("sqlite"): dbapi_connection.create_function("lower", 1, lambda x: x.lower() if x else None)

## 📱 Telegram Web App

Для тестирования в Telegram нужен HTTPS URL. Варианты:

### Cloudflare Tunnel (рекомендуется)

powershell # Установка winget install Cloudflare.cloudflared # Запуск cloudflared tunnel --url http://localhost:3000

### localtunnel

powershell npx localtunnel --port 3000

Полученный URL вставить в bot/.env → WEBAPP_URL

## 🧪 Тестирование API

1. Зарегистрироваться: POST /api/auth/register
   json {"email": "test@example.com", "password": "test123"}
2. Войти: POST /api/auth/login → получить токен
3. Использовать токен в заголовке: Authorization: Bearer <token>

## 📝 Лицензия

MIT

## 👨‍ Разработка

Проект находится в активной разработке. Текущий статус:

- ✅ Базовый функционал дневника
- ✅ Расчёт суточной нормы КБЖУ
- ✅ Статистика за неделю
- ✅ Удаление с undo
- 🔄 Telegram интеграция (в процессе)
- ⏳ Экспорт данных
- ⏳ PWA
