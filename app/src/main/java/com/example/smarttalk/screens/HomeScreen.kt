package com.example.smarttalk.screens

import android.Manifest
import android.content.Intent
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
){
    SetStatusBarColor( color = theme_blue )

    val context = LocalContext.current
    val firebaseViewModel : FirebaseChatViewmodel = hiltViewModel()

    val contacts by viewModel.contacts

    val phone = SharedPref.get().userPhone
    val name = SharedPref.get().userName

    if (phone != null && name != null) {
        firebaseViewModel.registerUser(
            userId = phone,
            name = name,
            profilePicUrl = "https://picsum.photos/200/300"
        )
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
                Icon(
                    painter = painterResource(R.drawable.icon_ai),
                    contentDescription = "ai",
                    modifier = Modifier.size(50.dp)
                        .padding(10.dp)
                        .clickable {
                            val intent = Intent(context,ChatActivity::class.java)
                            context.startActivity(intent)
                        }
                )
            }
        }
    ) { innerPadding ->

        Column(modifier = modifier.fillMaxSize().padding(innerPadding)) {
            ChatItem("Raghav", "Hii")
            ExtendedButtonWithTExtAndImage( navController)
        }
    }



}

@Composable
fun ChatItem( name : String , lastMessage : String){

    Box(modifier = Modifier
        .fillMaxWidth(),
        ){
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(10.dp)){
                RoundImage(image = painterResource(R.drawable.profilepicture), modifier = Modifier.size(50.dp))
                Spacer(modifier = Modifier.width(15.dp))
                Column {
                    Text(
                        text = name, fontWeight = FontWeight.Bold
                    )
                    Text(text = lastMessage,
                        color = Color.Gray,
                        fontSize = 14.sp)
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
        ExtendedFloatingActionButton(
            onClick = {
                navController.navigate(Routes.contactScreen.route)
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = "ai",
//                    modifier = Modifier.size(50.dp)
//                        .padding(5.dp)
                )
            },
            text = {},
            modifier = Modifier
                .padding(end = 16.dp, bottom = 16.dp)
        )
    }
}