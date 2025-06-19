package com.example.smarttalk.screens

import android.Manifest
import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.smarttalk.Navigation.Routes
import com.example.smarttalk.R
import com.example.smarttalk.ui.theme.theme_blue
import com.example.smarttalk.viewModels.HomeScreenViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    navController : NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val permissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS)
    val contactList by viewModel.contacts
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            viewModel.readContacts(context)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                title = { Text("Contact Screen") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = theme_blue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

            )
        }

    ) {innerPadding ->
        if (permissionState.status.isGranted) {
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()){
                LazyColumn(
                    contentPadding = PaddingValues(0.dp)
                ) {
                    items(contactList) { item ->
                        ChatItem(
                            name = item.name,
                            lastMessage = item.phone,
                        )
                    }
                }
            }
        }
    }
}
