package com.example.smarttalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.smarttalk.Navigation.Navigation
import com.example.smarttalk.ui.theme.SmartTalkTheme
import com.example.smarttalk.viewModels.AuthViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class LoginActivty : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartTalkTheme {
                val authViewModel: AuthViewModel = AuthViewModel()
                    Navigation(authViewModel)
            }
        }
    }
}