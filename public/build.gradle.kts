plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.serialization") version "1.9.10"
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.github.johnrengelman.shadow") version "8.1.1"
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
	maven {
		url = uri("https://maven.pkg.github.com/developernaolgithub/custom_apps_backend_utils")
		credentials {
//			username = project.findProperty("gpr.user") as String?
//			password = project.findProperty("gpr.key") as String?

			username = "tanya1403"
			password = "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"
		}
	}
}

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-web")
//	implementation("org.springframework.boot:spring-boot-starter-security")
//	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")

	// Test
//	testImplementation("org.springframework.boot:spring-boot-starter-test")
//	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
//	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
//	testImplementation("org.springframework.security:spring-security-test")


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
