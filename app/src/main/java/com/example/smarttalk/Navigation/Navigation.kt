package com.example.smarttalk.Navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smarttalk.repository.HomeScreenRepository
import com.example.smarttalk.screens.ContactsScreen
import com.example.smarttalk.screens.HomeScreen
import com.example.smarttalk.screens.LoginScreen
import com.example.smarttalk.screens.OTPScreen
import com.example.smarttalk.screens.SplashScreen
import com.example.smarttalk.viewModels.AuthViewModel
import com.example.smarttalk.viewModels.HomeScreenViewModel

@Composable
fun Navigation(authViewModel: AuthViewModel){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.splashScreen.route,modifier = Modifier) {

        composable(route = Routes.splashScreen.route,
        ){
            SplashScreen(navController = navController)
        }

        composable(route = Routes.loginScreen.route,
            ){
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(route = Routes.otpScreen.route + "/{name}/{number}",
            arguments = listOf(
                navArgument("name"){
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("number"){
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ){
            val number = it.arguments?.getString("number")
            val name = it.arguments?.getString("name")
                OTPScreen(navController = navController , authViewModel, number = number , name = name)
        }

        composable(route = Routes.homeScreen.route) { backStackEntry ->
            val viewModel: HomeScreenViewModel = hiltViewModel(backStackEntry)
            HomeScreen( modifier = Modifier ,navController = navController ,viewModel = viewModel)
        }

        composable(route = Routes.contactScreen.route) { backStackEntry ->
            val viewModel: HomeScreenViewModel = hiltViewModel(backStackEntry)
            ContactsScreen( navController = navController, viewModel = viewModel)
        }

    }
}