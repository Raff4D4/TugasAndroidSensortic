package com.zenspace.app.model

enum class FocusCategory(val label: String) {
    STUDY("Belajar"),
    CODING("Ngoding"),
    READING("Membaca"),
    DEEP_WORK("Deep Work")
}

enum class LightStatus(val label: String, val advice: String) {
    TOO_DARK("Terlalu Gelap", "Nyalakan lampu meja atau buka tirai agar mata tidak cepat lelah (< 100 lx)."),
    COMFORTABLE("Nyaman", "Pencahayaan cukup baik untuk membaca santai atau mencatat (100–300 lx)."),
    OPTIMAL("Ideal", "Intensitas cahaya optimal untuk fokus belajar dan bekerja (300–1000 lx)."),
    TOO_BRIGHT("Terlalu Silau", "Kurangi silau berlebih atau atur sudut layar agar tetap nyaman (> 1000 lx).")
}

data class FocusSession(
    val id: Long = 0L,
    val title: String,
    val category: String,
    val durationSeconds: Int,
    val averageLux: Float,
    val lightStatus: String,
    val distractionCount: Int,
    val stabilityScore: Int,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class SessionStats(
    val totalSessions: Int = 0,
    val totalSeconds: Int = 0,
    val averageLux: Float = 0f,
    val totalDistractions: Int = 0
)

data class SensorData(
    val lux: Float = 0f,
    val hasLightReading: Boolean = false,
    val isLightAvailable: Boolean = true,
    val accelDelta: Float = 0f,
    val hasAccelReading: Boolean = false,
    val isStable: Boolean = true,
    val isAccelAvailable: Boolean = true
) {
    val lightStatus: LightStatus
        get() = when {
            lux < 100f -> LightStatus.TOO_DARK
            lux < 300f -> LightStatus.COMFORTABLE
            lux <= 1000f -> LightStatus.OPTIMAL
            else -> LightStatus.TOO_BRIGHT
        }
}
