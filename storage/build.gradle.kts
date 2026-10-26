plugins {
    `java-library`
}

dependencies {
    // The repository interface and the domain objects it stores
    api(project(":core"))

    // Provided at runtime by Paper's library loader
    compileOnly(libs.hikari)
    compileOnly(libs.jspecify)

    // The tests talk to real databases, so the pool and every driver must be on the test classpath
    testImplementation(libs.hikari)
    testRuntimeOnly(libs.mysql)
    testRuntimeOnly(libs.sqlite)
}
