package app.nogram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as NogramApp

        // Установка .plugin по тапу на файл — с подтверждением прав в реальном UI (TODO диалог)
        intent?.data?.let { uri ->
            runCatching { contentResolver.openInputStream(uri)?.use { app.plugins.install(app.plugins.parse(it)) } }
        }

        setContent {
            val scope = rememberCoroutineScope()
            val settings by app.settings.flow.collectAsState(Settings())
            val chats by app.repo.chats().collectAsState(emptyList())
            val nav = rememberNavController()
            val badgeCache = remember { mutableStateMapOf<Long, List<Badge>>() }
            // TODO: myId берётся из TDLib после входа; для владельца пока сравнение с OWNER_ID
            val myId = BuildConfig.OWNER_ID
            val isOwner = myId == BuildConfig.OWNER_ID
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
                            plugins = app.plugins.plugins.map { it.manifest.name },
                            isOwner = isOwner,
                            onBack = { nav.popBackStack() },
                            onPlugins = { /* TODO: экран плагинов + «создать с ИИ» */ },
                            onAdmin = { nav.navigate("admin") },
                        )
                    }
                    composable("admin") {
                        // adminToken выдаёт сервер после проверки входа владельца (см. backend/README)
                        val token = "" // TODO: получить после авторизации
                        AdminScreen(
                            onGrant = { u, b -> scope.launch { app.badgeApi.grant(token, u, b) } },
                            onRevoke = { u, b -> scope.launch { app.badgeApi.revoke(token, u, b) } },
                            onBack = { nav.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
