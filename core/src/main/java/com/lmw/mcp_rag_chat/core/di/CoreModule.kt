package com.lmw.mcp_rag_chat.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lmw.mcp_rag_chat.core.constants.NetworkConstants
import com.lmw.mcp_rag_chat.core.network.interceptor.HttpLoggingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 核心模块的依赖注入模块，提供全局通用的依赖
 */
@Module
@InstallIn(SingletonComponent::class)
class CoreModule {
    
    companion object {
        /**
         * 提供Gson实例
         */
        @Provides
        @Singleton
        fun provideGson(): Gson {
            return GsonBuilder().create()
        }
        
        /**
         * 提供HttpLoggingInterceptor实例
         */
        @Provides
        @Singleton
        fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor()
        }
        
        /**
         * 提供基础OkHttpClient实例
         */
        @Provides
        @Singleton
        fun provideBaseOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS) // 无限读取超时
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(httpLoggingInterceptor)
                .build()
        }
        
        /**
         * 提供GsonConverterFactory实例
         */
        @Provides
        @Singleton
        fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
            return GsonConverterFactory.create(gson)
        }
    }
}