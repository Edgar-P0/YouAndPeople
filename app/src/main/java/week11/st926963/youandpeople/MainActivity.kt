package week11.st926963.youandpeople

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import week11.st926963.youandpeople.model.ChatItem
import week11.st926963.youandpeople.model.ChatRoom
import week11.st926963.youandpeople.util.UiState
import week11.st926963.youandpeople.viewmodel.MainViewModel
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var voiceLauncher: ActivityResultLauncher<Intent>
    private val vm: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register launcher
        voiceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
                spokenText?.let { vm.setVoiceInput(it) }
            }
        }
        tts = TextToSpeech(this, this)
        enableEdgeToEdge()
        setContent {

            val uiState by vm.uiState.collectAsState()
            val chats by vm.chats.collectAsState()
            BackHandler(enabled = uiState != UiState.Login) {
                vm.onBackPressed()
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = "Background IMage",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            when (uiState) {
                UiState.Login -> LoginScreen(vm)
                UiState.Chat -> ChatScreen(vm, onSpeak = { text ->
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ttsId")
                }, onVoiceInput = { startVoiceRecognition() })
                UiState.Chatrooms -> ChatroomsScreen(vm)
                UiState.LookingForChats -> LookingForChatsScreen(vm)
                UiState.Reset -> ResetScreen(vm)
            }
        }
    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = java.util.Locale.US
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
        }
        voiceLauncher.launch(intent)

    }
}


@Composable
fun ChatroomsScreen(vm: MainViewModel) {

    LaunchedEffect(Unit){
        vm.loadChatRooms()
    }

    fun formatTimestamp(ts: String): String {
        return try {
            val instant = Instant.parse(ts)
            val duration = Duration.between(instant, Instant.now())

            when {
                duration.toMinutes() < 1 -> "Now"
                duration.toMinutes() < 60 -> "${duration.toMinutes()} min"
                duration.toHours() < 24 -> "${duration.toHours()} hr"
                else -> "${duration.toDays()} d"
            }
        } catch (e: Exception) {
            ""
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chatrooms",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(id = R.drawable.pfp),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(width = 40.dp, height = 40.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Pinned Chats",
            color = Color.White,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ){
            PinnedCircle("S", "SDNE")
            PinnedCircle("R", "Robotics Club")
            PinnedCircle("E", "Engineering Club")
        }
        Spacer(modifier = Modifier.height(25.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(vm.chatRooms) { room ->
                ChatCard(
                    vm = vm,
                    letter = room.name.take(1).uppercase(),
                    title = room.name,
                    message = room.lastMessage,
                    time = formatTimestamp(room.lastMessageTimestamp),
                    onClick = {
                        vm.openChatroom(room.id)
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 10.dp)
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFF4C23FF))
                .clickable{
                    vm.lookingForChatScreen()
                },
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "+",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PinnedCircle(letter: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Color(0xFF4C23FF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                letter,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun ChatCard(vm: MainViewModel, letter: String, title: String, message: String, time: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(25.dp)
            )
            .clickable{ onClick() }
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4C23FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(letter, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = Color(0xFF4C23FF),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = time,
                        color = Color(0xFF4C23FF),
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = message,
                    color = Color(0xFF4C23FF),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun ResetScreen(vm: MainViewModel){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ){
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back button",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .size(width = 50.dp, height = 50.dp)
                    .clickable{
                        vm.loginScreen()
                    }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "You & People",
            fontSize = 32.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 45.sp
        )
        Spacer(modifier = Modifier.height(140.dp))
        Text(
            text = "Enter Email Address",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
        )
        TextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {},
            modifier = Modifier
                .height(55.dp)
                .width(200.dp),
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C4DFF),
                contentColor = Color.White
            ),
            border = BorderStroke(2.dp, Color.White.copy(alpha = 0.4f))
        ){
            Text(
                text = "Reset Password",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LookingForChatsScreen(vm: MainViewModel){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Look For Chats",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Search Chatrooms",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
        )
        TextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {},
            modifier = Modifier
                .height(55.dp)
                .width(200.dp),
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C4DFF),
                contentColor = Color.White
            ),
            border = BorderStroke(2.dp, Color.White.copy(alpha = 0.4f))
        ){
            Text(
                text = "Search",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LoginScreen(vm: MainViewModel){
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = "You & People",
            fontSize = 32.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 45.sp
        )
        Spacer(modifier = Modifier.height(140.dp))
        Text(
            text = "Username",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
        )
        TextField(
            value = email,
            onValueChange = {email = it},
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Password",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
        )
        TextField(
            value = password,
            onValueChange = {password = it},
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Forgot Password?",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable{
                    vm.resetScreen()
                }
        )
        Text(error, color = Color.Red)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if(email.isNotEmpty() && password.isNotEmpty()){
                    vm.login(email, password)
                }else{
                    error = "Please fill in all fields"
                }
            },
            modifier = Modifier
                .height(55.dp)
                .width(200.dp),
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C4DFF),
                contentColor = Color.White
            ),
            border = BorderStroke(2.dp, Color.White.copy(alpha = 0.4f))
        ){
            Text(
                text = "Login",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick =
                {
                    if(email.isNotEmpty() && password.isNotEmpty())
                    {
                        vm.signUp(email, password)
                    }else{
                        error = "Please fill in all fields"
                    }
                },
            modifier = Modifier
                .height(55.dp)
                .width(200.dp),
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C4DFF),
                contentColor = Color.White
            ),
            border = BorderStroke(2.dp, Color.White.copy(alpha = 0.4f))
        ){
            Text(
                text = "Register",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Sign-in or Sign-up Using",
            color = Color.White,
            fontSize = 12.sp,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google Login",
                modifier = Modifier.size(width = 30.dp, height = 30.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.twitter),
                contentDescription = "Twitter/X Login",
                modifier = Modifier.size(width = 30.dp, height = 30.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.github),
                contentDescription = "GitHub Login",
                modifier = Modifier.size(width = 30.dp, height = 30.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.facebook),
                contentDescription = "Facebook Login",
                modifier = Modifier.size(width = 30.dp, height = 30.dp)
            )
        }
    }
}

