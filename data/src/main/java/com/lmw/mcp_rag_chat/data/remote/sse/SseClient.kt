package com.lmw.mcp_rag_chat.data.remote.sse

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SseClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private var eventSource: EventSource? = null
    private var userId: String = generateRandomUserId()

    companion object {
        private const val TAG = "SseClient"
    }

    /**
     * 连接SSE服务器并返回消息流
     * @param baseUrl 服务器基础URL
     * @return 消息流
     */
    fun connect(baseUrl: String): Flow<SseEvent> = callbackFlow {
        val url = "$baseUrl/sse/connect?userId=$userId"
        Log.d(TAG, "开始连接SSE服务器: $url")

        val request = Request.Builder()
            .url(url)
            .header("Accept", "text/event-stream")
            .header("Cache-Control", "no-cache")
            .build()

        Log.d(TAG, "SSE请求头: Accept=text/event-stream, Cache-Control=no-cache")

        val listener = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                Log.d(TAG, "SSE连接已建立: userId=$userId, 响应码=${response.code}")
                trySend(SseEvent.Open)
            }

            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                Log.d(TAG, "收到SSE事件: type=${type ?: "默认"}, id=${id ?: "无"}, data长度=${data.length}")
                if (data.length <= 100) {
                    Log.d(TAG, "事件数据: $data")
                } else {
                    Log.d(TAG, "事件数据(截断): ${data.substring(0, 100)}...")
                }

                when (type) {
                    "add" -> {
                        Log.d(TAG, "处理add事件")
                        trySend(SseEvent.Add(data))
                    }
                    "finish" -> {
                        Log.d(TAG, "处理finish事件")
                        trySend(SseEvent.Finish(data))
                    }
                    else -> {
                        // 处理其他类型的事件
                        trySend(SseEvent.Message(type ?: "", data))
                    }
                }
            }

            override fun onClosed(eventSource: EventSource) {
                Log.d(TAG, "SSE连接已关闭: userId=$userId")
                trySend(SseEvent.Close)
                channel.close()
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                val errorMsg = t?.message ?: "未知错误"
                val responseCode = response?.code ?: "无响应码"
                Log.e(TAG, "SSE连接失败: userId=$userId, 错误=${errorMsg}, 响应码=$responseCode", t)
                trySend(SseEvent.Error(errorMsg))
                channel.close(t)
            }
        }

        Log.d(TAG, "创建SSE EventSource")
        eventSource = EventSources.createFactory(okHttpClient).newEventSource(request, listener)

        awaitClose {
            Log.d(TAG, "Flow被取消，关闭SSE连接")
            eventSource?.cancel()
        }
    }

    /**
     * 发送消息到服务器
     * @param baseUrl 服务器基础URL
     * @param message 消息内容
     */
    suspend fun sendMessage(baseUrl: String, message: String) {
        val displayMessage = if (message.length <= 50) message else "${message.substring(0, 50)}..."
        Log.d(TAG, "开始发送消息: userId=$userId, message=$displayMessage")

        val url = "$baseUrl/sse/sendMessage?userId=$userId&message=$message"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val startTime = System.currentTimeMillis()
        try {
            okHttpClient.newCall(request).execute().use { response ->
                val duration = System.currentTimeMillis() - startTime
                if (response.isSuccessful) {
                    Log.d(TAG, "消息发送成功: 耗时=${duration}ms, 状态码=${response.code}")
                } else {
                    Log.e(TAG, "消息发送失败: 耗时=${duration}ms, 状态码=${response.code}")
                    throw Exception("发送消息失败，状态码: ${response.code}")
                }
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            Log.e(TAG, "消息发送异常: 耗时=${duration}ms, 异常=${e.message}")
            throw e
        }
    }

    /**
     * 关闭SSE连接
     */
    fun close() {
        Log.d(TAG, "主动关闭SSE连接: userId=$userId")
        eventSource?.cancel()
    }

    /**
     * 生成随机用户ID
     */
    private fun generateRandomUserId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = java.util.Random()
        return (1..10)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }

    /**
     * 获取用户ID
     */
    fun getUserId(): String = userId

    /**
     * 设置用户ID
     * @param newUserId 新的用户ID
     */
    fun setUserId(newUserId: String) {
        userId = newUserId
        android.util.Log.d(TAG, "用户ID已更新: $userId")
    }
}