package app.nogram.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val FallbackDark = darkColorScheme(
    primary = Color(0xFFB8C4FF), secondary = Color(0xFFC2C5DD), tertiary = Color(0xFFE3BADA),
    surface = Color(0xFF121318), background = Color(0xFF121318)
)
private val FallbackLight = lightColorScheme(
    primary = Color(0xFF4355B9), secondary = Color(0xFF5A5D72), tertiary = Color(0xFF76546E),
    surface = Color(0xFFFBF8FF), background = Color(0xFFFBF8FF)
)

/** Material You: динамические цвета из обоев (Android 12+), иначе запасная палитра. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NogramTheme(
    dark: Boolean = isSystemInDarkTheme(),
    dynamic: Boolean = true,
    content: @Composable () -> Unit
) {
    val ctx = LocalContext.current
    val scheme = when {
        dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        dark -> FallbackDark
        else -> FallbackLight
    }
    MaterialExpressiveTheme(
        colorScheme = scheme,
        motionScheme = MotionScheme.expressive(),   // плавные «пружинные» анимации
        content = content
    )
}
