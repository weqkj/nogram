package app.nogram.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.nogram.badges.Badge
import app.nogram.data.Chat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    chats: List<Chat>,
    badgesOf: (Long?) -> List<Badge>,
    onOpen: (Chat) -> Unit,
    onSettings: () -> Unit,
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Nogram") },
                actions = { IconButton(onClick = onSettings) { Icon(Icons.Filled.Settings, "Настройки") } }
            )
        }
    ) { pad ->
        LazyColumn(Modifier.padding(pad)) {
            items(chats, key = { it.id }) { c ->
                ListItem(
                    modifier = Modifier.clickable { onOpen(c) },
                    leadingContent = {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(48.dp)) {
                            Box(contentAlignment = Alignment.Center) { Text(c.title.take(1)) }
                        }
                    },
                    headlineContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(c.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            badgesOf(c.peerUserId).firstOrNull()?.let {
                                Spacer(Modifier.width(6.dp))
                                Icon(it.icon, it.title, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    },
                    supportingContent = { Text(c.lastMessage, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    trailingContent = { if (c.unread > 0) androidx.compose.material3.Badge { Text("${c.unread}") } },
                )
            }
        }
    }
}
