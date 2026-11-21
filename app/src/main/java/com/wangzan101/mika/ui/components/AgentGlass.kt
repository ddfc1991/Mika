package com.wangzan101.mika.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wangzan101.mika.ui.theme.MikaTheme

/**
 * Solid Glass 智能体组件（对话屏用）：
 * - [StatusGlyph]：状态徽标，三点，运行中第一点绿色脉冲
 * - [ToolSteps]：工具步骤，时间线，每步标题+说明+状态
 * - [ContextRing]：上下文环，Canvas 画弧 + 百分比
 * - [GlassChip]：玻璃胶囊标签
 * 颜色一律走 [MikaTheme.colors]，不写死。
 */
@Composable
fun StatusGlyph(active: Boolean = true, modifier: Modifier = Modifier) {
    val c = MikaTheme.colors
    val infinite = rememberInfiniteTransition()
    val alpha by infinite.animateFloat(
        initialValue = 1f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
    )
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(3) { i ->
            Box(
                Modifier.size(4.dp).background(
                    if (active && i == 0) c.ok.copy(alpha = alpha) else c.ink3.copy(alpha = 0.7f),
                    CircleShape,
                ),
            )
        }
    }
}

/** 一步工具：标题、说明、完成/进行中。 */
data class ToolStep(val title: String, val note: String, val done: Boolean, val running: Boolean = false)

@Composable
fun ToolSteps(steps: List<ToolStep>, modifier: Modifier = Modifier) {
    val c = MikaTheme.colors
    Column(modifier.fillMaxWidth().background(c.answer, RoundedCornerShape(14.dp)).padding(12.dp)) {
        Text("工具步骤", color = c.ink2, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        steps.forEach { s ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                Box(Modifier.width(20.dp).padding(top = 4.dp)) {
                    Box(
                        Modifier.size(6.dp).align(Alignment.Center).background(
                            when { s.running -> c.ok; s.done -> c.accent; else -> c.ink3 },
                            CircleShape,
                        ),
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(s.title, color = c.ink, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(s.note, color = c.ink3, fontSize = 10.sp)
                }
                when {
                    s.running -> Text("进行中", color = c.ok, fontSize = 10.sp)
                    s.done -> Text("完成", color = c.ok, fontSize = 10.sp)
                }
            }
        }
    }
}

/** 上下文环：Canvas 画弧 + 百分比，[percent] 0..1。 */
@Composable
fun ContextRing(percent: Float, label: String, modifier: Modifier = Modifier) {
    val c = MikaTheme.colors
    Box(modifier.size(56.dp)) {
        Canvas(Modifier.matchParentSize()) {
            val stroke = 4.dp.toPx()
            val inset = stroke / 2
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(
                c.hair, 0f, 360f, false,
                topLeft = Offset(inset, inset), size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
            drawArc(
                c.ok, -90f, 360f * percent.coerceIn(0f, 1f), false,
                topLeft = Offset(inset, inset), size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
        }
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(percent * 100).toInt()}%", color = c.ink, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(label, color = c.ink3, fontSize = 8.sp)
        }
    }
}

/** 玻璃胶囊标签：[tint] 给底色/描边，不给就用中性。 */
@Composable
fun GlassChip(text: String, tint: Color? = null, modifier: Modifier = Modifier) {
    val c = MikaTheme.colors
    val bg = tint?.copy(alpha = 0.16f) ?: c.fill
    val fg = tint ?: c.ink2
    val edge = tint?.copy(alpha = 0.25f) ?: c.edge
    Text(
        text,
        modifier.background(bg, RoundedCornerShape(8.dp))
            .border(1.dp, edge, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        color = fg, fontSize = 10.sp, fontWeight = FontWeight.Medium,
    )
}

/**
 * 动态猫娘头像：会动（呼吸/眨眼/说话口型）+ 会发声（说话时口型动，配 TTS/语音）。
 * [speaking] 说话中（口型快速开合 + 声波环）；[blink] 眨眼帧。
 */
@Composable
fun CatGirlAvatar(
    speaking: Boolean,
    modifier: Modifier = Modifier,
    size: Int = 64,
) {
    val c = MikaTheme.colors
    val infinite = rememberInfiniteTransition()
    // 呼吸：整体轻微缩放
    val breathe by infinite.animateFloat(
        initialValue = 1f, targetValue = 1.035f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
    )
    // 说话：口型开合（快速）
    val mouth by infinite.animateFloat(
        initialValue = 0.15f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(140), RepeatMode.Reverse),
    )
    // 眨眼：周期闭合（动画）
    val blinkAlpha by infinite.animateFloat(
        initialValue = 1f, targetValue = 0.12f,
        animationSpec = infiniteRepeatable(tween(120, delayMillis = 3000), RepeatMode.Reverse),
    )
    Box(modifier.size(size.dp), contentAlignment = Alignment.Center) {
        // 声波环（说话时）
        if (speaking) {
            val ringAlpha by infinite.animateFloat(
                initialValue = 0.5f, targetValue = 0.1f,
                animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
            )
            Box(Modifier.size((size * 1.15f).dp).border(2.dp, c.ok.copy(alpha = ringAlpha), CircleShape))
        }
        // 头像底（深色圆 + 白圆）
        Box(
            Modifier.size((size * 0.95f).dp).scale(breathe)
                .background(c.bg, CircleShape),
        ) {
            // 猫娘（简笔：白脸 + 黑发 + 猫耳 + 大眼）
            CatGirlFace(blinkAlpha, mouth, speaking, c)
        }
    }
}

@Composable
private fun CatGirlFace(blink: Float, mouth: Float, speaking: Boolean, c: com.wangzan101.mika.ui.theme.MikaColors) {
    // Canvas 简笔猫娘：圆脸白、黑发、猫耳、大眼、嘴（说话开合）、腮红
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        // 脸
        drawCircle(androidx.compose.ui.graphics.Color.White, radius = w * 0.30f, center = center)
        // 黑发（上半圆）
        drawArc(androidx.compose.ui.graphics.Color.Black, 180f, 180f, false,
            topLeft = Offset(w * 0.20f, h * 0.12f), size = Size(w * 0.60f, h * 0.60f))
        // 猫耳
        drawPath(
            androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.28f, h * 0.42f); lineTo(w * 0.34f, h * 0.14f); lineTo(w * 0.52f, h * 0.34f); close()
            },
            androidx.compose.ui.graphics.Color.Black,
        )
        drawPath(
            androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.72f, h * 0.42f); lineTo(w * 0.66f, h * 0.14f); lineTo(w * 0.48f, h * 0.34f); close()
            },
            androidx.compose.ui.graphics.Color.Black,
        )
        // 大眼（眨眼时闭合）
        val eyeH = w * 0.14f * blink
        drawOval(androidx.compose.ui.graphics.Color.Black, topLeft = Offset(w * 0.36f, h * 0.48f), size = Size(w * 0.11f, eyeH))
        drawOval(androidx.compose.ui.graphics.Color.Black, topLeft = Offset(w * 0.53f, h * 0.48f), size = Size(w * 0.11f, eyeH))
        // 嘴（说话开合）
        val mouthH = w * (if (speaking) 0.06f * mouth else 0.025f)
        drawOval(androidx.compose.ui.graphics.Color.Black, topLeft = Offset(w * 0.46f, h * 0.66f), size = Size(w * 0.08f, mouthH))
    }
}
