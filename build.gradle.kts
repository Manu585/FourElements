plugins {
    base
}

// Version catalog accessors only exist on the root project, so everything the
// `subprojects` block needs has to be captured out here first.
val javaVersion = JavaLanguageVersion.of(libs.versions.java.get().toInt())
val checkstyleVersion = libs.versions.checkstyle
val lombok = libs.lombok
val junitBom = libs.junit.bom
val junitJupiter = libs.junit.jupiter
val junitLauncher = libs.junit.platform.launcher

subprojects {
    group = "com.github.manu585.fourelements"

    // Each module applies its own plugins so the Kotlin DSL can generate type-safe accessors for it
    pluginManager.withPlugin("java-library") {
        extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion = javaVersion
        }

        dependencies {
            "compileOnly"(lombok)
            "annotationProcessor"(lombok)
            "testCompileOnly"(lombok)
            "testAnnotationProcessor"(lombok)

            "testImplementation"(platform(junitBom))
            "testImplementation"(junitJupiter)
            "testRuntimeOnly"(junitLauncher)
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    tasks.withType<Jar>().configureEach {
        isReproducibleFileOrder = true
        isPreserveFileTimestamps = false
        manifest.attributes(
            "Implementation-Title" to "${rootProject.name}-${project.name}",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "Manu585",
        )
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