@Composable
fun ChatScreen(vm: MainViewModel, onSpeak: (String) -> Unit, onVoiceInput: () -> Unit) {

    val messages by vm.messages.collectAsState()
    val roomId = vm.selectedChatroomId.collectAsState().value
    val room = vm.getSelectedRoom()

    var message by rememberSaveable { mutableStateOf("") }

    // React to voice input from the ViewModel
    val voiceInput by vm.voiceInput.collectAsState()
    LaunchedEffect(voiceInput) {
        voiceInput?.let {
            message += it           // append the recognized text
            vm.clearVoiceInput()    // clear in the VM
        }
    }


    if (roomId == null) {
        Text("No chatroom selected")
        return
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        ChatHeader(room)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f)
        ) {
            MessageList(
                modifier = Modifier.fillMaxWidth(),
                chats = messages,
                vm = vm,
                onSpeak = onSpeak
            )
        }

        // Input bar
        MessageInputBar(
            message = message,
            onMessageChange = { message = it },
            onSend = {
                if (message.isNotEmpty()) {
                    vm.addChat(message)
                    message = ""
                }
            },
            vm = vm,
            onVoiceInput = onVoiceInput
        )
    }
}



@Composable
fun ChatHeader(room: ChatRoom?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF6C4DFF),
                shape = RoundedCornerShape(0.dp, 0.dp, 30.dp, 30.dp)
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color.White.copy(alpha = 0.2f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = room?.name?.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = room?.name ?: "Chat",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


data class Message(val text: String, val isUser: Boolean)

@Composable
fun MessageList(modifier: Modifier = Modifier, chats: List<ChatItem>, vm: MainViewModel, onSpeak: (String) -> Unit) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        items(chats) { msg ->
            val isUser = msg.user == vm.getEmail()
            if (!msg.gifUrl.isNullOrEmpty()) {
                if (isUser) {
                    UserGifMessage(msg.gifUrl)
                }
            } else if (!msg.message.isNullOrEmpty()) {
                if (isUser) {
                    UserMessage(msg.message, onSpeak = onSpeak)
                } else {
                    OtherMessage(msg.message, onSpeak = onSpeak)
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
@Composable
fun UserGifMessage(gifUrl: String) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(GifDecoder.Factory()) // enable GIF animation
        }
        .build()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFF6957FF), shape = RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(gifUrl)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "GIF",
                modifier = Modifier
                    .width(200.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun OtherMessage(text: String?, onSpeak: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.pfp),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            if (text != null) {
                Text(text, color = Color.Black)
            }
        }
        IconButton(
            onClick = {
                text?.let { onSpeak(it) }
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = "Speak",
                tint = Color(0xFF6957FF)
            )
        }
    }
}

