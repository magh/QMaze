import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"
    application
    maven
    id("org.openjfx.javafxplugin") version "0.0.7"
    id("org.beryx.jlink") version "2.10.2"
}

group = "qmaze"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

javafx {
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web")
}

val kotlinVersion = "1.3.41"
val tornadofxVersion = "1.7.15"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("no.tornado:tornadofx:$tornadofxVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

application {
    mainClassName = "qmaze.QMaze"
}

jlink {
    launcher {
        name = "qmazeapp"
    }
    addExtraDependencies("javafx")
    imageZip.set(project.file("${project.buildDir}/image-zip/qmazeapp-image.zip"))
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
