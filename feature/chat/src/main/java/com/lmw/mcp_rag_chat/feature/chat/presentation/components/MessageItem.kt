package com.lmw.mcp_rag_chat.feature.chat.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lmw.mcp_rag_chat.domain.model.Message
import com.lmw.mcp_rag_chat.domain.model.MessageType
import com.lmw.mcp_rag_chat.core.ui.theme.Blue500
import com.lmw.mcp_rag_chat.core.ui.theme.Green100
import com.lmw.mcp_rag_chat.core.ui.theme.Green200
import com.lmw.mcp_rag_chat.core.ui.theme.Green500
import com.lmw.mcp_rag_chat.core.ui.theme.Blue100
import com.lmw.mcp_rag_chat.core.ui.theme.Blue200

@Composable
fun MessageItem(message: Message, modifier: Modifier = Modifier) {
    when (message.type) {
        MessageType.BOT -> BotMessageItem(message, modifier)
        MessageType.USER -> UserMessageItem(message, modifier)
    }
}

@Composable
fun BotMessageItem(message: Message, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        // Avatar
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Blue200
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "🤖", textAlign = TextAlign.Center)
            }
        }
        
        // Message bubble
        Card(
            modifier = Modifier.padding(start = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Blue100)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                color = Blue500
            )
        }
    }
}

@Composable
fun UserMessageItem(message: Message, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        // Message bubble (right-aligned)
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            Card(
                modifier = Modifier.padding(end = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Green100)
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(12.dp),
                    color = Green500
                )
            }
        }
        
        // Avatar
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Green200
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "👤", textAlign = TextAlign.Center)
            }
        }
    }
}