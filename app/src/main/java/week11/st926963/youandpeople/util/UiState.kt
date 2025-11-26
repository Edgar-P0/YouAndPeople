package week11.st926963.youandpeople.util

sealed class UiState {
    object Login : UiState()
    object Reset : UiState()
    object Chat : UiState()
    object LookingForChats : UiState()
    object Chatrooms : UiState()
}