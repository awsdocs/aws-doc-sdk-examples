// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.location.scenario

import aws.sdk.kotlin.services.geoplaces.GeoPlacesClient
import aws.sdk.kotlin.services.geoplaces.model.GetPlaceRequest
import aws.sdk.kotlin.services.geoplaces.model.ReverseGeocodeRequest
import aws.sdk.kotlin.services.geoplaces.model.SearchNearbyRequest
import aws.sdk.kotlin.services.geoplaces.model.SearchTextRequest
import aws.sdk.kotlin.services.location.LocationClient
import aws.sdk.kotlin.services.location.model.ApiKeyRestrictions
import aws.sdk.kotlin.services.location.model.BatchUpdateDevicePositionRequest
import aws.sdk.kotlin.services.location.model.CalculateRouteRequest
import aws.sdk.kotlin.services.location.model.CalculateRouteResponse
import aws.sdk.kotlin.services.location.model.CreateGeofenceCollectionRequest
import aws.sdk.kotlin.services.location.model.CreateKeyRequest
import aws.sdk.kotlin.services.location.model.CreateMapRequest
import aws.sdk.kotlin.services.location.model.CreateRouteCalculatorRequest
import aws.sdk.kotlin.services.location.model.CreateRouteCalculatorResponse
import aws.sdk.kotlin.services.location.model.CreateTrackerRequest
import aws.sdk.kotlin.services.location.model.DeleteGeofenceCollectionRequest
import aws.sdk.kotlin.services.location.model.DeleteKeyRequest
import aws.sdk.kotlin.services.location.model.DeleteMapRequest
import aws.sdk.kotlin.services.location.model.DeleteRouteCalculatorRequest
import aws.sdk.kotlin.services.location.model.DeleteTrackerRequest
import aws.sdk.kotlin.services.location.model.DevicePositionUpdate
import aws.sdk.kotlin.services.location.model.DistanceUnit
import aws.sdk.kotlin.services.location.model.GeofenceGeometry
import aws.sdk.kotlin.services.location.model.GetDevicePositionRequest
import aws.sdk.kotlin.services.location.model.GetDevicePositionResponse
import aws.sdk.kotlin.services.location.model.MapConfiguration
import aws.sdk.kotlin.services.location.model.PositionFiltering
import aws.sdk.kotlin.services.location.model.PutGeofenceRequest
import aws.sdk.kotlin.services.location.model.TravelMode
import java.util.Scanner

// snippet-start:[location.kotlin.scenario.main]
/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

