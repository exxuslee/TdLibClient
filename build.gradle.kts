import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "org.exxuslee"
version = ""
//val architecture = "windows_amd64"
val architecture = "linux_arm64_gnu_ssl3"


repositories {
    mavenCentral()
    maven { url = uri("https://mvn.mchv.eu/repository/mchv/") }

}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation(platform("it.tdlight:tdlight-java-bom:3.4.0+td.1.8.26"))
    implementation("it.tdlight:tdlight-java")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    // Replace classifier with your OS: e.g. macos_x86_64, linux_amd64_gnu_ssl1, windows_x86_64
    implementation("it.tdlight:tdlight-natives") {
        artifact {
            classifier = architecture
        }
    }
    implementation("org.telegram:telegrambots-client:9.0.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    implementation("ch.qos.logback:logback-classic:1.5.12")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("org.exxuslee.MainKt")
}

tasks.withType<ShadowJar> {
    manifest {
        attributes["Main-Class"] = "org.exxuslee.MainKt"
    }
}