plugins {
	id("idea")
	id("fabric-loom") version "1.16-SNAPSHOT" apply false
	id("net.neoforged.moddev") version "2.0.141" apply false
	id("maven-publish")
	id("me.modmuss50.mod-publish-plugin") version "1.1.0"
	id("org.jetbrains.kotlin.jvm") version "2.3.20"
	id("com.google.devtools.ksp") version "2.3.6"
	id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.23" apply false
	id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.23" apply false
}

val modVersion: String = providers.gradleProperty("mod_version").get()
val mavenGroup: String = providers.gradleProperty("maven_group").get()

version = modVersion
group = mavenGroup

val isFabric = stonecutter.current.project.endsWith("-fabric")
val isNeoForge = stonecutter.current.project.endsWith("-neoforge")

sourceSets {
	main {
		if (isFabric) {
			kotlin.srcDir("src/fabric/kotlin")
			resources.srcDir("src/fabric/resources")
		} else if (isNeoForge) {
			kotlin.srcDir("src/neoforge/kotlin")
			resources.srcDir("src/neoforge/resources")
		}
	}
}

if (isFabric) {
	apply(plugin = "fabric-loom")
	apply(plugin = "dev.kikugie.fletching-table.fabric")
} else if (isNeoForge) {
	apply(plugin = "net.neoforged.moddev")
	apply(plugin = "dev.kikugie.fletching-table.neoforge")
}

repositories {
	maven {
		name = "Xander Maven"
		url = uri("https://maven.isxander.dev/releases")
	}
	maven {
		name = "TerraformersMC Maven"
		url = uri("https://maven.terraformersmc.com/releases")
	}
	maven {
		name = "Kotlin for Forge"
		url = uri("https://thedarkcolour.github.io/KotlinForForge/")
	}
}

if (isFabric) {
	configure<net.fabricmc.loom.api.LoomGradleExtensionAPI> {
		mods {
			create("advanced-timer") {
				sourceSet(sourceSets.main.get())
			}
		}
	}
} else if (isNeoForge) {
	configure<net.neoforged.moddevgradle.dsl.NeoForgeExtension> {
		version = providers.gradleProperty("neoforge_version").get()
		runs {
			create("client") {
				client()
			}
		}
		mods {
			create("advanced_timer") {
				sourceSet(sourceSets.main.get())
			}
		}
	}
}

dependencies {
	if (isFabric) {
		"minecraft"("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
		"mappings"(project.extensions.getByType<net.fabricmc.loom.api.LoomGradleExtensionAPI>().officialMojangMappings())
		"modImplementation"("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")
		"modImplementation"("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")
		"modImplementation"("net.fabricmc:fabric-language-kotlin:${providers.gradleProperty("fabric_kotlin_version").get()}")
		"modImplementation"("dev.isxander:yet-another-config-lib:${providers.gradleProperty("yacl_version").get()}-fabric")
		"modImplementation"("com.terraformersmc:modmenu:17.0.0")
	} else if (isNeoForge) {
		"implementation"("dev.isxander:yet-another-config-lib:${providers.gradleProperty("yacl_version").get()}-neoforge")
		"implementation"("thedarkcolour:kotlinforforge-neoforge:6.2.0")
	}
}

tasks.withType<ProcessResources>().configureEach {
	inputs.property("version", project.version)

	if (isFabric) {
		filesMatching("fabric.mod.json") {
			expand("version" to project.version)
		}
	} else if (isNeoForge) {
		filesMatching("META-INF/neoforge.mods.toml") {
			expand("version" to project.version)
		}
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(21)
}

kotlin {
	compilerOptions {
		jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
	}
}

java {
	withSourcesJar()
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.named<Jar>("jar").configure {
	inputs.property("projectName", project.name)
	from("LICENSE") {
		rename { "${it}_${project.name}" }
	}
}

publishMods {
	// 1. Set the release file based on the active loader
	if (isFabric) {
		// Fabric Loom outputs the final mod to the remapJar task
		file.set(tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar").get().archiveFile)
		modLoaders.add("fabric")
	} else if (isNeoForge) {
		// NeoForge ModDevGradle outputs the final mod to the standard jar task
		file.set(tasks.named<Jar>("jar").get().archiveFile)
		modLoaders.add("neoforge")
	}

	// 2. Read the changelog file from your root project folder
	val changelogFile = rootProject.file("CHANGELOG.md")
	if (changelogFile.exists()) {
		changelog.set(changelogFile.readText())
	} else {
		changelog.set("No changelog provided for this release.")
	}

	// Set release type (STABLE, BETA, or ALPHA)
	type.set(me.modmuss50.mpp.ReleaseType.STABLE)

	val mcVersion = providers.gradleProperty("minecraft_version").get()

	// 3. Modrinth Configuration
	val modrinthToken = providers.environmentVariable("MODRINTH_TOKEN")
	if (modrinthToken.isPresent) {
		modrinth {
			accessToken.set(modrinthToken)
			projectId.set("Lgo2vvWd") // Replace with your actual project ID
			minecraftVersions.add(mcVersion)

			// Adapt dependencies based on the loader
			if (isFabric) {
				requires("fabric-api")
				requires("yacl") // Make sure the slug matches Modrinth's URL exactly
				optional("modmenu")
			} else if (isNeoForge) {
				requires("yacl")
			}
		}
	}

	// 4. CurseForge Configuration (Optional, if you also want to publish there)
	val curseforgeToken = providers.environmentVariable("CURSEFORGE_TOKEN")
	if (curseforgeToken.isPresent) {
		curseforge {
			accessToken.set(curseforgeToken)
			projectId.set("YOUR_CURSEFORGE_ID_HERE")
			minecraftVersions.add(mcVersion)

			if (isFabric) {
				requires("fabric-api")
				requires("yacl")
			} else if (isNeoForge) {
				requires("yacl")
			}
		}
	}
}