val scanner = Scanner(System.`in`)
val DASHES = String(CharArray(80)).replace("\u0000", "-")
suspend fun main(args: Array<String>) {

    val usage = """

            Usage:    <mapName> <keyName> <collectionName> <geoId> <trackerName> <calculatorName> <deviceId>

            Where:
              mapName - The name of the map to be create (e.g., "AWSMap").
              keyName - The name of the API key to create (e.g., "AWSApiKey").
              collectionName - The name of the geofence collection (e.g., "AWSLocationCollection").
              geoId - The geographic identifier used for the geofence or map (e.g., "geoId").
              trackerName - The name of the tracker (e.g., "geoTracker").
              calculatorName - The name of the route calculator (e.g., "AWSRouteCalc").
              deviceId - The ID of the device (e.g., "iPhone-112356").
            
            """.trimIndent()

    // if (args.size != 7) {
    //     println(usage)
    //     exitProcess(0)
    // }

    val mapName = "AWSMap301"; //args[0]
    val keyName = "AWSApiKey301"; //args[1]
    val collectionName = "AWSLocationCollection301"; //args[2]
    val geoId = "geoId301";//args[3]
    val trackerName = "geoTracker301" // args[4]
    val calculatorName = "AWSRouteCalc301"; //args[5]
    val deviceId = "iPhone-112356"

    println(
        """
            AWS Location Service is a fully managed service offered by Amazon Web Services (AWS) that
            provides location-based services for developers. This service simplifies
            the integration of location-based features into applications, making it
            easier to build and deploy location-aware applications.
                        
            The AWS Location Service offers a range of location-based services,
            including:
                        
            Maps: The service provides access to high-quality maps, satellite imagery, 
            and geospatial data from various providers, allowing developers to 
            easily embed maps into their applications.
                        
            Tracking: The Location Service enables real-time tracking of mobile devices, 
            assets, or other entities, allowing developers to build applications 
            that can monitor the location of people, vehicles, or other objects.
                        
            Geocoding: The service provides the ability to convert addresses or 
            location names into geographic coordinates (latitude and longitude), 
            and vice versa, enabling developers to integrate location-based search 
            and routing functionality into their applications.
            
            """.trimIndent()
    )
    waitForInputToContinue(scanner)

    println(DASHES)
    println("1. Create an AWS Location Service map")
    println(
        """
             An AWS Location map can enhance the user experience of your
             application by providing accurate and personalized location-based
             features. For example, you could use the geocoding capabilities to
             allow users to search for and locate businesses, landmarks, or
             other points of interest within a specific region.
            
            """.trimIndent()
    )

    waitForInputToContinue(scanner)
    val mapArn = createMap(mapName)
    println("The Map ARN is: $mapArn")
    waitForInputToContinue(scanner)
    println(DASHES)

    waitForInputToContinue(scanner)
    println("2. Create an AWS Location API key")
    println(
        """
            When you embed a map in a web app or website, the API key is
            included in the map tile URL to authenticate requests. You can
            restrict API keys to specific AWS Location operations (e.g., only
            maps, not geocoding). API keys can expire, ensuring temporary
            access control.
            
            """.trimIndent()
    )
    val keyArn = createKey(keyName, mapArn)
    println("The Key ARN is: $keyArn")
    waitForInputToContinue(scanner)
    println(DASHES)

    println(DASHES)
    println("3. Display Map URL")
    println(
        """
            In order to get the MAP URL, you need to get the API Key value.
            You can get the key value using the AWS Management Console under
            Location Services. This operation cannot be completed using the
            AWS SDK. For more information about getting the key value, see 
            the AWS Location Documentation.
            
            """.trimIndent()
    )
    val mapUrl = "https://maps.geo.aws.amazon.com/maps/v0/maps/$mapName/tiles/{z}/{x}/{y}?key={KeyValue}"
    println("Embed this URL in your Web app: $mapUrl")
    println("")
    waitForInputToContinue(scanner)
    println(DASHES)

    println(DASHES)
    println("4. Create a geofence collection, which manages and stores geofences.")
    waitForInputToContinue(scanner)
    val collectionArn: String =
        createGeofenceCollection(collectionName)
    println("The geofence collection was successfully created: $collectionArn")
    waitForInputToContinue(scanner)

    println(DASHES)
    println("5. Store a geofence geometry in a given geofence collection.")
    println(
        """
            An AWS Location geofence is a virtual boundary that defines a geographic area
            on a map. It is a useful feature for tracking the location of
            assets or monitoring the movement of objects within a specific region.
                        
            To define a geofence, you need to specify the coordinates of a
            polygon that represents the area of interest. The polygon must be
            defined in a counter-clockwise direction, meaning that the points of
            the polygon must be listed in a counter-clockwise order.
                        
            This is a requirement for the AWS Location service to correctly
            interpret the geofence and ensure that the location data is
            accurately processed within the defined area.
            
            """.trimIndent()
    )

    waitForInputToContinue(scanner)
    putGeofence(collectionName, geoId)
    println("Successfully created geofence: $geoId")
    waitForInputToContinue(scanner)
    println(DASHES)

    println(DASHES)
    println("6. Create a tracker resource which lets you retrieve current and historical location of devices.")
    waitForInputToContinue(scanner)
    val trackerArn: String = createTracker(trackerName)
    println("Successfully created tracker. ARN: $trackerArn")
    waitForInputToContinue(scanner)
    println(DASHES)

    println(DASHES)
    println("7. Update the position of a device in the location tracking system.")
    println(
        """
            The AWS location service does not enforce a strict format for deviceId, but it must:
              - Be a string (case-sensitive).
              - Be 1–100 characters long.
              - Contain only:
                - Alphanumeric characters (A-Z, a-z, 0-9)
                - Underscores (_)
                - Hyphens (-)
                - Be the same ID used when sending and retrieving positions.
            
            """.trimIndent()
    )

    waitForInputToContinue(scanner)
    updateDevicePosition(trackerName, deviceId)
    println("$deviceId was successfully updated in the location tracking system.")
    waitForInputToContinue(scanner)
    println(DASHES)

    println(DASHES)
    println("8. Retrieve the most recent position update for a specified device.")
    waitForInputToContinue(scanner)
    val response = getDevicePosition(trackerName, deviceId)
    println("Successfully fetched device position: ${response.position}")
    waitForInputToContinue(scanner)
    println(DASHES)

    println(DASHES)
    println("9. Create a route calculator.")
    waitForInputToContinue(scanner)
    val routeResponse = createRouteCalculator(calculatorName)
    println("Route calculator created successfully: ${routeResponse.calculatorArn}")
    waitForInputToContinue(scanner)
    println(DASHES)

    println(DASHES)
    println("10. Determine the distance in kilometers between Seattle and Vancouver using the route calculator.")
    waitForInputToContinue(scanner)
    val responseDis = calcDistance(calculatorName)
    println("Successfully calculated route. The distance in kilometers is ${responseDis.summary?.distance}")
    waitForInputToContinue(scanner)
    println(DASHES)

    println(DASHES)
    println("11. Use the GeoPlacesClient to perform additional operations.")
    println(
        """
            This scenario will show use of the GeoPlacesClient that enables  
            location search and geocoding capabilities for your applications. 
                    
            We are going to use this client to perform these AWS Location tasks:
             - Reverse Geocoding (reverseGeocode): Converts geographic coordinates into addresses.
             - Place Search (searchText): Finds places based on search queries.
             - Nearby Search (searchNearby): Finds places near a specific location.
            
            """.trimIndent()
    )

    waitForInputToContinue(scanner)
    println("First we will perform a Reverse Geocoding operation")
    waitForInputToContinue(scanner)
    reverseGeocode()

    println("Now we are going to perform a text search using coffee shop.")
    waitForInputToContinue(scanner)
    searchText("coffee shop")
    waitForInputToContinue(scanner)

    println("Now we are going to perform a nearby Search.")
    waitForInputToContinue(scanner)
    searchNearby()
    waitForInputToContinue(scanner)
    println(DASHES)

    println(DASHES)
    println("12. Delete the AWS Location Services resources.")
    println("Would you like to delete the AWS Location Services resources? (y/n)")
    val delAns = scanner.nextLine().trim { it <= ' ' }
    if (delAns.equals("y", ignoreCase = true)) {
        deleteMap(mapName)
        deleteKey(keyName)
        deleteGeofenceCollection(collectionName)
        deleteTracker(trackerName)
        deleteRouteCalculator(calculatorName)
    } else {
        println("The AWS resources will not be deleted.")
    }
    waitForInputToContinue(scanner)
    println(DASHES)

    println(DASHES)
    println(" This concludes the AWS Location Service scenario.")
    println(DASHES)
}

