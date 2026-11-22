package app.nogram.ui.components

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * «Liquid glass»: полупрозрачная панель с градиентной подсветкой кромки.
 * Настоящий размытый фон (blur под панелью) работает на Android 12+ через RenderEffect;
 * ниже — просто более плотная полупрозрачность.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    corner: Dp = 28.dp,
    blurRadius: Dp = 24.dp,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(corner)
    val base = MaterialTheme.colorScheme.surfaceContainerHigh
    val canBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    Box(modifier) {
        // слой размытия (дублирует фон панели)
        if (canBlur) Box(Modifier.matchParentSize().blur(blurRadius).background(base.copy(alpha = 0.35f), shape))
        Surface(
            shape = shape,
            color = base.copy(alpha = if (canBlur) 0.55f else 0.85f),
            border = BorderStroke(
                1.dp,
                Brush.linearGradient(listOf(Color.White.copy(0.55f), Color.White.copy(0.05f), Color.White.copy(0.25f)))
            ),
            content = content
        )
    }
}
