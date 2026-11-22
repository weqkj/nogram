package app.nogram.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.nogram.badges.Badge

/** Админка владельца. Сервер всё равно перепроверяет права по токену. */
@Composable
fun AdminScreen(
    token: String, onToken: (String) -> Unit,
    onGrant: (Long, Badge) -> Unit, onRevoke: (Long, Badge) -> Unit, onBack: () -> Unit,
) {
    var idText by remember { mutableStateOf("") }
    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Админка значков", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            token, onToken, label = { Text("Админ-токен (ADMIN_TOKEN сервера)") },
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
        )
        OutlinedTextField(idText, { idText = it.filter(Char::isDigit) }, label = { Text("Telegram ID пользователя") })
        val uid = idText.toLongOrNull()
        Badge.entries.forEach { b ->
            ListItem(
                headlineContent = { Text(b.title) }, supportingContent = { Text(b.description) },
                trailingContent = {
                    Row {
                        TextButton(onClick = { uid?.let { onGrant(it, b) } }, enabled = uid != null) { Text("Выдать") }
                        TextButton(onClick = { uid?.let { onRevoke(it, b) } }, enabled = uid != null) { Text("Снять") }
                    }
                }
            )
        }
        TextButton(onBack) { Text("Назад") }
    }
}
