package app.nogram.data

import kotlinx.coroutines.flow.*

/** Демо-данные, чтобы UI запускался до подключения TDLib. */
class FakeRepository : TelegramRepository {
    private val msgs = MutableStateFlow(
        mapOf(
            1L to listOf(Message(1, 1, "Привет! Это Nogram", false, System.currentTimeMillis() - 60_000)),
            2L to listOf(Message(1, 2, "Подарок получен", false, System.currentTimeMillis() - 3_600_000)),
        )
    )
    private val chatList = listOf(
        Chat(1, "Nuxoku", "Привет! Это Nogram", 1, peerUserId = 7876031245L),
        Chat(2, "Gifts", "Подарок получен", 0),
    )

    override fun chats() = flowOf(chatList)
    override fun messages(chatId: Long) = msgs.map { it[chatId].orEmpty() }
    override suspend fun send(chatId: Long, text: String) {
        msgs.update { m ->
            val cur = m[chatId].orEmpty()
            m + (chatId to cur + Message(cur.size + 1L, chatId, text, true, System.currentTimeMillis()))
        }
    }
    override suspend fun sendFile(chatId: Long, path: String) {}
    override suspend fun stories() = emptyList<Story>()
    override suspend fun publishStory(path: String, caption: String?) {}
    override suspend fun setOnline(online: Boolean) {}
    override suspend fun markRead(chatId: Long, upTo: Long) {}
}
