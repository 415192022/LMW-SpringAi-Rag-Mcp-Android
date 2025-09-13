package com.lmw.mcp_rag_chat.domain.usecase

import com.lmw.mcp_rag_chat.domain.model.Message
import com.lmw.mcp_rag_chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    val repository: ChatRepository  // 修改为公开属性，以便可以直接访问
) {
    operator fun invoke(): Flow<List<Message>> {
        return repository.getMessages()
    }
}