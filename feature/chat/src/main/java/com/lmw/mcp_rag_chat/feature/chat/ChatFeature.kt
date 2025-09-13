package com.lmw.mcp_rag_chat.feature.chat

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.lmw.mcp_rag_chat.feature.chat.presentation.chat.ChatScreen

/**
 * Chat功能模块的入口点
 */
object ChatFeature {
    
    const val ROUTE = "chat"
    
    /**
     * 将聊天功能添加到导航图中
     */
    fun NavGraphBuilder.chatGraph(navController: NavHostController) {
        composable(ROUTE) {
            ChatScreen()
        }
    }
    
    /**
     * 导航到聊天界面
     */
    fun NavHostController.navigateToChat() {
        this.navigate(ROUTE)
    }
}