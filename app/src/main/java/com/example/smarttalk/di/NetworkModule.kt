package com.example.smarttalk.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.smarttalk.Roomdatabase.ChatDao
import com.example.smarttalk.Roomdatabase.ChatDatabase
import com.example.smarttalk.api.OpenAIInterface
import com.example.smarttalk.repository.OpenAIRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.example.smarttalk.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://api.openai.com/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        Log.d("OpenAIKey", BuildConfig.OPENAI_API_KEY)
        return OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS) // Increase timeout to 60s
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesOPenAIApiService(retrofit: Retrofit): OpenAIInterface{
        return retrofit.create(OpenAIInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context : Context) : ChatDatabase {
        return Room.databaseBuilder(context, ChatDatabase::class.java, "chat_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun ProvidechatDao(database :ChatDatabase) : ChatDao {
        return database.chatDao()
    }

}