// snippet-start:[location.kotlin.delete.calculator.main]
/**
 * Deletes a route calculator from the system.
 * @param calcName the name of the route calculator to delete
 */
suspend fun deleteRouteCalculator(calcName: String) {
    val calculatorRequest = DeleteRouteCalculatorRequest {
        this.calculatorName = calcName
    }

    LocationClient { region = "us-east-1" }.use { client ->
        client.deleteRouteCalculator(calculatorRequest)
        println("The route calculator $calcName was deleted.")
    }
}
// snippet-end:[location.kotlin.delete.calculator.main]

// snippet-start:[location.kotlin.delete.tracker.main]

/**
 * Deletes a tracker with the specified name.
 * @param trackerName the name of the tracker to be deleted
 */
suspend fun deleteTracker(trackerName: String) {
    val trackerRequest = DeleteTrackerRequest {
        this.trackerName = trackerName
    }

    LocationClient { region = "us-east-1" }.use { client ->
        client.deleteTracker(trackerRequest)
        println("The tracker $trackerName was deleted.")

    }
}
// snippet-kotlin:[location.kotlin.delete.tracker.main]

// snippet-start:[location.kotlin.delete.collection.main]

/**
 * Deletes a geofence collection.
 *
 * @param collectionName the name of the geofence collection to be deleted
 * @return a {@link CompletableFuture} that completes when the geofence collection has been deleted
 */
