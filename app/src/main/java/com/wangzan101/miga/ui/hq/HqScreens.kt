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

data class HqSession(val title: String, val summary: String, val time: String, val bot: String)

private val demoSessions = listOf(
    HqSession("发布说明", "部署 · 2.4 带来 14 项变更：更快的同步、新的导出流程", "上午 9:37", "部署"),
    HqSession("对比向量库", "研究 · 三个方案，成本与召回率", "上午 9:12", "研究"),
    HqSession("晨间简报", "Hermes · 14 条新消息，3 条需要你今天处理", "上午 9:05", "Hermes"),
    HqSession("发票跟进", "财务 · 两张发票仍需你确认", "周一", "财务"),
    HqSession("霜冻监测", "花园 · 浇水至早上 7 点，已加提醒", "周日", "花园"),
)

/** Sessions 屏：日期分组 + 会话列表。 */
@Composable
fun HqSessionsScreen(
    onOpenSession: (HqSession) -> Unit = {},
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
                    Text("会话", style = HqType.ScreenTitle, color = Hq.ink)
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
        listOf("置顶", "今天早些时候", "昨天", "本周早些时候").forEachIndexed { gi, group ->
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
                HqSessionRow(s, onClick = { onOpenSession(s) })
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun HqSessionRow(s: HqSession, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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

data class HqJob(val name: String, val schedule: String, val next: String)

private val demoJobs = listOf(
    HqJob("夜间发布构建", "工作日 8:00", "下次：周一 8:00"),
    HqJob("备份网关", "每天 3:00", "下次：今晚 3:00"),
    HqJob("清理旧会话", "周日 2:00", "已暂停"),
)

/** Jobs 屏：cron 任务三分组。 */
@Composable
fun HqJobsScreen(onOpenJob: (HqJob) -> Unit = {}, modifier: Modifier = Modifier) {
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
                Text("任务", style = HqType.ScreenTitle, color = Hq.ink)
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
        listOf("运行中", "已计划", "已暂停").forEach { group ->
            Text(
                group,
                style = HqType.Section,
                color = Hq.ink2,
                modifier = Modifier.padding(horizontal = HqSpace.Page + 4.dp, vertical = 6.dp),
            )
            demoJobs.filter { it.schedule.contains("工作日") || group == "已计划" }.take(if (group == "已暂停") 1 else 2).forEach { job ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenJob(job) }
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

data class HqTask(val title: String, val meta: String, val col: String)

private val demoKanban = listOf(
    HqTask("批准 2.4 发布说明", "部署 · 3小时 · 2条评论", "阻塞"),
    HqTask("签署向量库方案", "研究 · 1小时", "待办"),
    HqTask("起草发票提醒", "财务 · 5小时", "待办"),
    HqTask("为霜冻准备温室", "花园 · 明天", "已计划"),
)

/** Kanban 屏：列芯片 + 任务卡。 */
@Composable
fun HqKanbanScreen(onOpenTask: (HqTask) -> Unit = {}, modifier: Modifier = Modifier) {
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
                Text("看板", style = HqType.ScreenTitle, color = Hq.ink)
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
                Triple("阻塞", Color(0xFFFF453A), true),
                Triple("待办", Color(0xFF8E8E93), false),
                Triple("待处理", Color(0xFFC7C7CC), false),
                Triple("已计划", Color(0xFFBF5AF2), false),
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
                    .clickable { onOpenTask(task) }
                    .padding(horizontal = HqSpace.Page, vertical = 5.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Hq.surface)
                    .padding(16.dp),
            ) {
                Text(task.title, style = HqType.RowTitle, color = Hq.ink)
                Spacer(Modifier.height(4.dp))
                Text(task.meta, style = HqType.Caption, color = Hq.ink2)
                if (task.col == "阻塞") {
                    Spacer(Modifier.height(6.dp))
                    Text("需要你的输入", style = HqType.Caption, color = Color(0xFFFF6B5E))
                }
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}
