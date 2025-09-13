package com.lmw.mcp_rag_chat.domain.usecase

import com.lmw.mcp_rag_chat.domain.repository.ChatRepository
import javax.inject.Inject

class ClearMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke() {
        repository.clearMessages()
    }
}