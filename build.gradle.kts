plugins {
    kotlin("jvm") version "1.8.21"
    java
    `java-library`
    `maven-publish`
}

group = "io.iohk.atala"

repositories {
    mavenCentral()
}

dependencies {
    api("junit:junit:4.13.2")

    api("net.serenity-bdd:serenity-core:3.8.1")
    api("net.serenity-bdd:serenity-ensure:3.8.1")
    api("net.serenity-bdd:serenity-cucumber:3.8.1")
    api("net.serenity-bdd:serenity-screenplay:3.8.1")
    api("net.serenity-bdd:serenity-screenplay-rest:3.8.1")

    api("ch.qos.logback:logback-classic:1.4.8")
    api("org.slf4j:slf4j-api:2.0.7")

    api("io.ktor:ktor-client-core-jvm:2.3.1")
    api("com.jayway.jsonpath:json-path:2.8.0")
    api("org.awaitility:awaitility:4.2.0")

    api("org.jetbrains.kotlin:kotlin-reflect:1.8.22")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:2.35.0")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/input-output-hk/atala-automation")
            credentials {
                username = System.getenv("ATALA_GITHUB_ACTOR")
                password = System.getenv("ATALA_GITHUB_TOKEN")
            }
        }
    }
}

kotlin {
    jvmToolchain(11)
}
