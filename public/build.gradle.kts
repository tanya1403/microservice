plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.serialization") version "1.9.10"
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("application")

}

group = "com.homefirst"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

application {
	mainClass.set("com.homefirst.microservices.MicroservicesApplicationKt")
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")


	// Ktor
	implementation("io.ktor:ktor-server-core:2.3.9")
	implementation("io.ktor:ktor-server-netty:2.3.9")
	implementation("io.ktor:ktor-server-jetty:2.3.9")
	implementation("io.ktor:ktor-server-servlet:2.3.9")
	implementation("io.ktor:ktor-server-content-negotiation:2.3.9")
	implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.9")
	implementation("io.ktor:ktor-server-call-logging:2.3.9")
	implementation("io.ktor:ktor-server-status-pages:2.3.9")

	// Koin DI
	implementation("io.insert-koin:koin-ktor:3.4.0")
	implementation("io.insert-koin:koin-logger-slf4j:3.4.0")

	implementation("io.ktor:ktor-server-content-negotiation:2.3.4")
	implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
