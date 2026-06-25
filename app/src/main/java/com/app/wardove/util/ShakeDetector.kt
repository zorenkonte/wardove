package com.app.wardove.util

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.sqrt

/**
 * Detects a shake gesture via the accelerometer.
 *
 * Uses a simple high-pass filter to isolate linear acceleration from gravity,
 * then fires [onShake] when the resultant magnitude exceeds [threshold] m/s²
 * and at least [debounceMs] has elapsed since the last detected shake.
 */
class ShakeDetector(
    private val threshold: Float = 12f,
    private val debounceMs: Long = 1_000L,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val gravity = FloatArray(3)
    private var lastShakeTime = 0L

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        // High-pass filter: isolate linear acceleration from gravity
        val alpha = 0.8f
        gravity[0] = alpha * gravity[0] + (1f - alpha) * event.values[0]
        gravity[1] = alpha * gravity[1] + (1f - alpha) * event.values[1]
        gravity[2] = alpha * gravity[2] + (1f - alpha) * event.values[2]

        val x = event.values[0] - gravity[0]
        val y = event.values[1] - gravity[1]
        val z = event.values[2] - gravity[2]

        val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val now = System.currentTimeMillis()
        if (acceleration > threshold && now - lastShakeTime > debounceMs) {
            lastShakeTime = now
            onShake()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
