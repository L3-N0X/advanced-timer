package de.lenox.client

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.minecraft.client.Minecraft
import java.io.File

object TimerManager {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    var currentData = TimerData()

    val isRunning: Boolean
        get() = currentData.isRunning

    val currentTimeMs: Long
        get() {
            var elapsedMs = currentData.accumulatedTimeMs
            if (currentData.isRunning) {
                elapsedMs += (System.currentTimeMillis() - currentData.lastStartTimeMs)
            }
            if (currentData.direction == TimerDirection.DOWN) {
                return kotlin.math.max(0L, currentData.countdownStartTimeMs - elapsedMs)
            }
            return elapsedMs
        }

    fun start() {
        if (!currentData.isRunning) {
            currentData.isRunning = true
            currentData.lastStartTimeMs = System.currentTimeMillis()
            save()
        }
    }

    fun pause() {
        if (currentData.isRunning) {
            currentData.isRunning = false
            currentData.accumulatedTimeMs += System.currentTimeMillis() - currentData.lastStartTimeMs
            save()
        }
    }

    fun reset() {
        val config = GlobalConfigManager.config
        currentData = TimerData(
            direction = config.timerDirection,
            countdownHours = config.countdownHours,
            countdownMinutes = config.countdownMinutes,
            countdownSeconds = config.countdownSeconds
        )
        save()
    }

    fun setTimeMs(ms: Long) {
        val targetMs = kotlin.math.max(0L, ms)
        val runningOffset =
            if (currentData.isRunning) (System.currentTimeMillis() - currentData.lastStartTimeMs) else 0L
        if (currentData.direction == TimerDirection.DOWN) {
            currentData.accumulatedTimeMs = currentData.countdownStartTimeMs - targetMs - runningOffset
        } else {
            currentData.accumulatedTimeMs = targetMs - runningOffset
        }
        save()
    }

    fun addTimeMs(ms: Long) {
        setTimeMs(currentTimeMs + ms)
    }

    fun subtractTimeMs(ms: Long) {
        setTimeMs(currentTimeMs - ms)
    }

    private fun getSaveFile(): File? {
        val mc = Minecraft.getInstance()
        val server = mc.singleplayerServer

        if (server != null) { // It's a singleplayer world
            val levelName = server.worldData.levelName.replace("[^a-zA-Z0-9.-]".toRegex(), "_")
            return File(mc.gameDirectory, "advanced_timer/saves/$levelName/timer.json")
        }

        val currentServer = mc.currentServer
        if (currentServer != null) { // It's a multiplayer server
            val ip = currentServer.ip.replace("[^a-zA-Z0-9.-]".toRegex(), "_")
            return File(mc.gameDirectory, "advanced_timer/servers/$ip/timer.json")
        }

        return null
    }

    fun save() {
        val file = getSaveFile() ?: return
        file.parentFile.mkdirs()
        file.writeText(gson.toJson(currentData))
    }

    fun load() {
        val file = getSaveFile() ?: return
        if (file.exists()) {
            try {
                currentData = gson.fromJson(file.readText(), TimerData::class.java) ?: TimerData()
            } catch (e: Exception) {
                e.printStackTrace()
                currentData = TimerData()
            }
        } else {
            currentData = TimerData()
        }
    }
}
