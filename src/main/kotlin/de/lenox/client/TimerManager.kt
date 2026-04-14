package de.lenox.client

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.minecraft.client.Minecraft
import java.io.File

object TimerManager {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private var currentData = TimerData()

    val isRunning: Boolean
        get() = currentData.isRunning

    val currentTimeMs: Long
        get() {
            if (currentData.isRunning) {
                return currentData.accumulatedTimeMs + (System.currentTimeMillis() - currentData.lastStartTimeMs)
            }
            return currentData.accumulatedTimeMs
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
        currentData = TimerData()
        save()
    }

    private fun getSaveFile(): File? {
        val mc = Minecraft.getInstance()
        val server = mc.singleplayerServer
        
        if (server != null) {
            // It's a singleplayer world
            val levelName = server.worldData.levelName.replace("[^a-zA-Z0-9.-]".toRegex(), "_")
            return File(mc.gameDirectory, "advanced_timer/saves/$levelName/timer.json")
        }
        
        val currentServer = mc.currentServer
        if (currentServer != null) {
            // It's a multiplayer server
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
