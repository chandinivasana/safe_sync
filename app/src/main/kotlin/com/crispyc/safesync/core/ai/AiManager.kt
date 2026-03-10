package com.crispyc.safesync.core.ai

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var anomalyDetector: Interpreter? = null
    private var voiceStressModel: Interpreter? = null
    private var moodFusionModel: Interpreter? = null

    init {
        loadModels()
    }

    private fun loadModels() {
        try {
            loadModelFile("models/anomaly_detector.tflite")?.let {
                anomalyDetector = Interpreter(it)
            }
            loadModelFile("models/voice_stress.tflite")?.let {
                voiceStressModel = Interpreter(it)
            }
            loadModelFile("models/mood_fusion.tflite")?.let {
                moodFusionModel = Interpreter(it)
            }
            Log.d("AiManager", "Models loaded where available.")
        } catch (e: Exception) {
            Log.e("AiManager", "Error loading models: ${e.message}")
        }
    }

    private fun loadModelFile(modelPath: String): MappedByteBuffer? {
        return try {
            val fileDescriptor = context.assets.openFd(modelPath)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        } catch (e: Exception) {
            Log.e("AiManager", "Model not found: $modelPath. Skipping.")
            null
        }
    }

    fun detectAnomaly(input: Array<FloatArray>): FloatArray {
        val output = Array(1) { FloatArray(2) }
        val model = anomalyDetector
        if (model == null) return floatArrayOf(0.9f, 0.1f) // Mock Safe
        model.run(input, output)
        return output[0]
    }

    fun analyzeVoiceStress(input: Array<Array<FloatArray>>): FloatArray {
        val output = Array(1) { FloatArray(3) }
        val model = voiceStressModel
        if (model == null) return floatArrayOf(1.0f, 0.0f, 0.0f) // Mock Calm
        model.run(input, output)
        return output[0]
    }

    fun calculateMoodScore(input: FloatArray): Float {
        val output = Array(1) { FloatArray(1) }
        val model = moodFusionModel
        if (model == null) return 0.8f // Mock Good Mood
        model.run(input, output)
        return output[0][0]
    }
}
