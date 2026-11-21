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
 * sherpa-onnx 通过**反射**调用(编译期无依赖,引擎/模型装上才真转写,
 * 否则报"引擎未安装"友好错误)。这样不会因为 Maven 版本号挂编译。
 * 输入 16kHz 单声道 PCM,输出识别文字。
 */
class SenseVoiceLocal(context: Context) {
    private val modelDir = File(context.filesDir, "sensevoice")
    private val modelOnnx = File(modelDir, "model.onnx")
    private val tokens = File(modelDir, "tokens.txt")

    /** 模型文件齐全才可转写。 */
    val ready: Boolean get() = modelOnnx.isFile && tokens.isFile && modelOnnx.length() > 1024 * 1024

    /** 模型目录(界面显示下载路径)。 */
    val dir: File get() = modelDir

    /** SenseVoice 模型下载地址(int8 量化)。 */
    companion object {
        val MODEL_URL = "https://github.com/k2-fsa/sherpa-onnx/releases/download/asr-models/sherpa-onnx-sense-voice-zh-en-ja-ko-yue-2024-07-17.tar.bz2"
        val MODEL_TAR = "sherpa-onnx-sense-voice-zh-en-ja-ko-yue-2024-07-17.tar.bz2"

        // sherpa-onnx 类名(反射用,编译期无依赖)
        private const val CLS_RECOGNIZER = "com.k2fsa.sherpa.onnx.OfflineRecognizer"
        private const val CLS_RECOGNIZER_CFG = "com.k2fsa.sherpa.onnx.OfflineRecognizerConfig"
        private const val CLS_MODEL_CFG = "com.k2fsa.sherpa.onnx.OfflineModelConfig"
        private const val CLS_STREAM = "com.k2fsa.sherpa.onnx.OfflineStream"
    }

    @Volatile private var engine: Any? = null
    private var loadFailed = false

    @Synchronized
    private fun ensureLoaded(): Any {
        engine?.let { return it }
        if (loadFailed) throw ModelError.BadResponse("SenseVoice 引擎加载失败(模型或 so 损坏)")
        try {
            val recognizerCls = Class.forName(CLS_RECOGNIZER)
            val cfgCls = Class.forName(CLS_RECOGNIZER_CFG)
            val modelCls = Class.forName(CLS_MODEL_CFG)

            val modelCtor = modelCls.getConstructor(
                String::class.java, String::class.java,
                Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType,
                String::class.java, String::class.java,
                String::class.java, String::class.java, String::class.java,
            )
            val modelCfg = modelCtor.newInstance(
                modelOnnx.absolutePath, tokens.absolutePath,
                2, false, "cpu", "sensevoice", "", "", "",
            )
            val cfgCtor = cfgCls.getConstructor(
                modelCls, String::class.java,
                Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType,
                String::class.java, String::class.java,
                String::class.java, String::class.java,
            )
            val cfg = cfgCtor.newInstance(
                modelCfg, "greedy_search", 4, false, "", "", "", "",
            )
            val recCtor = recognizerCls.getConstructor(cfgCls)
            engine = recCtor.newInstance(cfg)
            return engine!!
        } catch (e: ClassNotFoundException) {
            loadFailed = true
            throw ModelError.BadResponse("SenseVoice 引擎未安装(依赖缺失),请更新 App 或下载模型")
        } catch (e: Throwable) {
            loadFailed = true
            throw ModelError.BadResponse("SenseVoice 引擎加载失败: ${e.message}")
        }
    }

    /**
     * 把一段 16kHz 单声道 PCM 转成文字。
     * @param pcm 16-bit PCM 采样
     */
    fun transcribe(pcm: ShortArray, sampleRate: Int = 16000): String {
        if (!ready) throw ModelError.BadResponse("SenseVoice 模型未就绪,请先下载到 ${modelDir.path}")
        val rec = ensureLoaded()
        var stream: Any? = null
        return try {
            val streamCls = Class.forName(CLS_STREAM)
            stream = rec.javaClass.getMethod("createStream").invoke(rec)
            val acceptWaveform = streamCls.getMethod("acceptWaveform", Int::class.javaPrimitiveType, ShortArray::class.java)
            acceptWaveform.invoke(stream, sampleRate, pcm)
            streamCls.getMethod("inputFinished").invoke(stream)
            val decode = rec.javaClass.getMethod("decode", streamCls)
            val result = decode.invoke(rec, stream)
            val getText = result.javaClass.getMethod("getText")
            (getText.invoke(result) as? String) ?: ""
        } finally {
            stream?.let { s -> runCatching { Class.forName(CLS_STREAM).getMethod("release").invoke(s) } }
        }
    }
}
