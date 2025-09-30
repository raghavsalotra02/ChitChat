package com.example.smarttalk.di

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent :: class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    @Named("usersRef")
    fun provideUsersRef(database: FirebaseDatabase): DatabaseReference {
        return database.getReference("users")
    }

    @Provides
    @Singleton
    @Named("chatsRef")
    fun provideChatsRef(database: FirebaseDatabase): DatabaseReference {
        return database.getReference("chats")
    }

//    @Provides
//    @Singleton
//    @Named("chatsRef")
//    fun provideContactsRef(database: FirebaseDatabase): DatabaseReference {
//        return database.getReference("contacts")
//    }

}