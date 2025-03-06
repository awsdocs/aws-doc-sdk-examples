// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.location.scenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.location.LocationAsyncClient;
import software.amazon.awssdk.services.location.model.ApiKeyRestrictions;
import software.amazon.awssdk.services.location.model.BatchUpdateDevicePositionRequest;
import software.amazon.awssdk.services.location.model.BatchUpdateDevicePositionResponse;
import software.amazon.awssdk.services.location.model.CalculateRouteRequest;
import software.amazon.awssdk.services.location.model.CalculateRouteResponse;
import software.amazon.awssdk.services.location.model.CreateGeofenceCollectionRequest;
import software.amazon.awssdk.services.location.model.CreateGeofenceCollectionResponse;
import software.amazon.awssdk.services.location.model.CreateKeyRequest;
import software.amazon.awssdk.services.location.model.CreateKeyResponse;
import software.amazon.awssdk.services.location.model.CreateMapRequest;
import software.amazon.awssdk.services.location.model.CreateMapResponse;
import software.amazon.awssdk.services.location.model.CreateRouteCalculatorRequest;
import software.amazon.awssdk.services.location.model.CreateRouteCalculatorResponse;
import software.amazon.awssdk.services.location.model.CreateTrackerRequest;
import software.amazon.awssdk.services.location.model.DescribeKeyRequest;
import software.amazon.awssdk.services.location.model.DescribeKeyResponse;
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
import software.amazon.awssdk.services.location.paginators.ListGeofencesPublisher;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class LocationActions {

    private static LocationAsyncClient locationAsyncClient;
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
                    logger.info("Estimated Duration: " + response.summary().durationSeconds() / 60 + " minutes");
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

    /**
     * Updates the position of a device in the location tracking system.
     *
     * @param trackerName the name of the tracker associated with the device
     * @param deviceId the unique identifier of the device
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

    /**
     * Lists the geofences in the specified collection.
     *
     * @param collectionName the name of the geofence collection to list
     */
    public CompletableFuture<Void> listGeofences(String collectionName) {
        ListGeofencesRequest geofencesRequest = ListGeofencesRequest.builder()
            .collectionName(collectionName)
            .build();

        ListGeofencesPublisher paginator = locationAsyncClient.listGeofencesPaginator(geofencesRequest);
        CompletableFuture<Void> future = paginator.subscribe(response -> {
            response.entries().forEach(geofence ->
                logger.info("Geofence ID: " + geofence.geofenceId())
            );
        });
        return future;
    }

    /**
     * Adds a new geofence to the specified collection.
     *
     * @param collectionName the name of the geofence collection to add the geofence to
     * @param geoId          the unique identifier for the geofence
     */
    CompletableFuture<PutGeofenceResponse> putGeofence(String collectionName, String geoId) {
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

        // Call the async API and handle response/exceptions
        return locationAsyncClient.putGeofence(geofenceRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("Successfully created geofence: " + geoId);
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while creating geofence.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof LocationException) {
                        throw new CompletionException("AWS Location Service error: " + cause.getMessage(), cause);
                    }
                    throw new CompletionException("Error creating geofence: " + exception.getMessage(), exception);
                }
            });
    }

    /**
     * Creates a new geofence collection.
     *
     * @param collectionName the name of the geofence collection to be created
     */
    CompletableFuture<CreateGeofenceCollectionResponse> createGeofenceCollection(String collectionName) {
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
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("The requested resource was not found while creating the geofence collection.", cause);
                    }
                    throw new CompletionException("Failed to create geofence collection: " + exception.getMessage(), exception);
                }
            });
    }

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
                    logger.info("API Key Created: " + keyArn);
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while creating the API key.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("The requested resource was not found while creating the API key.", cause);
                    }
                    throw new CompletionException("Failed to create API key: " + exception.getMessage(), exception);
                }
            }).thenApply(response -> response != null ? response.keyArn() : null); // Return the key ARN
    }
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
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("The requested resource was not found while creating the map.", cause);
                    }
                    throw new CompletionException("Failed to create map: " + exception.getMessage(), exception);
                }
            }).thenApply(response -> response != null ? response.mapArn() : null); // Return the map ARN
    }
}
