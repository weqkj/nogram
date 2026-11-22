package app.nogram.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.nogram.badges.Badge

/** Ряд значков внизу профиля. Тап — шторка с большим значком и описанием. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgeRow(badges: List<Badge>) {
    var selected by remember { mutableStateOf<Badge?>(null) }
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        badges.forEach { b ->
            Surface(
                shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(40.dp).clickable { selected = b }
            ) { Box(contentAlignment = Alignment.Center) { Icon(b.icon, b.title, tint = MaterialTheme.colorScheme.onSecondaryContainer) } }
        }
    }
    selected?.let { b ->
        ModalBottomSheet(onDismissRequest = { selected = null }) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(96.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(b.icon, null, Modifier.size(56.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(b.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text(b.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
