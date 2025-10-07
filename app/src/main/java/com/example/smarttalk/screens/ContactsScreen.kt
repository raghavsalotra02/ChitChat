package com.example.smarttalk.screens

import android.Manifest
import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
import kotlin.math.roundToInt


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    navController : NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val permissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS)
    val contactList by viewModel.finalContactList.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            viewModel.readContacts()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                title = { Text("Contacts") },
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
                val isLoading by viewModel.isLoadingContacts.collectAsState()
                val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                val itemHeight = 97.dp
                val shimmerItemsCount = (screenHeight / itemHeight).roundToInt() + 2

                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .padding()
                    ){
                        repeat(shimmerItemsCount){
                            ShimmerListItem()
                        }
                    }

                } else {

                    LazyColumn(
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        items(contactList) { item ->
                            val clickableModifier = if (item.isOnApp) {
                                Modifier.clickable {
                                    navController.navigate(Routes.userChatScreen.route + "/${item.name}/${item.phoneNumber}")
                                }
                            } else {
                                Modifier
                            }
                            ChatItemContacts(
                                name = item.name,
                                lastMessage = item.phoneNumber,
                                canChat = item.isOnApp,
                                modifier = clickableModifier,
                                phone = item.phoneNumber
                            )
                        }
                    }
                }
            }
        }
    }
}
