plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    id("com.gradleup.shadow") version "9.4.1"
}

group = "me.lukiiy"
version = "1.0-SNAPSHOT"
description = "agora sim."

repositories {
    mavenCentral()
    maven("https://repo.opencollab.dev/main/")
}

dependencies {
    paperweight.paperDevBundle("26.1.2.build.+")
    compileOnly("org.geysermc.geyser:api:2.9.5-SNAPSHOT")
    implementation(files("lib/WayTrick-1.0-26.1.jar"))
}

tasks {
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")
        minimize()
    }

    build { dependsOn(shadowJar) }

    processResources {
        val props = mapOf(
            "version" to version,
            "description" to rootProject.description
        )

        filesMatching("paper-plugin.yml") { expand(props) }
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}