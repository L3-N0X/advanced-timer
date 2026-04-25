import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.kotlin.dsl.getByType

plugins {
    id("idea")
    id("fabric-loom") version "1.16-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT" apply false
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

// val isFabric = stonecutter.current.project.endsWith("-fabric")
// val isNeoForge = stonecutter.current.project.endsWith("-neoforge")
// val mcVersion = stonecutter.current.version
val isFabric = project.name.endsWith("-fabric")
val isNeoForge = project.name.endsWith("-neoforge")
val mcVersion = stonecutter.current.version  // This one is fine, version is global state

println("[${project.rootDir}] ${project.version} - Minecraft Version is $mcVersion")

stonecutter {
    val loader = when {
        project.name.endsWith("-fabric") -> "fabric"
        project.name.endsWith("-neoforge") -> "neoforge"
        else -> error("Unknown loader for project ${project.name}")
    }
    constants.match(loader, "fabric", "neoforge")
}

sourceSets {
    main {
        if (isFabric) {
        	kotlin.srcDir(rootProject.file("src/fabric/kotlin"))
        	resources.srcDir(rootProject.file("src/fabric/resources"))
        } else
        if (isNeoForge) {
            kotlin.srcDir(rootProject.file("src/neoforge/kotlin"))
            resources.srcDir(rootProject.file("src/neoforge/resources"))
        }
    }
}

if (isFabric) {
    apply(
        plugin = if (mcVersion == "1.21.11") {
            "fabric-loom"
        } else {
            "net.fabricmc.fabric-loom"
        }
    )
    apply(plugin = "dev.kikugie.fletching-table.fabric")
} else if (isNeoForge) {
    apply(plugin = "net.neoforged.moddev")
}

data class FabricVersionSet(
    val loader: String,
    val fabricApi: String,
    val fabricKotlin: String,
    val yacl: String,
    val modmenu: String
)

val fabricVersions = when (mcVersion) {
    "1.21.11" -> FabricVersionSet(
        loader = "0.18.6",
        fabricApi = "0.141.3+1.21.11",
        fabricKotlin = "1.13.5+kotlin.2.2.10",
        yacl = "3.8.1+1.21.11",
        modmenu = "17.0.0"
    )

    "26.1.2" -> FabricVersionSet(
        loader = "0.18.6",
        fabricApi = "0.146.1+26.1.2",
        fabricKotlin = "1.13.10+kotlin.2.3.20",
        yacl = "3.9.3+26.1",
        modmenu = "18.0.0-alpha.8"
    )

    else -> error("No Fabric versions configured for $mcVersion")
}

data class NeoForgeVersionSet(
    val yacl: String,
    val kotlinforforge: String,
    val neoforgeVersion: String,
    val neoforgeVersionRange: String,
    val minecraftVersionRange: String,
    val yaclVersionRange: String
)

val neoforgeVersions = when (mcVersion) {
    "1.21.11" -> NeoForgeVersionSet(
        yacl = "3.8.1+1.21.11",
        kotlinforforge = "6.2.0",
        neoforgeVersion = "21.11.42",
        neoforgeVersionRange = "[21.11,)",
        minecraftVersionRange = "[1.21.11,)",
        yaclVersionRange = "[3.8.1,)"
    )

    "26.1.2" -> NeoForgeVersionSet(
        yacl = "3.9.3+26.1",
        kotlinforforge = "6.2.0",
        neoforgeVersion = "26.1.2.29-beta",
        neoforgeVersionRange = "[26.1,)",
        minecraftVersionRange = "[26.1,)",
        yaclVersionRange = "[3.9.2,)"
    )

    else -> error("No NeoForge versions configured for $mcVersion")
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
        version = neoforgeVersions.neoforgeVersion
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
        add("minecraft", "com.mojang:minecraft:$mcVersion")

        if (mcVersion == "1.21.11") {
            add("mappings", project.extensions.getByType<LoomGradleExtensionAPI>().officialMojangMappings())

            add("modImplementation", "net.fabricmc:fabric-loader:${fabricVersions.loader}")
            add("modImplementation", "net.fabricmc.fabric-api:fabric-api:${fabricVersions.fabricApi}")
            add("modImplementation", "net.fabricmc:fabric-language-kotlin:${fabricVersions.fabricKotlin}")
            add("modImplementation", "dev.isxander:yet-another-config-lib:${fabricVersions.yacl}-fabric")
            add("modImplementation", "com.terraformersmc:modmenu:${fabricVersions.modmenu}")
        } else {
            add("implementation", "net.fabricmc:fabric-loader:${fabricVersions.loader}")
            add("implementation", "net.fabricmc.fabric-api:fabric-api:${fabricVersions.fabricApi}")
            add("implementation", "net.fabricmc:fabric-language-kotlin:${fabricVersions.fabricKotlin}")
            add("implementation", "dev.isxander:yet-another-config-lib:${fabricVersions.yacl}-fabric")
            add("implementation", "com.terraformersmc:modmenu:${fabricVersions.modmenu}")
        }
    } else if (isNeoForge) {
        "implementation"("dev.isxander:yet-another-config-lib:${neoforgeVersions.yacl}-neoforge")
        "implementation"("thedarkcolour:kotlinforforge-neoforge:${neoforgeVersions.kotlinforforge}")
    }
}

val minecraftDependency = when {
    mcVersion == "1.21.11" -> ">=1.21.11 <1.22"
    mcVersion.startsWith("26.1") -> ">=26.1 <26.2"
    else -> error("No minecraft dependency range configured for $mcVersion")
}

tasks.withType<ProcessResources>().configureEach {
    inputs.property("version", project.version)
    inputs.property("minecraft_dependency", minecraftDependency)

    if (isFabric) {
        inputs.property("loader_version", fabricVersions.loader)
        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version,
                "minecraft_dependency" to minecraftDependency,
                "loader_version" to fabricVersions.loader
            )
        }
    } else if (isNeoForge) {
        inputs.property("neoforge_version_range", neoforgeVersions.neoforgeVersion)
        inputs.property("minecraft_version_range", neoforgeVersions.minecraftVersionRange)
        inputs.property("yacl_version_range", neoforgeVersions.yaclVersionRange)
        inputs.property("kotlinforforge_version", neoforgeVersions.kotlinforforge)
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(
                "version" to project.version,
                "neoforge_version_range" to neoforgeVersions.neoforgeVersionRange,
                "minecraft_version_range" to neoforgeVersions.minecraftVersionRange,
                "yacl_version_range" to neoforgeVersions.yaclVersionRange,
                "kotlinforforge_version" to neoforgeVersions.kotlinforforge
            )
        }
    }
}

val javaVersion = when (mcVersion) {
    "1.21.11" -> JavaVersion.VERSION_21
    "26.1.2" -> JavaVersion.VERSION_25
    else -> error("No Java version configured for $mcVersion")
}

val kotlinJvmTarget = when (mcVersion) {
    "1.21.11" -> org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
    "26.1.2" -> org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25
    else -> error("No Kotlin JVM target configured for $mcVersion")
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(
        when (mcVersion) {
            "1.21.11" -> 21
            "26.1.2" -> 25
            else -> error("No Java release configured for $mcVersion")
        }
    )
}

kotlin {
    compilerOptions {
        jvmTarget.set(kotlinJvmTarget)
    }
}


java {
    withSourcesJar()
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
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
        if (mcVersion == "1.21.11") {
            file.set(tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar").get().archiveFile)
        } else {
            file.set(tasks.named<Jar>("jar").get().archiveFile)
        }
        modLoaders.add("fabric")
    } else if (isNeoForge) {
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
                requires("yacl")
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
