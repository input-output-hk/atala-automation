import java.util.*

plugins {
    kotlin("jvm") version "1.8.21"
    `java-library`
    `maven-publish`
    id("io.gitlab.arturbosch.detekt") version "1.23.0"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0-rc-1"
    signing
}

group = "io.iohk.atala"

repositories {
    mavenCentral()
}

dependencies {
    api("javax.inject:javax.inject:1")
    api("junit:junit:4.13.2")

    api("net.serenity-bdd:serenity-core:4.0.0")
    api("net.serenity-bdd:serenity-ensure:4.0.0")
    api("net.serenity-bdd:serenity-cucumber:4.0.0")
    api("net.serenity-bdd:serenity-screenplay:4.0.0")
    api("net.serenity-bdd:serenity-screenplay-rest:4.0.0")

    api("ch.qos.logback:logback-classic:1.4.8")
    api("org.slf4j:slf4j-api:2.0.7")

    api("io.ktor:ktor-client-core-jvm:2.3.1")
    api("com.jayway.jsonpath:json-path:2.8.0")
    api("org.awaitility:awaitility:4.2.0")

    api("org.jetbrains.kotlin:kotlin-reflect:1.8.22")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:2.35.0")
}

kotlin {
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>(rootProject.name) {
            from(components["java"])
            groupId = "io.iohk.atala"
            artifactId = name
            version = project.version.toString()
            pom {
                name.set("Atala PRISM Automation Helpers")
                description.set("Automation helpers for PRISM identity ecosystem.")
                url.set("https://docs.atalaprism.io/")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("amagyar-iohk")
                        name.set("Allain Magyar")
                        email.set("allain.magyar@iohk.io")
                    }
                    developer {
                        id.set("antonbaliasnikov")
                        name.set("Anton Baliasnikov")
                        email.set("anton.baliasnikov@iohk.io")
                    }
                }
                scm {
                    connection.set("scm:git:git://input-output-hk/atala-automation.git")
                    developerConnection.set("scm:git:ssh://input-output-hk/atala-automation.git")
                    url.set("https://github.com/input-output-hk/atala-automation")
                }
            }
        }
    }
}

kotlin {
    jvmToolchain(11)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://oss.sonatype.org/content/repositories/snapshots/"))
            username.set(System.getenv("SONATYPE_USERNAME"))
            password.set(System.getenv("SONATYPE_PASSWORD"))
        }
    }
}

signing {
    val base64EncodedAsciiArmoredSigningKey: String = System.getenv("BASE64_ARMORED_GPG_SIGNING_KEY_MAVEN") ?: ""
    val signingKeyPassword: String = System.getenv("SIGNING_KEY_PASSWORD") ?: ""
    useInMemoryPgpKeys(String(Base64.getDecoder().decode(base64EncodedAsciiArmoredSigningKey.toByteArray())), signingKeyPassword)
    sign(publishing.publications)
}
