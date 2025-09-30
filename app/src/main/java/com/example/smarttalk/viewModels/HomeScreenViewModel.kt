package com.example.smarttalk.viewModels

import android.content.Context
import android.util.Log
import android.util.Log.d
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttalk.Model.Contact
import com.example.smarttalk.Model.User
import com.example.smarttalk.Model.UserList
import com.example.smarttalk.repository.FirebaseChatRepository
import com.example.smarttalk.repository.HomeScreenRepository
import com.example.smarttalk.sharedPref.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository : HomeScreenRepository,
    private val firebaseChatRepository : FirebaseChatRepository
) : ViewModel() {

    private val _contacts = MutableLiveData<List<Contact>>(emptyList())

    private val _matchedContacts = MutableLiveData<List<Pair<String, User>>>(emptyList())

    private val _chatList = MutableStateFlow<List<UserList>>(emptyList())
    val chatList: StateFlow<List<UserList>> = _chatList

    val _finalContactList = MutableLiveData<List<UserList>>(emptyList())
    val finalContactList: LiveData<List<UserList>> get() = _finalContactList

    private val _chatPartners = MutableStateFlow<Set<String>>(emptySet())
    val chatPartners: StateFlow<Set<String>> = _chatPartners

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingcContacts = MutableStateFlow(false)
    val isLoadingContacts: StateFlow<Boolean> = _isLoadingcContacts


    fun readContacts() {
        viewModelScope.launch {
            try {
                _isLoadingcContacts.value = true
                val contactList = repository.getContacts().toSet().toList()
                _contacts.value = contactList

                val contactNumbers = contactList.map { it.phone }

                val matched = firebaseChatRepository.fetchMatchedContactsSuspend(contactNumbers)
                _matchedContacts.postValue(matched)

                val merged = mergeContactLists(contactList, matched)
                _finalContactList.postValue(merged)
            } catch (e: Exception) {
                // Handle errors
                _matchedContacts.postValue(emptyList())
                _finalContactList.postValue(emptyList())
            } finally {
                _isLoadingcContacts.value = false
            }
        }
    }

    fun loadChatPartners() {
        viewModelScope.launch {
            val partners = firebaseChatRepository.getChatPartners()
            _chatPartners.value = partners
        }
    }

    fun loadChatList() {
        viewModelScope.launch {
            try {
                Log.d("HomeScreenViewModel", "Starting loadChatList")
                _isLoading.value = true
                
                // Get device contacts first
                val deviceContacts = repository.getContacts()
                Log.d("HomeScreenViewModel", "Device contacts count: ${deviceContacts.size}")
                
                // Get chat partners from Firebase
                val partners = firebaseChatRepository.getChatPartners()
                Log.d("HomeScreenViewModel", "Chat partners count: ${partners.size}")
                _chatPartners.value = partners

                // Show all contacts initially, not just those with existing chats
                // This allows users to see their contacts and start new conversations
                if (partners.isEmpty()) {
                    // If no existing chats, show all contacts
                    val allContacts = deviceContacts.map { contact ->
                        UserList(
                            phoneNumber = contact.phone,
                            name = contact.name,
                            profilePicUrl = null,
                            isOnApp = false
                        )
                    }
                    Log.d("HomeScreenViewModel", "Setting all contacts: ${allContacts.size}")
                    _chatList.value = allContacts
                } else {
                    // If there are existing chats, show them plus all contacts
                    val filtered = deviceContacts.filter { it.phone in partners }
                    Log.d("HomeScreenViewModel", "Filtered contacts count: ${filtered.size}")

                    // fetch firebase user details for those partners using suspend function
                    val matched = firebaseChatRepository.fetchMatchedContactsSuspend(partners.toList())
                    Log.d("HomeScreenViewModel", "Matched contacts count: ${matched.size}")
                    val merged = mergeContactLists(filtered, matched)
                    Log.d("HomeScreenViewModel", "Final merged list count: ${merged.size}")
                    _chatList.value = merged
                }
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "Error in loadChatList", e)
                // Handle any errors and show empty list
                _chatList.value = emptyList()
            } finally {
                _isLoading.value = false
                Log.d("HomeScreenViewModel", "loadChatList completed")
            }
        }
    }


//    fun getChatList() {
//        viewModelScope.launch {
//            // Step 1: get chat partners from repository (numbers from Firebase "chats")
//            val partners = firebaseChatRepository.getChatPartners()
//            _chatPartners.value = partners
//
//            // Step 2: map local contacts for quick lookup
//            val localContacts = _contacts.value ?: emptyList()
//            val localNameMap = localContacts.associateBy { it.phone }
//
//            // Step 3: fetch user details from Firebase "users" node (parallel fetch)
//            val firebaseUsers = firebaseChatRepository.getUsersByPhones(chatPartners)
//
//            // Step 4: merge into UserList for UI
//            val finalList = chatPartners.map { phone ->
//                val firebaseUser = firebaseUsers[phone]
//                val localName = localNameMap[phone]?.name ?: firebaseUser?.name ?: phone
//
//                UserList(
//                    phoneNumber = phone,
//                    name = localName,
//                    profilePicUrl = firebaseUser?.profilePicUrl,
//                    isOnApp = firebaseUser != null
//                )
//            }
//            _chatList.value = finalList
//        }
//    }

    }

fun mergeContactLists(
    deviceContacts: List<Contact>,
    matchedFirebaseUsers: List<Pair<String, User>>
): List<UserList> {

    val localNameMap = deviceContacts.associateBy { it.phone }
    val matchedPhones = matchedFirebaseUsers.map { it.first }.toSet()

    val matchedList = matchedFirebaseUsers.map { (phone, user) ->
        val localName = localNameMap[phone]?.name ?: user.name
        UserList(
            phoneNumber = phone,
            name = localName,
            profilePicUrl = user.profilePicUrl,
            isOnApp = true
        )
    }

    val unmatchedList = deviceContacts
        .filter { contact -> contact.phone !in matchedPhones }
        .map { contact ->
            UserList(
                phoneNumber = contact.phone,
                name = contact.name,
                profilePicUrl = null,
                isOnApp = false
            )
        }

    return matchedList + unmatchedList
}


