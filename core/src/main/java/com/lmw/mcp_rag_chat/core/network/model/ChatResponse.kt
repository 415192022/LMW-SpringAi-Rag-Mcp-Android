package com.lmw.mcp_rag_chat.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * 聊天响应数据类，用于解析finish事件中的数据
 */
data class ChatResponse(
    @SerializedName("message") val message: String,
    @SerializedName("botMsgId") val botMsgId: String
)