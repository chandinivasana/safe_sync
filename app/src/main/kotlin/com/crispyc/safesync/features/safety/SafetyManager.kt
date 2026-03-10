package com.crispyc.safesync.features.safety

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaRecorder
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

@Singleton
class SafetyManager @Inject constructor(
    @ApplicationContext private val context: Context
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    private var lastShakeTime: Long = 0
    private var shakeCount = 0
    private val SHAKE_THRESHOLD = 15f // Lower threshold for easier testing
    private val SHAKE_COOLDOWN = 300L // 300ms minimum between distinct shakes
    private val SHAKE_INTERVAL = 2000L // 2 seconds to complete the shakes
    private val MIN_SHAKE_COUNT = 3

    private val _sosTriggered = MutableSharedFlow<Unit>()
    val sosTriggered = _sosTriggered.asSharedFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gForce = sqrt(x * x + y * y + z * z)
            if (gForce > SHAKE_THRESHOLD) {
                val currentTime = System.currentTimeMillis()
                // Ignore events that are too close to each other (same physical shake)
                if (currentTime - lastShakeTime > SHAKE_COOLDOWN) {
                    if (currentTime - lastShakeTime < SHAKE_INTERVAL) {
                        shakeCount++
                    } else {
                        shakeCount = 1
                    }
                    lastShakeTime = currentTime
                    
                    if (shakeCount >= MIN_SHAKE_COUNT) {
                        triggerSos()
                        shakeCount = 0
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun triggerSos() {
        CoroutineScope(Dispatchers.Main).launch {
            _sosTriggered.emit(Unit)
            startRecording()
        }
    }

    private fun startRecording() {
        val fileName = "sos_audio_${System.currentTimeMillis()}.m4a"
        audioFile = File(context.filesDir, fileName)
        
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile?.absolutePath)
            
            try {
                prepare()
                start()
                Log.d("SafetyManager", "Started SOS audio recording")
            } catch (e: IOException) {
                Log.e("SafetyManager", "Failed to start recording: ${e.message}")
            }
        }
        
        // Auto-stop after 30 seconds as per PRD
        CoroutineScope(Dispatchers.IO).launch {
            kotlinx.coroutines.delay(30000)
            stopRecording()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            Log.d("SafetyManager", "Stopped SOS audio recording")
        } catch (e: Exception) {
            Log.e("SafetyManager", "Error stopping recorder: ${e.message}")
        }
    }
    
    fun getAudioRef(): String? = audioFile?.absolutePath
}
