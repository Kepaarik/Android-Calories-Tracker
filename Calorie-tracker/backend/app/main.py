from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi import Request
from fastapi.responses import JSONResponse
from sqlalchemy import text
from app.api.routers import auth, diary, products, telegram_auth, user_profile, weight
from app.core.config import settings
from app.core.database import engine
import warnings

if not settings.BOT_TOKEN:
    warnings.warn(
        "BOT_TOKEN is not configured — Telegram initData validation will compute "
        "signatures against an empty secret, so Telegram login will not work. "
        "Set BOT_TOKEN in backend/.env.",
        RuntimeWarning,
    )

app = FastAPI(title="Calorie Tracker API")

# Парсим CORS origins из переменной окружения
origins = [o.strip() for o in settings.CORS_ORIGINS.split(",") if o.strip()]

# Добавляем wildcard для cloudflare (важно для тестов!)
origins.append("https://*.trycloudflare.com")

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router)
app.include_router(products.router)
app.include_router(diary.router)
app.include_router(user_profile.router)
app.include_router(weight.router)
app.include_router(telegram_auth.router, prefix="/api/auth", tags=["auth"])


@app.on_event("startup")
async def ensure_user_profile_columns():
    """Добавляет колонки, добавленные в модель UserProfile без формальной
    Alembic-миграции (theme, accent_color), если их ещё нет в БД"""
    is_sqlite = settings.DATABASE_URL.startswith("sqlite")
    for column in ("theme", "accent_color"):
        try:
            async with engine.begin() as conn:
                if is_sqlite:
                    await conn.execute(text(f"ALTER TABLE user_profiles ADD COLUMN {column} VARCHAR"))
                else:
                    await conn.execute(text(f"ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS {column} VARCHAR"))
        except Exception:
            pass  # колонка уже существует


@app.get("/")
async def root():
    return {"message": "Calorie Tracker API", "status": "running"}

@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    import traceback
    traceback.print_exc()
    return JSONResponse(
        status_code=500,
        content={"detail": str(exc)},
    )

@app.get("/health")
async def health():
    return {"status": "healthy"}