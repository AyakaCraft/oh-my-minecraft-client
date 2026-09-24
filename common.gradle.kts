import java.util.*

plugins {
    `maven-publish`
    id("net.fabricmc.fabric-loom") version("1.17-SNAPSHOT")
    id("com.replaymod.preprocess") version ("c5abb4fb12")
    id("me.fallenbreath.yamlang") version("1.5.0")
    id("com.hypherionmc.modutils.modpublisher") version("2.2.1")
}

val modPlatform = "fabric"
//boolean fabricLike = modPlatform == "fabric" || modPlatform == "quilt"
//boolean forgeLike = modPlatform == "forge" || modPlatform == "neoforge"
val mcVersion = property("mcVersion") as Int

preprocess {
    //vars.put("FORGE", modPlatform == "forge" ? 1 : 0)
    //vars.put("NEO_FORGE", modPlatform == "neoforge" ? 1 : 0)
    //vars.put("FABRIC_LIKE", fabricLike ? 1 : 0)
    //vars.put("FORGE_LIKE", forgeLike ? 1 : 0)
}

val javaCompatibility = JavaVersion.VERSION_25

val jitpack = System.getenv("JITPACK") == "true"
val releasing = System.getenv("BUILD_RELEASE") == "true"
val ci = jitpack || releasing

val modId = property("mod.id")

repositories {
    mavenLocal()

    maven {
        name = "NeoForge"
        url = uri("https://maven.neoforged.net/releases/")
    }

    maven {
        name = "Curse Maven"
        url = uri("https://www.cursemaven.com")
    }

    maven {
        name = "Modrinth Maven"
        url = uri("https://api.modrinth.com/maven")
    }

    maven {
        name = "Jitpack Maven"
        url = uri("https://www.jitpack.io")
    }

    maven {
        name = "Nyan Maven"
        url = uri("https://maven.hendrixshen.top/releases")
    }

    mavenCentral()
}

dependencies {
    // Development environment
    minecraft("com.mojang:minecraft:${property("dependencies.minecraft_version")}")

    // Annotation processor
    compileOnly("org.projectlombok:lombok:${property("dependencies.lombok_version")}")
    annotationProcessor("org.projectlombok:lombok:${property("dependencies.lombok_version")}")

    // Dependency
    api((annotationProcessor("top.hendrixshen.magiclib:magiclib-all-${name}:${property("dependencies.magiclib_version")}") as Dependency))

    //switch (modPlatform) {
    //    case "fabric":
    //        break
    //    case "forge":
    //        forge("net.minecraftforge:forge:${property("dependencies.minecraft_version")}-${property("dependencies.forge_version")}")
    //        break
    //    case "neoforge":
    //        neoForge("net.neoforged:neoforge:${property("dependencies.neoforge_version")}")
    //        break
    //}
}

loom {
    //silentMojangMappingsLicense()

    // accessWidenerPath.set(file("src/main/resources/${property("mod.id")}.accesswidener"))

    //if (modPlatform == "forge") {
    //    forge {
    //        convertAccessWideners.set(true)
    //        mixinConfig("${property("mod.id")}.mixins.json")
    //    }
    //}

    runConfigs.configureEach {
        // Dump modified classes automatically.
        systemProperties.put("mixin.debug.export", "true")
    }

    runConfigs.named("client") {
        programArguments.addAll(
            "--width", "1920",
            "--height", "1080",
            "--username", "dev"
        )
        jvmArguments.addAll(
            "-Dmagiclib.debug=true",
            "-Dmagiclib.dev.qol=true",
            "-Dmagiclib.dev.qol.dfu.destroy=true"
        )
        runDirectory = file("run/client")
        generateRunConfig = true
    }

    runConfigs.named("server") {
        jvmArguments.addAll(
            "-Dmagiclib.debug=true",
            "-Dmagiclib.dev.qol=true",
            "-Dmagiclib.dev.qol.dfu.destroy=true"
        )
        runDirectory = file("run/server")
    }

    //if (fabricLike) {
    runs {
        register("mixinAuditClient") {
            inherit(runConfigs["client"])
            jvmArguments.add("-Dmagiclib.debug.mixinAuditor.enable=true")
            generateRunConfig = false
            runDirectory = file("run/client")
        }

        register("mixinAuditServer") {
            inherit(runConfigs["server"])
            jvmArguments.add("-Dmagiclib.debug.mixinAuditor.enable=true")
            generateRunConfig = false
            runDirectory = file("run/server")
        }
    }
    //}
}

