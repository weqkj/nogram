package app.nogram.data

import kotlinx.coroutines.flow.Flow

data class Chat(
    val id: Long,
    val title: String,
    val lastMessage: String,
    val unread: Int,
    val peerUserId: Long? = null,
    val avatarUrl: String? = null,
)

data class Message(
    val id: Long,
    val chatId: Long,
    val text: String,
    val outgoing: Boolean,
    val timeMs: Long,
    val mediaPath: String? = null,
)

data class Story(val id: Long, val ownerName: String, val mediaPath: String?)

/** Всё, что UI знает о Telegram. Реализация на TDLib подключается за этим интерфейсом. */
interface TelegramRepository {
    fun chats(): Flow<List<Chat>>
    fun messages(chatId: Long): Flow<List<Message>>
    suspend fun send(chatId: Long, text: String)
    suspend fun sendFile(chatId: Long, path: String)
    /** Обход бага: истории, недоступные в официальном клиенте. */
    suspend fun stories(): List<Story>
    suspend fun publishStory(path: String, caption: String?)
    suspend fun setOnline(online: Boolean)
    suspend fun markRead(chatId: Long, upTo: Long)
}
