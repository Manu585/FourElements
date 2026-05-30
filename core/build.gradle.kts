dependencies {
    implementation(project(":api"))
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "FourElements-Core",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "manu585",
        )
    }
}
