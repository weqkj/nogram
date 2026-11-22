package app.nogram.badges

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/** Каталог значков. Выдаёт только владелец через бэкенд; клиент их лишь показывает. */
enum class Badge(val id: String, val title: String, val description: String, val icon: ImageVector) {
    OWNER("owner", "Владелец Nogram", "Создатель и владелец проекта Nogram.", Icons.Filled.Shield),
    DONOR("donor", "Поддержал разработку", "Поддержал Nogram донатом.", Icons.Filled.Favorite),
    VETERAN("veteran", "Долго живущий в проекте", "С Nogram с самого начала.", Icons.Filled.Schedule),
    DEV("dev", "Разработчик", "Участвует в разработке клиента.", Icons.Filled.Code),
    PLUGIN_AUTHOR("plugin_author", "Автор плагинов", "Создал плагин для Nogram.", Icons.Filled.Extension),
    TESTER("tester", "Тестер", "Помогает тестировать сборки.", Icons.Filled.BugReport),
    DESIGNER("designer", "Дизайнер", "Сделал темы и оформление.", Icons.Filled.Palette),
    TRANSLATOR("translator", "Переводчик", "Помог с переводами.", Icons.Filled.Translate),
    VERIFIED("verified", "Проверен", "Подтверждён владельцем.", Icons.Filled.Verified),
    STAR("star", "Особый гость", "Отмечен владельцем.", Icons.Filled.Star);

    companion object { fun byId(id: String) = entries.firstOrNull { it.id == id } }
}
