# Calorie Tracker - Android приложение

Нативное Android-приложение для отслеживания калорий с поддержкой:
- Аутентификация через Telegram
- Сканер штрих-кодов продуктов (ML Kit + CameraX)
- Отслеживание активности через Google Fit API
- Оптимизация под 120 FPS
- Material Design 3 UI

## Требования для разработки

1. **Android Studio** (Arctic Fox или новее)
2. **JDK 17**
3. **Android SDK** (API 34)
4. **Эмулятор** или физическое устройство (Android 8.0+)

## Настройка проекта

### 1. Откройте проект в Android Studio

```bash
# Если у вас есть Git
git clone <your-repo-url>
cd CalorieTracker

# Или откройте существующую папку в Android Studio
```

### 2. Настройте Telegram Bot Token

Откройте файл `app/src/main/java/com/calorietracker/data/repository/TelegramAuthRepository.kt` и замените:

```kotlin
private const val BOT_TOKEN = "YOUR_BOT_TOKEN"
```

на ваш реальный токен от Telegram Bot Father.

### 3. Настройте Google Fit API

1. Зайдите в [Google Cloud Console](https://console.cloud.google.com/)
2. Создайте новый проект или выберите существующий
3. Включите Google Fit API
4. Создайте OAuth 2.0 credentials
5. Добавьте SHA-1 отпечаток вашего ключа подписи

Для получения debug SHA-1:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

### 4. Синхронизируйте Gradle

В Android Studio:
- File → Sync Project with Gradle Files
- Или нажмите кнопку "Sync Now"

## Запуск приложения

### Вариант 1: Через Android Studio

1. Выберите устройство/эмулятор вверху
2. Нажмите зеленую кнопку "Run" (▶️)
3. Или используйте горячую клавишу `Shift + F10`

### Вариант 2: Через командную строку

```bash
# Сборка debug APK
./gradlew assembleDebug

# Установка на подключенное устройство
./gradlew installDebug

# Сборка и запуск
./gradlew installDebug && adb shell am start -n com.calorietracker/.MainActivity
```

### Вариант 3: Создание APK для тестирования

```bash
# Debug APK (для тестирования)
./gradlew assembleDebug

# Release APK (для публикации)
./gradlew assembleRelease
```

APK файлы будут находиться в:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

## Тестирование функций

### 1. Аутентификация через Telegram

- При первом запуске нажмите "Войти через Telegram"
- Приложение перенаправит вас к Telegram боту
- После авторизации вы вернетесь в приложение

### 2. Сканер штрих-кодов

1. На главном экране нажмите "Сканер"
2. Предоставьте разрешение на использование камеры
3. Наведите камеру на штрих-код продукта
4. Приложение автоматически распознает код

### 3. Отслеживание активности (Google Fit)

1. Перейдите в раздел "Активность"
2. Нажмите "Синхронизировать с Google Fit"
3. Предоставьте необходимые разрешения
4. Данные о шагах, калориях и расстоянии синхронизируются

### 4. Добавление еды

1. Нажмите "Добавить" в нижней навигации
2. Введите название продукта и КБЖУ
3. Или отсканируйте штрих-код для автоматического заполнения

## Оптимизация под 120 FPS

Приложение использует следующие оптимизации:

1. **Compose Recomposition optimization** - минимизация перерисовок
2. **Lazy lists** - ленивая загрузка списков
3. **StateFlow** - эффективное управление состоянием
4. **Hardware acceleration** - включено по умолчанию

Для проверки FPS:
```bash
adb shell dumpsys SurfaceFlinger --latency <WINDOW_NAME>
```

Или включите "Show refresh rate" в настройках разработчика на устройстве.

## Будущие виджеты

Архитектура приложения предусматривает добавление виджетов:

1. Создайте класс виджета в `app/src/main/java/com/calorietracker/widget/`
2. Обновите `AndroidManifest.xml` с декларацией виджета
3. Создайте layout для виджета в `res/layout/`
4. Добавьте настройки виджета в `res/xml/`

Пример структуры для виджета:
```
app/src/main/java/com/calorietracker/widget/
    ├── CalorieTrackerWidgetProvider.kt
    └── CalorieTrackerWidgetConfigActivity.kt
app/src/main/res/
    ├── layout/widget_calorie_tracker.xml
    └── xml/widget_info.xml
```

## Структура проекта

```
app/src/main/java/com/calorietracker/
├── MainActivity.kt              # Главная активность
├── CalorieTrackerApp.kt         # Application класс с Hilt
├── data/
│   ├── local/                   # Room Database, DAO
│   ├── model/                   # Data classes
│   ├── repository/              # Репозитории
│   └── remote/                  # API clients
├── di/                          # Dependency Injection модули
├── ui/
│   ├── screens/                 # Экраны (Composables)
│   ├── components/              # Переиспользуемые компоненты
│   ├── navigation/              # Навигация
│   └── theme/                   # Тема, цвета, типографика
└── util/                        # Утилиты
```

## Технологии

- **Jetpack Compose** - современный UI toolkit
- **Hilt** - dependency injection
- **Room** - локальная база данных
- **CameraX** - работа с камерой
- **ML Kit** - сканирование штрих-кодов
- **Google Fit API** - отслеживание активности
- **Material 3** - дизайн система
- **Kotlin Coroutines & Flow** - асинхронность

## Решение проблем

### Ошибка сборки Gradle
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### Проблемы с эмулятором
- Убедитесь, что эмулятор имеет Google Play Services
- Используйте образ с API 34
- Выделите больше RAM (минимум 2GB)

### Ошибки разрешений
- Проверьте `AndroidManifest.xml`
- Для физических устройств: Настройки → Приложения → Calorie Tracker → Разрешения

### Telegram auth не работает
- Проверьте BOT_TOKEN
- Убедитесь, что бот активен
- Проверьте init_data формат

## Публикация в Google Play

1. Создайте release key:
```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias
```

2. Обновите `gradle.properties`:
```properties
RELEASE_STORE_FILE=my-release-key.jks
RELEASE_KEY_ALIAS=my-alias
RELEASE_PASSWORD=your_password
```

3. Соберите release APK:
```bash
./gradlew assembleRelease
```

4. Подпишите APK и загрузите в Google Play Console

## Лицензия

MIT License
