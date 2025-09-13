package com.lmw.mcp_rag_chat.data.di

import com.lmw.mcp_rag_chat.data.remote.api.ChatApi
import com.lmw.mcp_rag_chat.data.remote.sse.SseClient
import com.lmw.mcp_rag_chat.data.repository.ChatRepositoryImpl
import com.lmw.mcp_rag_chat.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository

    companion object {
        // Gson现在由CoreModule提供

        // OkHttpClient现在由CoreModule提供

        @Provides
        @Singleton
        fun provideSseClient(okHttpClient: OkHttpClient): SseClient {
            return SseClient(okHttpClient)
        }

        @Provides
        @Singleton
        fun provideBaseUrl(): String {
            return "http://192.168.5.10:9090"
        }

        @Provides
        @Singleton
        fun provideChatApi(okHttpClient: OkHttpClient, gsonConverterFactory: GsonConverterFactory, baseUrl: String): ChatApi {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(gsonConverterFactory)
                .build()
                .create(ChatApi::class.java)
        }
    }
}