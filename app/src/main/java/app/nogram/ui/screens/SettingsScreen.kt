package app.nogram.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.nogram.settings.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    s: Settings,
    onChange: ((Settings) -> Settings) -> Unit,
    plugins: List<String>,
    isOwner: Boolean,
    onBack: () -> Unit,
    onPlugins: () -> Unit,
    onAdmin: () -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Настройки") }, navigationIcon = {
            IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") }
        })
    }) { pad ->
        Column(Modifier.padding(pad).verticalScroll(rememberScrollState())) {
            Section("Внешний вид")
            Toggle("Material You (цвета из обоев)", s.dynamicColor) { v -> onChange { it.copy(dynamicColor = v) } }
            Toggle("Liquid glass", s.glass) { v -> onChange { it.copy(glass = v) } }
            Toggle("Motion blur анимации", s.motionBlur) { v -> onChange { it.copy(motionBlur = v) } }
            Toggle("Визуальный Premium (только у вас на экране)", s.fakePremium) { v -> onChange { it.copy(fakePremium = v) } }

            Section("Приватность")
            Toggle("Режим призрака (не в сети)", s.ghostMode) { v -> onChange { it.copy(ghostMode = v) } }
            Toggle("Не отмечать прочитанным", s.noReadReceipts) { v -> onChange { it.copy(noReadReceipts = v) } }
            Toggle("Не показывать «печатает»", s.noTyping) { v -> onChange { it.copy(noTyping = v) } }

            Section("Плагины (${plugins.size})")
            ListItem(headlineContent = { Text("Управление плагинами") }, modifier = Modifier, supportingContent = { Text("Установить .plugin или создать с ИИ") })
            TextButton(onPlugins, Modifier.padding(horizontal = 8.dp)) { Text("Открыть") }

            if (isOwner) {
                Section("Владелец")
                TextButton(onAdmin, Modifier.padding(horizontal = 8.dp)) { Text("Админка значков") }
            }
        }
    }
}

@Composable private fun Section(t: String) =
    Text(t, Modifier.padding(16.dp, 20.dp, 16.dp, 4.dp), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)

@Composable private fun Toggle(label: String, checked: Boolean, onChange: (Boolean) -> Unit) =
    ListItem(headlineContent = { Text(label) }, trailingContent = { Switch(checked, onChange) })
