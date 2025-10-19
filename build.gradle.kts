plugins {
    kotlin("jvm") version "1.9.0"
    application
	kotlin("plugin.serialization") version "1.9.10"
}

repositories {
    maven {
        url = uri("https://repo.osgeo.org/repository/release/")
    }
    mavenCentral()
}

dependencies {
    implementation("org.geotools:gt-referencing:27.2")
    implementation("org.locationtech.jts:jts-core:1.19.0")
    implementation("org.geotools:gt-shapefile:27.2") {
		exclude("javax.media", "jai_core")
	}
	implementation("javax.media:jai-core:1.1.3")
	 implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.6.3")

}

application {
    mainClass.set("MainKt")
}
