package com.lmw.mcp_rag_chat.data.remote.sse

sealed class SseEvent {
    object Open : SseEvent()
    object Close : SseEvent()
    data class Error(val message: String) : SseEvent()
    data class Message(val type: String, val data: String) : SseEvent()
    data class Add(val data: String) : SseEvent()
    data class Finish(val data: String) : SseEvent()
}