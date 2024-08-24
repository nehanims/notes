plugins {
	id("org.springframework.boot") version "3.3.2"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("plugin.jpa") version "1.9.24"
	kotlin("jvm") version "2.0.0"
	kotlin("plugin.spring") version "1.9.24"
	kotlin("plugin.serialization") version "2.0.0"
}


group = "notes"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("io.minio:minio:8.5.11")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.reactivestreams:reactive-streams:1.0.4")//TODO: Check if this is how to add reactive streams or some BOM is needed
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
	//implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.0")
	//implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.5.0")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	//runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
