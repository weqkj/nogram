package app.nogram.plugins

import android.content.Context
import app.cash.quickjs.QuickJs
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.util.zip.ZipInputStream

/**
 * Формат .plugin — zip-архив:
 *   manifest.json  {"id","name","version","author","description","permissions":[...],"hooks":[...]}
 *   main.js        код плагина (выполняется в песочнице QuickJS, без сети и файловой системы)
 *
 * Плагину доступен только объект `nogram`, и только в рамках выданных permissions.
 * Хуки: onMessageReceived(msg), onMessageSend(text) -> text|null, onChatOpen(chat).
 * Плагины exteraGram (Python) напрямую не запускаются — нужен перевод в этот формат (см. README).
 */
@Serializable
data class PluginManifest(
    val id: String, val name: String, val version: String = "1.0",
    val author: String = "", val description: String = "",
    val permissions: List<String> = emptyList(),
    val hooks: List<String> = emptyList(),
)

class LoadedPlugin(val manifest: PluginManifest, val js: String)

class PluginEngine(private val ctx: Context) {
    private val dir = File(ctx.filesDir, "plugins").apply { mkdirs() }
    private val json = Json { ignoreUnknownKeys = true }
    val plugins = mutableListOf<LoadedPlugin>()

    private val allowed = setOf("messages.read", "messages.modify", "ui.toast", "storage")

    /** Устанавливает .plugin из потока. Права показываются пользователю до подтверждения. */
    fun parse(input: java.io.InputStream): LoadedPlugin {
        var manifest: PluginManifest? = null
        var js: String? = null
        ZipInputStream(input).use { z ->
            generateSequence { z.nextEntry }.forEach { e ->
                val bytes = z.readBytes()
                require(bytes.size < 512 * 1024) { "Файл слишком большой" }
                when (e.name) {
                    "manifest.json" -> manifest = json.decodeFromString(bytes.decodeToString())
                    "main.js" -> js = bytes.decodeToString()
                }
            }
        }
        val m = requireNotNull(manifest) { "Нет manifest.json" }
        require(m.id.matches(Regex("[a-z0-9_.-]{3,64}"))) { "Некорректный id" }
        val bad = m.permissions - allowed
        require(bad.isEmpty()) { "Неизвестные права: $bad" }
        return LoadedPlugin(m, requireNotNull(js) { "Нет main.js" })
    }

    fun install(p: LoadedPlugin) {
        File(dir, "${p.manifest.id}.js").writeText(p.js)
        File(dir, "${p.manifest.id}.json").writeText(json.encodeToString(PluginManifest.serializer(), p.manifest))
        plugins.removeAll { it.manifest.id == p.manifest.id }; plugins += p
    }

    fun loadInstalled() {
        plugins.clear()
        dir.listFiles { f -> f.extension == "json" }?.forEach { mf ->
            val js = File(dir, mf.nameWithoutExtension + ".js")
            if (js.exists()) plugins += LoadedPlugin(json.decodeFromString(mf.readText()), js.readText())
        }
    }

    fun uninstall(id: String) {
        File(dir, "$id.js").delete(); File(dir, "$id.json").delete(); plugins.removeAll { it.manifest.id == id }
    }

    /** Прогоняет исходящий текст через плагины с хуком onMessageSend. */
    fun filterOutgoing(text: String): String {
        var out = text
        for (p in plugins) {
            if ("messages.modify" !in p.manifest.permissions || "onMessageSend" !in p.manifest.hooks) continue
            out = runCatching {
                QuickJs.create().use { js ->
                    js.evaluate(p.js)
                    js.evaluate("(typeof onMessageSend==='function')?String(onMessageSend(${quote(out)})):${quote(out)}") as String
                }
            }.getOrDefault(out)
        }
        return out
    }

    private fun quote(s: String) = "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\""
}
