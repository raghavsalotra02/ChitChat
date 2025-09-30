package com.example.smarttalk.screens
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarttalk.ChatActivity
import com.example.smarttalk.Model.ChatMessage
import com.example.smarttalk.Navigation.Routes
import com.example.smarttalk.R
import com.example.smarttalk.ui.theme.lightBlue
import com.example.smarttalk.ui.theme.lightGrey
import com.example.smarttalk.ui.theme.theme_blue
import com.example.smarttalk.utils.SetStatusBarColor
import com.example.smarttalk.viewModels.ChatScreenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(modifier : Modifier = Modifier) {

    SetStatusBarColor( color = theme_blue )

    val viewModel : ChatScreenViewModel = hiltViewModel()

    val messages by viewModel.messages.collectAsState()
    val userInput by viewModel.userInput.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    BackHandler(enabled = messages.isNotEmpty()){
        showDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text( text = "Chat with AI", Modifier.padding(start = 5.dp))},
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = theme_blue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = {(context as Activity).finish()})  {
                        Icon(imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back",
                            modifier = Modifier.size(24.dp))
                    }}
            )
        }
    ) { innerPadding ->

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
//                .background(Color.White)
                .consumeWindowInsets(PaddingValues())
                .windowInsetsPadding(WindowInsets.ime)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
                    .padding(horizontal = 5.dp),
                reverseLayout = true
            ) {
                items(messages) { message ->
                    if(message.content.contains("** **")){

                    }
                    ChatBubble(message)
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            ChatBox(
                modifier = Modifier.fillMaxWidth(),
                userInput = userInput,
                onUserInputChange = { viewModel.updateUserInput(it) },
                onSendMessage = {
                    viewModel.sendMesasge()
                }
            )

            if (showDialog) {

                ShowSaveChatDialog(
                    onSave = {
                        viewModel.clearChat()
                        viewModel.saveChat()
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
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = if (message.role == "user") Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 50.dp, max = 300.dp)
                .background(
                    if (message.role == "user")
                        colorResource(id = R.color.light_purple) else lightGrey,
                    shape = RoundedCornerShape(25.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                color = if (message.role == "user") Color.White else Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}



@Composable
fun ChatBox(
    modifier: Modifier = Modifier,
    userInput: String,
    onUserInputChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 5.dp, bottom = 5.dp)
                    .weight(4.5f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color = lightGrey)
            ) {
                BasicTextField(
                    value = userInput,
                    onValueChange = { onUserInputChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(40.dp,200.dp)
                        .padding(15.dp),
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                    decorationBox = { innerTextField ->
                        Box {
                            if (userInput.isEmpty()) {
                                Text("Enter your message", color = Color.Gray)
                            }
                            innerTextField()
                        }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding( 5.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(color = Color.DarkGray)
                    .clickable {
                        onSendMessage()
                    }
            ) {
                Image(
                    painter = painterResource(R.drawable.send),
                    contentDescription = "",
                    Modifier.size(50.dp)
                        .padding(start= 15.dp, top = 5.dp, bottom = 5.dp , end = 5.dp)
                )
            }
        }
    }
}

@Composable
fun ShowSaveChatDialog(onSave: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Chat?") },
        text = { Text("Do you want to save this chat before exiting?") },
        confirmButton = {
            Button(onClick = onSave) { Text("Yes") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("No") }
        }
    )
}
