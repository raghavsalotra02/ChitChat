package com.example.smarttalk.viewModels

import android.app.Activity
import com.example.smarttalk.repository.AuthRepository

class AuthViewModel {

    private val repository = AuthRepository()

    val verificationId = repository.verificationId
    val authState = repository.authState
    val error = repository.error

    fun sendVerificationCode(phoneNumber : String, activity: Activity , onCallback : () -> Unit){
        repository.sendVerificationCode(phoneNumber,activity,onCallback)
    }

    fun verifyOtp(otp: String, callback : ()-> Unit ) {
        repository.verifyOtp(otp, callback)
    }

    fun resendVerificationCode(phoneNumber: String, activity: Activity) {
        repository.resendVerificationCode(phoneNumber, activity)
    }


}