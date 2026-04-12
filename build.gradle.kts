plugins {
	id("idea")
	id("fabric-loom") version "1.16-SNAPSHOT" apply false
	id("net.neoforged.moddev") version "2.0.141" apply false
	id("maven-publish")
	id("me.modmuss50.mod-publish-plugin") version "1.1.0"
	id("org.jetbrains.kotlin.jvm") version "2.2.10"
	id("com.google.devtools.ksp") version "2.2.10-2.0.2"
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
		kotlin {
			if (isFabric) {
				srcDir("src/fabric/kotlin")
			} else if (isNeoForge) {
				srcDir("src/neoforge/kotlin")
			}
		}
		resources {
			if (isFabric) {
				srcDir("src/fabric/resources")
			} else if (isNeoForge) {
				srcDir("src/neoforge/resources")
			}
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
			create("advanced-timer") {
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

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}
}
