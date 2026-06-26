package com.app.wardove.util

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.sqrt

/**
 * Detects a shake gesture via the accelerometer.
 *
 * Requires [shakeCount] acceleration spikes above [threshold] m/s² within
 * [windowMs] to fire [onShake]. Mirrors the FB/Instagram shake-to-report feel:
 * a single jolt or gentle bump is ignored; a deliberate multi-directional shake fires it.
 */
class ShakeDetector(
    private val threshold: Float = 25f,
    private val debounceMs: Long = 1_500L,
    private val shakeCount: Int = 3,
    private val windowMs: Long = 1_500L,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val gravity = FloatArray(3)
    private var lastSpike = 0L
    private var lastFired = 0L
    private var shakeWindowStart = 0L
    private var shakesInWindow = 0

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val alpha = 0.8f
        gravity[0] = alpha * gravity[0] + (1f - alpha) * event.values[0]
        gravity[1] = alpha * gravity[1] + (1f - alpha) * event.values[1]
        gravity[2] = alpha * gravity[2] + (1f - alpha) * event.values[2]

        val x = event.values[0] - gravity[0]
        val y = event.values[1] - gravity[1]
        val z = event.values[2] - gravity[2]

        val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val now = System.currentTimeMillis()
        if (acceleration > threshold && now - lastSpike > 300L) {
            lastSpike = now
            if (now - shakeWindowStart > windowMs) {
                shakeWindowStart = now
                shakesInWindow = 1
            } else {
                shakesInWindow++
            }
            if (shakesInWindow >= shakeCount && now - lastFired > debounceMs) {
                lastFired = now
                shakesInWindow = 0
                shakeWindowStart = 0L
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
