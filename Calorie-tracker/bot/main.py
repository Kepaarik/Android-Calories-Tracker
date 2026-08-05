import sys
import os
import ssl
import certifi
from pathlib import Path

# === ИСПРАВЛЕНИЕ SSL ДЛЯ WINDOWS (Должно быть до импорта aiogram!) ===
# Перехватываем создание SSL-контекста, чтобы принудительно использовать certifi
_original_create_default_context = ssl.create_default_context

def _patched_create_default_context(*args, **kwargs):
    # Если сертификат не указан явно, подставляем bundle из certifi
    if 'cafile' not in kwargs and 'capath' not in kwargs and 'cadata' not in kwargs:
        kwargs['cafile'] = certifi.where()
    return _original_create_default_context(*args, **kwargs)

ssl.create_default_context = _patched_create_default_context
# =====================================================================

import asyncio
import logging

# Добавляем родительскую директорию в sys.path
sys.path.insert(0, str(Path(__file__).parent.parent))

from aiogram import Bot, Dispatcher
from aiogram.fsm.storage.memory import MemoryStorage
from config import settings
from handlers import start

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

async def main():
    # Теперь можно использовать стандартный Bot без кастомных сессий
    bot = Bot(token=settings.BOT_TOKEN)
    dp = Dispatcher(storage=MemoryStorage())
    
    # Регистрируем роутеры
    dp.include_router(start.router)
    
    logger.info("Бот запущен...")
    logger.info(f"Web App URL: {settings.WEBAPP_URL}")
    
    # Запуск polling
    await dp.start_polling(bot, allowed_updates=dp.resolve_used_update_types())

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        logger.info("Бот остановлен пользователем")