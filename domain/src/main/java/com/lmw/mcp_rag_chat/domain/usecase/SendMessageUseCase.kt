package com.lmw.mcp_rag_chat.domain.usecase

import com.lmw.mcp_rag_chat.domain.model.Message
import com.lmw.mcp_rag_chat.domain.model.SearchMode
import com.lmw.mcp_rag_chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(content: String, searchMode: SearchMode): Flow<Message> {
        return repository.sendMessage(content, searchMode)
    }
}