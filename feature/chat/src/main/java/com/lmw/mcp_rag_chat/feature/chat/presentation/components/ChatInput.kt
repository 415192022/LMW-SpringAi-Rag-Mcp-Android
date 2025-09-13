package com.lmw.mcp_rag_chat.feature.chat.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import com.lmw.mcp_rag_chat.core.ui.theme.Background
import com.lmw.mcp_rag_chat.core.ui.theme.Blue500
import com.lmw.mcp_rag_chat.core.ui.theme.Surface
import com.lmw.mcp_rag_chat.core.ui.theme.TextHint
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "请输入内容...",
    autoFocus: Boolean = true,
    isLoading: Boolean = false
) {
    // 创建一个焦点请求器
    val focusRequester = remember { FocusRequester() }
    
    // 如果autoFocus为true，自动请求焦点
    if (autoFocus) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 输入框
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = TextHint) },
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Blue500,
                unfocusedBorderColor = TextHint,
                focusedContainerColor = Background,
                unfocusedContainerColor = Background
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSendClick() }),
            maxLines = 3
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 根据加载状态显示发送按钮或进度条
        if (isLoading) {
            // 加载中显示进度条
            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Blue500,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            // 非加载状态显示发送按钮
            Button(
                onClick = onSendClick,
                enabled = value.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue500,
                    contentColor = Surface
                )
            ) {
                Text(text = "发送")
            }
        }
    }
}