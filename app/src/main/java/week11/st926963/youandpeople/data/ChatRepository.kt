package week11.st926963.youandpeople.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import week11.st926963.youandpeople.model.ChatItem

class ChatRepository {
    private val auth = FirebaseAuth.getInstance()

    private val db = FirebaseFirestore.getInstance()

    suspend fun addChat(item: ChatItem) {
        val user = auth.currentUser ?: return

        val itemWithEmail = item.copy(user = user.email)
        db.collection("chats")
            .add(itemWithEmail)
            .await()
    }

    suspend fun deleteChat(item: ChatItem){
        val user = auth.currentUser ?: return

        db.collection("chats")
            .document(item.id).delete()
            .await()

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