
val minestom = "net.minestom:minestom-snapshots:static-serializer"

repositories {
    mavenLocal()
}

dependencies {

    implementation(project(":core"))

    compileOnly(minestom)
    testImplementation(minestom)

    testImplementation("ch.qos.logback:logback-classic:1.5.7")
}