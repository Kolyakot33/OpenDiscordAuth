
plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")

    maven("https://jitpack.io")
    maven("https://m2.dv8tion.net/releases")

}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
    implementation("org.json:json:20210307")
    implementation("net.dv8tion:JDA:4.3.0_277")
}


