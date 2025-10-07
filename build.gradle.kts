plugins {
    kotlin("jvm") version "2.2.0"
}

group = "org.exxuslee"
version = "1.0-SNAPSHOT"
val architecture = "windows_amd64"


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

    runtimeOnly("org.slf4j:slf4j-simple:2.0.12")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}