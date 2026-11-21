package com.wangzan101.mika.settings

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

/**
 * 助手头像：用户可随时自定义换图（长按顶栏头像 → 相册选图）。
 * 只换图，动画（呼吸/眨眼/口型）和语言（TTS/语音）逻辑不变。
 * 自定义图存 filesDir/avatar.png；[version] 变化时组件重载。
 */
object AvatarStore {
    private const val FILE = "avatar.png"

    /** 头像版本号：换图/清除时自增，Compose 里 collectAsState 触发重组。 */
    val version = MutableStateFlow(0)

    fun file(ctx: Context) = File(ctx.filesDir, FILE)

    fun has(ctx: Context) = file(ctx).exists()

    /** 从相册 URI 复制到 filesDir/avatar.png；成功返回 true 并 bump 版本。 */
    fun set(ctx: Context, uri: Uri): Boolean {
        val ok = runCatching {
            ctx.contentResolver.openInputStream(uri)?.use { input ->
                file(ctx).outputStream().use { input.copyTo(it) }
            } ?: return false
            true
        }.getOrDefault(false)
        if (ok) version.value++
        return ok
    }

    /** 恢复默认头像（删自定义图）。 */
    fun clear(ctx: Context) {
        file(ctx).delete()
        version.value++
    }
}
