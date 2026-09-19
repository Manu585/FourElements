plugins {
    `java-library`
    checkstyle
}

dependencies {
    // Paper is provided by the server at runtime, but its types leak into the
    // public API, so consumers need it on their compile classpath too.
    compileOnlyApi(libs.paper.api)
}
