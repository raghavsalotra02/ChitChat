package com.example.smarttalk.repository

import android.app.Activity
import android.app.KeyguardManager.KeyguardDismissCallback
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit

class AuthRepository {

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId : StateFlow<String?> get() = _verificationId

    private val _authState = MutableStateFlow<FirebaseUser?>(null)
    val authState: StateFlow<FirebaseUser?> get() = _authState

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private var resendingToken: PhoneAuthProvider.ForceResendingToken? = null

    fun sendVerificationCode(phoneNumber : String, activity: Activity,onCallback : () -> Unit) {
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(100L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        signInWithPhoneAuthCredential(credential, callback = onCallback)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        _error.value = e.message
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        _verificationId.value = verificationId
                        resendingToken = token
                        onCallback()
                    }
                })
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
    }

        fun verifyOtp(otp: String, callback: () -> Unit) {
            val storedVerificationId = _verificationId.value
            if (storedVerificationId != null) {
                val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
                signInWithPhoneAuthCredential(credential, callback)

            } else {
                _error.value = "Invalidate verification ID"
            }

        }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, callback: () -> Unit){
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    _authState.value = firebaseAuth.currentUser
                    callback()
                }else{
                    _error.value = task.exception?.message
                }
            }
    }

    fun resendVerificationCode(phoneNumber : String, activity: Activity) {
        resendingToken?.let {
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {

                    }

                    override fun onVerificationFailed(p0: FirebaseException) {

                    }
                })
        }

    }
}