suspend fun deleteGeofenceCollection(collectionName: String) {
    val collectionRequest = DeleteGeofenceCollectionRequest {
        this.collectionName = collectionName
    }

    LocationClient { region = "us-east-1" }.use { client ->
        client.deleteGeofenceCollection(collectionRequest)
        println("The geofence collection $collectionName was deleted.")
    }
}
// snippet-end:[location.kotlin.delete.collection.main]

// snippet-start:[location.kotlin.delete.key.main]
/**
 * Deletes the specified key from the key-value store.
 *
 * @param keyName the name of the key to be deleted
 */
suspend fun deleteKey(keyName: String) {
    val keyRequest = DeleteKeyRequest {
        this.keyName = keyName
    }

    LocationClient { region = "us-east-1" }.use { client ->
        client.deleteKey(keyRequest)
        println("The key $keyName was deleted.")
    }
}
// snippet-end:[location.kotlin.delete.key.main]

// snippet-start:[location.kotlin.delete.map.main]
/**
 * Deletes the specified key from the key-value store.
 *
 * @param keyName the name of the key to be deleted
 */
suspend fun deleteMap(mapName: String) {
    val mapRequest = DeleteMapRequest {
        this.mapName = mapName
    }

    LocationClient { region = "us-east-1" }.use { client ->
        client.deleteMap(mapRequest)
        println("The map $mapName was deleted.")
    }
}
// snippet-end:[location.kotlin.delete.map.main]

// snippet-start:[geoplaces.kotlin.search.near.main]

/**
 * Performs a nearby places search based on the provided geographic coordinates (latitude and longitude).
 * The method sends an asynchronous request to search for places within a 1-kilometer radius of the specified location.
 * The results are processed and printed once the search completes successfully.
 */
suspend fun searchNearby() {
    val latitude = 37.7749  // San Francisco
    val longitude = -122.4194
    val queryPosition = listOf(longitude, latitude)

    // Set up the request for searching nearby places.
    val request = SearchNearbyRequest {
        this.queryPosition = queryPosition  // Set the position
        this.queryRadius = 1000L  // Radius in meters (1000 meters = 1 km)
    }

    GeoPlacesClient { region = "us-east-1" }.use { client ->
        val response = client.searchNearby(request)

        // Process the response and print the results.
        response.resultItems?.forEach { result ->
            println("Title: ${result.title}")
            println("Address: ${result.address?.label}")
            println("Distance: ${result.distance} meters")
            println("-------------------------")
        }
    }
}
// snippet-end:[geoplaces.kotlin.search.near.main]

// snippet-start:[geoplaces.kotlin.search.text.main]

/**
 * Searches for a place using the provided search query and prints the detailed information of the first result.
 *
 * @param searchQuery the search query to be used for the place search (ex, coffee shop)
 */
suspend fun searchText(searchQuery: String) {
    val latitude = 37.7749  // San Francisco
    val longitude = -122.4194
    val queryPosition = listOf(longitude, latitude)

    val request = SearchTextRequest {
        this.queryText = searchQuery
        this.biasPosition = queryPosition
    }

    GeoPlacesClient { region = "us-east-1" }.use { client ->
        val response = client.searchText(request)

        response.resultItems?.firstOrNull()?.let { result ->
            val placeId = result.placeId // Get Place ID
            println("Found Place with id: $placeId")

            // Fetch detailed info using getPlace.
            val getPlaceRequest = GetPlaceRequest {
                this.placeId = placeId
            }

            val placeResponse = client.getPlace(getPlaceRequest)

            // Print detailed place information.
            println("Detailed Place Information:")
            println("Title: ${placeResponse.title}")
            println("Address: ${placeResponse.address?.label}")

            // Print each food type (if any).
            placeResponse.foodTypes?.takeIf { it.isNotEmpty() }?.let {
                println("Food Types:")
                it.forEach { foodType ->
                    println("  - $foodType")
                }
            } ?: run {
                println("No food types available.")
            }

            println("-------------------------")
        }
    }
}
// snippet-end:[geoplaces.kotlin.search.text.main]

// snippet-start:[geoplaces.kotlin.geocode.main]
/**
 * Performs reverse geocoding using the AWS Geo Places API.
 * Reverse geocoding is the process of converting geographic coordinates (latitude and longitude) to a human-readable address.
 * This method uses the latitude and longitude of San Francisco as the input, and prints the resulting address.
 */
