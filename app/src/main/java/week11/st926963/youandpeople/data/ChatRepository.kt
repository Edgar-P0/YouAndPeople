package week11.st926963.youandpeople.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import week11.st926963.youandpeople.model.ChatItem
import week11.st926963.youandpeople.model.ChatRoom
import java.time.Instant

class ChatRepository {
    private val auth = FirebaseAuth.getInstance()

    private val db = FirebaseFirestore.getInstance()

    private val chatsCollection = Firebase.firestore.collection("chats")

    suspend fun addChat(roomId: String, date: String, message: String?, gifUrl: String? = null) {
        val user = auth.currentUser ?: return

        val item = ChatItem(
            roomId = roomId,
            message = message,
            user = user.email,
            date = date,
            gifUrl = gifUrl
        )

        db.collection("chats").add(item).await()

        db.collection("chatRooms")
            .document(roomId)
            .update(
                mapOf(
                    "lastMessage" to message,
                    "lastMessageTime" to date
                )
            )
    }

    suspend fun deleteChat(item: ChatItem){
        val user = auth.currentUser ?: return

        db.collection("chats")
            .document(item.id).delete()
            .await()

    }

    fun getMessagesForRoom(roomId: String): Flow<List<ChatItem>> = callbackFlow {
        val reg = db.collection("chats")
            .whereEqualTo("roomId", roomId)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    val msg = doc.toObject(ChatItem::class.java)
                    msg?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list.sortedBy { it.date })
            }

        awaitClose { reg.remove() }
    }


    fun getChatRooms(onResult: (List<ChatRoom>) -> Unit) {
        db.collection("chatRooms")
            .get()
            .addOnSuccessListener { snapshot ->
                val rooms = snapshot.toObjects(ChatRoom::class.java)
                    .sortedByDescending {
                        if (it.lastMessageTimestamp.isNotEmpty())
                            Instant.parse(it.lastMessageTimestamp)
                        else Instant.EPOCH
                    }
                onResult(rooms)
            }
    }

    fun getChats(): Flow<List<ChatItem>> = callbackFlow {
        val user = auth.currentUser
        if (user == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val reg = db.collection("chats")
            .addSnapshotListener { snapshot, _ ->
                val chats = snapshot?.toObjects(ChatItem::class.java) ?: emptyList()

                val sortedChats = chats.sortedBy { chat ->
                    try {
                        java.time.LocalDateTime.parse(chat.date)
                    } catch (e: Exception) {
                        java.time.LocalDateTime.MIN
                    }
                }

                trySend(sortedChats)
            }
        awaitClose { reg.remove() }
    }

}