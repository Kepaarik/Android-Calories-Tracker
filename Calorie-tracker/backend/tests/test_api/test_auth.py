# backend/tests/test_api/test_auth.py
import pytest
from httpx import AsyncClient


@pytest.mark.asyncio
async def test_register_success(client: AsyncClient):
    """Успешная регистрация."""
    payload = {
        "email": "newuser@example.com",
        "password": "password123",
    }
    resp = await client.post("/api/auth/register", json=payload)
    assert resp.status_code == 200
    data = resp.json()
    assert data["email"] == "newuser@example.com"
    assert "id" in data


@pytest.mark.asyncio
async def test_register_duplicate_email(client: AsyncClient, test_user):
    """Регистрация с существующим email → 400."""
    payload = {
        "email": test_user.email,
        "password": "password123",
    }
    resp = await client.post("/api/auth/register", json=payload)
    assert resp.status_code == 400


@pytest.mark.asyncio
async def test_register_invalid_email(client: AsyncClient):
    """Регистрация с невалидным email → 422."""
    payload = {
        "email": "not-an-email",
        "password": "password123",
    }
    resp = await client.post("/api/auth/register", json=payload)
    assert resp.status_code == 422


@pytest.mark.asyncio
async def test_login_success(client: AsyncClient, test_user):
    """Успешный логин."""
    payload = {
        "email": test_user.email,
        "password": "password123",
    }
    resp = await client.post("/api/auth/login", json=payload)
    assert resp.status_code == 200
    data = resp.json()
    assert "access_token" in data
    assert data["token_type"] == "bearer"


@pytest.mark.asyncio
async def test_login_wrong_password(client: AsyncClient, test_user):
    """Логин с неправильным паролем → 401."""
    payload = {
        "email": test_user.email,
        "password": "wrongpassword",
    }
    resp = await client.post("/api/auth/login", json=payload)
    assert resp.status_code == 401


@pytest.mark.asyncio
async def test_login_nonexistent_user(client: AsyncClient):
    """Логин несуществующего пользователя → 401."""
    payload = {
        "email": "nonexistent@example.com",
        "password": "password123",
    }
    resp = await client.post("/api/auth/login", json=payload)
    assert resp.status_code == 401


@pytest.mark.asyncio
async def test_get_me_requires_auth(client: AsyncClient):
    """GET /api/auth/me без токена → 401."""
    resp = await client.get("/api/auth/me")
    assert resp.status_code in (401, 403)


@pytest.mark.asyncio
async def test_get_me_success(client: AsyncClient, auth_headers, test_user):
    """Получение информации о текущем пользователе."""
    resp = await client.get("/api/auth/me", headers=auth_headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data["email"] == test_user.email