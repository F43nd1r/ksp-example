plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.6.21-1.0.5"
    application
}

dependencies {
    implementation(project(":annotation"))
    ksp(project(":processor"))
}

application {
    mainClass.set("MainKt")
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}
