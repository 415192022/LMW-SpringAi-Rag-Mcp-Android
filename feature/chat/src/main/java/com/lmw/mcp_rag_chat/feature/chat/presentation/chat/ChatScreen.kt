package com.lmw.mcp_rag_chat.feature.chat.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lmw.mcp_rag_chat.core.ui.theme.Background
import com.lmw.mcp_rag_chat.core.ui.theme.Blue500
import com.lmw.mcp_rag_chat.core.ui.theme.SseConnected
import com.lmw.mcp_rag_chat.core.ui.theme.SseConnecting
import com.lmw.mcp_rag_chat.core.ui.theme.SseDisconnected
import com.lmw.mcp_rag_chat.core.ui.theme.SseError
import com.lmw.mcp_rag_chat.core.ui.theme.Surface
import com.lmw.mcp_rag_chat.feature.chat.presentation.components.ChatInput
import com.lmw.mcp_rag_chat.feature.chat.presentation.components.MessageItem
import com.lmw.mcp_rag_chat.feature.chat.presentation.components.SearchModeSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val searchMode by viewModel.searchMode.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val sseConnected by viewModel.sseConnected.collectAsState()
    
    val listState = rememberLazyListState()
    
    // 当消息列表更新时，滚动到底部
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    // 根据SSE连接状态确定指示器颜色
    val sseStatusColor = when {
        sseConnected -> SseConnected  // 连接成功 - 绿色
        viewModel.isConnecting -> SseConnecting  // 连接中 - 黄色
        viewModel.hasConnectionError -> SseError  // 连接失败 - 红色
        else -> SseDisconnected  // 未连接 - 灰色
    }
    
    Scaffold(
        // 设置windowInsets为空，防止Scaffold自动应用insets
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("McpChat", color = Surface)
                        // SSE连接状态指示器
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(8.dp)
                                .background(color = sseStatusColor, shape = CircleShape)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Blue500
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
                // 添加imePadding到整个Column布局
                .imePadding()
        ) {
            // 消息列表
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(16.dp)
            ) {
                items(messages) { message ->
                    MessageItem(message = message)
                }
            }
            
            // 输入区域
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                color = Surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 搜索模式选择器
                    SearchModeSelector(
                        selectedMode = searchMode,
                        onModeSelected = viewModel::onSearchModeChanged,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // 输入框和发送按钮
                    ChatInput(
                        value = inputText,
                        onValueChange = viewModel::onInputTextChanged,
                        onSendClick = viewModel::sendMessage,
                        isLoading = isLoading
                    )
                }
            }
        }
    }
}