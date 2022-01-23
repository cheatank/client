import com.soywiz.korge.gradle.*

plugins {
	kotlin("multiplatform") version "1.6.0"
	id("org.jmailen.kotlinter") version "3.7.0"
	id("com.github.ben-manes.versions") version "0.39.0"
}

buildscript {
	repositories {
		mavenCentral()
		google()
	}
	dependencies {
		classpath("com.soywiz.korlibs.korge.plugins:korge-gradle-plugin:2.4.8")
	}
}

apply<KorgeGradlePlugin>()

korge {
	id = "com.github.cheatank.client"

	targetJvm()
	targetJs()
}

kotlin {
	sourceSets {
		commonMain {
			repositories {
				mavenLocal()
			}

			dependencies {
				implementation("com.github.cheatank:common:1.0.0-SNAPSHOT")
				implementation("io.ktor:ktor-client-cio:1.6.7")
			}
		}
	}
}
