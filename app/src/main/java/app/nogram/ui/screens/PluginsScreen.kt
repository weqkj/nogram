package app.nogram.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.nogram.plugins.PluginManifest

private val permissionTitles = mapOf(
    "messages.read" to "Читать ваши сообщения",
    "messages.modify" to "Изменять сообщения (например, исходящие)",
    "ui.toast" to "Показывать уведомления в приложении",
    "storage" to "Хранить свои данные на устройстве",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginsScreen(
    plugins: List<PluginManifest>,
    onPick: () -> Unit,
    onCopyPrompt: () -> Unit,
    onUninstall: (String) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Плагины") }, navigationIcon = {
                IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") }
            })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onPick, icon = { Icon(Icons.Filled.Add, null) }, text = { Text("Установить .plugin") })
        }
    ) { pad ->
        LazyColumn(Modifier.padding(pad), contentPadding = PaddingValues(bottom = 96.dp)) {
            item {
                ElevatedCard(Modifier.padding(16.dp).fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Создать плагин с ИИ", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Скопируйте промпт, вставьте в любой чат с ИИ и допишите, что должен делать плагин. " +
                                "Получите manifest.json и main.js, упакуйте их в zip и переименуйте в .plugin.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        FilledTonalButton(onCopyPrompt) { Icon(Icons.Filled.ContentCopy, null); Spacer(Modifier.width(8.dp)); Text("Скопировать промпт") }
                    }
                }
            }
            if (plugins.isEmpty()) {
                item { Text("Плагинов пока нет", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            items(plugins, key = { it.id }) { p ->
                ListItem(
                    headlineContent = { Text("${p.name} ${p.version}") },
                    supportingContent = { Text(p.description.ifBlank { p.id }) },
                    trailingContent = { IconButton({ onUninstall(p.id) }) { Icon(Icons.Filled.Delete, "Удалить") } },
                )
            }
        }
    }
}

/** Показывается перед установкой: пользователь видит, какие права просит плагин. */
@Composable
fun PluginInstallDialog(m: PluginManifest, onConfirm: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        icon = { Icon(Icons.Filled.Extension, null) },
        title = { Text("Установить «${m.name}»?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (m.author.isNotBlank()) Text("Автор: ${m.author}")
                Text(if (m.permissions.isEmpty()) "Плагин не запрашивает прав." else "Плагин получит права:")
                m.permissions.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Check, null, Modifier.size(16.dp)); Spacer(Modifier.width(8.dp))
                        Text(permissionTitles[it] ?: it)
                    }
                }
                Text("Ставьте только плагины, которым доверяете.", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = { TextButton(onConfirm) { Text("Установить") } },
        dismissButton = { TextButton(onCancel) { Text("Отмена") } },
    )
}
