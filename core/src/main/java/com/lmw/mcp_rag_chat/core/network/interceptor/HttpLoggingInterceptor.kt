package com.lmw.mcp_rag_chat.core.network.interceptor

import android.util.Log
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 自定义HTTP日志拦截器，用于记录请求和响应的详细信息
 */
@Singleton
class HttpLoggingInterceptor @Inject constructor() : Interceptor {

    companion object {
        private const val TAG = "HttpLoggingInterceptor"
        private val UTF8 = StandardCharsets.UTF_8
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()

        // 记录请求信息
        logRequest(request)

        // 执行请求
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            Log.e(TAG, "请求失败: ${e.message}")
            throw e
        }

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // 记录响应信息
        return logResponse(response, duration)
    }

    /**
     * 记录请求信息
     */
    private fun logRequest(request: Request) {
        val method = request.method
        val url = request.url

        Log.d(TAG, "┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
        Log.d(TAG, "│ 请求开始: $method $url")
        Log.d(TAG, "│ 请求头:")

        val headers = request.headers
        logHeaders(headers)

        // 记录请求体
        val requestBody = request.body
        if (requestBody != null) {
            Log.d(TAG, "│ 请求体:")
            if (bodyHasUnknownEncoding(request.headers)) {
                Log.d(TAG, "│ 请求体编码不支持，无法打印")
            } else {
                val buffer = Buffer()
                requestBody.writeTo(buffer)

                val contentType = requestBody.contentType()
                val charset: Charset = contentType?.charset(UTF8) ?: UTF8

                if (buffer.size < 1024) {
                    Log.d(TAG, "│ ${buffer.readString(charset)}")
                } else {
                    Log.d(TAG, "│ 请求体太大 (${buffer.size}字节)，不打印")
                }
            }
        }

        Log.d(TAG, "└────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
    }

    /**
     * 记录响应信息
     */
    private fun logResponse(response: Response, duration: Long): Response {
        val request = response.request
        val code = response.code

        Log.d(TAG, "┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
        Log.d(TAG, "│ 响应信息: ${request.method} ${response.request.url} - $code ${response.message} (${duration}ms)")
        Log.d(TAG, "│ 响应头:")

        val headers = response.headers
        logHeaders(headers)

        // 记录响应体
        val responseBody = response.body
        if (responseBody != null) {
            val contentLength = responseBody.contentLength()
            val bodySize = if (contentLength != -1L) "$contentLength 字节" else "未知大小"

            Log.d(TAG, "│ 响应体: ($bodySize)")

            if (bodyHasUnknownEncoding(response.headers)) {
                Log.d(TAG, "│ 响应体编码不支持，无法打印")
                Log.d(TAG, "└────────────────────────────────────────────────────────────────────────────────────────────────────────────────")
                return response
            }

            if (contentLength > 0) {
                // 克隆响应体以便读取
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // 缓存整个响应体
                val buffer = source.buffer.clone()

                val contentType = responseBody.contentType()
                val charset: Charset = contentType?.charset(UTF8) ?: UTF8

                if (contentLength < 1024) {
                    val responseContent = buffer.readString(charset)
                    Log.d(TAG, "│ $responseContent")
                } else {
                    Log.d(TAG, "│ 响应体太大 (${buffer.size}字节)，不打印")
                }
            }
        }

        Log.d(TAG, "└────────────────────────────────────────────────────────────────────────────────────────────────────────────────")

        return response
    }

    /**
     * 记录HTTP头信息
     */
    private fun logHeaders(headers: Headers) {
        for (i in 0 until headers.size) {
            Log.d(TAG, "│   ${headers.name(i)}: ${headers.value(i)}")
        }
    }

    /**
     * 检查是否有不支持的编码
     */
    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }
}