package app.nogram.settings

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.store by preferencesDataStore("nogram_settings")

data class Settings(
    val ghostMode: Boolean = false,        // не показывать «в сети»
    val noReadReceipts: Boolean = false,   // не отмечать прочитанным
    val noTyping: Boolean = false,         // не показывать «печатает»
    val fakePremium: Boolean = false,      // ТОЛЬКО визуальный значок на вашем устройстве
    val dynamicColor: Boolean = true,      // Material You
    val glass: Boolean = true,             // liquid glass
    val motionBlur: Boolean = true,
)

class SettingsStore(private val ctx: Context) {
    private object K {
        val ghost = booleanPreferencesKey("ghost"); val read = booleanPreferencesKey("noread")
        val typing = booleanPreferencesKey("notyping"); val prem = booleanPreferencesKey("vprem")
        val dyn = booleanPreferencesKey("dyn"); val glass = booleanPreferencesKey("glass")
        val mblur = booleanPreferencesKey("mblur")
    }

    val flow: Flow<Settings> = ctx.store.data.map {
        Settings(
            it[K.ghost] ?: false, it[K.read] ?: false, it[K.typing] ?: false,
            it[K.prem] ?: false, it[K.dyn] ?: true, it[K.glass] ?: true, it[K.mblur] ?: true
        )
    }

    suspend fun update(block: (Settings) -> Settings) = ctx.store.edit { p ->
        val n = block(
            Settings(
                p[K.ghost] ?: false, p[K.read] ?: false, p[K.typing] ?: false,
                p[K.prem] ?: false, p[K.dyn] ?: true, p[K.glass] ?: true, p[K.mblur] ?: true
            )
        )
        p[K.ghost] = n.ghostMode; p[K.read] = n.noReadReceipts; p[K.typing] = n.noTyping
        p[K.prem] = n.fakePremium; p[K.dyn] = n.dynamicColor; p[K.glass] = n.glass; p[K.mblur] = n.motionBlur
    }
}
