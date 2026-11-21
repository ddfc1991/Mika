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
    }

    private var recognizer: com.k2fsa.sherpa.onnx.OfflineRecognizer? = null
    private var loadFailed = false

    @Synchronized
    private fun ensureLoaded(): com.k2fsa.sherpa.onnx.OfflineRecognizer {
        recognizer?.let { return it }
        if (loadFailed) throw ModelError.BadResponse("SenseVoice 引擎加载失败(模型或 so 损坏)")
        val cfg = com.k2fsa.sherpa.onnx.OfflineRecognizerConfig(
            com.k2fsa.sherpa.onnx.OfflineModelConfig(
                modelOnnx.absolutePath,
                tokens.absolutePath,
                /* numThreads= */ 2,
                /* debug= */ false,
                /* provider= */ "cpu",
                /* modelType= */ "sensevoice",
                /* tokens= */ "",
                /* modelingUnit= */ "",
                /* bpeVocab= */ "",
            ),
            /* decodingMethod= */ "greedy_search",
            /* maxActivePaths= */ 4,
            /* enableEndpoint= */ false,
            /* rule1= */ "",
            /* rule2= */ "",
            /* rule3= */ "",
            /* rule4= */ "",
        )
        return try {
            com.k2fsa.sherpa.onnx.OfflineRecognizer(cfg).also { recognizer = it }
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
        val stream = rec.createStream()
        try {
            stream.acceptWaveform(sampleRate, pcm)
            stream.inputFinished()
            val result = rec.decode(stream)
            return result.text
        } finally {
            stream.release()
        }
    }
}
