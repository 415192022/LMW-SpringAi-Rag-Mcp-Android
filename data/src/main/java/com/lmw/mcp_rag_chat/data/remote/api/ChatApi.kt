package com.lmw.mcp_rag_chat.data.remote.api

import com.lmw.mcp_rag_chat.data.remote.dto.ChatRequest
import com.lmw.mcp_rag_chat.core.network.model.LeeResult
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {
    @POST("/chat/doChat")
    suspend fun doChat(@Body request: ChatRequest): LeeResult<Unit>
    
    @POST("/rag/search")
    suspend fun ragSearch(@Body request: ChatRequest): LeeResult<Unit>
    
    @POST("/internet/search")
    suspend fun internetSearch(@Body request: ChatRequest): LeeResult<Unit>
}