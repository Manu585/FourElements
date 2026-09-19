plugins {
    `java-library`
    checkstyle
    alias(libs.plugins.shadow)
}

/**
 * Dependencies that Paper's library loader resolves at runtime instead of us
 * shading them. They are compile-only here and their coordinates are written
 * into the jar for FourElementsPluginLoader to read.
 */
val paperLibrary: Configuration = configurations.create("paperLibrary") {
    isCanBeResolved = false
    isCanBeConsumed = false
}

configurations.compileOnly {
    extendsFrom(paperLibrary)
}

dependencies {
    implementation(project(":core")) // pulls in :api and paper-api transitively
    compileOnly(libs.packetevents)

    paperLibrary(libs.hikari)
    paperLibrary(libs.mysql)
}

val generatePaperLibraries = tasks.register("generatePaperLibraries") {
    group = "build"
    description = "Writes the runtime library coordinates read by the Paper plugin loader."

    val coordinates = providers.provider {
        paperLibrary.dependencies.map { "${it.group}:${it.name}:${it.version}" }
    }
    val outputDir = layout.buildDirectory.dir("generated/paper-libraries")

    inputs.property("coordinates", coordinates)
    outputs.dir(outputDir)

    doLast {
        outputDir.get().file("paper-libraries.txt").asFile
            .writeText(coordinates.get().joinToString("\n", postfix = "\n"))
    }
}

sourceSets.main {
    resources.srcDir(generatePaperLibraries)
}

tasks.processResources {
    val props = mapOf("version" to project.version.toString())
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") { expand(props) }
}

val deployPlugin = tasks.register<Copy>("deployPlugin") {
    group = "deployment"
    description = "Copies the shadow jar into the test server's plugins folder."

    val defaultDir = rootProject.layout.projectDirectory.dir("run/plugins").asFile.path

    from(tasks.shadowJar)
    into(providers.gradleProperty("pluginDir").orElse(defaultDir))
    rename { "${rootProject.name}.jar" }
}

tasks.shadowJar {
    archiveBaseName = rootProject.name
    archiveClassifier = "all"
    mergeServiceFiles()
    manifest.attributes(
        "Implementation-Title" to rootProject.name,
        "papermc-plugin-name" to rootProject.name,
    )

    finalizedBy(deployPlugin)
}

// The shadow jar is the only publishable output of this module: a thin jar
// would be missing :api and :core.
tasks.jar {
    enabled = false
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}
