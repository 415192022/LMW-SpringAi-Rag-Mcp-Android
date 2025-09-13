package com.lmw.mcp_rag_chat.core.utils

import android.content.Context
import android.widget.Toast

/**
 * 显示短时间Toast
 */
fun Context.showShortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * 显示长时间Toast
 */
fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}