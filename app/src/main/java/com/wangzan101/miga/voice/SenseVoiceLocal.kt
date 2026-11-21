package com.wangzan101.miga.voice

import android.content.Context
import com.wangzan101.miga.core.model.ModelError
import java.io.File

/**
 * 本地 SenseVoice 转写(DeepDeep 那套):用 sherpa-onnx 的 SenseVoice 模型在手机上离线转写,
 * 绕开系统识别、不用 API 密钥。
 *
 * 模型文件放在 `files/sensevoice/`(int8 量化,几百 MB):
 * - model.onnx      SenseVoice 主模型
 * - tokens.txt      词表
 * - 可选 language/use_itn 参数
 *
 * [ready] 为真(模型文件齐全)才能转写;否则界面引导去下载模型。
 * sherpa-onnx 的 JNI 加载在 [transcribe] 里做(首次调用时 [ensureLoaded]);[transcribe]
 * 输入 16kHz 单声道 PCM(WAV 编码由 [Wav.encode] 产出),输出识别文字。
 *
 * 说明:sherpa-onnx AAR 依赖与模型下载是独立步骤;此处先落引擎接口与就绪检测,
 * 模型到位后 [transcribe] 直接可用(引擎实现见下方 TODO 接缝)。
 */
class SenseVoiceLocal(context: Context) {
    private val modelDir = File(context.filesDir, "sensevoice")
    private val modelOnnx = File(modelDir, "model.onnx")
    private val tokens = File(modelDir, "tokens.txt")

    /** 模型文件齐全才可转写。 */
    val ready: Boolean get() = modelOnnx.isFile && tokens.isFile && modelOnnx.length() > 1024 * 1024

    /** 模型目录(界面显示下载路径)。 */
    val dir: File get() = modelDir

    /**
     * 把一段 16kHz 单声道 PCM 转成文字。
     * @param pcm 16-bit PCM 采样
     */
    fun transcribe(pcm: ShortArray, sampleRate: Int = 16000): String {
        if (!ready) throw ModelError.BadResponse("SenseVoice 模型未就绪,请先下载到 ${modelDir.path}")
        // TODO(sensevoice): 集成 sherpa-onnx SenseVoice 引擎 ——
        //  1. app/build.gradle.kts 加依赖: implementation("com.k2-fsa:sherpa-onnx:<ver>")
        //  2. 首次调用 System.loadLibrary("sherpa-onnx-jni")
        //  3. 用 modelOnnx/tokens 初始化 OfflineRecognizer(enableEndpoint=false)
        //  4. pcm 转 FloatArray → recognizer.decode 拿文字
        //  5. 释放 recognizer
        // 在模型文件到位、sherpa-onnx 依赖加入前,这里返回占位错误由界面提示。
        throw ModelError.BadResponse("SenseVoice 引擎未集成(模型已就绪,需接入 sherpa-onnx)")
    }
}
