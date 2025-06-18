plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	war
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.homefirst"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven {
		url = uri("https://maven.pkg.github.com/developernaolgithub/custom_apps_backend_utils")
		credentials {
			username = project.findProperty("gpr.user") as String?
			password = project.findProperty("gpr.key") as String?
		}
	}
}

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.springframework.security:spring-security-test")

	// External / Custom
	implementation("com.homefirst:utilities:0.0.9")
	implementation("org.json:json:20230227")
	runtimeOnly("com.mysql:mysql-connector-j")

	// Firebase / AWS
	implementation("com.google.firebase:firebase-admin:9.2.0")
	implementation("com.amazonaws:aws-java-sdk-s3:1.12.452")

	// Misc
	implementation("net.ttddyy:datasource-proxy:1.9")
	implementation("org.apache.commons:commons-lang3:3.12.0")
	implementation("commons-io:commons-io:2.11.0")
	implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
	implementation("org.hibernate:hibernate-core:6.3.1.Final")
	implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
	implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")

	// Gmail API
	implementation("com.google.apis:google-api-services-gmail:v1-rev110-1.25.0")
	implementation("com.google.api-client:google-api-client:1.34.1")
	implementation("com.google.http-client:google-http-client-jackson2:1.43.3")
	implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
	implementation("com.sun.mail:jakarta.mail:2.0.1")
	implementation("org.eclipse.angus:angus-activation:2.0.1")
	implementation("com.sun.activation:jakarta.activation:2.0.1")

	// OkHttp
	implementation("com.squareup.okhttp3:okhttp:4.10.0")

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
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
