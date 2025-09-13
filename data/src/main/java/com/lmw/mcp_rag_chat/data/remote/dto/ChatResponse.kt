package com.lmw.mcp_rag_chat.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @SerializedName("content")
    val content: String
)