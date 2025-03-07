package com.example.location;

import com.example.location.scenario.LocationActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.location.LocationAsyncClient;
import software.amazon.awssdk.services.location.LocationClient;
import software.amazon.awssdk.services.location.model.DescribeKeyRequest;
import software.amazon.awssdk.services.location.model.DescribeKeyResponse;
import software.amazon.awssdk.services.location.model.ListGeofencesRequest;
import software.amazon.awssdk.services.location.paginators.ListGeofencesPublisher;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class HelloLocation {

    private static LocationAsyncClient locationAsyncClient;
    private static final Logger logger = LoggerFactory.getLogger(HelloLocation.class);

    // This Singleton pattern ensures that only one `LocationClient`
    // instance is used throughout the application.
    private static LocationAsyncClient getClient() {
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

    public static void main(String[] args) {
        final String usage = """

            Usage:
                <colletionName>

            Where:
                colletionName - The Amazon location collection name. 
            """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String colletionName = args[0];
        listGeofences(colletionName);
    }

    public static CompletableFuture<Void> listGeofences(String collectionName) {
        ListGeofencesRequest geofencesRequest = ListGeofencesRequest.builder()
            .collectionName(collectionName)
            .build();

        ListGeofencesPublisher paginator = getClient().listGeofencesPaginator(geofencesRequest);
        CompletableFuture<Void> future = paginator.subscribe(response -> {
            response.entries().forEach(geofence ->
                logger.info("Geofence ID: " + geofence.geofenceId())
            );
        });
        return future;
    }

}
