package com.example.smarttalk.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.composeuisuite.ohteepee.OhTeePeeInput
import com.composeuisuite.ohteepee.configuration.OhTeePeeCellConfiguration
import com.composeuisuite.ohteepee.configuration.OhTeePeeConfigurations
import com.example.smarttalk.Navigation.Routes
import com.example.smarttalk.R
import com.example.smarttalk.sharedPref.SharedPref
import com.example.smarttalk.utils.ProgressIndicator
import com.example.smarttalk.viewModels.AuthViewModel
import okhttp3.Route

@Composable
fun OTPScreen(navController: NavController ,viewModel: AuthViewModel,number : String?,name : String? ,modifier: Modifier = Modifier) {
    var newOtpValue: String by remember { mutableStateOf("") }
    val context = LocalContext.current
    val progressBar = remember {  mutableStateOf(false) }

    if(progressBar.value){
        ProgressIndicator()
    }

    Column(
        modifier = modifier.fillMaxSize()
            .consumeWindowInsets(PaddingValues())
            .windowInsetsPadding(WindowInsets.ime),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = "OTP Verification",
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )
        Text(
            "Enter the OTP we have sent you on\n +91 $number",
            textAlign = TextAlign.Center,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(30.dp))
        OtpInput(){ otpValue->
            newOtpValue = otpValue
        }
        Box(modifier = Modifier.fillMaxSize()
            .padding(20.dp),
            contentAlignment = Alignment.BottomCenter){
            Button(
                onClick = {
                    progressBar.value = true
                    viewModel.verifyOtp(newOtpValue,
                        {
                            navController.navigate(Routes.homeScreen.route)
                            SharedPref.get().userPhone = number
                            SharedPref.get().userName = name
                            progressBar.value = false
                            SharedPref.get().loggedIn = true
                        })
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
                enabled = (!(newOtpValue.contains(" ")) && !(newOtpValue.isNullOrBlank()))){
                Text(text = "Login")
            }
        }
    }
}

    @Composable
    fun OtpInput(newOtpValue : (String) -> Unit) {
        var otpValue: String by remember { mutableStateOf("") }
        val defaultCellConfig = OhTeePeeCellConfiguration.withDefaults(
            borderColor = Color.LightGray,
            borderWidth = 1.dp,
            shape = RoundedCornerShape(10.dp),
            textStyle = TextStyle(
                color = Color.Black
            )
        )

        OhTeePeeInput(
            value = otpValue,
            onValueChange = { newValue, isValid ->
                otpValue = newValue
                newOtpValue(newValue)
            },
            configurations = OhTeePeeConfigurations.withDefaults(
                cellsCount = 6,
                emptyCellConfig = defaultCellConfig,
                cellModifier = Modifier
                    .padding(horizontal = 1.dp)
                    .size(48.dp),
            ),
        )
    }
