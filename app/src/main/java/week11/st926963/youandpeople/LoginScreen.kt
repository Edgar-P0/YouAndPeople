package week11.st926963.youandpeople


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun LoginScreen(navController: NavHostController){
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
            value = "",
            onValueChange = {},
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
            value = "",
            onValueChange = {},
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
                    navController.navigate("screen_two")
                }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                navController.navigate("screen_three")
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