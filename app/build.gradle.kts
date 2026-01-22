plugins {
    application
    jacoco
    checkstyle
    id("com.github.ben-manes.versions") version "0.52.0"
    id("io.freefair.lombok") version "8.13.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    //id("org.sonarqube") version "7.2.2.6593"
}

//sonar {
//    properties {
//        property("sonar.projectKey", "KulikovGM_java-project-78")
//        property("sonar.organization", "kulikovgm")
//        // property("sonar.host.url", "https://sonarcloud.io")
//    }
//}

application {
    mainClass = "hexlet.code.Main"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("info.picocli:picocli:4.7.7")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")
    implementation("gg.jte:jte:3.2.0")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("io.javalin:javalin:6.6.0")
    implementation("io.javalin:javalin-bundle:6.6.0")
    implementation("io.javalin:javalin-rendering:6.6.0")
}

//tasks.test {
//    useJUnitPlatform()
//    finalizedBy(tasks.jacocoTestReport)
//}