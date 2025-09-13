package com.lmw.mcp_rag_chat.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 基础拦截器接口，所有网络拦截器应实现此接口
 */
interface BaseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response
}