package com.lmw.mcp_rag_chat.feature.chat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmw.mcp_rag_chat.data.remote.sse.SseClient
import com.lmw.mcp_rag_chat.data.remote.sse.SseEvent
import com.lmw.mcp_rag_chat.domain.model.Message
import com.lmw.mcp_rag_chat.domain.model.MessageType
import com.lmw.mcp_rag_chat.domain.model.SearchMode
import com.lmw.mcp_rag_chat.domain.usecase.ClearMessagesUseCase
import com.lmw.mcp_rag_chat.domain.usecase.GetMessagesUseCase
import com.lmw.mcp_rag_chat.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val clearMessagesUseCase: ClearMessagesUseCase,
    private val sseClient: SseClient,
    private val baseUrl: String
) : ViewModel() {

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _searchMode = MutableStateFlow(SearchMode.NORMAL)
    val searchMode: StateFlow<SearchMode> = _searchMode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // 当前SSE连接状态
    private val _sseConnected = MutableStateFlow(false)
    val sseConnected: StateFlow<Boolean> = _sseConnected.asStateFlow()
    
    // SSE连接中状态
    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: Boolean
        get() = _isConnecting.value
    
    // SSE连接错误状态
    private val _hasConnectionError = MutableStateFlow(false)
    val hasConnectionError: Boolean
        get() = _hasConnectionError.value
    
    // 当前正在处理的机器人消息ID
    private var currentBotMessageId: String? = null
    
    init {
        // 在ViewModel创建时初始化SSE连接
        initSseConnection()
    }

    val messages: StateFlow<List<Message>> = getMessagesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun onSearchModeChanged(mode: SearchMode) {
        _searchMode.value = mode
    }

    /**
     * 初始化SSE连接
     */
    private fun initSseConnection() {
        viewModelScope.launch {
            try {
                // 设置连接中状态
                _isConnecting.value = true
                _hasConnectionError.value = false
                _sseConnected.value = false
                
                android.util.Log.d("ChatViewModel", "开始连接SSE服务...")
                
                // 连接SSE并处理事件
                sseClient.connect(baseUrl).collect { event ->
                    when (event) {
                        is SseEvent.Message -> {
                            // 处理收到的消息
                            _isConnecting.value = false
                            _sseConnected.value = true
                            handleSseMessage(event.data)
                        }
                        is SseEvent.Add -> {
                            // 处理add事件
                            _isConnecting.value = false
                            _sseConnected.value = true
                            handleSseAddEvent(event.data)
                        }
                        is SseEvent.Finish -> {
                            // 处理finish事件
                            _isConnecting.value = false
                            _sseConnected.value = true
                            handleSseFinishEvent(event.data)
                        }
                        is SseEvent.Error -> {
                            // 处理错误
                            _isConnecting.value = false
                            _sseConnected.value = false
                            _hasConnectionError.value = true
                            android.util.Log.e("ChatViewModel", "SSE连接错误")
                            // 尝试重新连接
                            kotlinx.coroutines.delay(3000)
                            initSseConnection()
                        }
                        is SseEvent.Close -> {
                            // 连接关闭
                            _isConnecting.value = false
                            _sseConnected.value = false
                            android.util.Log.d("ChatViewModel", "SSE连接已关闭")
                        }
                        else -> { /* 忽略其他事件 */ }
                    }
                }
            } catch (e: Exception) {
                _isConnecting.value = false
                _sseConnected.value = false
                _hasConnectionError.value = true
                android.util.Log.e("ChatViewModel", "SSE连接异常: ${e.message}")
                // 连接失败后延迟重试
                kotlinx.coroutines.delay(5000)
                initSseConnection()
            }
        }
    }
    
    /**
     * 处理SSE消息
     */
    private fun handleSseMessage(data: String) {
        val botMessageId = currentBotMessageId ?: return
        
        // 获取当前消息列表
        val currentMessages = messages.value.toMutableList()
        
        // 查找是否已存在该ID的消息
        val existingIndex = currentMessages.indexOfFirst { it.id == botMessageId }
        
        if (existingIndex >= 0) {
            // 更新现有消息
            val existingMessage = currentMessages[existingIndex]
            val updatedMessage = existingMessage.copy(
                content = data
            )
            currentMessages[existingIndex] = updatedMessage
        } else {
            // 添加新消息
            val newMessage = Message(
                id = botMessageId,
                content = data,
                type = MessageType.BOT
            )
            currentMessages.add(newMessage)
        }
        
        // 更新消息列表
        (messages as? MutableStateFlow)?.value = currentMessages
    }
    
    /**
     * 处理SSE add事件
     */
    private fun handleSseAddEvent(data: String) {
        android.util.Log.d("ChatViewModel", "收到SSE add事件: $data")
        
        // 检查是否已存在机器人消息ID
        if (currentBotMessageId == null) {
            // 如果没有当前机器人消息ID，创建一个新的
            currentBotMessageId = UUID.randomUUID().toString()
            android.util.Log.d("ChatViewModel", "创建新的机器人消息ID: $currentBotMessageId")
            
            // 使用clearMessagesUseCase和sendMessageUseCase无法直接修改消息列表
            // 需要通过repository来添加消息
            viewModelScope.launch {
                try {
                    // 创建一个新的机器人消息并添加到列表
                    val newMessage = Message(
                        id = currentBotMessageId!!,
                        content = data,
                        type = MessageType.BOT
                    )
                    
                    // 直接访问repository添加消息
                    (getMessagesUseCase as? GetMessagesUseCase)?.repository?.addMessage(newMessage)
                    
                    android.util.Log.d("ChatViewModel", "已添加新的机器人消息: $newMessage")
                } catch (e: Exception) {
                    android.util.Log.e("ChatViewModel", "添加机器人消息失败: ${e.message}")
                }
            }
        } else {
            android.util.Log.d("ChatViewModel", "更新现有机器人消息: $currentBotMessageId")
            
            viewModelScope.launch {
                try {
                    // 获取当前消息
                    val currentMessages = messages.value
                    val existingIndex = currentMessages.indexOfFirst { it.id == currentBotMessageId }
                    
                    if (existingIndex >= 0) {
                        // 找到现有消息，更新内容
                        val existingMessage = currentMessages[existingIndex]
                        val updatedMessage = existingMessage.copy(
                            content = existingMessage.content + data
                        )
                        
                        // 直接访问repository更新消息
                        (getMessagesUseCase as? GetMessagesUseCase)?.repository?.updateMessage(updatedMessage)
                        
                        android.util.Log.d("ChatViewModel", "已更新机器人消息: $updatedMessage")
                    } else {
                        android.util.Log.w("ChatViewModel", "未找到要更新的机器人消息: $currentBotMessageId")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ChatViewModel", "更新机器人消息失败: ${e.message}")
                }
            }
        }
    }
    
    /**
     * 处理SSE finish事件
     */
    private fun handleSseFinishEvent(data: String) {
        android.util.Log.d("ChatViewModel", "收到SSE finish事件: $data")
        
        try {
            // 解析finish事件数据
            val gson = com.google.gson.Gson()
            val chatResponse = gson.fromJson(data, com.lmw.mcp_rag_chat.core.network.model.ChatResponse::class.java)
            
            android.util.Log.d("ChatViewModel", "解析finish事件数据: botMsgId=${chatResponse.botMsgId}, message长度=${chatResponse.message.length}")
            
            viewModelScope.launch {
                try {
                    // 获取当前消息列表
                    val currentMessages = messages.value
                    
                    // 查找对应botMsgId的消息
                    val existingIndex = currentMessages.indexOfFirst { it.id == chatResponse.botMsgId }
                    
                    if (existingIndex >= 0) {
                        // 更新现有消息，设置最终内容
                        val existingMessage = currentMessages[existingIndex]
                        val updatedMessage = existingMessage.copy(
                            content = chatResponse.message
                        )
                        
                        // 直接访问repository更新消息
                        (getMessagesUseCase as? GetMessagesUseCase)?.repository?.updateMessage(updatedMessage)
                        
                        android.util.Log.d("ChatViewModel", "已更新机器人消息最终内容: ${chatResponse.botMsgId}")
                    } else {
                        android.util.Log.w("ChatViewModel", "未找到要更新的机器人消息: ${chatResponse.botMsgId}")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ChatViewModel", "更新机器人消息最终内容失败: ${e.message}")
                }
            }
            
            // 清除当前机器人消息ID
            currentBotMessageId = null
            android.util.Log.d("ChatViewModel", "已清除当前机器人消息ID")
        } catch (e: Exception) {
            // 处理解析异常
            android.util.Log.e("ChatViewModel", "解析finish事件数据失败: ${e.message}")
        }
    }
    
    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            _inputText.value = ""
            
            try {
                // 所有聊天模式都使用ChatApi发送消息
                sendMessageUseCase(text, _searchMode.value).collect {}
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        viewModelScope.launch {
            clearMessagesUseCase()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        // 关闭SSE连接
        sseClient.close()
    }
}