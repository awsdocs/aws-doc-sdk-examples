// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.location.scenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.geoplaces.GeoPlacesAsyncClient;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.geoplaces.model.GetPlaceRequest;
import software.amazon.awssdk.services.geoplaces.model.ReverseGeocodeRequest;
import software.amazon.awssdk.services.geoplaces.model.ReverseGeocodeResponse;
import software.amazon.awssdk.services.geoplaces.model.SearchNearbyRequest;
import software.amazon.awssdk.services.geoplaces.model.SearchNearbyResponse;
import software.amazon.awssdk.services.geoplaces.model.SearchTextRequest;
import software.amazon.awssdk.services.geoplaces.model.SearchTextResponse;
import software.amazon.awssdk.services.location.LocationAsyncClient;
import software.amazon.awssdk.services.location.model.ApiKeyRestrictions;
import software.amazon.awssdk.services.location.model.BatchUpdateDevicePositionRequest;
import software.amazon.awssdk.services.location.model.BatchUpdateDevicePositionResponse;
import software.amazon.awssdk.services.location.model.CalculateRouteRequest;
import software.amazon.awssdk.services.location.model.CalculateRouteResponse;
import software.amazon.awssdk.services.location.model.ConflictException;
import software.amazon.awssdk.services.location.model.CreateGeofenceCollectionRequest;
import software.amazon.awssdk.services.location.model.CreateGeofenceCollectionResponse;
import software.amazon.awssdk.services.location.model.CreateKeyRequest;
import software.amazon.awssdk.services.location.model.CreateMapRequest;
import software.amazon.awssdk.services.location.model.CreateRouteCalculatorRequest;
import software.amazon.awssdk.services.location.model.CreateRouteCalculatorResponse;
import software.amazon.awssdk.services.location.model.CreateTrackerRequest;
import software.amazon.awssdk.services.location.model.DeleteGeofenceCollectionRequest;
import software.amazon.awssdk.services.location.model.DeleteKeyRequest;
import software.amazon.awssdk.services.location.model.DeleteMapRequest;
import software.amazon.awssdk.services.location.model.DeleteRouteCalculatorRequest;
import software.amazon.awssdk.services.location.model.DeleteTrackerRequest;
import software.amazon.awssdk.services.location.model.DevicePositionUpdate;
import software.amazon.awssdk.services.location.model.GeofenceGeometry;
import software.amazon.awssdk.services.location.model.CreateTrackerResponse;
import software.amazon.awssdk.services.location.model.GetDevicePositionRequest;
import software.amazon.awssdk.services.location.model.GetDevicePositionResponse;
import software.amazon.awssdk.services.location.model.ListGeofencesRequest;
import software.amazon.awssdk.services.location.model.LocationException;
import software.amazon.awssdk.services.location.model.MapConfiguration;
import software.amazon.awssdk.services.location.model.PutGeofenceRequest;
import software.amazon.awssdk.services.location.model.PutGeofenceResponse;
import software.amazon.awssdk.services.location.model.ResourceNotFoundException;
import software.amazon.awssdk.services.location.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.location.model.ThrottlingException;
import software.amazon.awssdk.services.location.model.ValidationException;
import software.amazon.awssdk.services.location.paginators.ListGeofencesPublisher;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

// snippet-start:[location.java2.actions.main]
public class LocationActions {

    private static LocationAsyncClient locationAsyncClient;

    private static GeoPlacesAsyncClient geoPlacesAsyncClient;
    private static final Logger logger = LoggerFactory.getLogger(LocationActions.class);

