# bot/handlers/start.py
from aiogram import Router, types
from aiogram.filters import CommandStart

router = Router()


@router.message(CommandStart())
async def cmd_start(message: types.Message):
    # Определяем имя пользователя
    name = message.from_user.first_name or "друг"

    # Простое сообщение без клавиатуры
    await message.answer(
        f"Привет, {name}!\n\n"
        f"Чтобы открыть дневник питания, нажми на кнопку <b>слева от поля ввода сообщения</b> "
        f"(рядом со скрепкой).\n\n"
        f"Там ты сможешь:\n"
        f"• Вести учёт калорий и БЖУ\n"
        f"• Сканировать штрих-коды продуктов\n"
        f"• Отслеживать вес и прогресс"
    )