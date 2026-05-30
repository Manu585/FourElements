repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "FourElements-API",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "manu585",
        )
    }
}
