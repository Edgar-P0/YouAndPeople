package week11.st926963.youandpeople.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import week11.st926963.youandpeople.data.ChatRepository
import week11.st926963.youandpeople.model.ChatItem
import week11.st926963.youandpeople.model.ChatRoom
import week11.st926963.youandpeople.util.UiState


class MainViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val repo = ChatRepository()

    private val _uiState = MutableStateFlow<UiState>(UiState.Login)
    val uiState: StateFlow<UiState> = _uiState

    private val _chats = MutableStateFlow<List<ChatItem>>(emptyList())
    val chats: StateFlow<List<ChatItem>> = _chats

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _messages = MutableStateFlow<List<ChatItem>>(emptyList())
    val messages: StateFlow<List<ChatItem>> = _messages

    var chatRooms by mutableStateOf(emptyList<ChatRoom>())
        private set

    private val _selectedChatroomId = MutableStateFlow<String?>(null)
    val selectedChatroomId: StateFlow<String?> = _selectedChatroomId

    init {
       auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                _uiState.value = UiState.Login
                _chats.value = emptyList()
            } else {
                _uiState.value = UiState.Chatrooms
                observeChatList()
            }
        }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { _uiState.value = UiState.Chatrooms }
            .addOnFailureListener { e ->
                _uiState.value = UiState.Login
                _message.value = e.localizedMessage ?: "Login failed"
            }
    }

    fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { _uiState.value = UiState.Chatrooms }
            .addOnFailureListener { e ->
                _uiState.value = UiState.Login
                _message.value = e.localizedMessage ?: "Sign up failed"
            }
    }

    fun logout() {
        auth.signOut()
        _uiState.value = UiState.Login
    }

    private fun observeChatList() {
        viewModelScope.launch {
            repo.getChats().collect { list ->
                _chats.value = list
            }
        }
    }

    fun addChat(message: String) {
        val roomId = selectedChatroomId.value ?: return
        viewModelScope.launch {
            repo.addChat(roomId, java.time.LocalDateTime.now().toString(), message)
        }
    }

    fun getSelectedRoom(): ChatRoom? {
        val id = selectedChatroomId.value ?: return null
        return chatRooms.find { it.id == id }
    }


    fun deleteChat(item : ChatItem){
        viewModelScope.launch{
            repo.deleteChat(item)
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun loadChatRooms() {
        repo.getChatRooms { rooms ->
            chatRooms = rooms
        }
    }

    fun openChatroom(id: String) {
        _selectedChatroomId.value = id
        observeMessages(id)
        _uiState.value = UiState.Chat
    }

    private fun observeMessages(roomId: String) {
        viewModelScope.launch {
            repo.getMessagesForRoom(roomId).collect { list ->
                _messages.value = list
            }
        }
    }


    fun getEmail(): String{
        return auth.currentUser?.email.toString()
    }

    fun loginScreen(){
        _uiState.value = UiState.Login
    }
    fun chatScreen(){
        _uiState.value = UiState.Chat
    }
    fun chatroomScreen(){
        _uiState.value = UiState.Chatrooms
    }
    fun lookingForChatScreen(){
        _uiState.value = UiState.LookingForChats
    }
    fun resetScreen(){
        _uiState.value = UiState.Reset
    }

    fun onBackPressed() {
        when (_uiState.value) {

            UiState.Chat -> {
                _uiState.value = UiState.Chatrooms
            }

            UiState.Chatrooms -> {
                _uiState.value = UiState.Login
            }

            UiState.LookingForChats -> {
                _uiState.value = UiState.Chatrooms
            }

            UiState.Reset -> {
                _uiState.value = UiState.Login
            }

            UiState.Login -> {
            }
        }
    }
}