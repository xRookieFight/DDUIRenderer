plugins {
    id("java")
    id("io.freefair.lombok") version "8.4"
}

group = "com.xrookiefight.dduirenderer"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.powernukkitx.org/releases")
    maven("https://repo.opencollab.dev/maven-releases")
    maven("https://repo.opencollab.dev/maven-snapshots")
}

dependencies {
    compileOnly("org.powernukkitx:server:2.0.0-SNAPSHOT")
}

tasks.jar {
    archiveFileName.set("DDUIRenderer.jar")
}