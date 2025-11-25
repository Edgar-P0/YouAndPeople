package week11.st926963.youandpeople

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import week11.st926963.youandpeople.ui.theme.YouAndPeopleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YouAndPeopleTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background IMage",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "screen_one"
        ) {
            composable("screen_one") { LoginScreen(navController) }
            composable("screen_two") { ResetScreen(navController) }
            composable ("screen_three") {ChatroomsScreen(navController)}
            composable("screen_four") {LookingForChatsScreen(navController)}
            composable("screen_five") {ChatScreen(navController)}
        }
    }
}


