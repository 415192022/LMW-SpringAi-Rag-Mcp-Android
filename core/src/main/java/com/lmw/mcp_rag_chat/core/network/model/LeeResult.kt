package com.lmw.mcp_rag_chat.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * 自定义响应数据结构
 * 本类可提供给 H5/ios/安卓/公众号/小程序 使用
 * 前端接受此类数据（json object)后，可自行根据业务去实现相关功能
 * 
 * 200：表示成功
 * 500：表示错误，错误信息在msg字段中
 * 501：bean验证错误，不管多少个错误都以map形式返回
 * 502：拦截器拦截到用户token出错
 * 555：异常抛出信息
 * 556: 用户qq校验异常
 * 557: 校验用户是否在CAS登录，用户门票的校验
 */
data class LeeResult<T>(
    // 响应业务状态
    @SerializedName("status")
    val status: Int,

    // 响应消息
    @SerializedName("msg")
    val msg: String,

    // 响应中的数据
    @SerializedName("data")
    val data: T?,
    
    // 不使用
    @SerializedName("ok")
    val ok: String? = null
) {
    companion object {
        fun <T> build(status: Int, msg: String, data: T?): LeeResult<T> {
            return LeeResult(status, msg, data)
        }

        fun <T> build(status: Int, msg: String, data: T?, ok: String): LeeResult<T> {
            return LeeResult(status, msg, data, ok)
        }
        
        fun <T> ok(data: T?): LeeResult<T> {
            return LeeResult(200, "OK", data)
        }

        fun <T> ok(): LeeResult<T> {
            return LeeResult(200, "OK", null)
        }
        
        fun <T> errorMsg(msg: String): LeeResult<T> {
            return LeeResult(500, msg, null)
        }

        fun <T> errorUserTicket(msg: String): LeeResult<T> {
            return LeeResult(557, msg, null)
        }

        fun <T> errorMap(data: T?): LeeResult<T> {
            return LeeResult(501, "error", data)
        }
        
        fun <T> errorTokenMsg(msg: String): LeeResult<T> {
            return LeeResult(502, msg, null)
        }
        
        fun <T> errorException(msg: String): LeeResult<T> {
            return LeeResult(555, msg, null)
        }
        
        fun <T> errorUserQQ(msg: String): LeeResult<T> {
            return LeeResult(556, msg, null)
        }
    }
    
    fun isOK(): Boolean {
        return this.status == 200
    }
}