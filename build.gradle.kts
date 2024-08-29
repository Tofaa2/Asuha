plugins {
    id("java")
    id("java-library")
}

allprojects {

    apply {
        plugin("java")
        plugin("java-library")
    }

    group = "ac.asuha"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}
