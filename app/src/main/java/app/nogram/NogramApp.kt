package app.nogram

import android.app.Application
import app.nogram.badges.BadgeApi
import app.nogram.data.FakeRepository
import app.nogram.data.TelegramRepository
import app.nogram.plugins.PluginEngine
import app.nogram.settings.SettingsStore

class NogramApp : Application() {
    lateinit var repo: TelegramRepository      // TODO: заменить на TdLibRepository
    lateinit var settings: SettingsStore
    lateinit var plugins: PluginEngine
    val badgeApi = BadgeApi()

    override fun onCreate() {
        super.onCreate()
        repo = FakeRepository()
        settings = SettingsStore(this)
        plugins = PluginEngine(this).also { it.loadInstalled() }
    }
}