@Composable
fun UserMessage(
    text: String?,
    onSpeak: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {


        IconButton(
            onClick = {
                text?.let { onSpeak(it) }
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = "Speak",
                tint = Color(0xFF6957FF)
            )
        }

        Box(
            modifier = Modifier
                .background(Color(0xFF6957FF), shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            if (text != null) {
                Text(text, color = Color.White)
            }
        }
    }
}

@Composable
fun MessageInputBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    vm: MainViewModel,
    onVoiceInput: () -> Unit
) {
    var showGifPicker by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .background(Color.White, RoundedCornerShape(5.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                TextField(
                    value = message,
                    onValueChange = onMessageChange,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF6C63FF)
                    ),
                    modifier = Modifier.weight(1f),
                )

                IconButton(onClick =
                    {
                        onVoiceInput()
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.microphone),
                        contentDescription = null,
                        tint = Color(0xFF6C4DFF),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = { showGifPicker = true }) {
            Icon(
                painter = painterResource(id = R.drawable.gif),
                contentDescription = "Send GIF",
                tint = Color(0xFF6C4DFF),
                modifier = Modifier.size(28.dp)
            )
        }

        IconButton(onClick = onSend) {
            Icon(
                painter = painterResource(id = R.drawable.send),
                contentDescription = null,
                tint = Color(0xFF6C4DFF),
                modifier = Modifier.size(28.dp)
            )
        }
    }
    if (showGifPicker) {
        GifPicker(
            onDismiss = { showGifPicker = false },
            onGifSelected = { gifUrl ->
                vm.addsGifToChat(LocalDateTime.now().toString(), gifUrl)
            }
        )
    }
}



@Composable
fun GifPicker(
    onDismiss: () -> Unit,
    onGifSelected: (String) -> Unit
) {
    val gifs = listOf(
        "https://media.giphy.com/media/3o7aD2saalBwwftBIY/giphy.gif",
        "https://media.giphy.com/media/l0MYt5jPR6QX5pnqM/giphy.gif",
        "https://media.giphy.com/media/3oriO0OEd9QIDdllqo/giphy.gif",
        "https://media.giphy.com/media/26uf5Hghn9SQzCKL6/giphy.gif",
        "https://media.giphy.com/media/g9582DNuQppxC/giphy.gif",
        "https://media.giphy.com/media/ICOgUNjpvO0PC/giphy.gif",
        "https://media.giphy.com/media/l46Cy1rHbQ92uuLXa/giphy.gif",
        "https://media.giphy.com/media/26u4cqiYI30juCOGY/giphy.gif",
        "https://media1.tenor.com/m/nntljd7AbREAAAAd/cant-sleep-awake.gif",
        "https://media1.tenor.com/m/GLMgQt-oMSEAAAAd/teach-me-how-to-dougie.gif"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF6C4DFF))
                        .padding(16.dp)
                ) {
                    Text("Choose a GIF", color = Color.White, fontSize = 18.sp)
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(gifs) { gifUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(gifUrl),
                            contentDescription = "GIF",
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onGifSelected(gifUrl)
                                    onDismiss()
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}


