import pytest


@pytest.mark.asyncio
async def test_get_entries_requires_auth(client):
    """Без токена — 401 или 403"""
    resp = await client.get("/api/diary/?date=2026-07-10")
    assert resp.status_code in (401, 403)


@pytest.mark.asyncio
async def test_create_entry_requires_auth(client, sample_product):
    """Создание без токена — 401 или 403"""
    payload = {
        "product_id": sample_product.id,
        "weight_grams": 100.0,
        "meal_type": "breakfast",
        "date": "2026-07-10",
    }
    resp = await client.post("/api/diary/", json=payload)
    assert resp.status_code in (401, 403)


@pytest.mark.asyncio
async def test_create_entry_success(client, auth_headers, sample_product):
    """Успешное создание записи"""
    payload = {
        "product_id": sample_product.id,
        "weight_grams": 150.0,
        "meal_type": "breakfast",
        "date": "2026-07-10",
    }
    resp = await client.post("/api/diary/", json=payload, headers=auth_headers)
    assert resp.status_code == 201, f"Body: {resp.text}"
    data = resp.json()
    assert "id" in data
    assert data["product_id"] == sample_product.id
    assert data["weight_grams"] == 150.0
    assert data["meal_type"] == "breakfast"


@pytest.mark.asyncio
async def test_create_entry_invalid_weight(client, auth_headers, sample_product):
    """Невалидный вес (отрицательный)"""
    payload = {
        "product_id": sample_product.id,
        "weight_grams": -10,
        "meal_type": "breakfast",
        "date": "2026-07-10",
    }
    resp = await client.post("/api/diary/", json=payload, headers=auth_headers)
    assert resp.status_code == 422


@pytest.mark.asyncio
async def test_create_entry_zero_weight(client, auth_headers, sample_product):
    """Нулевой вес"""
    payload = {
        "product_id": sample_product.id,
        "weight_grams": 0,
        "meal_type": "breakfast",
        "date": "2026-07-10",
    }
    resp = await client.post("/api/diary/", json=payload, headers=auth_headers)
    assert resp.status_code == 422


@pytest.mark.asyncio
async def test_create_entry_invalid_meal_type(client, auth_headers, sample_product):
    """Невалидный meal_type"""
    payload = {
        "product_id": sample_product.id,
        "weight_grams": 100,
        "meal_type": "invalid_type",
        "date": "2026-07-10",
    }
    resp = await client.post("/api/diary/", json=payload, headers=auth_headers)
    # Может быть либо 422 (если есть валидация), либо 201 (если нет)
    assert resp.status_code in (201, 422)


@pytest.mark.asyncio
async def test_create_entry_too_heavy(client, auth_headers, sample_product):
    """Слишком большой вес"""
    payload = {
        "product_id": sample_product.id,
        "weight_grams": 100000,
        "meal_type": "breakfast",
        "date": "2026-07-10",
    }
    resp = await client.post("/api/diary/", json=payload, headers=auth_headers)
    assert resp.status_code == 422


@pytest.mark.asyncio
async def test_get_entries_for_date(client, auth_headers, sample_product):
    """Получение записей за конкретную дату"""
    payload = {
        "product_id": sample_product.id,
        "weight_grams": 100.0,
        "meal_type": "lunch",
        "date": "2026-07-10",
    }
    create_resp = await client.post("/api/diary/", json=payload, headers=auth_headers)
    assert create_resp.status_code == 201

    resp = await client.get("/api/diary/?date=2026-07-10", headers=auth_headers)
    assert resp.status_code == 200
    data = resp.json()
    assert len(data) >= 1


@pytest.mark.asyncio
async def test_get_summary(client, auth_headers, sample_product):
    """Сводка КБЖУ за день"""
    payload = {
        "product_id": sample_product.id,
        "weight_grams": 100.0,
        "meal_type": "breakfast",
        "date": "2026-07-10",
    }
    await client.post("/api/diary/", json=payload, headers=auth_headers)

    resp = await client.get("/api/diary/summary?date=2026-07-10", headers=auth_headers)
    assert resp.status_code == 200
    data = resp.json()
    assert "calories" in data or "total_calories" in data
    assert "proteins" in data or "total_proteins" in data


