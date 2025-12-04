package com.example.smarttalk.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Telephony
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.example.smarttalk.ChatActivity
import com.example.smarttalk.Navigation.Routes
import com.example.smarttalk.R
import com.example.smarttalk.sharedPref.SharedPref
import com.example.smarttalk.ui.theme.theme_blue
import com.example.smarttalk.utils.SetStatusBarColor
import com.example.smarttalk.viewModels.FirebaseChatViewmodel
import com.example.smarttalk.viewModels.HomeScreenViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.math.roundToInt

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
){
    SetStatusBarColor( color = theme_blue )
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS)

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            viewModel.readContacts()
            viewModel.loadChatList()
        }
    }

    // Also load chat list when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadChatList()
    }


    Scaffold(
        topBar = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = theme_blue),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                Text( modifier = Modifier.padding(start = 16.dp),text = "ChitChat", color = Color.White,fontSize = 24.sp)

                Row(){
                    Icon(
                        painter = painterResource(R.drawable.icon_ai),
                        contentDescription = "ai",
                        modifier = Modifier.size(50.dp)
                            .padding(10.dp)
                            .clickable {
                                val intent = Intent(context,ChatActivity::class.java)
                                context.startActivity(intent)
                            },
                        tint = Color.Unspecified
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_profile),
                        contentDescription = "profileIcon",
                        modifier = Modifier.size(53.dp)
                            .padding(10.dp)
                            .clickable {
                                navController.navigate(Routes.profileScreen.route)
                            },
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                }
            }
        }
    ) { innerPadding ->

        val chatList by viewModel.chatList.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()

        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
        val itemHeight = 97.dp
        val shimmerItemsCount = (screenHeight / itemHeight).roundToInt() + 2

        if (isLoading) {
            Column(
                modifier = Modifier//.fillMaxSize()
                    .padding(innerPadding)
            ){
                repeat(shimmerItemsCount){
                    ShimmerListItem()
                }
            }

        } else {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(chatList) { chat ->

                    val clickableModifier = if (chat.isOnApp) {
                        Modifier.clickable {
                            navController.navigate(Routes.userChatScreen.route + "/${chat.name}/${chat.phoneNumber}")
                        }
                    } else {
                        Modifier
                    }
                    ChatItemContacts(
                        name = chat.name,
                        lastMessage = "lastMessage",
                        canChat = true,
                        modifier = clickableModifier
                    )
                }
            }
        }
            Column(modifier = modifier.fillMaxSize().padding(innerPadding)) {
                ExtendedButtonWithTExtAndImage( navController)
            }
    }
}

@Composable
fun ChatItemContacts( name : String , lastMessage : String, canChat : Boolean, modifier: Modifier, phone : String? = null){

    val context = LocalContext.current

    Box(modifier = modifier
        .fillMaxWidth(),
        ){
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    RoundImage(
                        image = painterResource(R.drawable.profilepicture),
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Column {
                        val displayName = if (name.length > 16) name.take(16) + "..." else name
                        Text(
                            text = displayName, fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = lastMessage,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                if(!canChat){
                    Text(
                        text = "Invite + ",
                        color = theme_blue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .clickable{
                                val inviteMessage = "Hey! Join me on ChitChat app. Download it here: https://yourapp.link"

                                val smsUri = Uri.parse("smsto:$phone")
                                val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
                                    putExtra("sms_body", inviteMessage)
                                }

                                // Get the package name of the default SMS app
                                val defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(context)

                                if (defaultSmsPackage != null) {
                                    // Explicitly open the system SMS app
                                    smsIntent.setPackage(defaultSmsPackage)
                                }

                                try {
                                    context.startActivity(smsIntent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(context, "No SMS app found", Toast.LENGTH_SHORT).show()
                                }
                            })
                }
            }
            Divider(color = Color.LightGray, thickness = 0.7.dp)
        }
    }
}

@Composable
fun RoundImage(image : Painter, modifier : Modifier = Modifier){
    Image(
        painter = image,
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f,matchHeightConstraintsFirst = true)
            .clip(CircleShape)
    )
}

@Composable
fun ExtendedButtonWithTExtAndImage( navController : NavController) {
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd,
    ) {
        FloatingActionButton(
            onClick = {
                navController.navigate(Routes.contactScreen.route)
            },
            modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = "Add Contact"
            )
        }
    }
}

@Composable
fun ShimmerAnimation(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim, y = 0f),
        end = Offset(x = translateAnim + 200f, y = 0f)
    )

    Box(
        modifier = modifier
            .background(brush = brush, shape = RoundedCornerShape(8.dp))
    ){

    }
}


@Composable
fun ShimmerListItem() {

    Box(modifier = Modifier
        .fillMaxWidth(),
    ){
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    ShimmerAnimation(
                        modifier = Modifier
                            .requiredSize(50.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Column(verticalArrangement = Arrangement.Center) {
                        ShimmerAnimation(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(12.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        ShimmerAnimation(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(12.dp)
                        )
                    }
                }
            }
            Divider(color = Color.LightGray, thickness = 0.7.dp)
        }
    }
}