suspend fun reverseGeocode() {
    val latitude = 37.7749  // San Francisco
    val longitude = -122.4194
    println("Use latitude 37.7749 and longitude -122.4194")

    // AWS expects [longitude, latitude].
    val queryPosition = listOf(longitude, latitude)
    val request = ReverseGeocodeRequest {
        this.queryPosition = queryPosition
    }

    GeoPlacesClient { region = "us-east-1" }.use { client ->
        val response = client.reverseGeocode(request)
        response.resultItems?.forEach { result ->
            println("The address is: ${result.address?.label}")
        }
    }
}
// snippet-end:[geoplaces.kotlin.geocode.main]

// snippet-start:[location.kotlin.calc.distance.main]

/**
 * Calculates the distance between two locations.
 *
 * @param routeCalcName the name of the route calculator to use
 * @return a {@link CompletableFuture} that will complete with a {@link CalculateRouteResponse} containing the distance and estimated duration of the route
 */
suspend fun calcDistance(routeCalcName: String): CalculateRouteResponse {
    // Define coordinates for Seattle, WA and Vancouver, BC.
    val departurePosition = listOf(-122.3321, 47.6062)
    val arrivePosition = listOf(-123.1216, 49.2827)

    val request = CalculateRouteRequest {
        this.calculatorName = routeCalcName
        this.departurePosition = departurePosition
        this.destinationPosition = arrivePosition
        this.travelMode = TravelMode.Car // Options: Car, Truck, Walking, Bicycle
        this.distanceUnit = DistanceUnit.Kilometers// Options: Meters, Kilometers, Miles
    }

    LocationClient { region = "us-east-1" }.use { client ->
        return client.calculateRoute(request)
    }
}
// snippet-end:[location.kotlin.calc.distance.main]

// snippet-start:[location.kotlin.create.calculator.main]
/**
 * Creates a new route calculator with the specified name and data source.
 *
 * @param routeCalcName the name of the route calculator to be created
 */
suspend fun createRouteCalculator(routeCalcName: String): CreateRouteCalculatorResponse {
    val dataSource = "Esri" // or "Here"

    val request = CreateRouteCalculatorRequest {
        this.calculatorName = routeCalcName
        this.dataSource = dataSource
    }

    LocationClient { region = "us-east-1" }.use { client ->
        return client.createRouteCalculator(request)
    }
}
// snippet-end:[location.kotlin.create.calculator.main]

// snippet-start:[location.kotlin.get.device.position.main]
/**
 * Retrieves the position of a device using the provided LocationClient.
 *
 * @param trackerName The name of the tracker associated with the device.
 * @param deviceId    The ID of the device to retrieve the position for.
 */
suspend fun getDevicePosition(trackerName: String, deviceId: String): GetDevicePositionResponse {
    val request = GetDevicePositionRequest {
        this.trackerName = trackerName
        this.deviceId = deviceId
    }

    LocationClient { region = "us-east-1" }.use { client ->
        return client.getDevicePosition(request)
    }
}
// snippet-end:[location.kotlin.get.device.position.main]

// snippet-start:[location.kotlin.update.device.position.main]
/**
 * Updates the position of a device in the location tracking system.
 *
 * @param trackerName the name of the tracker associated with the device
 * @param deviceId    the unique identifier of the device
 */
suspend fun updateDevicePosition(trackerName: String, deviceId: String) {
    val latitude = 37.7749  // Example: San Francisco
    val longitude = -122.4194

    val positionUpdate = DevicePositionUpdate {
        this.deviceId = deviceId
        sampleTime = aws.smithy.kotlin.runtime.time.Instant.now() // Timestamp of position update.
        position = listOf(longitude, latitude) // AWS requires [longitude, latitude]
    }

    val request = BatchUpdateDevicePositionRequest {
        this.trackerName = trackerName
        updates = listOf(positionUpdate)
    }

    LocationClient { region = "us-east-1" }.use { client ->
        client.batchUpdateDevicePosition(request)
    }
}
// snippet-end:[location.kotlin.update.device.position.main]

// snippet-start:[location.kotlin.create.tracker.main]
/**
 * Creates a new tracker resource in your AWS account, which you can use to track the location of devices.
 *
 * @param trackerName the name of the tracker to be created
 * @return a {@link CompletableFuture} that, when completed, will contain the Amazon Resource Name (ARN) of the created tracker
 */
