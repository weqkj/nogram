package app.nogram.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.nogram.data.Chat
import app.nogram.data.Message
import app.nogram.ui.components.GlassSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chat: Chat,
    messages: List<Message>,
    onSend: (String) -> Unit,
    onAttach: () -> Unit,
    onBack: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    val list = rememberLazyListState()
    LaunchedEffect(messages.size) { if (messages.isNotEmpty()) list.animateScrollToItem(messages.lastIndex) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(chat.title) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") } }
            )
        },
        bottomBar = {
            GlassSurface(Modifier.fillMaxWidth().navigationBarsPadding().imePadding().padding(8.dp), corner = 28.dp) {
                Row(Modifier.padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onAttach) { Icon(Icons.Filled.AttachFile, "Файл") }
                    TextField(
                        value = text, onValueChange = { text = it }, modifier = Modifier.weight(1f),
                        placeholder = { Text("Сообщение") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        )
                    )
                    FilledIconButton(onClick = { if (text.isNotBlank()) { onSend(text); text = "" } }) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Отправить")
                    }
                }
            }
        }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).padding(horizontal = 12.dp), state = list, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(messages, key = { it.id }) { m ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (m.outgoing) Arrangement.End else Arrangement.Start) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (m.outgoing) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                    ) { Text(m.text, Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) }
                }
            }
        }
    }
}
