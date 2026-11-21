package com.wangzan101.miga.ui.hq

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wangzan101.miga.chat.ConversationSummary
import com.wangzan101.miga.task.TaskRun

/**
 * HQ 5-tab 主框架：Bots（聊天）/ Sessions / Jobs / Kanban / More。
 * [botsContent] 是聊天本体（Miga 现有 ChatScreen），其余 tab 是 HQ 复刻屏。
 * 点 bot 打开聊天时由外层控制。
 */
@Composable
fun HqShell(
    botsContent: @Composable () -> Unit,
    sessions: List<ConversationSummary> = emptyList(),
    jobs: List<TaskRun> = emptyList(),
    onOpenSession: (String) -> Unit = {},
    onMoreEntry: (HqMoreEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    var tab by rememberSaveable { mutableStateOf(HqTab.Bots) }
    Column(modifier = modifier.fillMaxSize().background(Hq.bg).statusBarsPadding()) {
        Box(Modifier.weight(1f)) {
            when (tab) {
                HqTab.Bots -> botsContent()
                HqTab.Sessions -> HqSessionsScreen(sessions, onOpenSession = { onOpenSession(it.id) })
                HqTab.Jobs -> HqJobsScreen(jobs)
                HqTab.Kanban -> HqFeedScreen(tasks = jobs, sessions = sessions, onOpenSession = { onOpenSession(it) })
                HqTab.More -> HqMoreScreen(onMoreEntry)
            }
        }
        Box(
            Modifier
                .navigationBarsPadding()
                .padding(top = 8.dp),
        ) {
            HqTabBar(current = tab, onSelect = { tab = it })
        }
    }
}
