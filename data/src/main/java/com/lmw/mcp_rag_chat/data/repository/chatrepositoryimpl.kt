package com.lmw.mcp_rag_chat.data.repository

import com.lmw.mcp_rag_chat.core.network.model.LeeResult
import com.lmw.mcp_rag_chat.data.remote.api.ChatApi
import com.lmw.mcp_rag_chat.data.remote.dto.ChatRequest
import com.lmw.mcp_rag_chat.data.remote.sse.SseClient
import com.lmw.mcp_rag_chat.data.remote.sse.SseEvent
import com.lmw.mcp_rag_chat.domain.model.Message
import com.lmw.mcp_rag_chat.domain.model.MessageType
import com.lmw.mcp_rag_chat.domain.model.SearchMode
import com.lmw.mcp_rag_chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val sseClient: SseClient,
    private val baseUrl: String
) : ChatRepository {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    
    override suspend fun sendMessage(content: String, searchMode: SearchMode): Flow<Message> = flow {
        // 添加用户消息
        val userMessage = Message(
            content = content,
            type = MessageType.USER
        )
        _messages.value = _messages.value + userMessage
        emit(userMessage)
        
        // 创建一个固定ID的消息对象，以便在更新时保持一致
        val botMessageId = UUID.randomUUID().toString()
        
        // 根据搜索模式选择不同的API调用
        val request = ChatRequest(
            currentUserName = sseClient.getUserId(), // 使用SseClient中的userId
            message = content,
            botMsgId = botMessageId
        )
        
        android.util.Log.d("ChatRepositoryImpl", "发送消息使用的用户ID: ${sseClient.getUserId()}")
        
        try {
            val leeResult = when (searchMode) {
                SearchMode.NORMAL -> {
                    // 使用普通聊天API，仅触发服务端处理
                    chatApi.doChat(request)
                }
                SearchMode.KNOWLEDGE_BASE -> {
                    // 使用知识库搜索API，仅触发服务端处理
                    chatApi.ragSearch(request)
                }
                SearchMode.WEB -> {
                    // 使用网络搜索API，仅触发服务端处理
                    chatApi.internetSearch(request)
                }
            }
            // 检查响应状态
            if (leeResult.isOK()) {
                android.util.Log.d("ChatRepositoryImpl", "API调用成功，等待SSE事件更新消息内容")
            } else {
                android.util.Log.e("ChatRepositoryImpl", "API调用失败: ${leeResult.msg}")
                // 创建错误消息
                val errorMessage = Message(
                    id = botMessageId,
                    content = "请求失败: ${leeResult.msg}",
                    type = MessageType.BOT
                )
                
                // 更新消息列表
                addMessage(errorMessage)
                emit(errorMessage)
            }
        } catch (e: Exception) {
            android.util.Log.e("ChatRepositoryImpl", "API调用异常: ${e.message}")
            // 发生错误时，添加错误消息
            val errorMessage = Message(
                id = botMessageId,
                content = "发生错误: ${e.message}",
                type = MessageType.BOT
            )
            
            // 更新消息列表
            addMessage(errorMessage)
            emit(errorMessage)
        }
    }

    override fun getMessages(): Flow<List<Message>> {
        return _messages.asStateFlow()
    }

    override suspend fun clearMessages() {
        _messages.value = emptyList()
    }
    
    override suspend fun addMessage(message: Message) {
        android.util.Log.d("ChatRepositoryImpl", "添加新消息: $message")
        _messages.value = _messages.value + message
    }
    
    override suspend fun updateMessage(message: Message) {
        android.util.Log.d("ChatRepositoryImpl", "更新消息: $message")
        val currentMessages = _messages.value.toMutableList()
        val index = currentMessages.indexOfFirst { it.id == message.id }
        
        if (index >= 0) {
            currentMessages[index] = message
            _messages.value = currentMessages
            android.util.Log.d("ChatRepositoryImpl", "消息更新成功: ${message.id}")
        } else {
            android.util.Log.w("ChatRepositoryImpl", "未找到要更新的消息: ${message.id}")
        }
    }
}