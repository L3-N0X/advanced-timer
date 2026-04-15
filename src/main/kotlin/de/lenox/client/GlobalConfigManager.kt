package de.lenox.client

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.minecraft.client.Minecraft
import java.io.File

object GlobalConfigManager {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    var config = GlobalConfig()
    private var loaded = false
    
    var activeProfile: String = "default"
    var newProfileName: String = "" // Temporary for the UI

    private fun getProfilesDir(): File {
        return File(Minecraft.getInstance().gameDirectory, "config/advanced_timer/profiles")
    }

    private fun getMetaFile(): File {
        return File(Minecraft.getInstance().gameDirectory, "config/advanced_timer_meta.json")
    }

    private fun getConfigFile(profile: String): File {
        return File(getProfilesDir(), "$profile.json")
    }
    
    fun getAvailableProfiles(): List<String> {
        val dir = getProfilesDir()
        if (!dir.exists()) return listOf("default")
        val files = dir.listFiles { _, name -> name.endsWith(".json") }
        val names = files?.map { it.nameWithoutExtension }?.toMutableList() ?: mutableListOf()
        if (!names.contains("default")) names.add(0, "default")
        return names.sorted()
    }
    
    fun cycleProfile() {
        val profiles = getAvailableProfiles()
        val currentIndex = profiles.indexOf(activeProfile)
        val nextIndex = if (currentIndex + 1 >= profiles.size) 0 else currentIndex + 1
        switchProfile(profiles[nextIndex])
    }
    
    fun switchProfile(profile: String) {
        save() // save current
        activeProfile = profile
        saveMeta()
        loadProfile(profile)
    }

    fun createAndSwitchProfile(profile: String) {
        if (profile.isBlank()) return
        val safeName = profile.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        save() // save current
        activeProfile = safeName
        saveMeta()
        save() // save new profile to disk immediately
        loadProfile(safeName)
    }
    
    fun deleteCurrentProfile() {
        if (activeProfile == "default") return
        val file = getConfigFile(activeProfile)
        if (file.exists()) file.delete()
        switchProfile("default")
    }

    fun saveMeta() {
        val file = getMetaFile()
        file.parentFile.mkdirs()
        file.writeText(gson.toJson(mapOf("activeProfile" to activeProfile)))
    }

    fun loadMeta() {
        val file = getMetaFile()
        if (file.exists()) {
            try {
                val map = gson.fromJson(file.readText(), Map::class.java)
                activeProfile = map["activeProfile"] as? String ?: "default"
            } catch (e: Exception) {
                activeProfile = "default"
            }
        } else {
            activeProfile = "default"
            saveMeta()
        }
    }

    fun save() {
        val file = getConfigFile(activeProfile)
        file.parentFile.mkdirs()
        file.writeText(gson.toJson(config))
    }

    private fun loadProfile(profile: String) {
        val file = getConfigFile(profile)
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
    }

    fun load() {
        loadMeta()
        
        // Migrate old config if it exists and default doesn't
        val oldFile = File(Minecraft.getInstance().gameDirectory, "config/advanced_timer.json")
        if (oldFile.exists() && getAvailableProfiles().size == 1 && !getConfigFile("default").exists()) {
            try {
                config = gson.fromJson(oldFile.readText(), GlobalConfig::class.java) ?: GlobalConfig()
                activeProfile = "default"
                saveMeta()
                save()
                oldFile.delete()
            } catch (e: Exception) {
                e.printStackTrace()
                loadProfile(activeProfile)
            }
        } else {
            loadProfile(activeProfile)
        }
        
        loaded = true
    }

    fun ensureLoaded() {
        if (!loaded) {
            load()
        }
    }
}