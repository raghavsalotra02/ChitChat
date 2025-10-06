package com.example.smarttalk.sharedPref

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.example.smarttalk.application.SmartTalkApp

class SharedPref {

    companion object{

        private val instance : SharedPref? = null

        fun get() : SharedPref{

            if(instance == null){
                return SharedPref()
            }
            return instance
        }
    }

    private val USER_NAME = "user_name"
    private val USER_PHONE = "user_phone"
    private val LOGGED_IN = "logged_in"

    private val sharedPref : SharedPreferences =
        SmartTalkApp.get().getContext().getSharedPreferences("Prefs", Context.MODE_PRIVATE)

    var loggedIn : Boolean
        get() {
            return sharedPref.getBoolean(LOGGED_IN,false)
        }
        set(value){
            sharedPref.edit {
                putBoolean(LOGGED_IN,value)
            }
        }

    var userName : String?
        get() {
            val str = sharedPref.getString(USER_NAME,"") ?: ""
            if(!str.isNullOrBlank()){
                return str
            }
            return null
        }
        set(value){

            sharedPref.edit {
                putString(USER_NAME,value)
            }

        }

    var userPhone : String?
        get() {
            val str = sharedPref.getString(USER_PHONE, "") ?: ""
            if( !str.isNullOrBlank()){
                return str
            }
            return null
        }
        set(value){
            sharedPref.edit {
                putString(USER_PHONE,value)
            }
        }



}