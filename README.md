# Nogram

Android-клиент Telegram (Kotlin + Jetpack Compose). Владелец: @nuxoku (ID 7876031245).

## Что готово в каркасе
- Material You (динамические цвета) + Material 3 Expressive (spring-анимации), «liquid glass» панели (`ui/components/Glass.kt`)
- Список чатов, переписка, настройки, ряд значков с шторкой-описанием, админка владельца
- 10 значков (`badges/Badges.kt`) + бэкенд выдачи/снятия (`backend/server.js`)
- Настройки приватности: призрак, без «прочитано», без «печатает», визуальный Premium (локальный)
- Плагины `.plugin` (zip: manifest.json + main.js) в песочнице QuickJS с системой прав
- Демо-данные (`FakeRepository`), чтобы UI запускался без Telegram

## Что ещё НЕ сделано (честно)
1. **Подключение TDLib** — реализовать `TelegramRepository` на TDLib (вход по телефону, чаты, сообщения, файлы). Нужны `api_id`/`api_hash` с https://my.telegram.org, передаются в `gradle.properties`: `TG_API_ID`, `TG_API_HASH`.
2. Реализация настроек приватности поверх TDLib (`setOption online`, отключение `viewMessages`, отключение `sendChatAction`).
3. Истории: просмотр/публикация в обход бага — зависит от TDLib (`getChatActiveStories`, `postStory`).
4. Экран плагинов, диалог подтверждения прав, «создать плагин с ИИ» (нужен API-ключ ИИ и промпт, который генерирует manifest.json + main.js).
5. Motion blur при переходах, анимации значков, значки Google и т.п. (нужны ресурсы/лицензии).
6. Полировка UI; сборка и тесты на устройстве — в этой среде Android SDK нет, проект ни разу не компилировался.

## Сборка
Открыть папку в Android Studio (Ladybug+), Sync, Run. Или `gradle :app:assembleDebug` с установленным Android SDK.

## Значки и админка
```
cd backend
ADMIN_TOKEN=<длинный-секрет-24+ символов> node server.js
```
Выдача/снятие: `POST /admin/grant|revoke` с заголовком `Authorization: Bearer <ADMIN_TOKEN>` и `{"userId":123,"badge":"donor"}`.
Проверка «только мой ID» делается на сервере по секрету, а не в приложении: APK можно изменить, и проверка `userId == 7876031245` внутри клиента ничего не защищает. Секрет вводится в админке один раз и хранится только у вас.

## Плагины
Сборка: `cd plugins-examples/hello.plugin.src && zip ../hello.plugin manifest.json main.js`. Тап по файлу `.plugin` устанавливает его.
Права: `messages.read`, `messages.modify`, `ui.toast`, `storage`. Плагины exteraGram написаны на Python и напрямую не запустятся; их нужно переписать в этот формат (это можно поручить ИИ).

## Сборка в GitHub
Вкладка Actions → «Build Android APK» → Run workflow → скачать артефакт `nogram-debug-apk`.
Секреты репозитория: `TG_API_ID`, `TG_API_HASH` (my.telegram.org). Переменная `BACKEND_URL` — адрес бэкенда значков.

## iOS (.ipa)
Этот проект — только Android. iOS-клиент потребует отдельной реализации (Swift/SwiftUI + TDLib или Kotlin Multiplatform), а сборка .ipa — Apple Developer аккаунта и подписи. Пока не сделано.
