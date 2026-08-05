# Исправление ошибок установки APK

## Выполненные изменения

### 1. Обновлен `app/build.gradle.kts`
Добавлена настройка `jniLibs.useLegacyPackaging = false` в блок `packaging` для обеспечения совместимости с 16 KB page size:

```kotlin
packaging {
    resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
    jniLibs {
        useLegacyPackaging = false
    }
}
```

### 2. Обновлен `build.gradle.kts` (root)
Добавлена задача clean для корректной очистки сборки.

### 3. Создан gradlew скрипт
Добавлен скрипт Gradle Wrapper для возможности сборки проекта.

## Что нужно сделать в Android Studio на Windows

### Для решения проблемы "User rejected permissions":

1. **Откройте Settings → Apps → Your App → Permissions** на устройстве/эмуляторе
2. **Сбросьте разрешения** для вашего приложения или удалите и переустановите приложение
3. При запуске из Android Studio **внимательно читайте диалог разрешений** и принимайте их

Или выполните команду ADB для сброса разрешений:
```bash
adb uninstall com.calorietracker
```

### Для решения проблемы с 16 KB page size:

1. **Очистите проект**: Build → Clean Project, затем Build → Rebuild Project
2. **Убедитесь, что все нативные библиотеки обновлены**:
   - ML Kit Barcode Scanning (`com.google.mlkit:barcode-scanning`) - уже используется версия 17.2.0
   - CameraX - уже используется версия 1.3.0
   - Google Play Services Fitness - версия 21.1.0

3. **Проверьте версии библиотек** - некоторые библиотеки могут требовать обновления для полной совместимости с 16 KB:
   - Убедитесь, что используете последние версии всех зависимостей
   - Особенно это касается библиотек с нативным кодом (.so файлы)

4. **Если проблема сохраняется**, проверьте конкретные библиотеки:
   - `libbarhopper_v3.so` - часть ML Kit
   - `libimage_processing_util_jni.so` - часть библиотек обработки изображений

   Попробуйте обновить:
   ```kotlin
   implementation("com.google.mlkit:barcode-scanning:17.3.0") // или новее
   implementation("androidx.camera:camera-core:1.4.0") // или новее
   implementation("androidx.camera:camera-camera2:1.4.0")
   implementation("androidx.camera:camera-lifecycle:1.4.0")
   implementation("androidx.camera:camera-view:1.4.0")
   ```

### Дополнительные рекомендации:

1. **Обновите Android Gradle Plugin** до последней версии (8.2.0 уже используется)
2. **Используйте Android Studio Hedgehog или новее**
3. **Проверьте targetSdk** - рекомендуется использовать API 34 или выше

## Ссылки для дополнительной информации

- [Android 16 KB Page Size Compatibility](https://developer.android.com/16kb-page-size)
- [Google Play требования к 16 KB](https://developer.android.com/google/play/requirements/16kb-page-size)
