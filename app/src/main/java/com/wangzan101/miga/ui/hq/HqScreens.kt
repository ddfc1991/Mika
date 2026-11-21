
@Composable
fun HqFeedScreen(
    tasks: List<TaskRun> = emptyList(),
    sessions: List<ConversationSummary> = emptyList(),
    onOpenTask: (TaskRun) -> Unit = {},
    onOpenSession: (String) -> Unit = {},
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
            modifier = Modifier.fillMaxWidth().padding(horizontal = HqSpace.Page),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("动态", style = HqType.ScreenTitle, color = Hq.ink)
                Text("只看你的 · agent 活动流", style = HqType.Caption, color = Hq.ink2)
            }
        }
        Spacer(Modifier.height(16.dp))

        // 正在跑的任务（实时）
        if (tasks.isNotEmpty()) {
            Text("正在跑", style = HqType.RowTitle, color = Hq.ink2, modifier = Modifier.padding(horizontal = HqSpace.Page))
            Spacer(Modifier.height(8.dp))
            tasks.forEach { task ->
                Column(
                    modifier = Modifier.fillMaxWidth().clickable { onOpenTask(task) }
                        .padding(horizontal = HqSpace.Page, vertical = 5.dp)
                        .clip(RoundedCornerShape(18.dp)).background(Hq.surface).padding(16.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF34C759)))
                        Spacer(Modifier.width(8.dp))
                        Text("任务运行中", style = HqType.Caption, color = Hq.ink2)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(task.goal, style = HqType.RowTitle, color = Hq.ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // 会话动态（按最近更新倒序）
        Text("动态", style = HqType.RowTitle, color = Hq.ink2, modifier = Modifier.padding(horizontal = HqSpace.Page))
        Spacer(Modifier.height(8.dp))
        val sorted = sessions.sortedByDescending { it.updatedAt }
        if (sorted.isEmpty()) {
            Text("还没有动态——去对话里跟 Miga 聊聊吧", style = HqType.Caption, color = Hq.ink3, modifier = Modifier.padding(horizontal = HqSpace.Page, vertical = 24.dp))
        } else {
            sorted.forEach { s ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onOpenSession(s.id) }
                        .padding(horizontal = HqSpace.Page, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp)).background(Hq.surface).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(Hq.surface2), contentAlignment = Alignment.Center) {
                        Text("💬", style = HqType.RowTitle)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(s.title.ifBlank { "未命名会话" }, style = HqType.RowTitle, color = Hq.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(2.dp))
                        Text(relTime(s.updatedAt), style = HqType.Caption, color = Hq.ink2)
                    }
                }
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}

private fun relTime(ms: Long): String {
    val now = System.currentTimeMillis()
    val d = now - ms
    if (d < 60_000) return "刚刚"
    if (d < 3_600_000) return "${d / 60_000} 分钟前"
    if (d < 86_400_000) return "${d / 3_600_000} 小时前"
    return "${d / 86_400_000} 天前"
}
