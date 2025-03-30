import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.1.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.kittizz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:9.5")
    implementation("org.ow2.asm:asm-commons:9.5")
    implementation("org.ow2.asm:asm-util:9.5")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}

tasks.withType<ShadowJar> {
    manifest {
        attributes(mapOf("Main-Class" to "MainKt")) // หรือชื่อคลาสหลักของคุณ
    }
    archiveBaseName.set("JARFieldExtractor")
    archiveClassifier.set("")
    archiveVersion.set("")
}