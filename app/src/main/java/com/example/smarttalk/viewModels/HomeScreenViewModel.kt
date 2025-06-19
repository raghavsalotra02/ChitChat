package com.example.smarttalk.viewModels

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttalk.Model.Contact
import com.example.smarttalk.repository.HomeScreenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository : HomeScreenRepository
) : ViewModel() {

    private val _contacts = mutableStateOf<List<Contact>>(emptyList())
    val contacts : State<List<Contact>> = _contacts

    fun readContacts(context: Context) {
        viewModelScope.launch {
            _contacts.value = repository.getContacts()
        }
    }
}