package app.nogram.plugins

/** Промпт для ИИ: вставьте его в любой чат с ИИ, дописав в конце, какой плагин нужен. */
const val AI_PLUGIN_PROMPT = """Ты помогаешь создать плагин для Telegram-клиента Nogram.
Формат .plugin — zip-архив с двумя файлами:

1) manifest.json:
{"id":"author.name","name":"Название","version":"1.0","author":"ник","description":"что делает",
 "permissions":[...],"hooks":[...]}
 - id: только a-z, 0-9, точка, дефис, подчёркивание, 3-64 символа
 - permissions: только из списка messages.read, messages.modify, ui.toast, storage
 - hooks: только из списка onMessageReceived, onMessageSend, onChatOpen

2) main.js — обычный JavaScript (ES2020), без сети, файлов и require/import.
 Объявляй функции-хуки глобально, например:
   function onMessageSend(text) { return text; }   // верни новый текст
 Просят минимальные права: не запрашивай лишние permissions.

Выведи два файла отдельными блоками кода (manifest.json и main.js) и коротко скажи, какие права нужны и зачем.

Мне нужен плагин, который: """
