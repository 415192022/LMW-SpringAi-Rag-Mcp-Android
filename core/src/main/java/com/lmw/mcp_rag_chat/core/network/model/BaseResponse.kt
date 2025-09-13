package com.lmw.mcp_rag_chat.core.network.model

/**
 * 网络响应的基础模型
 * @param T 响应数据的类型
 */
sealed class BaseResponse<out T> {
    /**
     * 成功响应
     * @param data 响应数据
     */
    data class Success<T>(val data: T) : BaseResponse<T>()
    
    /**
     * 错误响应
     * @param code 错误码
     * @param message 错误信息
     */
    data class Error(val code: Int, val message: String) : BaseResponse<Nothing>()
    
    /**
     * 加载中状态
     */
    object Loading : BaseResponse<Nothing>()
}