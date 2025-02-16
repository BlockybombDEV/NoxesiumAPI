import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "me.iris"
version = "2.2.3"

plugins {
    kotlin("jvm") version "1.9.24"
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
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.7.0")
    implementation("fr.skytasul:glowingentities:1.4.3")
    implementation("com.noxcrew.noxesium:api:2.5.0")
    implementation("com.noxcrew.noxesium:paper:2.5.0")
    implementation("dev.jorel:commandapi-bukkit-kotlin:9.7.0")
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
        kotlinOptions {
            jvmTarget = 21.toString()
            freeCompilerArgs += listOf("-Xexplicit-api=strict")
        }
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
            version = "2.2.3"
            from(components["java"])
        }
    }
}
