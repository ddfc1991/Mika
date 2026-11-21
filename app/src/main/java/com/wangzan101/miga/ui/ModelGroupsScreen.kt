package com.wangzan101.miga.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.wangzan101.miga.R
import com.wangzan101.miga.settings.ModelGroupStore
import com.wangzan101.miga.ui.components.FooterNote
import com.wangzan101.miga.ui.components.GroupCard
import com.wangzan101.miga.ui.components.GroupLabel
import com.wangzan101.miga.ui.components.ListRow
import com.wangzan101.miga.ui.theme.MikaIcons
import com.wangzan101.miga.ui.theme.MikaTheme
import com.wangzan101.miga.ui.theme.MikaType

/** 模型组页:模型分组管理(组名 + 成员),Mibot 模型组款。 */
@Composable
fun ModelGroupsScreen(
    store: ModelGroupStore,
    onBack: () -> Unit,
    onOpenGroup: (String) -> Unit = {},
) {
    val groups by store.groups.collectAsState()
    val c = MikaTheme.colors
    var adding by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    Page(stringResource(R.string.models_groups_title), onBack) {
        if (groups.isEmpty() && !adding) {
            FooterNote(stringResource(R.string.models_groups_empty))
        } else {
            GroupLabel(stringResource(R.string.models_groups_label))
            GroupCard {
                groups.forEachIndexed { i, g ->
                    ListRow(
                        icon = MikaIcons.Apps,
                        title = g.name,
                        subtitle = stringResource(R.string.models_groups_member_count, g.members.size),
                        first = i == 0,
                        onClick = { onOpenGroup(g.id) },
                        trailing = {
                            TextButton(onClick = { store.remove(g.id) }) {
                                Text(stringResource(R.string.models_groups_delete), color = c.bad)
                            }
                        },
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        if (adding) {
            GroupCard {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = { Text(stringResource(R.string.models_groups_name_hint)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    )
                    Spacer(Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            if (name.isNotBlank()) { store.add(name); name = ""; adding = false }
                        },
                    ) { Text(stringResource(R.string.models_groups_create)) }
                    TextButton(onClick = { adding = false; name = "" }) {
                        Text(stringResource(R.string.models_groups_cancel), color = c.ink3)
                    }
                }
            }
        } else {
            GroupCard {
                ListRow(
                    icon = MikaIcons.Plus,
                    title = stringResource(R.string.models_groups_add),
                    first = true,
                    onClick = { adding = true },
                )
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}
