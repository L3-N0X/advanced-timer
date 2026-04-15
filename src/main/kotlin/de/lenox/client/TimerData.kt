package de.lenox.client

data class TimerData(
    var isRunning: Boolean = false,
    var accumulatedTimeMs: Long = 0L,
    var lastStartTimeMs: Long = 0L,
    var direction: TimerDirection = TimerDirection.UP,
    var countdownHours: Int = 0,
    var countdownMinutes: Int = 0,
    var countdownSeconds: Int = 0
) {
    val countdownStartTimeMs: Long
        get() = (countdownHours * 3600000L) + (countdownMinutes * 60000L) + (countdownSeconds * 1000L)
}
