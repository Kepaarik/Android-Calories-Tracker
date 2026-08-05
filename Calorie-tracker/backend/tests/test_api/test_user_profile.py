# backend/tests/test_api/test_user_profile.py
import pytest
from httpx import AsyncClient


@pytest.mark.asyncio
async def test_get_profile_requires_auth(client: AsyncClient):
    """GET /api/profile/ без токена → 401."""
    resp = await client.get("/api/profile/")
    assert resp.status_code in (401, 403)


@pytest.mark.asyncio
async def test_create_profile_success(client: AsyncClient, auth_headers):
    """Успешное создание профиля."""
    payload = {
        "gender": "male",
        "age": 30,
        "weight_kg": 75.0,
        "height_cm": 180.0,
        "activity_level": "moderate",
        "fitness_goal": "maintain",
        "calculation_formula": "mifflin_st_jeor",
    }
    resp = await client.post("/api/profile/", json=payload, headers=auth_headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data["gender"] == "male"
    assert data["age"] == 30
    assert data["weight_kg"] == 75.0
    assert data["height_cm"] == 180.0
    assert "calculated_calories" in data
    assert data["calculated_calories"] > 0


@pytest.mark.asyncio
async def test_create_profile_with_custom_goals(client: AsyncClient, auth_headers):
    """Создание профиля с пользовательскими целями."""
    payload = {
        "gender": "female",
        "age": 25,
        "weight_kg": 60.0,
        "height_cm": 165.0,
        "activity_level": "light",
        "fitness_goal": "lose",
        "custom_calorie_goal": 1500.0,
        "custom_protein_goal": 120.0,
    }
    resp = await client.post("/api/profile/", json=payload, headers=auth_headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data["custom_calorie_goal"] == 1500.0
    assert data["custom_protein_goal"] == 120.0


@pytest.mark.asyncio
async def test_create_profile_invalid_age(client: AsyncClient, auth_headers):
    """Невалидный возраст (< 10) → 422."""
    payload = {
        "gender": "male",
        "age": 5,
        "weight_kg": 75.0,
        "height_cm": 180.0,
    }
    resp = await client.post("/api/profile/", json=payload, headers=auth_headers)
    assert resp.status_code == 422


@pytest.mark.asyncio
async def test_create_profile_invalid_weight(client: AsyncClient, auth_headers):
    """Невалидный вес (< 30) → 422."""
    payload = {
        "gender": "male",
        "age": 30,
        "weight_kg": 20.0,
        "height_cm": 180.0,
    }
    resp = await client.post("/api/profile/", json=payload, headers=auth_headers)
    assert resp.status_code == 422


@pytest.mark.asyncio
async def test_create_profile_invalid_height(client: AsyncClient, auth_headers):
    """Невалидный рост (< 100) → 422."""
    payload = {
        "gender": "male",
        "age": 30,
        "weight_kg": 75.0,
        "height_cm": 50.0,
    }
    resp = await client.post("/api/profile/", json=payload, headers=auth_headers)
    assert resp.status_code == 422


@pytest.mark.asyncio
async def test_get_profile_after_create(client: AsyncClient, auth_headers):
    """Получение профиля после создания."""
    # Создаём профиль
    create_payload = {
        "gender": "male",
        "age": 28,
        "weight_kg": 80.0,
        "height_cm": 175.0,
        "activity_level": "active",
        "fitness_goal": "gain",
    }
    await client.post("/api/profile/", json=create_payload, headers=auth_headers)

    # Получаем профиль
    resp = await client.get("/api/profile/", headers=auth_headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data["gender"] == "male"
    assert data["age"] == 28


@pytest.mark.asyncio
async def test_update_profile(client: AsyncClient, auth_headers):
    """Обновление профиля."""
    # Создаём профиль
    create_payload = {
        "gender": "male",
        "age": 25,
        "weight_kg": 70.0,
        "height_cm": 175.0,
    }
    await client.post("/api/profile/", json=create_payload, headers=auth_headers)

    # Обновляем
    update_payload = {"weight_kg": 72.0, "age": 26}
    resp = await client.put("/api/profile/", json=update_payload, headers=auth_headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data["weight_kg"] == 72.0
    assert data["age"] == 26


@pytest.mark.asyncio
async def test_update_profile_empty_data(client: AsyncClient, auth_headers):
    """Обновление с пустыми данными → 400."""
    # Создаём профиль
    create_payload = {
        "gender": "male",
        "age": 25,
        "weight_kg": 70.0,
        "height_cm": 175.0,
    }
    await client.post("/api/profile/", json=create_payload, headers=auth_headers)

    # Пытаемся обновить с пустыми данными
    resp = await client.put("/api/profile/", json={}, headers=auth_headers)
    assert resp.status_code == 400