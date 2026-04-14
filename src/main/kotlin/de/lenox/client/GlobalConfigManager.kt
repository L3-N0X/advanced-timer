package de.lenox.client

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.minecraft.client.Minecraft
import java.io.File

object GlobalConfigManager {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    var config = GlobalConfig()
    private var loaded = false

    private fun getConfigFile(): File {
        return File(Minecraft.getInstance().gameDirectory, "config/advanced_timer.json")
    }

    fun save() {
        val file = getConfigFile()
        file.parentFile.mkdirs()
        file.writeText(gson.toJson(config))
    }

    fun load() {
        val file = getConfigFile()
        if (file.exists()) {
            try {
                config = gson.fromJson(file.readText(), GlobalConfig::class.java) ?: GlobalConfig()
            } catch (e: Exception) {
                e.printStackTrace()
                config = GlobalConfig()
            }
        } else {
            config = GlobalConfig()
            save()
        }
        loaded = true
    }

    fun ensureLoaded() {
        if (!loaded) {
            load()
        }
    }
}