    // This Singleton pattern ensures that only one `LocationClient`
    // instance is used throughout the application.
    private LocationAsyncClient getClient() {
        if (locationAsyncClient == null) {
            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .maxConcurrency(100)
                .connectionTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .writeTimeout(Duration.ofSeconds(60))
                .build();

            ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(2))
                .apiCallAttemptTimeout(Duration.ofSeconds(90))
                .retryStrategy(RetryMode.STANDARD)
                .build();

            locationAsyncClient = LocationAsyncClient.builder()
                .httpClient(httpClient)
                .overrideConfiguration(overrideConfig)
                .build();
        }
        return locationAsyncClient;
    }

    private static GeoPlacesAsyncClient getGeoPlacesClient() {
        if (geoPlacesAsyncClient == null) {
            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .maxConcurrency(100)
                .connectionTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .writeTimeout(Duration.ofSeconds(60))
                .build();

            ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(2))
                .apiCallAttemptTimeout(Duration.ofSeconds(90))
                .retryStrategy(RetryMode.STANDARD)
                .build();

            geoPlacesAsyncClient = GeoPlacesAsyncClient.builder()
                .httpClient(httpClient)
                .overrideConfiguration(overrideConfig)
                .build();
        }
        return geoPlacesAsyncClient;
    }

    // snippet-start:[geoplaces.java2.search.near.main]
    /**
     * Performs a nearby places search based on the provided geographic coordinates (latitude and longitude).
     * The method sends an asynchronous request to search for places within a 1-kilometer radius of the specified location.
     * The results are processed and printed once the search completes successfully.
     *
     * The search is conducted using the specified latitude and longitude for San Francisco, with the search radius
     * set to 1000 meters (1 kilometer).
     */
    public void searchNearBy() {
        double latitude = 37.7749;  // San Francisco
        double longitude = -122.4194;
        List<Double> queryPosition = List.of(longitude, latitude);

        // Set up the request for searching nearby places.
        SearchNearbyRequest request = SearchNearbyRequest.builder()
            .queryPosition(queryPosition)  // Set the position
            .queryRadius(1000L)  // Radius in meters (1000 meters = 1 km)
            .build();

        CompletableFuture<SearchNearbyResponse> futureResponse = getGeoPlacesClient().searchNearby(request);
        futureResponse.whenComplete((response, exception) -> {
            if (exception != null) {
                throw new RuntimeException("Error performing nearby search", exception);
            }

            // Process the response and print the results
            response.resultItems().forEach(result -> {
                System.out.println("Place Name: " + result.placeType().name());
                System.out.println("Address: " + result.address().label());
                System.out.println("Distance: " + result.distance() + " meters");
                System.out.println("-------------------------");
            });
        }).join();
    }
    // snippet-end:[geoplaces.java2.search.near.main]

    // snippet-start:[geoplaces.java2.search.text.main]
    /**
     * Searches for a place using the provided search query and prints the detailed information of the first result.
     *
     * @param searchQuery the search query to be used for the place search (ex, coffee shop)
     */
    public void searchText(String searchQuery) {
        double latitude = 37.7749;  // San Francisco
        double longitude = -122.4194;
        List<Double> queryPosition = List.of(longitude, latitude);

        SearchTextRequest request = SearchTextRequest.builder()
            .queryText(searchQuery)
            .biasPosition(queryPosition)
            .build();

        CompletableFuture<SearchTextResponse> futureResponse = getGeoPlacesClient().searchText(request);
        futureResponse.whenComplete((response, exception) -> {
            if (exception != null) {
                throw new RuntimeException("Error performing place search", exception);
            }

            // Process the response and fetch detailed information about the place.
            response.resultItems().stream().findFirst().ifPresent(result -> {
                String placeId = result.placeId(); // Get Place ID
                System.out.println("Found Place with id: " + placeId);

                // Fetch detailed info using getPlace.
                GetPlaceRequest getPlaceRequest = GetPlaceRequest.builder()
                    .placeId(placeId)
                    .build();

                getGeoPlacesClient().getPlace(getPlaceRequest)
                    .whenComplete((placeResponse, placeException) -> {
                        if (placeException != null) {
                            throw new CompletionException("Error fetching place details", placeException);
                        }

                        // Print detailed place information.
                        System.out.println("Detailed Place Information:");
                        System.out.println("Name: " + placeResponse.placeType().name());
                        System.out.println("Address: " + placeResponse.address().label());

                        // Print each food type (if any).
                        if (placeResponse.foodTypes() != null && !placeResponse.foodTypes().isEmpty()) {
                            System.out.println("Food Types:");
                            placeResponse.foodTypes().forEach(foodType -> {
                                System.out.println("  - " + foodType);
                            });
                        } else {
                            System.out.println("No food types available.");
                        }

                        System.out.println("-------------------------");
                    }).join();
            });
        }).join();
    }
    // snippet-end:[geoplaces.java2.search.text.main]

    // snippet-start:[geoplaces.java2.geocode.main]
    /**
     * Performs reverse geocoding using the AWS Geo Places API.
     * Reverse geocoding is the process of converting geographic coordinates (latitude and longitude) to a human-readable address.
     * This method uses the latitude and longitude of San Francisco as the input, and prints the resulting address.
     */
    public void reverseGeocode() {
        double latitude = 37.7749;  // San Francisco
        double longitude = -122.4194;
        System.out.println("Use latitude 37.7749 and longitude -122.4194");

        // AWS expects [longitude, latitude].
        List<Double> queryPosition = List.of(longitude, latitude);
        ReverseGeocodeRequest request = ReverseGeocodeRequest.builder()
            .queryPosition(queryPosition)
            .build();
        CompletableFuture<ReverseGeocodeResponse> futureResponse =
            getGeoPlacesClient().reverseGeocode(request);

        futureResponse.whenComplete((response, exception) -> {
            if (exception != null) {
                throw new CompletionException("Error performing reverse geocoding", exception);
            }

            response.resultItems().forEach(result ->
                System.out.println("The address is: " + result.address().label())
            );
        }).join();
    }
    // snippet-end:[geoplaces.java2.geocode.main]

    // snippet-start:[location.java2.calc.distance.main]
    /**
     * Calculates the distance between two locations asynchronously.
     *
     * @param routeCalcName the name of the route calculator to use
     * @return a {@link CompletableFuture} that will complete with a {@link CalculateRouteResponse} containing the distance and estimated duration of the route
     */
    public CompletableFuture<CalculateRouteResponse> calcDistanceAsync(String routeCalcName) {
        // Define coordinates for Seattle, WA and Vancouver, BC.
        List<Double> departurePosition = Arrays.asList(-122.3321, 47.6062);
        List<Double> arrivePosition = Arrays.asList(-123.1216, 49.2827);

        CalculateRouteRequest request = CalculateRouteRequest.builder()
            .calculatorName(routeCalcName)
            .departurePosition(departurePosition)
            .destinationPosition(arrivePosition)
            .travelMode("Car") // Options: Car, Truck, Walking, Bicycle
            .distanceUnit("Kilometers") // Options: Meters, Kilometers, Miles
            .build();

        return getClient().calculateRoute(request)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("Total Distance: " + response.summary().distance() + " km");
                    logger.info("Estimated Duration by car is: " + response.summary().durationSeconds() / 60 + " minutes");
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while calculating route.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof LocationException) {
                        throw new CompletionException("AWS Location Service error: " + cause.getMessage(), cause);
                    }

                    throw new CompletionException("Failed to calculate route: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[location.java2.calc.distance.main]

    // snippet-start:[location.java2.create.calculator.main]
    /**
     * Creates a new route calculator with the specified name and data source.
     *
     * @param routeCalcName the name of the route calculator to be created
     */
    public CompletableFuture<CreateRouteCalculatorResponse> createRouteCalculator(String routeCalcName) {
        String dataSource = "Esri"; // or "Here"
        CreateRouteCalculatorRequest request = CreateRouteCalculatorRequest.builder()
            .calculatorName(routeCalcName)
            .dataSource(dataSource)
            .build();

        return getClient().createRouteCalculator(request)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("Route calculator created: " + response.calculatorArn());
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while creating route calculator.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof LocationException) {
                        throw new CompletionException("AWS Location Service error: " + cause.getMessage(), cause);
                    }
                    throw new CompletionException("Failed to create route calculator: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[location.java2.create.calculator.main]

    // snippet-start:[location.java2.get.device.position.main]
    /**
     * Retrieves the position of a device using the provided LocationClient.
     *
     * @param trackerName The name of the tracker associated with the device.
     * @param deviceId    The ID of the device to retrieve the position for.
     * @throws RuntimeException If there is an error fetching the device position.
     */
    public CompletableFuture<GetDevicePositionResponse> getDevicePosition(String trackerName, String deviceId) {
        GetDevicePositionRequest request = GetDevicePositionRequest.builder()
            .trackerName(trackerName)
            .deviceId(deviceId)
            .build();

        return getClient().getDevicePosition(request)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("Device Position: " + response.position());
                    logger.info("Received at: " + response.receivedTime());
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while fetching device position.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof LocationException) {
                        throw new CompletionException("AWS Location Service error: " + cause.getMessage(), cause);
                    }
                    throw new CompletionException("Error fetching device position: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[location.java2.get.device.position.main]

    // snippet-start:[location.java2.update.device.position.main]
    /**
     * Updates the position of a device in the location tracking system.
     *
     * @param trackerName the name of the tracker associated with the device
     * @param deviceId    the unique identifier of the device
     * @throws RuntimeException if an error occurs while updating the device position
     */
    public CompletableFuture<BatchUpdateDevicePositionResponse> updateDevicePosition(String trackerName, String deviceId) {
        double latitude = 37.7749;  // Example: San Francisco
        double longitude = -122.4194;

        DevicePositionUpdate positionUpdate = DevicePositionUpdate.builder()
            .deviceId(deviceId)
            .sampleTime(Instant.now()) // Timestamp of position update.
            .position(Arrays.asList(longitude, latitude)) // AWS requires [longitude, latitude]
            .build();

        BatchUpdateDevicePositionRequest request = BatchUpdateDevicePositionRequest.builder()
            .trackerName(trackerName)
            .updates(positionUpdate)
            .build();

        return getClient().batchUpdateDevicePosition(request)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info(deviceId + " was updated in the location tracking system");
                    if (!response.errors().isEmpty()) {
                        logger.error("Errors updating position: " + response.errors());
                    }
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while updating device position.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof LocationException) {
                        throw new CompletionException("AWS Location Service error: " + cause.getMessage(), cause);
                    }
                    throw new CompletionException("Error updating device position: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[location.java2.update.device.position.main]

    // snippet-start:[location.java2.create.tracker.main]
    /**
     * Creates a new tracker resource in your AWS account, which you can use to track the location of devices.
     *
     * @param trackerName the name of the tracker to be created
     * @return a {@link CompletableFuture} that, when completed, will contain the Amazon Resource Name (ARN) of the created tracker
     */
    public CompletableFuture<String> createTracker(String trackerName) {
        CreateTrackerRequest trackerRequest = CreateTrackerRequest.builder()
            .description("Created using the Java V2 SDK")
            .trackerName(trackerName)
            .positionFiltering("TimeBased") // Options: TimeBased, DistanceBased, AccuracyBased
            .build();

        return locationAsyncClient.createTracker(trackerRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("The tracker ARN is " + response.trackerArn());
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while creating tracker.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof LocationException) {
                        throw new CompletionException("AWS Location Service error: " + cause.getMessage(), cause);
                    }
                    throw new CompletionException("Error creating tracker: " + exception.getMessage(), exception);
                }
            }).thenApply(CreateTrackerResponse::trackerArn); // Return tracker ARN
    }
    // snippet-start:[location.java2.create.tracker.main]

    // snippet-start:[location.java2.put.geo.main]
    /**
     * Adds a new geofence to the specified collection.
     *
     * @param collectionName the name of the geofence collection to add the geofence to
     * @param geoId          the unique identifier for the geofence
     */
    public CompletableFuture<PutGeofenceResponse> putGeofence(String collectionName, String geoId) {
        // Define the geofence geometry (polygon)
        GeofenceGeometry geofenceGeometry = GeofenceGeometry.builder()
            .polygon(List.of(
                List.of(
                    List.of(-122.3381, 47.6101), // First point
                    List.of(-122.3281, 47.6101),
                    List.of(-122.3281, 47.6201),
                    List.of(-122.3381, 47.6201),
                    List.of(-122.3381, 47.6101) // Closing the polygon
                )
            ))
            .build();

        PutGeofenceRequest geofenceRequest = PutGeofenceRequest.builder()
            .collectionName(collectionName) // Specify the collection
            .geofenceId(geoId) // Unique ID for the geofence
            .geometry(geofenceGeometry)
            .build();

        return locationAsyncClient.putGeofence(geofenceRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("Successfully created geofence: " + geoId);
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while creating geofence.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ValidationException) {
                        throw new CompletionException("AWS Location Service error: " + cause.getMessage(), cause);
                    }
                    throw new CompletionException("Error creating geofence: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[location.java2.put.geo.main]

    // snippet-start:[location.java2.create.collection.main]
    /**
     * Creates a new geofence collection.
     *
     * @param collectionName the name of the geofence collection to be created
     */
    public CompletableFuture<CreateGeofenceCollectionResponse> createGeofenceCollection(String collectionName) {
        CreateGeofenceCollectionRequest collectionRequest = CreateGeofenceCollectionRequest.builder()
            .collectionName(collectionName)
            .description("Created by using the AWS SDK for Java")
            .build();

        return getClient().createGeofenceCollection(collectionRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("The ARN is " + response.collectionArn());
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while creating the geofence collection.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ConflictException) {
                        throw new CompletionException("The geofence collection was not created due to a conflict. ", cause);
                    }
                    throw new CompletionException("Failed to create geofence collection: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[location.java2.create.collection.main]

    // snippet-start:[location.java2.create.key.main]
    /**
     * Creates a new API key with the specified name and restrictions.
     *
     * @param keyName    the name of the API key to be created
     * @param mapArn     the Amazon Resource Name (ARN) of the map resource to which the API key will be associated
     * @return a {@link CompletableFuture} that completes with the Amazon Resource Name (ARN) of the created API key,
     *         or {@code null} if the operation failed
     */
    public CompletableFuture<String> createKey(String keyName, String mapArn) {
        ApiKeyRestrictions keyRestrictions = ApiKeyRestrictions.builder()
            .allowActions("geo:GetMap*")
            .allowResources(mapArn)
            .build();

        CreateKeyRequest request = CreateKeyRequest.builder()
            .keyName(keyName)
            .restrictions(keyRestrictions)
            .noExpiry(true)
            .build();

        return getClient().createKey(request)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    String keyArn = response.keyArn();
                    logger.info("The API key was successfully created: " + keyArn);
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while creating the API key.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ThrottlingException) {
                        throw new CompletionException("Request throttling was detected.", cause);
                    }
                    throw new CompletionException("Failed to create API key: " + exception.getMessage(), exception);
                }
            }).thenApply(response -> response != null ? response.keyArn() : null); // Return the key ARN
    }
    // snippet-end:[location.java2.create.key.main]

    // snippet-start:[location.java2.create.map.main]
    /**
     * Creates a new map with the specified name and configuration.
     *
     * @param mapName the name of the map to be created
     * @return a {@link CompletableFuture} that, when completed, will contain the Amazon Resource Name (ARN) of the created map
     * @throws CompletionException if an error occurs while creating the map, such as exceeding the service quota
     */
    public CompletableFuture<String> createMap(String mapName) {
        MapConfiguration configuration = MapConfiguration.builder()
            .style("VectorEsriNavigation")
            .build();

        CreateMapRequest mapRequest = CreateMapRequest.builder()
            .mapName(mapName)
            .configuration(configuration)
            .description("A map created using the Java V2 API")
            .build();

        return getClient().createMap(mapRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("The Map ARN is " + response.mapArn());
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while creating the map.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ServiceQuotaExceededException) {
                        throw new CompletionException("The operation was denied because the request would exceed the maximum quota.", cause);
                    }
                    throw new CompletionException("Failed to create map: " + exception.getMessage(), exception);
                }
            }).thenApply(response -> response != null ? response.mapArn() : null); // Return the map ARN
    }
    // snippet-end:[location.java2.create.map.main]

    // snippet-start:[location.java2.delete.collection.main]
    /**
     * Deletes a geofence collection asynchronously.
     *
     * @param collectionName the name of the geofence collection to be deleted
     * @return a {@link CompletableFuture} that completes when the geofence collection has been deleted
     */
    public CompletableFuture<Void> deleteGeofenceCollectionAsync(String collectionName) {
        DeleteGeofenceCollectionRequest collectionRequest = DeleteGeofenceCollectionRequest.builder()
            .collectionName(collectionName)
            .build();

        return getClient().deleteGeofenceCollection(collectionRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("The geofence collection {} was deleted.", collectionName);
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while deleting the geofence collection.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("The requested geofence collection was not found.", cause);
                    }

                    throw new CompletionException("Failed to delete geofence collection: " + exception.getMessage(), exception);
                }
            }).thenApply(response -> null); // Ensures the method returns CompletableFuture<Void>
    }
    // snippet-end:[location.java2.delete.collection.main]

    // snippet-start:[location.java2.delete.key.main]
    /**
     * Deletes the specified key from the key-value store.
     *
     * @param keyName the name of the key to be deleted
     * @return a {@link CompletableFuture} that completes when the key has been deleted
     * @throws CompletionException if the key was not found or if an error occurred during the deletion process
     */
    public CompletableFuture<Void> deleteKey(String keyName) {
        DeleteKeyRequest keyRequest = DeleteKeyRequest.builder()
            .keyName(keyName)
            .build();

        return getClient().deleteKey(keyRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("The key {} was deleted.", keyName);
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while deleting the geofence collection.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("The key was not found.", cause);
                    }

                    throw new CompletionException("Failed to delete key: " + exception.getMessage(), exception);
                }
            }).thenApply(response -> null); // Ensures the method returns CompletableFuture<Void>
    }
    // snippet-end:[location.java2.delete.key.main]

    // snippet-start:[location.java2.delete.map.main]
    /**
     * Deletes a map with the specified name.
     *
     * @param mapName the name of the map to be deleted
     * @return a {@link CompletableFuture} that completes when the map deletion is successful, or throws a {@link CompletionException} if an error occurs
     */
    public CompletableFuture<Void> deleteMap(String mapName) {
        DeleteMapRequest mapRequest = DeleteMapRequest.builder()
            .mapName(mapName)
            .build();

        return getClient().deleteMap(mapRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("The map {} was deleted.", mapName);
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while deleting the map.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("The map was not found.", cause);
                    }

                    throw new CompletionException("Failed to delete map: " + exception.getMessage(), exception);
                }
            }).thenApply(response -> null);
    }
    // snippet-end:[location.java2.delete.map.main]

    // snippet-start:[location.java2.delete.tracker.main]
    /**
     * Deletes a tracker with the specified name.
     *
     * @param trackerName the name of the tracker to be deleted
     * @return a {@link CompletableFuture} that completes when the tracker has been deleted
     * @throws CompletionException if an error occurs while deleting the tracker
     *     - if the tracker was not found, a {@link ResourceNotFoundException} is thrown wrapped in the CompletionException
     *     - if any other error occurs, a generic CompletionException is thrown with the error message
     */
    public CompletableFuture<Void> deleteTracker(String trackerName) {
        DeleteTrackerRequest trackerRequest = DeleteTrackerRequest.builder()
            .trackerName(trackerName)
            .build();

        return getClient().deleteTracker(trackerRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("The tracker {} was deleted.", trackerName);
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while deleting the tracker.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("The tracker was not found.", cause);
                    }

                    throw new CompletionException("Failed to delete the tracker: " + exception.getMessage(), exception);
                }
            }).thenApply(response -> null);
    }
    // snippet-end:[location.java2.delete.tracker.main]

    // snippet-start:[location.java2.delete.calculator.main]
    /**
     * Deletes a route calculator from the system.
     *
     * @param calcName the name of the route calculator to delete
     * @return a {@link CompletableFuture} that completes when the route calculator has been deleted
     * @throws CompletionException if an error occurs while deleting the route calculator
     *                            - If the route calculator was not found, a {@link ResourceNotFoundException} will be thrown
     *                            - If any other error occurs, a generic {@link CompletionException} will be thrown
     */
    public CompletableFuture<Void> deleteRouteCalculator(String calcName) {
        DeleteRouteCalculatorRequest calculatorRequest = DeleteRouteCalculatorRequest.builder()
            .calculatorName(calcName)
            .build();

        return getClient().deleteRouteCalculator(calculatorRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("The route calculator {} was deleted.", calcName);
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while deleting the route calculator.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("The route calculator was not found.", cause);
                    }

                    throw new CompletionException("Failed to delete the route calculator: " + exception.getMessage(), exception);
                }
            }).thenApply(response -> null);
    }
    // snippet-end:[location.java2.delete.calculator.main]
}
// snippet-end:[location.java2.actions.main]
