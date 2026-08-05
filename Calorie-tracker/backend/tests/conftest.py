import pytest
import pytest_asyncio
from httpx import AsyncClient, ASGITransport
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession, async_sessionmaker

from app.core.database import Base, get_db
from app.main import app
from app.db.models import User, Product
from app.core.security import create_access_token, get_password_hash

TEST_DATABASE_URL = "sqlite+aiosqlite:///:memory:"
from sqlalchemy.dialects.sqlite import JSON as SQLiteJSON

# Маппинг PostgreSQL типов на SQLite для тестов
from sqlalchemy import TypeDecorator, JSON

class JSONType(TypeDecorator):
    impl = SQLiteJSON
    cache_ok = True

@pytest_asyncio.fixture(scope="session")
async def engine():
    """Движок БД — создаётся один раз на всю сессию."""
    eng = create_async_engine(TEST_DATABASE_URL, echo=False)
    async with eng.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    yield eng
    await eng.dispose()


@pytest_asyncio.fixture(scope="session")
async def db_session(engine):
    """Сессия БД — одна на всю сессию тестов."""
    session_factory = async_sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)
    async with session_factory() as session:
        yield session


@pytest_asyncio.fixture(scope="session")
async def test_user(db_session):
    """Тестовый пользователь — создаётся один раз."""
    user = User(
        email="test@example.com",
        hashed_password=get_password_hash("password123"),
    )
    db_session.add(user)
    await db_session.commit()
    await db_session.refresh(user)
    return user


@pytest_asyncio.fixture(scope="session")
async def auth_headers(test_user):
    """Заголовки авторизации — один раз."""
    token = create_access_token({"sub": str(test_user.id)})
    return {"Authorization": f"Bearer {token}"}


@pytest_asyncio.fixture(scope="session")
async def sample_product(db_session):
    """Тестовый продукт — создаётся один раз."""
    product = Product(
        name="Яблоко",
        calories=52,
        proteins=0.3,
        fats=0.2,
        carbs=14,
    )
    db_session.add(product)
    await db_session.commit()
    await db_session.refresh(product)
    return product


@pytest_asyncio.fixture
async def client(db_session, test_user):
    """HTTP-клиент без авторизации — для каждого теста свой."""

    async def override_get_db():
        yield db_session

    app.dependency_overrides[get_db] = override_get_db
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as ac:
        yield ac
    app.dependency_overrides.clear()


# НОВАЯ ФИКСТУРА: авторизованный клиент
@pytest_asyncio.fixture
async def authenticated_client(db_session, test_user, auth_headers):
    """HTTP-клиент с авторизацией — для тестов требующих токен."""

    async def override_get_db():
        yield db_session

    app.dependency_overrides[get_db] = override_get_db
    transport = ASGITransport(app=app)
    async with AsyncClient(
        transport=transport, 
        base_url="http://test",
        headers=auth_headers  # ← добавляем токен
    ) as ac:
        yield ac
    app.dependency_overrides.clear()