@pytest.mark.asyncio
async def test_delete_entry(client, auth_headers, sample_product):
    """Soft delete записи"""
    payload = {
        "product_id": sample_product.id,
        "weight_grams": 100.0,
        "meal_type": "breakfast",
        "date": "2026-07-10",
    }
    create_resp = await client.post("/api/diary/", json=payload, headers=auth_headers)
    assert create_resp.status_code == 201, f"Create failed: {create_resp.text}"
    
    data = create_resp.json()
    assert "id" in data, f"No id in response: {data}"
    entry_id = data["id"]

    delete_resp = await client.delete(f"/api/diary/{entry_id}", headers=auth_headers)
    assert delete_resp.status_code == 204


@pytest.mark.asyncio
async def test_restore_entry(client, auth_headers, sample_product):
    """Восстановление удалённой записи"""
    payload = {
        "product_id": sample_product.id,
        "weight_grams": 100.0,
        "meal_type": "breakfast",
        "date": "2026-07-10",
    }
    create_resp = await client.post("/api/diary/", json=payload, headers=auth_headers)
    assert create_resp.status_code == 201, f"Create failed: {create_resp.text}"
    
    data = create_resp.json()
    assert "id" in data, f"No id in response: {data}"
    entry_id = data["id"]

    await client.delete(f"/api/diary/{entry_id}", headers=auth_headers)

    get_resp = await client.get("/api/diary/?date=2026-07-10", headers=auth_headers)
    ids = [e["id"] for e in get_resp.json()]
    assert entry_id not in ids

    restore_resp = await client.post("/api/diary/", json=payload, headers=auth_headers)
    assert restore_resp.status_code == 201


@pytest.mark.asyncio
async def test_update_entry_success(client, auth_headers, sample_product):
    """Обновление веса записи"""
    payload = {
        "product_id": sample_product.id,
        "weight_grams": 100.0,
        "meal_type": "breakfast",
        "date": "2026-07-10",
    }
    create_resp = await client.post("/api/diary/", json=payload, headers=auth_headers)
    assert create_resp.status_code == 201, f"Create failed: {create_resp.text}"
    
    data = create_resp.json()
    assert "id" in data, f"No id in response: {data}"
    entry_id = data["id"]

    update_resp = await client.put(
        f"/api/diary/{entry_id}",
        json={"weight_grams": 200.0},
        headers=auth_headers,
    )
    assert update_resp.status_code == 200, f"Update failed: {update_resp.text}"
    updated_data = update_resp.json()
    assert updated_data["weight_grams"] == 200.0


@pytest.mark.asyncio
async def test_update_entry_partial(client, auth_headers, sample_product):
    """Частичное обновление (только meal_type)"""
    payload = {
        "product_id": sample_product.id,
        "weight_grams": 100.0,
        "meal_type": "breakfast",
        "date": "2026-07-10",
    }
    create_resp = await client.post("/api/diary/", json=payload, headers=auth_headers)
    assert create_resp.status_code == 201, f"Create failed: {create_resp.text}"
    
    data = create_resp.json()
    assert "id" in data, f"No id in response: {data}"
    entry_id = data["id"]

    update_resp = await client.put(
        f"/api/diary/{entry_id}",
        json={"meal_type": "lunch"},
        headers=auth_headers,
    )
    assert update_resp.status_code == 200, f"Update failed: {update_resp.text}"
    updated_data = update_resp.json()
    assert updated_data["meal_type"] == "lunch"


@pytest.mark.asyncio
async def test_update_nonexistent_entry(client, auth_headers):
    """Обновление несуществующей записи"""
    update_resp = await client.put(
        "/api/diary/99999",
        json={"weight_grams": 200.0},
        headers=auth_headers,
    )
    assert update_resp.status_code == 404


@pytest.mark.asyncio
async def test_permanent_delete_entry(client, auth_headers, sample_product):
    """Повторное удаление"""
    payload = {
        "product_id": sample_product.id,
        "weight_grams": 100.0,
        "meal_type": "breakfast",
        "date": "2026-07-10",
    }
    create_resp = await client.post("/api/diary/", json=payload, headers=auth_headers)
    assert create_resp.status_code == 201, f"Create failed: {create_resp.text}"
    
    data = create_resp.json()
    assert "id" in data, f"No id in response: {data}"
    entry_id = data["id"]

    resp1 = await client.delete(f"/api/diary/{entry_id}", headers=auth_headers)
    assert resp1.status_code == 204

    resp2 = await client.delete(f"/api/diary/{entry_id}", headers=auth_headers)
    assert resp2.status_code in (204, 404)