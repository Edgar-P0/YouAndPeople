package week11.st926963.youandpeople.model

import com.google.firebase.firestore.DocumentId


data class ChatItem(
    val roomId: String? = "",
    val date: String? = "",
    val message: String? = "",
    val user: String? = "",
    @DocumentId
    val id: String = ""
)
