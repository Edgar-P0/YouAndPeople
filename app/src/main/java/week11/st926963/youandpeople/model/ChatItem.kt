package week11.st926963.youandpeople.model

import com.google.firebase.firestore.DocumentId


data class ChatItem(
    val date: String? = "",
    val user: String? = "",
    val message: String? = "",
    @DocumentId
    val id: String = ""
)
