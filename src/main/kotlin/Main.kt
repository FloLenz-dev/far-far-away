import org.geotools.data.FileDataStoreFinder
import org.geotools.data.simple.SimpleFeatureSource
import org.locationtech.jts.geom.*
import kotlin.math.*
import java.io.File

//dictionary

data class GeoPoint(val lat: Double, val lon: Double)

const val EARTH_RADIUS_KM = 6371.0

fun haversine(p1: GeoPoint, p2: GeoPoint): Double {
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
    val point: Point = GeometryFactory().createPoint(Coordinate(lon, lat)) // ACHTUNG: (lon, lat)!
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

            // Finde den nächsten Referenzpunkt + seine Distanz
            val (nearestRef, minDistance) = referencePoints
                .map { it to haversine(current, it) }
                .minByOrNull { it.second } ?: continue

            if (minDistance >= maxMinDistance) {
				println("Closest point: Latitude: ${lat}, Longitude: ${lon}")
				if (!isLand(current, landPolygons)) continue
                maxMinDistance = minDistance
                bestPoint = current
                closestReference = nearestRef
        }
		println("point: Latitude: ${lat}, Longitude: ${lon}")
    }
    lat -= stepSize
    }

    return Triple(bestPoint, closestReference, maxMinDistance)
}

fun main() {
    val shapefilePath = "data/ne_10m_land.shp" // Pfad zur .shp-Datei
    val landPolygons = loadLandGeometry(shapefilePath)

	val referencePoints = listOf(
		GeoPoint(-21.1151, 55.5364),   // La Réunion
		GeoPoint(-25.6953, -54.4367),  // Iguazú
		GeoPoint(60.4518, 22.2666),    // Turku
		GeoPoint(41.1579, -8.6291),    // Porto
		GeoPoint(40.7128, -74.0060),   // New York
		GeoPoint(36.6513, 138.1810),   // Nagano
		GeoPoint(13.7563, 100.5018),   // Bangkok
		GeoPoint(25.03, 121.5),   // Taipeh
		GeoPoint(24.3, 124.0),   // Ishigaki
		GeoPoint(35.6, 139.7),   // Tokio
		GeoPoint(35.7595, -5.8339),    // Tangier
		GeoPoint(42.6977, 23.3219),    // Sofia
		GeoPoint(32.0853, 34.7818),    // Tel Aviv
		GeoPoint(39.9208, 32.8541),   // Ankara
		GeoPoint(-44.28, -176.20),	//pitt's island
		GeoPoint(60.0, -151.0), //south-east alaska
		GeoPoint(-30.0, 123.0), //western-inner australia
		GeoPoint (-7.0, 13.0), //central africa, angola/kongo
		GeoPoint (-9.0, 161.0), //malaita, solomon inseln
		GeoPoint (4.0, -77.0),	//columbia
		GeoPoint(48.0, 84.0), //russland, altaische repuclik near kasachstan
		GeoPoint(-63.0, -58.0), //south shetland islands, antarctica
		GeoPoint(27.0,-114.0), //mexiko, baja califonia
		GeoPoint(73.0, 128.0), //north-east siberia
		GeoPoint(70.0, -54.0), //greenland westcoast
		GeoPoint(-49.0, 69.0), //crozetinseln, french, subantarctic
		GeoPoint(21.0, 71.0), //india, west, near arabian sea
		GeoPoint(-34.0, 24.0) //south africa, south east
	)

    val (bestPoint5, closestReference5, distance5) = findMostDistantLandPoint(referencePoints, landPolygons, stepSize = 5.0, -1.0)
    val (bestPoint1, closestReference1, distance1) = findMostDistantLandPoint(referencePoints, landPolygons, stepSize = 1.0, distance5)
    //val (bestPoint01, closestReference01, distance01) = findMostDistantLandPoint(referencePoints, landPolygons, stepSize = 0.1, distance1)

    println("Am weitesten entfernter Landpunkt:")
    println("Latitude: ${bestPoint1.lat}, Longitude: ${bestPoint1.lon}")
    println("Minimaler Abstand zu Referenzpunkten: %.2f km".format(distance1))
    println("Closest point: Latitude: ${closestReference1.lat}, Longitude: ${closestReference1.lon}")
}