package com.example.smarttalk.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smarttalk.Navigation.Routes
import com.example.smarttalk.R
import com.example.smarttalk.customState.MyUserInputField
import com.example.smarttalk.customState.rememberMyUserInputState
import com.example.smarttalk.utils.ProgressIndicator
import com.example.smarttalk.viewModels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import java.util.concurrent.TimeUnit

@Composable
fun LoginScreen(navController: NavController,modifier: Modifier = Modifier, authViewModel : AuthViewModel){
    val nameState = rememberMyUserInputState(
        hint = "Enter Name"
    )

    val phoneNummberState = rememberMyUserInputState(
        hint = "Enter Mobile Number"
    )
    val loading = remember { mutableStateOf(false) }

    if (loading.value){
        ProgressIndicator()
    }

    Column(modifier = modifier.fillMaxSize()
        .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){

        MyUserInputField(
            state = nameState
        )
        Spacer(modifier = Modifier.height(20.dp))
        MyUserInputField(
            state = phoneNummberState,
            maxCount = 10,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
//            validate = {
//                if(it.length>0 && it.length<10){
//                    "Fill appropriate number"
//                }else{
//                    null
//                }
//            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            loading.value = true
            authViewModel.sendVerificationCode(
                phoneNumber = "+91"+phoneNummberState.text,
                activity = Activity(),
                        onCallback = {
                            loading.value= false
                    navController.navigate(Routes.otpScreen.route+"/${nameState.text}/${phoneNummberState.text}")
        }
            )
        },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth()
                .height(50.dp),
            colors = ButtonColors(
                containerColor = colorResource(R.color.purple),
                disabledContainerColor = Color.LightGray,
                contentColor = Color.White,
                disabledContentColor = Color.White
            ),
            enabled = !nameState.text.isNullOrEmpty() && phoneNummberState.text.length == 10
        ) {
            Text(text = "Login")
        }


    }
}
