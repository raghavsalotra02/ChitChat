package com.example.smarttalk.screens

import android.window.SplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.smarttalk.Navigation.Routes
import com.example.smarttalk.R
import com.example.smarttalk.sharedPref.SharedPref
import com.example.smarttalk.ui.theme.theme_blue
import com.example.smarttalk.utils.SetStatusBarColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreen( navController: NavController){
    SetStatusBarColor( color = theme_blue )
    val loggedIn = SharedPref.get().loggedIn

    LaunchedEffect(Unit) {
        delay(2000)

        if(loggedIn){
            navController.navigate(Routes.homeScreen.route) {
                popUpTo(Routes.splashScreen.route) { inclusive = true }
            }
        }else{
            navController.navigate(Routes.loginScreen.route) {
                popUpTo(Routes.splashScreen.route) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()
        .background(color = theme_blue),
        contentAlignment = Alignment.Center) {
        Image(painterResource(R.drawable.chitchat),
            "")
    }

}