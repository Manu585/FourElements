plugins {
    id("com.gradleup.shadow") version "9.4.2"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":core"))
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    compileOnly("com.github.retrooper:packetevents-spigot:2.12.1")
    compileOnly("com.zaxxer:HikariCP:6.2.1")
    compileOnly("com.mysql:mysql-connector-j:9.3.0")
}

tasks.shadowJar {
    archiveFileName.set("${rootProject.name}-${project.version}-all.jar")

    // Merge duplicate META-INF/services files from shaded libraries
    mergeServiceFiles()

    manifest {
        attributes(
            "Implementation-Title" to rootProject.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "manu585",
            "Multi-Release" to "true",
            "papermc-plugin-name" to rootProject.name,
        )
    }
}

// Make the standard lifecycle tasks depend on shadowJar instead of jar
tasks.assemble { dependsOn(tasks.shadowJar) }

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

// ---------------------------------------------------------------------------
// Disable the standard jar task so the shadow JAR is the only output.
// This prevents the classloader from picking up a thin JAR that is missing
// shaded dependencies (api, core, HikariCP, MySQL connector).
// ---------------------------------------------------------------------------
tasks.jar { enabled = false }
