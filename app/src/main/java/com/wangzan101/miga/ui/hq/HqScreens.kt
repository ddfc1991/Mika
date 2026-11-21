package com.wangzan101.miga.ui.hq

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wangzan101.miga.ui.theme.MikaIcons

private data class HqSession(val title: String, val summary: String, val time: String, val bot: String)

private val demoSessions = listOf(
    HqSession("Release notes", "Deploys · 2.4 ships 14 changes: faster sync, new export flow", "9:37 AM", "Deploys"),
    HqSession("Comparing vector stores", "Research · Three options, cost and recall", "9:12 AM", "Research"),
    HqSession("Morning briefing", "Hermes · 14 new overnight, 3 need you today", "9:05 AM", "Hermes"),
    HqSession("Invoice follow-ups", "Finance · Two invoices still need your OK", "Mon", "Finance"),
    HqSession("Frost watch", "Garden · Watering to 7 AM, reminder added", "Sun", "Garden"),
)

/** Sessions 屏：日期分组 + 会话列表。 */
@Composable
fun HqSessionsScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Hq.bg)
            .verticalScroll(rememberScrollState())
            .padding(top = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HqSpace.Page),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sessions", style = HqType.ScreenTitle, color = Hq.ink)
                    Spacer(Modifier.width(6.dp))
                    Icon(MikaIcons.Down, null, tint = Hq.ink2, modifier = Modifier.size(14.dp))
                }
                Text("Mac mini", style = HqType.Caption, color = Hq.ink2)
            }
            Row(
                modifier = Modifier
                    .clip(HqShape.Capsule)
                    .background(Hq.surface)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Icon(MikaIcons.ListPlus, null, tint = Hq.ink, modifier = Modifier.size(20.dp))
                Icon(MikaIcons.Search, null, tint = Hq.ink, modifier = Modifier.size(20.dp))
                Icon(MikaIcons.Edit, null, tint = Hq.ink, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.height(16.dp))

        // 日期分组：今天 / 昨天 / 本周
        listOf("Pinned", "Earlier today", "Yesterday", "Earlier this week").forEachIndexed { gi, group ->
            val items = demoSessions.drop(gi * 1).take(2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HqSpace.Page, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(group, style = HqType.Section, color = Hq.ink2, modifier = Modifier.weight(1f))
                Icon(MikaIcons.Down, null, tint = Hq.accent.copy(alpha = 0.6f), modifier = Modifier.size(13.dp))
            }
            items.forEach { s ->
                HqSessionRow(s)
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun HqSessionRow(s: HqSession) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = HqSpace.Page, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PebbleAvatar(s.bot, Modifier.size(28.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(s.title, style = HqType.RowTitle, color = Hq.ink, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(s.time, style = HqType.Caption, color = Hq.ink3)
            }
            Spacer(Modifier.height(2.dp))
            Text(s.summary, style = HqType.Caption, color = Hq.ink2, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(8.dp))
        Icon(MikaIcons.More, null, tint = Hq.ink2, modifier = Modifier.size(16.dp))
    }
}

private data class HqJob(val name: String, val schedule: String, val next: String)

private val demoJobs = listOf(
    HqJob("Nightly release build", "Weekdays at 8:00 AM", "Next: Mon 8:00 AM"),
    HqJob("Backup gateway", "Every day at 3:00 AM", "Next: tonight 3:00 AM"),
    HqJob("Prune old sessions", "Sundays at 2:00 AM", "Paused"),
)

/** Jobs 屏：cron 任务三分组。 */
@Composable
fun HqJobsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Hq.bg)
            .verticalScroll(rememberScrollState())
            .padding(top = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HqSpace.Page),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Jobs", style = HqType.ScreenTitle, color = Hq.ink)
                Text("Mac mini", style = HqType.Caption, color = Hq.ink2)
            }
            Row(
                modifier = Modifier
                    .clip(HqShape.Capsule)
                    .background(Hq.surface)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Icon(MikaIcons.ListPlus, null, tint = Hq.ink, modifier = Modifier.size(20.dp))
                Icon(MikaIcons.Search, null, tint = Hq.ink, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        listOf("Running", "Scheduled", "Paused").forEach { group ->
            Text(
                group,
                style = HqType.Section,
                color = Hq.ink2,
                modifier = Modifier.padding(horizontal = HqSpace.Page + 4.dp, vertical = 6.dp),
            )
            demoJobs.filter { it.schedule.contains("Weekdays") || group == "Scheduled" }.take(if (group == "Paused") 1 else 2).forEach { job ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HqSpace.Page, vertical = 3.dp)
                        .clip(HqShape.Card)
                        .background(Hq.surface)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(MikaIcons.Clock, null, tint = Hq.accent, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(job.name, style = HqType.RowTitle, color = Hq.ink)
                        Text(job.schedule, style = HqType.Caption, color = Hq.ink2)
                    }
                    Text(job.next, style = HqType.Caption, color = Hq.ink3)
                }
                Spacer(Modifier.height(6.dp))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

private data class HqTask(val title: String, val meta: String, val col: String)

private val demoKanban = listOf(
    HqTask("Approve the 2.4 release notes", "Deploys · 3h · 2 comments", "Blocked"),
    HqTask("Sign off on vector store", "Research · 1h", "Triage"),
    HqTask("Draft invoice reminders", "Finance · 5h", "Todo"),
    HqTask("Prep greenhouse for frost", "Garden · tomorrow", "Scheduled"),
)

/** Kanban 屏：列芯片 + 任务卡。 */
@Composable
fun HqKanbanScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Hq.bg)
            .padding(top = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HqSpace.Page),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Kanban", style = HqType.ScreenTitle, color = Hq.ink)
                Text("Release 2.4", style = HqType.Caption, color = Hq.ink)
            }
            Icon(MikaIcons.ListPlus, null, tint = Hq.ink, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(16.dp))
            Icon(MikaIcons.Info, null, tint = Hq.ink, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(16.dp))
        // 列芯片
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = HqSpace.Page),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(
                Triple("Blocked", Color(0xFFFF453A), true),
                Triple("Triage", Color(0xFF8E8E93), false),
                Triple("Todo", Color(0xFFC7C7CC), false),
                Triple("Scheduled", Color(0xFFBF5AF2), false),
            ).forEach { (name, dot, sel) ->
                Row(
                    modifier = Modifier
                        .clip(HqShape.Chip)
                        .background(if (sel) Color(0xFF16213D) else Hq.surface)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(dot))
                    Spacer(Modifier.width(6.dp))
                    Text(name, style = HqType.Caption, color = if (sel) Hq.accent else Hq.ink3)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        demoKanban.forEach { task ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HqSpace.Page, vertical = 5.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Hq.surface)
                    .padding(16.dp),
            ) {
                Text(task.title, style = HqType.RowTitle, color = Hq.ink)
                Spacer(Modifier.height(4.dp))
                Text(task.meta, style = HqType.Caption, color = Hq.ink2)
                if (task.col == "Blocked") {
                    Spacer(Modifier.height(6.dp))
                    Text("Needs your input", style = HqType.Caption, color = Color(0xFFFF6B5E))
                }
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}