suspend fun createTracker(trackerName: String): String {
    val trackerRequest = CreateTrackerRequest {
        description = "Created using the Kotlin SDK"
        this.trackerName = trackerName
        positionFiltering = PositionFiltering.TimeBased // Options: TimeBased, DistanceBased, AccuracyBased
    }

    LocationClient { region = "us-east-1" }.use { client ->
        val response = client.createTracker(trackerRequest)
        return response.trackerArn
    }
}
// snippet-end:[location.kotlin.create.tracker.main]

// snippet-start:[location.kotlin.put.geo.main]
/**
 * Adds a new geofence to the specified collection.
 *
 * @param collectionName the name of the geofence collection to add the geofence to
 * @param geoId          the unique identifier for the geofence
 */
suspend fun putGeofence(collectionName: String, geoId: String) {
    val geofenceGeometry = GeofenceGeometry {
        polygon = listOf(
            listOf(
                listOf(-122.3381, 47.6101), // First point
                listOf(-122.3281, 47.6101),
                listOf(-122.3281, 47.6201),
                listOf(-122.3381, 47.6201),
                listOf(-122.3381, 47.6101) // Closing the polygon
            )
        )
    }

    val geofenceRequest = PutGeofenceRequest {
        this.collectionName = collectionName
        this.geofenceId = geoId
        this.geometry = geofenceGeometry
    }

    LocationClient { region = "us-east-1" }.use { client ->
        client.putGeofence(geofenceRequest)
    }
}
// snippet-end:[location.kotlin.put.geo.main]

// snippet-start:[location.kotlin.create.collection.main]
/**
 * Creates a new geofence collection.
 *
 * @param collectionName the name of the geofence collection to be created
 */
suspend fun createGeofenceCollection(collectionName: String): String {
    val collectionRequest = CreateGeofenceCollectionRequest {
        this.collectionName = collectionName
        description = "Created by using the AWS SDK for Kotlin"
    }

    LocationClient { region = "us-east-1" }.use { client ->
        val response = client.createGeofenceCollection(collectionRequest)
        return response.collectionArn
    }
}
// snippet-end:[location.kotlin.create.collection.main]

// snippet-start:[location.kotlin.create.key.main]
/**
 * Creates a new API key with the specified name and restrictions.
 *
 * @param keyName the name of the API key to be created
 * @param mapArn  the Amazon Resource Name (ARN) of the map resource to which the API key will be associated
 * @return the Amazon Resource Name (ARN) of the created API key
 */
suspend fun createKey(keyName: String, mapArn: String): String {
    val keyRestrictions = ApiKeyRestrictions {
        allowActions = listOf("geo:GetMap*")
        allowResources = listOf(mapArn)
    }

    val request = CreateKeyRequest {
        this.keyName = keyName
        this.restrictions = keyRestrictions
        noExpiry = true
    }

    LocationClient { region = "us-east-1" }.use { client ->
        val response = client.createKey(request)
        return response.keyArn
    }
}
// snippet-end:[location.kotlin.create.key.main]

// snippet-start:[location.kotlin.create.map.main]
/**
 * Creates a new map with the specified name and configuration.
 *
 * @param mapName the name of the map to be created
 * @return he Amazon Resource Name (ARN) of the created map
 */
suspend fun createMap(mapName: String): String {
    val configuration = MapConfiguration {
        style = "VectorEsriNavigation"
    }

    val mapRequest = CreateMapRequest {
        this.mapName = mapName
        this.configuration = configuration
        description = "A map created using the Kotlin SDK"
    }

    LocationClient { region = "us-east-1" }.use { client ->
        val response = client.createMap(mapRequest)
        return response.mapArn
    }
}
// snippet-end:[location.kotlin.create.map.main]

fun waitForInputToContinue(scanner: Scanner) {
    while (true) {
        println("")
        println("Enter 'c' followed by <ENTER> to continue:")
        val input = scanner.nextLine()
        if (input.trim { it <= ' ' }.equals("c", ignoreCase = true)) {
            println("Continuing with the program...")
            println("")
            break
        } else {
            println("Invalid input. Please try again.")
        }
    }
}
// snippet-end:[location.kotlin.scenario.main]