package week11.st926963.youandpeople

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.lifecycle.viewmodel.compose.viewModel
import week11.st926963.youandpeople.model.ChatItem
import week11.st926963.youandpeople.util.UiState
import week11.st926963.youandpeople.viewmodel.MainViewModel
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: MainViewModel = viewModel()
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
                UiState.Chat -> ChatScreen(vm, chats)
                UiState.Chatrooms -> ChatroomsScreen(vm)
                UiState.LookingForChats -> LookingForChatsScreen(vm)
                UiState.Reset -> ResetScreen(vm)
            }
        }
    }
}


@Composable
fun ChatroomsScreen(vm: MainViewModel) {
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
            items(4){ index ->
                when (index){
                    0 -> ChatCard(vm,"S", "SDNE", "Edgar: Are you guys ready for the quiz?", "4 min")
                    1 -> ChatCard(vm,"R", "Robotics Club", "Andriy: Guys I have some news!", "25 min")
                    2 -> ChatCard(vm,"E", "Engineering Club", "Josh: Are guys coming tonight?", "1 Day")
                    3 -> ChatCard(vm, "B", "Book Club", "Joe: Did you guys read Harry Potter?", "4 Days")
                }
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
fun ChatCard(vm: MainViewModel, letter: String, title: String, message: String, time: String) {
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
            .clickable{ vm.chatScreen() }
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
fun ChatScreen(vm: MainViewModel, chats : List<ChatItem>){
    var message by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ){
        ChatHeader()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f)
        ){
            MessageList(
                modifier = Modifier
                    .fillMaxWidth(), chats, vm
            )
        }

        MessageInputBar(
            message = message,
            onMessageChange = {message = it},
            onSend =
                {
                    if(message.isNotEmpty())
                    {
                        vm.addChat(LocalDateTime.now().toString(), message)
                        message = ""
                    }
                }
        )
    }
}

@Composable
fun ChatHeader(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF6C4DFF), shape = RoundedCornerShape(0.dp, 0.dp, 30.dp, 30.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ){
        Row(verticalAlignment = Alignment.CenterVertically){
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFF6C4DFF), shape = CircleShape),
                contentAlignment = Alignment.Center
            ){
                Text("S", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Text("SDNE", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

data class Message(val text: String, val isUser: Boolean)

@Composable
fun MessageList(modifier: Modifier = Modifier, chats: List<ChatItem>, vm: MainViewModel) {
    val messages = listOf(
        Message("Hey did you register for your new courses", isUser = false),
        Message("Im trying to register for my new courses but im having issues", isUser = true),
        Message("Bummer", isUser = false)
    )

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        items(chats) { msg ->
            if (msg.user == vm.getEmail()) UserMessage(msg.message)
            else OtherMessage(msg.message)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun OtherMessage(text: String?) {
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
    }
}

@Composable
fun UserMessage(text: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
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
    onSend: () -> Unit
) {
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

                IconButton(onClick = { /* TODO handle microphone */ }) {
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

        IconButton(onClick = { /* TODO handle GIF */ }) {
            Icon(
                painter = painterResource(id = R.drawable.gif),
                contentDescription = null,
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
}

