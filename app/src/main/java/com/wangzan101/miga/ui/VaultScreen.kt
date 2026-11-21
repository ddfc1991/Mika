package com.wangzan101.miga.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wangzan101.miga.ui.components.ListRow
import com.wangzan101.miga.ui.components.RowChevron
import com.wangzan101.miga.ui.theme.MikaIcons
import com.wangzan101.miga.ui.theme.MikaTheme
import com.wangzan101.miga.ui.theme.MikaType
import com.wangzan101.miga.vault.VaultEntry
import com.wangzan101.miga.vault.VaultPermission
import com.wangzan101.miga.vault.VaultStore

/** 账户金库：账号台账，密钥只存加密（Android Keystore AES-GCM）。 */
@Composable
fun VaultScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val entries by VaultStore.entries(ctx).collectAsState()
    val c = MikaTheme.colors

    Column(Modifier.fillMaxSize()) {
        // 标题栏
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(40.dp).clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.material3.Icon(MikaIcons.Back, contentDescription = "返回", tint = c.ink)
            }
            Spacer(Modifier.width(8.dp))
            Text("账户金库", style = MikaType.Title, color = c.ink)
        }

        if (entries.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("还没有账号", style = MikaType.Body, color = c.ink.copy(alpha = 0.6f))
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "点下面「添加账号」存第一个；密钥只存设备加密区",
                        style = MikaType.Caption, color = c.ink.copy(alpha = 0.45f),
                    )
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(entries, key = { it.id }) { e ->
                    VaultRow(e) { onBack() }
                }
            }
        }

        // 底部添加（占位，下轮接编辑表单）
        Row(
            Modifier.fillMaxWidth().padding(16.dp).clickable { },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("＋ 添加账号", color = c.ink, style = MikaType.Body)
        }
    }
}

@Composable
private fun VaultRow(e: VaultEntry, onEdit: () -> Unit) {
    val c = MikaTheme.colors
    val perm = when (e.permission) {
        VaultPermission.AUTO -> "自动"
        VaultPermission.ASK -> "询问"
        VaultPermission.DISABLED -> "禁用"
    }
    val statusText = e.status?.let { s ->
        s.credits?.let { " · $it" } ?: s.balance?.let { " · $it" } ?: s.quota?.let { " · $it" } ?: ""
    } ?: ""
    ListRow(
        icon = MikaIcons.Key,
        title = e.site,
        subtitle = "${e.username} · $perm$statusText",
        onClick = onEdit,
    ) { RowChevron() }
}
