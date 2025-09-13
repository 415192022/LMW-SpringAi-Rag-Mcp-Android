package com.lmw.mcp_rag_chat.feature.chat.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 提供Chat功能模块所需的依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object ChatModule {
    // 这里可以添加Chat功能模块特有的依赖提供方法
    // 目前所有依赖都来自core、domain和data模块，所以暂时不需要额外提供
}