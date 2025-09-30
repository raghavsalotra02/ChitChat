package com.example.smarttalk.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smarttalk.Model.Message
import com.example.smarttalk.Model.User
import com.example.smarttalk.repository.FirebaseChatRepository
import com.example.smarttalk.sharedPref.SharedPref
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FirebaseChatViewmodel @Inject constructor(
    private val chatRepository : FirebaseChatRepository
) : ViewModel() {



    val phone = SharedPref.get().userPhone
    val name = SharedPref.get().userName

    init{
        if (phone != null && name != null) {
            registerUser(
                    userId = phone,
                    name = name,
                    profilePicUrl = "https://picsum.photos/200/300"
            )
        }
    }

//    init {
//        getMessage(number)
//    }

    val _userInput = MutableStateFlow("")
    val userInput: StateFlow<String> = _userInput
    val messagesList = chatRepository.messagesList

    fun registerUser(userId: String, name: String, profilePicUrl: String) {
        val user = User(name = name, profilePicUrl = profilePicUrl, status = "Online")
        chatRepository.addUser(userId, user)
    }

    fun sendMessage(senderNumber: String,receiverNumber : String,message: String){
        chatRepository.sendMessage(senderNumber,receiverNumber,message)
    }

    fun updateUserInput(input : String){
        _userInput.value = input
    }

    fun getMessage(phoneNumber : String){
        chatRepository.getChat(phoneNumber)
    }



}