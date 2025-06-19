package com.example.smarttalk.repository

import android.content.Context
import android.provider.ContactsContract
import com.example.smarttalk.Model.Contact
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class HomeScreenRepository @Inject constructor(
    @ApplicationContext val context: Context
) {

    fun getContacts() : List<Contact>{

        val contactList = mutableListOf<Contact>()
        val resolver = context.contentResolver
        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)
                contactList.add(Contact(name, number))
            }
        }
        return contactList
    }
}