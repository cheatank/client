import com.soywiz.korge.gradle.*

plugins {
	kotlin("multiplatform") version "1.6.0"
	id("org.jmailen.kotlinter") version "3.6.0"
}

buildscript {
	repositories {
		mavenCentral()
		google()
	}
	dependencies {
		classpath("com.soywiz.korlibs.korge.plugins:korge-gradle-plugin:2.4.6")
	}
}

apply<KorgeGradlePlugin>()

korge {
	id = "com.github.cheatank.client"

	targetJvm()
	targetJs()
	targetDesktop()
}

kotlin {
	sourceSets {
		commonMain {
			repositories {
				mavenLocal()
			}

			dependencies {
				implementation("com.github.cheatank:common:1.0.0-SNAPSHOT")
			}
		}
	}
}
