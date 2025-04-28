import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "me.iris"
version = "${property("version")}"

plugins {
    kotlin("jvm") version "2.1.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://maven.noxcrew.com/public") {
        name = "Noxcrew Public Maven Repository"
    }
    maven(url = uri("https://repo.codemc.org/repository/maven-public/"))
}

dependencies {
    paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.11")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.11")
    implementation("io.github.revxrsal:lamp.brigadier:4.0.0-rc.11")
    implementation("fr.skytasul:glowingentities:1.4.3")
    implementation("com.noxcrew.noxesium:api:2.7.4")
    implementation("com.noxcrew.noxesium:paper:2.7.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            javaParameters = true
        }
    }
    withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<Jar> {
    // Otherwise you'll get a "No main manifest attribute" error
    manifest {
        attributes["Main-Class"] = "me.iris.noxesiumapi.NoxesiumAPI"
    }

    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

publishing {
    repositories {
        maven {
            name = "astrofoxRepository"
            url = uri("http://144.21.60.201:25566/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.iris"
            artifactId = "noxesiumapi"
            version = "${property("version")}"
            from(components["java"])
        }
    }
}
