package week11.st926963.youandpeople

import android.R.attr.onClick
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@Composable
fun ChatroomsScreen(navController: NavHostController) {
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
                    0 -> ChatCard(navController = navController,"S", "SDNE", "Edgar: Are you guys ready for the quiz?", "4 min")
                    1 -> ChatCard(navController = navController,"R", "Robotics Club", "Andriy: Guys I have some news!", "25 min")
                    2 -> ChatCard(navController = navController,"E", "Engineering Club", "Josh: Are guys coming tonight?", "1 Day")
                    3 -> ChatCard(navController = navController,"B", "Book Club", "Joe: Did you guys read Harry Potter?", "4 Days")
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
                    navController.navigate("screen_four")
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
fun ChatCard(navController: NavController, letter: String, title: String, message: String, time: String) {
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
            .clickable{ navController.navigate("screen_five") }
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
