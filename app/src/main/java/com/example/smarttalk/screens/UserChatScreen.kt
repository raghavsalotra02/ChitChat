package com.example.smarttalk.screens

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smarttalk.Model.ChatMessage
import com.example.smarttalk.Model.Message
import com.example.smarttalk.R
import com.example.smarttalk.sharedPref.SharedPref
import com.example.smarttalk.ui.theme.lightGrey
import com.example.smarttalk.ui.theme.theme_blue
import com.example.smarttalk.utils.formatTimestamp
import com.example.smarttalk.viewModels.FirebaseChatViewmodel


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserChatScreen(navController : NavController,
                   viewModel : FirebaseChatViewmodel = hiltViewModel() , name: String? , number : String?)
{
    LaunchedEffect(Unit) {
        if (number != null) {
            viewModel.getMessage(number)
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val userInput by viewModel.userInput.collectAsState()
    val messages : State<List<Message>> = viewModel.messagesList.collectAsState()

    Scaffold(topBar = {
            TopAppBar(
                title = {
                    if (name != null) {
                        Text(name)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = theme_blue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
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
        }) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .consumeWindowInsets(PaddingValues())
                .windowInsetsPadding(WindowInsets.ime)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
                    .padding(horizontal = 5.dp),
                reverseLayout = true
            ) {
                items(messages.value) { message ->
                    MyChatBubble(message)
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            ChatBox(
                modifier = Modifier.fillMaxWidth(),
                userInput = userInput,
                onUserInputChange = {
                    viewModel.updateUserInput(it)
                },
                onSendMessage = {
                    SharedPref.get().userPhone?.let { if (number != null) {
                        viewModel.sendMessage(it,number,userInput)
                    }
                        viewModel.updateUserInput("")
                    }
                }
            )

            if (showDialog) {

                ShowSaveChatDialog(
                    onSave = {
//                        viewModel.clearChat()
//                        viewModel.saveChat()
                        val activity = (context as? Activity)
                        activity?.finish()
                        showDialog = false
                    },
                    onDismiss = {
                        showDialog = false
                        val activity = (context as? Activity)
                        activity?.finish()
                    }
                )
            }
        }

    }
}

@Composable
fun MyChatBubble(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = if (message.sender == SharedPref.get().userPhone) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 50.dp, max = 300.dp)
                .background(
                    if (message.sender == SharedPref.get().userPhone)
                        colorResource(id = R.color.light_purple) else lightGrey,
                    shape = RoundedCornerShape(25.dp)
                )
                .padding(5.dp)

        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = message.text,
                    color = if (message.sender == SharedPref.get().userPhone) Color.White else Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp, end = 30.dp)
                )
                val timestamp = formatTimestamp(message.timestamp)
                Text(
                    text = timestamp,
                    fontSize = 9.sp,
                    color = if (message.sender == SharedPref.get().userPhone) Color.White else Color.Gray,
                    lineHeight = 9.sp,
                    modifier = Modifier.padding(end = 10.dp)
                )

            }
        }
    }
}
