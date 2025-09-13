package com.lmw.mcp_rag_chat.domain.model

import java.util.UUID

enum class MessageType {
    USER,
    BOT
}

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val type: MessageType,
    val timestamp: Long = System.currentTimeMillis()
)