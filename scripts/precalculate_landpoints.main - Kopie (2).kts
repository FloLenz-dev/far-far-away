#!/usr/bin/env kotlin

import org.geotools.data.FileDataStoreFinder
import org.geotools.data.simple.SimpleFeatureSource
import org.locationtech.jts.geom.*
import kotlin.math.*
import java.io.File
import kotlinx.serialization.*
import kotlinx.serialization.cbor.Cbor


println("GeoTools bereit 🚀")

@Serializable
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

fun findMostDistantLandPoint(
referencePoints: List<GeoPoint>,
landPolygons: List<Geometry>,
stepSize: Double = 0.5,
limit: Double = -1.0
): Triple<GeoPoint, GeoPoint, Double> {
    var bestPoint = GeoPoint(0.0, 0.0)
    var closestReference = GeoPoint(0.0, 0.0)
    var maxMinDistance = limit

    var lat = 84.0 //no landmass above 84 degree
    while (lat >= -63.0) { //don't look at antarctica
        var lon = -180.0
        while (lon <= 180.0) {
            val current = GeoPoint(lat, lon)
            lon += stepSize
            if (!isLand(current, landPolygons)) continue
        }
        println("point: Latitude: ${lat}, Longitude: ${lon}")
        lat -= stepSize
    }
    return Triple(bestPoint, closestReference, maxMinDistance)
}

fun main() {
    val visited = mutableMapOf<GeoPoint, Boolean>()
    val file = File("is_land.json")
    val bytes = Cbor.encodeToByteArray(visited)
    file.writeBytes(bytes)

    val shapefilePath = "data/ne_10m_land.shp" // path to .shp-Datei
    val landPolygons = loadLandGeometry(shapefilePath)

    val (bestPoint5, closestReference5, distance5) = findMostDistantLandPoint(
        referencePoints,
        landPolygons,
        stepSize = 5.0,
        -1.0
    )
    val (bestPoint1, closestReference1, distance1) = findMostDistantLandPoint(
        referencePoints,
        landPolygons,
        stepSize = 1.0,
        distance5
    )
    val (bestPoint01, closestReference01, distance01) = findMostDistantLandPoint(
        referencePoints,
        landPolygons,
        stepSize = 0.1,
        distance1
    )
}