// Setup client default settings.
tasks.runClient {
    defaultCharacterEncoding = "UTF-8"

    doFirst {
        file("run/client/config").mkdirs()
        with(file("run/client/options.txt")) {
            if (!exists()) {
                parentFile.mkdirs()
                val lines = listOf(
                    "autoJump:false",
                    "enableVsync:false",
                    "forceUnicodeFont:true",
                    "fov:1.0",
                    "gamma:16.0",
                    "guiScale:3",
                    "lang:${Locale.getDefault().toString().lowercase()}",
                    "maxFps:260",
                    "renderDistance:10",
                    "soundCategory_master:0.0"
                )
                writeText(lines.joinToString(separator = "\n") + "\n")
            }
        }
    }
}

// Setup server default settings.
tasks.runServer {
    defaultCharacterEncoding = "UTF-8"

    doFirst {
        // Agree eula before server init.
        with(file("run/server/eula.txt")) {
            if (!exists()) {
                parentFile.mkdirs()
                val lines = listOf(
                    "#By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).",
                    "#${Date()}",
                    "eula=true"
                )
                writeText(lines.joinToString(separator = "\n") + "\n")
            }
        }
    }
}

var modVersion = property("mod.version").toString()
val modVersionType = if (hasProperty("mod.version.type")) {
    when (property("mod.version.type").toString().lowercase(Locale.ROOT)) {
        "beta" -> "beta"
        "alpha" -> "alpha"
        else -> "release"
    }
} else {
    "release"
}
if (modVersionType != "release") {
    modVersion += "-$modVersionType"
}

val archivesBaseName = property("mod.archives_base_name").toString()
var modVersionSuffix = ""
val artifactVersion = modVersion
var artifactVersionSuffix = ""
// detect github action environment variables
// https://docs.github.com/en/actions/learn-github-actions/environment-variables#default-environment-variables
if (!releasing) {
    modVersionSuffix += "-SNAPSHOT"
    artifactVersionSuffix = "-SNAPSHOT"  // A non-release artifact is always a SNAPSHOT artifact
}
val fullModVersion = modVersion + modVersionSuffix
var fullProjectVersion: String
var fullArtifactVersion: String

// Example version values:
//   project.mod_version     1.0.3                      (the base mod version)
//   modVersionSuffix        +build.88                  (use github action build number if possible)
//   artifactVersionSuffix   -SNAPSHOT
//   fullModVersion          1.0.3+build.88             (the actual mod version to use in the mod)
//   fullProjectVersion      v1.0.3-mc1.15.2+build.88   (in build output jar name)
//   fullArtifactVersion     1.0.3-mc1.15.2-SNAPSHOT    (maven artifact version)

if (jitpack) {
    // move mc version into archivesBaseName, so jitpack will be able to organize archives from multiple subprojects correctly
    base.archivesName = "${archivesBaseName}-mc${name}"
    fullProjectVersion = "v${modVersion}${modVersionSuffix}"
    fullArtifactVersion = artifactVersion + artifactVersionSuffix
} else {
    base.archivesName = archivesBaseName
    fullProjectVersion = "v${modVersion}-mc${name}${modVersionSuffix}"
    fullArtifactVersion = "${modVersion}-mc${name}${artifactVersionSuffix}"
}
version = fullProjectVersion

