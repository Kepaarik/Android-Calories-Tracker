# Makefile (обновленная версия для Windows без Poetry)
.PHONY: install run-api run-frontend migrate

install:
	cd backend && python -m venv venv && venv\Scripts\activate && pip install -r requirements.txt
	cd frontend && npm install

run-api:
	cd backend && venv\Scripts\activate && uvicorn app.main:app --reload --host 0.0.0.0 --port 8000

run-frontend:
	cd frontend && npm run dev

migrate:
	cd backend && venv\Scripts\activate && alembic upgrade head

migrate-create:
	cd backend && venv\Scripts\activate && alembic revision --autogenerate -m "update"