package week11.st926963.youandpeople

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun ChatScreen(navController: NavHostController){
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
                    .fillMaxWidth()
            )
        }

        MessageInputBar(
            message = "",
            onMessageChange = {},
            onSend = {}
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
fun MessageList(modifier: Modifier = Modifier) {
    val messages = listOf(
        Message("Hey did you register for your new courses", isUser = false),
        Message("Im trying to register for my new courses but im having issues", isUser = true),
        Message("Bummer", isUser = false)
    )

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        items(messages) { msg ->
            if (msg.isUser) UserMessage(msg.text)
            else OtherMessage(msg.text)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun OtherMessage(text: String) {
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
            Text(text, color = Color.Black)
        }
    }
}

@Composable
fun UserMessage(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFF6957FF), shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(text, color = Color.White)
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


