package com.lmw.mcp_rag_chat.core.utils

/**
 * 字符串工具类
 */
object StringUtils {
    
    /**
     * 判断字符串是否为空或空白字符
     * @param str 要检查的字符串
     * @return 如果字符串为null或空白字符，返回true；否则返回false
     */
    fun isNullOrEmpty(str: String?): Boolean {
        return str == null || str.trim().isEmpty()
    }
    
    /**
     * 获取字符串的安全值，如果为null则返回默认值
     * @param str 原始字符串
     * @param defaultValue 默认值
     * @return 如果原始字符串不为null，则返回原始字符串；否则返回默认值
     */
    fun getOrDefault(str: String?, defaultValue: String): String {
        return str ?: defaultValue
    }
    
    /**
     * 截取字符串，如果超过最大长度，则在末尾添加省略号
     * @param str 原始字符串
     * @param maxLength 最大长度
     * @return 截取后的字符串
     */
    fun ellipsize(str: String, maxLength: Int): String {
        if (str.length <= maxLength) {
            return str
        }
        return str.substring(0, maxLength - 3) + "..."
    }
    
    /**
     * 将字符串的首字母大写
     * @param str 原始字符串
     * @return 首字母大写的字符串
     */
    fun capitalize(str: String?): String {
        if (isNullOrEmpty(str)) {
            return ""
        }
        return str!!.substring(0, 1).uppercase() + str.substring(1)
    }
}