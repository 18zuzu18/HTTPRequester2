plugins {
    java
    kotlin("jvm") version "1.5.21"
}

group = "de.alpha"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.postgresql:postgresql:42.2.22")
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("org.json:json:20180130")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions.jvmTarget = "15" }

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")