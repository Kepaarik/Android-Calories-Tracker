import os
from pathlib import Path
from pydantic_settings import BaseSettings, SettingsConfigDict

BASE_DIR = Path(__file__).resolve().parent.parent

class Settings(BaseSettings):
    DATABASE_URL: str = f"sqlite+aiosqlite:///{BASE_DIR / 'calorie_tracker.db'}"
    SECRET_KEY: str = "your-super-secret-key-change-in-production"
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24 * 7
    
    # Telegram
    BOT_TOKEN: str = ""
    TELEGRAM_BOT_TOKEN: str = ""  # Дублируем для совместимости
    TELEGRAM_API_ID: int = 0
    TELEGRAM_API_HASH: str = ""
    
    # Server
    BACKEND_PORT: int = 8081  # ← Нестандартный порт
    
    # CORS — разрешаем cloudflare домены
    CORS_ORIGINS: str = "http://localhost:5173,http://localhost:5174,http://127.0.0.1:5173,http://127.0.0.1:5174"

    model_config = SettingsConfigDict(
        env_file=str(BASE_DIR / ".env"),
        env_file_encoding="utf-8",
        extra="ignore",
    )

settings = Settings()