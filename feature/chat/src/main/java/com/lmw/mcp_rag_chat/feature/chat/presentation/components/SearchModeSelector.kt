package com.lmw.mcp_rag_chat.feature.chat.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lmw.mcp_rag_chat.domain.model.SearchMode
import com.lmw.mcp_rag_chat.core.ui.theme.Blue500
import com.lmw.mcp_rag_chat.core.ui.theme.Surface

@Composable
fun SearchModeSelector(
    selectedMode: SearchMode,
    onModeSelected: (SearchMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        // 知识库搜索按钮
        SearchModeButton(
            text = "知识库搜索",
            selected = selectedMode == SearchMode.KNOWLEDGE_BASE,
            onClick = { 
                // 如果当前已选中知识库搜索，则切换到普通模式，否则切换到知识库搜索
                if (selectedMode == SearchMode.KNOWLEDGE_BASE) {
                    onModeSelected(SearchMode.NORMAL)
                } else {
                    onModeSelected(SearchMode.KNOWLEDGE_BASE)
                }
            }
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 联网搜索按钮
        SearchModeButton(
            text = "联网搜索",
            selected = selectedMode == SearchMode.WEB,
            onClick = { 
                // 如果当前已选中联网搜索，则切换到普通模式，否则切换到联网搜索
                if (selectedMode == SearchMode.WEB) {
                    onModeSelected(SearchMode.NORMAL)
                } else {
                    onModeSelected(SearchMode.WEB)
                }
            }
        )
    }
}

@Composable
fun SearchModeButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .selectable(selected = selected, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Blue500 else Surface,
        border = BorderStroke(1.dp, Blue500)
    ) {
        Text(
            text = text,
            color = if (selected) Surface else Blue500,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}