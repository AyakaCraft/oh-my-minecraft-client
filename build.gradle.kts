@file:Suppress("LocalVariableName")

plugins {
    id("net.fabricmc.fabric-loom-remap") version("1.18-SNAPSHOT") apply(false)
    id("net.fabricmc.fabric-loom") version("1.18-SNAPSHOT") apply(false)
    //id("dev.architectury.loom") version("1.18-SNAPSHOT") apply(false)
    id("com.replaymod.preprocess") version("c5abb4fb12")
    id("me.fallenbreath.yamlang") version("1.5.0") apply(false)
    id("com.hypherionmc.modutils.modpublisher") version("2.2.1") apply(false)
}

preprocess {
    // Fabric
    val mc11404_fabric = createNode("1.14.4-fabric", 1_14_04, "")
    val mc11502_fabric = createNode("1.15.2-fabric", 1_15_02, "")
    val mc11605_fabric = createNode("1.16.5-fabric", 1_16_05, "")
    val mc11701_fabric = createNode("1.17.1-fabric", 1_17_01, "")
    val mc11802_fabric = createNode("1.18.2-fabric", 1_18_02, "")
    val mc11904_fabric = createNode("1.19.4-fabric", 1_19_04, "")
    val mc12001_fabric = createNode("1.20.1-fabric", 1_20_01, "")
    val mc12006_fabric = createNode("1.20.6-fabric", 1_20_06, "")
    val mc12101_fabric = createNode("1.21.1-fabric", 1_21_01, "")
    val mc12103_fabric = createNode("1.21.3-fabric", 1_21_03, "")
    val mc12104_fabric = createNode("1.21.4-fabric", 1_21_04, "")
    val mc12105_fabric = createNode("1.21.5-fabric", 1_21_05, "")
    val mc12108_fabric = createNode("1.21.8-fabric", 1_21_08, "")
    val mc12110_fabric = createNode("1.21.10-fabric", 1_21_10, "")
    val mc12111_fabric = createNode("1.21.11-fabric", 1_21_11, "")

    val mc2601_fabric = createNode("26.1.2-fabric", 26_01_00, "")
    val mc2602_fabric = createNode("26.2-fabric", 26_02_00, "")
    val mc2603_fabric = createNode("26.3-fabric", 26_03_00, "")

    mc11404_fabric.link(mc11502_fabric, null)
    mc11502_fabric.link(mc11605_fabric, null)
    mc11605_fabric.link(mc11701_fabric, null)
    mc11701_fabric.link(mc11802_fabric, null)
    mc11802_fabric.link(mc11904_fabric, file("versions/mapping-1.18.2-1.19.4.txt"))
    mc11904_fabric.link(mc12001_fabric, null)
    mc12001_fabric.link(mc12006_fabric, null)
    mc12006_fabric.link(mc12101_fabric, null)
    mc12101_fabric.link(mc12103_fabric, null)
    mc12103_fabric.link(mc12104_fabric, null)
    mc12104_fabric.link(mc12105_fabric, file("versions/mapping-1.21.4-1.21.5.txt"))
    mc12105_fabric.link(mc12108_fabric, null)
    mc12108_fabric.link(mc12110_fabric, null)
    mc12110_fabric.link(mc12111_fabric, null)

    mc12111_fabric.link(mc2601_fabric, file("versions/mapping-1.21.11-26.1.txt"))
    mc2601_fabric.link(mc2602_fabric, null)
    mc2602_fabric.link(mc2603_fabric, file("versions/mapping-26.2-26.3.txt"))
    strictExtraMappings = false
}

tasks.register("cleanPreprocessSources") {
    group = "${property("mod.id")}"

    doFirst {
        subprojects {
            delete("build/preprocessed")
        }
    }
}

fun libsDir(p: Project): Directory {
    return p.layout.buildDirectory.dir("libs").get()
}

tasks.register("buildAndGather") {
    subprojects {
        dependsOn(tasks["build"])
    }
    doLast {
        println("Gathering builds")

        delete(fileTree(libsDir(rootProject)) { include("*") })
        subprojects {
            print("Copying files for ${name}...    ")
            val currentLibsDir = libsDir(this)
            copy {
                from(currentLibsDir) { include("*-${version}.jar") }
                into(libsDir(rootProject))
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
            println("Succeeded")
        }
    }
}
