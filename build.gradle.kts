plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.serialization") version "1.9.10"
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
//	maven {
//		url = uri("https://maven.pkg.github.com/developernaolgithub/custom_apps_backend_utils")
//		credentials {
//			username = System.getenv("GPR_USER")
//			password = System.getenv("GPR_TOKEN")
//
//		}
//	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("io.ktor:ktor-server-core:2.3.3")
	implementation("io.ktor:ktor-server-netty:2.3.3")
	implementation("io.ktor:ktor-server-content-negotiation:2.3.3")
	implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.3")
	implementation("io.ktor:ktor-server-call-logging:2.3.3")
	implementation("io.ktor:ktor-server-status-pages:2.3.3")
	implementation("ch.qos.logback:logback-classic:1.2.11")
	implementation("com.homefirst:utilities:0.0.9")
	implementation("org.json:json:20230227")
	runtimeOnly("com.mysql:mysql-connector-j")
	implementation("com.google.firebase:firebase-admin:9.2.0") // or latest version
	implementation("net.ttddyy:datasource-proxy:1.9")
	implementation("org.apache.commons:commons-lang3:3.12.0")
	implementation("com.amazonaws:aws-java-sdk-s3:1.12.452")
	implementation("commons-io:commons-io:2.11.0")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
	implementation("com.google.apis:google-api-services-gmail:v1-rev110-1.25.0")
	implementation("com.google.api-client:google-api-client:1.34.1")
	implementation("com.google.http-client:google-http-client-jackson2:1.43.3")
	implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
	implementation("com.sun.mail:jakarta.mail:2.0.1")
	implementation("org.eclipse.angus:angus-activation:2.0.1")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
	implementation("com.sun.activation:jakarta.activation:2.0.1")
	implementation("org.hibernate:hibernate-core:6.3.1.Final")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.5")
	implementation("org.springframework.boot:spring-boot-starter:3.2.5")
	implementation("org.springframework:spring-context:6.1.5")
	implementation("com.squareup.okhttp3:okhttp:4.10.0")
	implementation("com.fasterxml.jackson.core:jackson-core:2.13.4")
	implementation("org.springframework.boot:spring-boot-starter-security")
//	implementation("com.homefirst:utilities:1.0.0")
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
