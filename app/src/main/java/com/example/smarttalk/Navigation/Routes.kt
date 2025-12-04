package com.example.smarttalk.Navigation

sealed class Routes(val route : String){
    object splashScreen : Routes("splashScreen")
    object loginScreen : Routes("loginScreen")
    object otpScreen : Routes("loginScreen")
    object contactScreen : Routes("contactScreen")
    object homeScreen : Routes("homeScreen")
    object userChatScreen : Routes("userChatScreen")
    object profileScreen : Routes("profileScreen")
}