sourceSets {
    register("dummy") {
        compileClasspath += main.get().compileClasspath
    }

    main {
        compileClasspath += sourceSets["dummy"].output
    }
}

java {
    sourceCompatibility = javaCompatibility
    targetCompatibility = javaCompatibility

    withSourcesJar()
}

val mixinFilePath = "${modId}.mixins.json"

val modMeta = mapOf(
    "magiclib_dependency"  to property("dependencies.magiclib_dependency"),
    "minecraft_dependency" to property("dependencies.minecraft_dependency"),
    "mod_alias"            to modId,
    "mod_description"      to property("mod.description"),
    "mod_homepage"         to property("mod.homepage"),
    "mod_id"               to modId,
    "mod_license"          to property("mod.license"),
    "mod_name"             to property("mod.name"),
    "mod_sources"          to property("mod.sources"),
    "mod_version"          to fullModVersion
)

tasks.processResources {
    outputs.upToDateWhen { false }

    mapOf(
        "fabric.mod.json"             to listOf("fabric"),
        "META-INF"                    to listOf("forge", "neoforge"),
        "META-INF/mods.toml"          to listOf("forge", "neoforge", if (mcVersion < 12005) "neoforge" else "none"),
        "META-INF/neoforge.mods.toml" to listOf(if (mcVersion > 12004) "neoforge" else "none")
    ).forEach { (file, platforms) ->
        if (platforms.contains(modPlatform)) {
            filesMatching(file) {
                expand(modMeta)
            }
        } else {
            exclude(file)
        }
    }

    filesMatching(mixinFilePath) {
        expand(mapOf(
            "MIXIN_COMPATIBILITY_LEVEL" to "JAVA_${javaCompatibility.majorVersion}"
        ))
    }

    from(rootProject.file("LICENSE"))
    from(rootProject.file("icon.png")) {
        //if (fabricLike) {
        into("assets/${modId}")
        //}
    }
}

yamlang {
    targetSourceSets = listOf(sourceSets["main"])
    inputDir = "assets/${modId}/lang"
}

tasks.withType<PublishToMavenRepository> {
    val predicate = provider {
        repository == publishing.repositories.mavenLocal() ||
                (repository == publishing.repositories["projectLocalSnapshot"] && publication == publishing.publications["snapshot"]) ||
                (repository == publishing.repositories["projectLocalRelease"] && publication == publishing.publications["release"])
    }

    onlyIf {
        predicate.get()
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.register("cleanRuns") {
    doLast {
        file(loom.runConfigs["client"].runDirectory).parentFile.deleteRecursively()
    }
}

tasks.register("cleanRunClient") {
    doLast {
        file(loom.runConfigs["client"].runDirectory).deleteRecursively()
    }
}

tasks.register("cleanRunServer") {
    doLast {
        file(loom.runConfigs["server"].runDirectory).deleteRecursively()
    }
}

listOf(
    "cleanRuns", "cleanRunClient", "cleanRunServer",
    "runClient", "runServer",
    "runMixinAuditClient", "runMixinAuditServer",
    "preprocessCode", "preprocessResources",
    "preprocessTestCode", "preprocessTestResources"
).forEach { taskName ->
    if (tasks.names.contains(taskName)) {
        tasks.named(taskName) {
            group = "$modId"
        }
    }
}

val minecraftVersions = property("game_versions").toString().split("\n")

// https://github.com/firstdarkdev/modpublisher
publisher {

    apiKeys {
        github(System.getenv("GITHUB_TOKEN") ?: "unset")
    }

    // debug = true

    versionType = property("mod.version.type").toString()
    projectVersion = fullProjectVersion

    gameVersions = minecraftVersions
    loaders = listOf("fabric")

    artifact.set(tasks.jar)

    github {
        repo(System.getenv("REPO"))
        tag(System.getenv("TAG"))
    }

}
