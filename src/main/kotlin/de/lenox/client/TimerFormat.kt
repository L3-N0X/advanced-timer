package de.lenox.client

import kotlin.time.Duration.Companion.milliseconds

enum class TimerFormat(val pattern: String) {
    HH_MM("[dd:]hh:mm"),
    HH_MM_SS("[dd:]hh:mm:ss"),
    H_MM_SS("[h:]mm:ss"),
    TEXT("[d'd ']h'h 'm'm 's's'"),
    TEXT_PADDED("[d'd ']h'h 'mm'm 'ss's'");

    val displayName: String
        get() = format(7509000L) // Example: 2h 5m 9s

    fun format(ms: Long): String {
        return ms.milliseconds.toComponents { days, hours, minutes, seconds, _ ->
            var result = pattern
            
            // Handle optional blocks [...]
            val optionalBlockRegex = Regex("\\[(.*?)\\]")
            result = optionalBlockRegex.replace(result) { matchResult ->
                val inner = matchResult.groupValues[1]
                val needsShowing = (inner.contains("d") && days > 0) ||
                                   (inner.contains("h") && hours > 0) ||
                                   (inner.contains("m") && minutes > 0) ||
                                   (inner.contains("s") && seconds > 0)
                if (needsShowing) inner else ""
            }
            
            // Protect literal strings '...'
            val literals = mutableListOf<String>()
            result = Regex("'(.*?)'").replace(result) { matchResult ->
                literals.add(matchResult.groupValues[1])
                "\u0000${literals.size - 1}\u0001"
            }
            
            // Replace time units
            result = result.replace("dd", days.toString().padStart(2, '0'))
                           .replace("d", days.toString())
                           .replace("hh", hours.toString().padStart(2, '0'))
                           .replace("h", hours.toString())
                           .replace("mm", minutes.toString().padStart(2, '0'))
                           .replace("m", minutes.toString())
                           .replace("ss", seconds.toString().padStart(2, '0'))
                           .replace("s", seconds.toString())
                           
            // Restore literals
            for (i in literals.indices) {
                result = result.replace("\u0000${i}\u0001", literals[i])
            }
            
            result
        }
    }
}
