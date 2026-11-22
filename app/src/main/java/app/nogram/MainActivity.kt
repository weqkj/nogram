package app.nogram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import app.nogram.plugins.AI_PLUGIN_PROMPT
import app.nogram.plugins.LoadedPlugin
import app.nogram.plugins.PluginManifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.nogram.badges.Badge
import app.nogram.data.Chat
import app.nogram.settings.Settings
import app.nogram.ui.screens.*
import app.nogram.ui.theme.NogramTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var pending by mutableStateOf<LoadedPlugin?>(null)

    /** Читает .plugin, но не ставит: сначала пользователь подтверждает права. */
    private fun readPlugin(uri: Uri) {
        val app = application as NogramApp
        runCatching { contentResolver.openInputStream(uri)!!.use { app.plugins.parse(it) } }
            .onSuccess { pending = it }
            .onFailure { Toast.makeText(this, "Не удалось прочитать плагин: ${it.message}", Toast.LENGTH_LONG).show() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as NogramApp

        intent?.data?.let { readPlugin(it) }

        setContent {
            val scope = rememberCoroutineScope()
            val settings by app.settings.flow.collectAsState(Settings())
            val chats by app.repo.chats().collectAsState(emptyList())
            val nav = rememberNavController()
            val badgeCache = remember { mutableStateMapOf<Long, List<Badge>>() }
            // TODO: myId берётся из TDLib после входа; для владельца пока сравнение с OWNER_ID
            val myId = BuildConfig.OWNER_ID
            val isOwner = myId == BuildConfig.OWNER_ID
            val installed = remember { mutableStateListOf<PluginManifest>().apply { addAll(app.plugins.plugins.map { it.manifest }) } }
            val clipboard = LocalClipboardManager.current
            val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let { readPlugin(it) } }
            var adminToken by remember { mutableStateOf("") }
            LaunchedEffect(chats) {
                chats.mapNotNull { it.peerUserId }.distinct().forEach { badgeCache[it] = app.badgeApi.badgesOf(it) }
            }

            NogramTheme(dynamic = settings.dynamicColor) {
                NavHost(nav, startDestination = "chats") {
                    composable("chats") {
                        ChatListScreen(
                            chats = chats,
                            badgesOf = { id -> if (id == null) emptyList() else badgeCache[id].orEmpty() },
                            onOpen = { nav.navigate("chat/${it.id}") },
                            onSettings = { nav.navigate("settings") },
                        )
                    }
                    composable("chat/{id}") { entry ->
                        val id = entry.arguments?.getString("id")!!.toLong()
                        val chat = chats.firstOrNull { it.id == id } ?: Chat(id, "…", "", 0)
                        val msgs by app.repo.messages(id).collectAsState(emptyList())
                        ChatScreen(
                            chat, msgs,
                            onSend = { t -> scope.launch { app.repo.send(id, app.plugins.filterOutgoing(t)) } },
                            onAttach = { /* TODO: системный пикер файлов -> repo.sendFile */ },
                            onBack = { nav.popBackStack() },
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            s = settings,
                            onChange = { f -> scope.launch { app.settings.update(f) } },
                            plugins = installed.map { it.name },
                            isOwner = isOwner,
                            onBack = { nav.popBackStack() },
                            onPlugins = { nav.navigate("plugins") },
                            onAdmin = { nav.navigate("admin") },
                        )
                    }
                    composable("plugins") {
                        PluginsScreen(
                            plugins = installed,
                            onPick = { picker.launch(arrayOf("*/*")) },
                            onCopyPrompt = {
                                clipboard.setText(AnnotatedString(AI_PLUGIN_PROMPT))
                                Toast.makeText(this@MainActivity, "Промпт скопирован", Toast.LENGTH_SHORT).show()
                            },
                            onUninstall = { id -> app.plugins.uninstall(id); installed.removeAll { it.id == id } },
                            onBack = { nav.popBackStack() },
                        )
                    }
                    composable("admin") {
                        // Токен вводит владелец; сервер сам проверяет его (ADMIN_TOKEN в backend/server.js)
                        AdminScreen(
                            token = adminToken,
                            onToken = { adminToken = it },
                            onGrant = { u, b -> scope.launch { app.badgeApi.grant(adminToken, u, b) } },
                            onRevoke = { u, b -> scope.launch { app.badgeApi.revoke(adminToken, u, b) } },
                            onBack = { nav.popBackStack() },
                        )
                    }
                }
                pending?.let { p ->
                    PluginInstallDialog(
                        p.manifest,
                        onConfirm = {
                            app.plugins.install(p)
                            installed.removeAll { it.id == p.manifest.id }; installed += p.manifest
                            pending = null
                        },
                        onCancel = { pending = null },
                    )
                }
            }
        }
    }
}
