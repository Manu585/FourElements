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
    version = "Alpha-0.0.1"

    // Each module applies its own plugins so the Kotlin DSL can generate
    // type-safe accessors for it; we only react to them here.
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

    pluginManager.withPlugin("checkstyle") {
        extensions.configure<CheckstyleExtension> {
            toolVersion = checkstyleVersion.get()
            configDirectory = rootProject.layout.projectDirectory.dir("config/checkstyle")
        }

        // Resolved against the project here; inside configureEach the receiver
        // would be the task, which has no toolchain service.
        val checkstyleLauncher = extensions.getByType<JavaToolchainService>()
            .launcherFor { languageVersion = javaVersion }

        tasks.withType<Checkstyle>().configureEach {
            // Checkstyle must run on the same JDK the sources target.
            javaLauncher = checkstyleLauncher

            reports {
                html.required = true
                xml.required = false
            }
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
