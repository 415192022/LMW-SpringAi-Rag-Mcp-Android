package com.lmw.mcp_rag_chat.domain.repository

import com.lmw.mcp_rag_chat.domain.model.Message
import com.lmw.mcp_rag_chat.domain.model.SearchMode
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(content: String, searchMode: SearchMode): Flow<Message>
    fun getMessages(): Flow<List<Message>>
    suspend fun clearMessages()
    
    /**
     * 添加新消息到消息列表
     * @param message 要添加的消息
     */
    suspend fun addMessage(message: Message)
    
    /**
     * 更新现有消息
     * @param message 更新后的消息
     */
    suspend fun updateMessage(message: Message)
}