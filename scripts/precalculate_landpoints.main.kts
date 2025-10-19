#!/usr/bin/env kotlin
@file:Repository("https://repo.osgeo.org/repository/release/")
@file:DependsOn("org.geotools:gt-main:29.2")
@file:DependsOn("org.geotools:gt-shapefile:29.2")
@file:DependsOn("org.geotools:gt-referencing:29.2")
@file:Repository("https://repo1.maven.org/maven2")

@file:DependsOn("com.fasterxml.jackson.core:jackson-core:2.15.2")
@file:DependsOn("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
@file:DependsOn("com.fasterxml.jackson.core:jackson-databind:2.15.2")
@file:DependsOn("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
@file:DependsOn("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:2.15.2")

import org.geotools.data.FileDataStoreFinder
import org.geotools.data.simple.SimpleFeatureSource
import org.locationtech.jts.geom.*
import kotlin.math.*
import java.io.File

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.fasterxml.jackson.module.kotlin.readValue

data class GeoPoint(val lat: Double, val lon: Double)

fun haversine(p1: GeoPoint, p2: GeoPoint): Double {
    val EARTH_RADIUS_KM = 6371.0
    
    val dLat = Math.toRadians(p2.lat - p1.lat)
    val dLon = Math.toRadians(p2.lon - p1.lon)
    val rLat1 = Math.toRadians(p1.lat)
    val rLat2 = Math.toRadians(p2.lat)

    val a = sin(dLat / 2).pow(2) + cos(rLat1) * cos(rLat2) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return EARTH_RADIUS_KM * c
}

fun loadLandGeometry(shapefilePath: String): List<Geometry> {
    val file = File(shapefilePath)
    val store = FileDataStoreFinder.getDataStore(file)
    val featureSource: SimpleFeatureSource = store.featureSource

    val landGeometries = mutableListOf<Geometry>()
    val features = featureSource.features
    val featureIterator = features.features()

    while (featureIterator.hasNext()) {
        val feature = featureIterator.next()
        val geom = feature.defaultGeometry as Geometry
        landGeometries.add(geom)
    }

    store.dispose()
    return landGeometries
}

fun isLandFreeNemo(pointToCheck: GeoPoint): Boolean{ //check if point is within the landfree square around point nemo
    return (pointToCheck.lat < -30) and (pointToCheck.lat > -70) and  (pointToCheck.lon > 105) and (pointToCheck.lat < 135)
}

fun isLand(pointToCheck: GeoPoint, landPolygons: List<Geometry>): Boolean {
    if (isLandFreeNemo (pointToCheck)) return false
    var lat = pointToCheck.lat
    var lon = pointToCheck.lon
    val point: Point = GeometryFactory().createPoint(Coordinate(lon, lat)) // watch out: (lon, lat)!
    return landPolygons.any { it.contains(point) }
}

fun calculateLandpoints(
    landPolygons: List<Geometry>,
    stepSize: Double = 0.5
): Boolean {


    val isLandMap = mutableMapOf<GeoPoint, Boolean>()
    val cborMapper = CBORMapper().registerKotlinModule()

    var lat = 84.0 //no landmass above 84 degree
    while (lat >= -63.0) { //don't look at antarctica
        var lon = -180.0
        
        while (lon <= 180.0) {
            val current = GeoPoint(lat, lon)
            isLandMap[current] = isLand(current, landPolygons)
            lon += stepSize
            println(lon)
        }

        val file = File("is_land${lat}.save")
        val bytes = cborMapper.writeValueAsBytes(isLandMap)
        file.writeBytes(bytes)

        lat -= stepSize
    }
    return true
}
println("databind: " + com.fasterxml.jackson.databind.ObjectMapper::class.java.`package`.implementationVersion)
println("core    : " + com.fasterxml.jackson.core.JsonFactory::class.java.`package`.implementationVersion)
println("cbor    : " + com.fasterxml.jackson.dataformat.cbor.CBORFactory::class.java.`package`.implementationVersion)

println("point: Latitude:")

val shapefilePath = "data/ne_10m_land.shp" // path to .shp-Datei
val landPolygons = loadLandGeometry(shapefilePath)

calculateLandpoints(
    landPolygons,
    stepSize = 0.1
)

println("point: Latitude:")