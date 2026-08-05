# backend/tests/test_api/test_weight.py
import pytest
from httpx import AsyncClient
from datetime import date


@pytest.mark.asyncio
async def test_get_weight_requires_auth(client: AsyncClient):
    """GET /api/weight/ без токена → 401."""
    resp = await client.get("/api/weight/")
    assert resp.status_code in (401, 403)


@pytest.mark.asyncio
async def test_add_weight_requires_auth(client: AsyncClient):
    """POST /api/weight/ без токена → 401."""
    payload = {"weight_kg": 70.5}
    resp = await client.post("/api/weight/", json=payload)
    assert resp.status_code in (401, 403)


@pytest.mark.asyncio
async def test_add_weight_success(client: AsyncClient, auth_headers):
    """Успешное добавление записи веса."""
    payload = {
        "weight_kg": 75.5,
        "date": str(date.today()),
    }
    resp = await client.post("/api/weight/", json=payload, headers=auth_headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data["weight_kg"] == 75.5
    assert "recorded_at" in data


@pytest.mark.asyncio
async def test_add_weight_without_date(client: AsyncClient, auth_headers):
    """Добавление веса без даты (используется сегодня)."""
    payload = {"weight_kg": 76.0}
    resp = await client.post("/api/weight/", json=payload, headers=auth_headers)
    assert resp.status_code == 200
    assert resp.json()["weight_kg"] == 76.0


@pytest.mark.asyncio
async def test_add_weight_too_low(client: AsyncClient, auth_headers):
    """Вес < 30 кг → 422."""
    payload = {"weight_kg": 25.0}
    resp = await client.post("/api/weight/", json=payload, headers=auth_headers)
    assert resp.status_code == 422


@pytest.mark.asyncio
async def test_add_weight_too_high(client: AsyncClient, auth_headers):
    """Вес > 300 кг → 422."""
    payload = {"weight_kg": 350.0}
    resp = await client.post("/api/weight/", json=payload, headers=auth_headers)
    assert resp.status_code == 422


@pytest.mark.asyncio
async def test_get_weight_entries(client: AsyncClient, auth_headers):
    """Получение списка записей веса."""
    resp = await client.get("/api/weight/?limit=10", headers=auth_headers)
    assert resp.status_code == 200
    assert isinstance(resp.json(), list)


@pytest.mark.asyncio
async def test_get_weight_stats(client: AsyncClient, auth_headers):
    """Получение статистики по весу."""
    resp = await client.get("/api/weight/stats", headers=auth_headers)
    assert resp.status_code == 200
    data = resp.json()
    assert "entries_count" in data


@pytest.mark.asyncio
async def test_delete_weight_entry(client: AsyncClient, auth_headers):
    """Удаление записи веса."""
    # Создаём запись
    create_payload = {"weight_kg": 74.0}
    create_resp = await client.post("/api/weight/", json=create_payload, headers=auth_headers)
    entry_id = create_resp.json()["id"]

    # Удаляем
    delete_resp = await client.delete(f"/api/weight/{entry_id}", headers=auth_headers)
    assert delete_resp.status_code == 200


@pytest.mark.asyncio
async def test_delete_nonexistent_weight_entry(client: AsyncClient, auth_headers):
    """Удаление несуществующей записи → 404."""
    resp = await client.delete("/api/weight/99999", headers=auth_headers)
    assert resp.status_code == 404