plugins {
    application
    jacoco
    checkstyle
    id("com.github.ben-manes.versions") version "0.52.0"
    id("io.freefair.lombok") version "8.13.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.sonarqube") version "7.1.0.6387"
}

sonar {
    properties {
        property("sonar.projectKey", "KulikovGM_java-project-72")
        property("sonar.organization", "kulikovgm")
        //
    }
}

application {
    mainClass = "hexlet.code.App"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:4.0.0-M1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    implementation("info.picocli:picocli:4.7.7")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")
    implementation("gg.jte:jte:3.2.0")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("io.javalin:javalin:6.6.0")
    implementation("io.javalin:javalin-bundle:6.7.0")
    implementation("io.javalin:javalin-rendering:6.6.0")
    implementation("com.h2database:h2:2.3.232")
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("org.postgresql:postgresql:42.7.5")
    implementation("org.jsoup:jsoup:1.18.3")
    implementation("com.konghq:unirest-java-core:4.5.1")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}