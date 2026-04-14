package de.lenox.client

data class TimerData(
    var isRunning: Boolean = false,
    var accumulatedTimeMs: Long = 0L,
    var lastStartTimeMs: Long = 0L
)
