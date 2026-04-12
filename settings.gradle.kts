pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		maven {
			name = "NeoForge"
			url = uri("https://maven.neoforged.net/releases/")
		}
		maven {
			name = "Architectury"
			url = uri("https://maven.architectury.dev/")
		}
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
	}

 plugins {
		id("net.fabricmc.fabric-loom-remap") version providers.gradleProperty("loom_version")
		id("net.neoforged.moddev") version "2.0.78-beta"
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.9.1-beta.5"
}

rootProject.name = "advanced-timer"

stonecutter {
	create(rootProject) {
		version("1.21.11-fabric", "1.21.11")
		version("1.21.11-neoforge", "1.21.11")
	}
}
