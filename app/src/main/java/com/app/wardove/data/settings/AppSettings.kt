package com.app.wardove.data.settings

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false,
    val laundryThreshold: Int = DEFAULT_LAUNDRY_THRESHOLD,
    val updateNotificationsEnabled: Boolean = true,
    val shakeToReportEnabled: Boolean = false
) {
    companion object {
        const val DEFAULT_LAUNDRY_THRESHOLD = 3
        const val MIN_LAUNDRY_THRESHOLD = 1
        const val MAX_LAUNDRY_THRESHOLD = 30

        val DEFAULT = AppSettings()
    }
}
