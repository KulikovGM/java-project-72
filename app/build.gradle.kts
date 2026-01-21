plugins {
    application
    jacoco
    checkstyle
    id("com.github.ben-manes.versions") version "0.52.0"
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
}

//tasks.test {
//    useJUnitPlatform()
//    finalizedBy(tasks.jacocoTestReport)
//}