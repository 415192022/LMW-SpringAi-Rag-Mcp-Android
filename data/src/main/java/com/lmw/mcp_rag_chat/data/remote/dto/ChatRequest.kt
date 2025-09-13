package com.lmw.mcp_rag_chat.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("currentUserName")
    val currentUserName: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("botMsgId")
    val botMsgId: String
)