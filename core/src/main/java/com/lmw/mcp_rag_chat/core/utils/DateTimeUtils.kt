package com.lmw.mcp_rag_chat.core.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期时间工具类
 */
object DateTimeUtils {
    private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val DATE_ONLY_FORMAT = "yyyy-MM-dd"
    private const val TIME_ONLY_FORMAT = "HH:mm:ss"
    
    /**
     * 获取当前时间的格式化字符串
     * @param pattern 日期格式，默认为 yyyy-MM-dd HH:mm:ss
     * @return 格式化后的日期字符串
     */
    fun getCurrentDateTime(pattern: String = DEFAULT_DATE_FORMAT): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    /**
     * 将时间戳转换为格式化的日期字符串
     * @param timestamp 时间戳（毫秒）
     * @param pattern 日期格式，默认为 yyyy-MM-dd HH:mm:ss
     * @return 格式化后的日期字符串
     */
    fun formatTimestamp(timestamp: Long, pattern: String = DEFAULT_DATE_FORMAT): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
    
    /**
     * 将日期字符串解析为Date对象
     * @param dateString 日期字符串
     * @param pattern 日期格式，默认为 yyyy-MM-dd HH:mm:ss
     * @return 解析后的Date对象，解析失败返回null
     */
    fun parseDate(dateString: String, pattern: String = DEFAULT_DATE_FORMAT): Date? {
        return try {
            val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 获取当前日期（不含时间）
     * @return 格式为yyyy-MM-dd的日期字符串
     */
    fun getCurrentDate(): String {
        return getCurrentDateTime(DATE_ONLY_FORMAT)
    }
    
    /**
     * 获取当前时间（不含日期）
     * @return 格式为HH:mm:ss的时间字符串
     */
    fun getCurrentTime(): String {
        return getCurrentDateTime(TIME_ONLY_FORMAT)
    }
}