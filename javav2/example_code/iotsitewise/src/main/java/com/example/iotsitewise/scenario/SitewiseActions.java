// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.iotsitewise.scenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.iotsitewise.model.BatchPutAssetPropertyValueResponse;
import software.amazon.awssdk.services.iotsitewise.model.CreateGatewayRequest;
import software.amazon.awssdk.services.iotsitewise.model.CreateGatewayResponse;
import software.amazon.awssdk.services.iotsitewise.model.DeleteGatewayRequest;
import software.amazon.awssdk.services.iotsitewise.model.DescribeGatewayRequest;
import software.amazon.awssdk.services.iotsitewise.model.DescribeGatewayResponse;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iotsitewise.IoTSiteWiseAsyncClient;
import software.amazon.awssdk.services.iotsitewise.IoTSiteWiseClient;
import software.amazon.awssdk.services.iotsitewise.model.AssetModelProperty;
import software.amazon.awssdk.services.iotsitewise.model.AssetModelPropertyDefinition;
import software.amazon.awssdk.services.iotsitewise.model.AssetModelSummary;
import software.amazon.awssdk.services.iotsitewise.model.AssetPropertyValue;
import software.amazon.awssdk.services.iotsitewise.model.BatchPutAssetPropertyValueRequest;
import software.amazon.awssdk.services.iotsitewise.model.CreateAssetModelRequest;
import software.amazon.awssdk.services.iotsitewise.model.CreateAssetModelResponse;
import software.amazon.awssdk.services.iotsitewise.model.CreateAssetRequest;
import software.amazon.awssdk.services.iotsitewise.model.CreateAssetResponse;
import software.amazon.awssdk.services.iotsitewise.model.CreatePortalRequest;
import software.amazon.awssdk.services.iotsitewise.model.CreatePortalResponse;
import software.amazon.awssdk.services.iotsitewise.model.DeleteAssetModelRequest;
import software.amazon.awssdk.services.iotsitewise.model.DeleteAssetModelResponse;
import software.amazon.awssdk.services.iotsitewise.model.DeleteAssetRequest;
import software.amazon.awssdk.services.iotsitewise.model.DeleteAssetResponse;
import software.amazon.awssdk.services.iotsitewise.model.DeletePortalRequest;
import software.amazon.awssdk.services.iotsitewise.model.DeletePortalResponse;
import software.amazon.awssdk.services.iotsitewise.model.DescribeAssetModelRequest;
import software.amazon.awssdk.services.iotsitewise.model.DescribePortalRequest;
import software.amazon.awssdk.services.iotsitewise.model.GatewayPlatform;
import software.amazon.awssdk.services.iotsitewise.model.GetAssetPropertyValueRequest;
import software.amazon.awssdk.services.iotsitewise.model.GetAssetPropertyValueResponse;
import software.amazon.awssdk.services.iotsitewise.model.GreengrassV2;
import software.amazon.awssdk.services.iotsitewise.model.ListAssetModelsRequest;
import software.amazon.awssdk.services.iotsitewise.model.Measurement;
import software.amazon.awssdk.services.iotsitewise.model.PropertyDataType;
import software.amazon.awssdk.services.iotsitewise.model.PropertyType;
import software.amazon.awssdk.services.iotsitewise.model.PutAssetPropertyValueEntry;
import software.amazon.awssdk.services.iotsitewise.model.TimeInNanos;
import software.amazon.awssdk.services.iotsitewise.model.Variant;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SitewiseActions {

    private static final Logger logger = LoggerFactory.getLogger(SitewiseActions.class);

    private static IoTSiteWiseAsyncClient ioTSiteWiseAsyncClient;

    private static IoTSiteWiseAsyncClient getAsyncClient() {
        if (ioTSiteWiseAsyncClient == null) {
            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .maxConcurrency(100)
                .connectionTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .writeTimeout(Duration.ofSeconds(60))
                .build();

            ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(2))
                .apiCallAttemptTimeout(Duration.ofSeconds(90))
                .retryPolicy(RetryPolicy.builder()
                    .numRetries(3)
                    .build())
                .build();

            ioTSiteWiseAsyncClient = IoTSiteWiseAsyncClient.builder()
                .httpClient(httpClient)
                .overrideConfiguration(overrideConfig)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        }
        return ioTSiteWiseAsyncClient;
    }

    private static IoTSiteWiseClient getClient() {
        IoTSiteWiseClient client = IoTSiteWiseClient.builder()
            .region(Region.US_EAST_1)
            .build();

        return client;
    }

    // snippet-start:[sitewise.java2_create_asset_model.main]
    /**
     * Creates an asset model asynchronously.
     *
     * @param name the name of the asset model to create
     * @return a {@link CompletableFuture} that completes with the created {@link CreateAssetModelResponse} when the operation is complete
     */
    public CompletableFuture<CreateAssetModelResponse> createAssetModelAsync(String name) {
        PropertyType humidity = PropertyType.builder()
            .measurement(Measurement.builder().build())
            .build();

        PropertyType propertyType = PropertyType.builder()
            .measurement(Measurement.builder().build())
            .build();

        AssetModelPropertyDefinition temperatureProperty = AssetModelPropertyDefinition.builder()
            .name("Temperature")
            .dataType(PropertyDataType.DOUBLE)
            .type(propertyType)
            .build();

        AssetModelPropertyDefinition humidityProperty = AssetModelPropertyDefinition.builder()
            .name("Humidity")
            .dataType(PropertyDataType.DOUBLE)
            .type(humidity)
            .build();

        AssetModelPropertyDefinition measurementProperty = AssetModelPropertyDefinition.builder()
            .name("Temperature")
            .dataType(PropertyDataType.DOUBLE)
            .type(temperatureProperty.type())
            .build();

        CreateAssetModelRequest createAssetModelRequest = CreateAssetModelRequest.builder()
            .assetModelName(name)
            .assetModelDescription("This is my asset model")
            .assetModelProperties(measurementProperty, humidityProperty)
            .build();

        return getAsyncClient().createAssetModel(createAssetModelRequest)
            .handle((response, exception) -> {
                if (exception != null) {
                    throw new RuntimeException("Failed to create asset model: " + exception.getMessage(), exception);
                }
                return response;
            });
    }

    // snippet-end:[sitewise.java2_create_asset_model.main]

    // snippet-start:[sitewise.java2_create_asset.main]
    /**
     * Asynchronously creates an asset with the specified name and model ARN.
     *
     * @param assetName the name of the asset to create
     * @param assetModelArn the ARN of the asset model to associate with the asset
     * @return a {@link CompletableFuture} that completes with the {@link CreateAssetResponse} when the asset creation is complete
     * @throws RuntimeException if the asset creation fails
     */
    public CompletableFuture<CreateAssetResponse> createAssetAsync(String assetName, String assetModelArn) {
        CreateAssetRequest createAssetRequest = CreateAssetRequest.builder()
            .assetModelId(assetModelArn)
            .assetDescription("Created using the AWS SDK for Java")
            .assetName(assetName)
            .build();

        return getAsyncClient().createAsset(createAssetRequest)
            .handle((response, exception) -> {
                if (exception != null) {
                    throw new RuntimeException("Failed to create asset: " + exception.getMessage(), exception);
                }
                return response; // Return the response if successful
            });
    }
    // snippet-end:[sitewise.java2_create_asset.main]

    // snippet-start:[sitewise.java2_put_property.main]
    public CompletableFuture<BatchPutAssetPropertyValueResponse> sendDataToSiteWiseAsync(String assetId, String humPropId, String idHum) {
        Map<String, Double> sampleData = generateSampleData();
        long timestamp = Instant.now().toEpochMilli();

        TimeInNanos time = TimeInNanos.builder()
            .timeInSeconds(timestamp / 1000)
            .offsetInNanos((int) ((timestamp % 1000) * 1000000))
            .build();

        BatchPutAssetPropertyValueRequest request = BatchPutAssetPropertyValueRequest.builder()
            .entries(Arrays.asList(
                PutAssetPropertyValueEntry.builder()
                    .entryId("entry-3")
                    .assetId(assetId)
                    .propertyId(humPropId)
                    .propertyValues(Arrays.asList(
                        AssetPropertyValue.builder()
                            .value(Variant.builder()
                                .doubleValue(sampleData.get("Temperature"))
                                .build())
                            .timestamp(time)
                            .build()
                    ))
                    .build(),
                PutAssetPropertyValueEntry.builder()
                    .entryId("entry-4")
                    .assetId(assetId)
                    .propertyId(idHum)
                    .propertyValues(Arrays.asList(
                        AssetPropertyValue.builder()
                            .value(Variant.builder()
                                .doubleValue(sampleData.get("Humidity"))
                                .build())
                            .timestamp(time)
                            .build()
                    ))
                    .build()
            ))
            .build();

        return getAsyncClient().batchPutAssetPropertyValue(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    throw new RuntimeException("Failed to send data to SiteWise: " + exception.getMessage(), exception);
                } else {
                    logger.info("Data sent successfully.");
                }
            });
    }
    // snippet-end:[sitewise.java2_put_property.main]

    // snippet-start:[sitewise.java2_get_property.main]
    public void getAssetPropValueAsync(String propName, String propId, String assetId) {
        GetAssetPropertyValueRequest assetPropertyValueRequest = GetAssetPropertyValueRequest.builder()
            .propertyId(propId)
            .assetId(assetId)
            .build();

        CompletableFuture<GetAssetPropertyValueResponse> futureResponse = getAsyncClient().getAssetPropertyValue(assetPropertyValueRequest);
        futureResponse.whenComplete((response, exception) -> {
            if (exception != null) {
                // Handle the exception, rethrow as RuntimeException or log it
                throw new RuntimeException("Error occurred while fetching property value: " + exception.getMessage(), exception);
            } else {
                // Process the response
                String assetPropName = response.toString();
                String assetVal = String.valueOf(response.propertyValue().value().doubleValue());

                logger.info("The property name is: " + propName);
                logger.info("The value of this property is " + assetVal);
            }
        }).join(); // You can remove this join() if you don't need to block the thread.
    }
    // snippet-end:[sitewise.java2_get_property.main]

    // snippet-start:[sitewise.java2.describe.asset.model.main]
    /**
     * Finds the property ID of a given property name asynchronously.
     *
     * @param propertyName the name of the property to search for
     * @return a {@link CompletableFuture} that completes with the property ID if found, or null if not found
     * @throws RuntimeException if an exception occurs during the search
     */
    public CompletableFuture<String> findPropertyIdByNameAsync(String propertyName) {
        ListAssetModelsRequest listRequest = ListAssetModelsRequest.builder().build();

        return getAsyncClient().listAssetModels(listRequest)
            .thenCompose(listResponse -> {
                List<CompletableFuture<String>> futures = new ArrayList<>();
                for (AssetModelSummary modelSummary : listResponse.assetModelSummaries()) {
                    DescribeAssetModelRequest describeRequest = DescribeAssetModelRequest.builder()
                        .assetModelId(modelSummary.id())
                        .build();

                    CompletableFuture<String> future = getAsyncClient().describeAssetModel(describeRequest)
                        .thenApply(describeResponse -> {
                            for (AssetModelProperty property : describeResponse.assetModelProperties()) {
                                if (property.name().equals(propertyName)) {
                                    return property.id();
                                }
                            }
                            return null; // Continue searching in other models if not found
                        });

                    futures.add(future);
                }

                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        for (CompletableFuture<String> future : futures) {
                            String propertyId = future.join();
                            if (propertyId != null) {
                                return propertyId;
                            }
                        }
                        return null;
                    });
            })
            .handle((propertyId, exception) -> {
                if (exception != null) {
                    // Handle exception, log it, or rethrow
                    throw new RuntimeException("Failed to find property by name: " + exception.getMessage(), exception);
                }
                return propertyId;
            });
    }
    // snippet-end:[sitewise.java2.describe.asset.model.main]

    // snippet-start:[sitewise.java2.delete.asset.main]
    /**
     * Deletes an asset asynchronously.
     *
     * @param assetId the ID of the asset to be deleted
     * @return a {@link CompletableFuture} that represents the asynchronous operation of deleting the asset
     * @throws RuntimeException if the asset deletion fails
     */
    public CompletableFuture<DeleteAssetResponse> deleteAssetAsync(String assetId) {
        DeleteAssetRequest deleteAssetRequest = DeleteAssetRequest.builder()
            .assetId(assetId)
            .build();

        return getAsyncClient().deleteAsset(deleteAssetRequest)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    throw new RuntimeException("Failed to delete asset with ID: " + assetId + ". Error: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[sitewise.java2.delete.asset.main]

    // snippet-start:[sitewise.java2.delete.asset.model.main]
    /**
     * Asynchronously deletes an Asset Model with the specified ID.
     *
     * @param assetModelId the ID of the Asset Model to delete
     * @return a {@link CompletableFuture} that completes with the {@link DeleteAssetModelResponse} when the operation is complete
     * @throws RuntimeException if the operation fails, containing the error message and the underlying exception
     */
    public CompletableFuture<DeleteAssetModelResponse> deleteAssetModelAsync(String assetModelId) {
        DeleteAssetModelRequest deleteAssetModelRequest = DeleteAssetModelRequest.builder()
            .assetModelId(assetModelId)
            .build();

        return getAsyncClient().deleteAssetModel(deleteAssetModelRequest)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    throw new RuntimeException("Failed to delete asset model with ID: " + assetModelId + ". Error: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[sitewise.java2.delete.asset.model.main]

    // snippet-start:[sitewise.java2.create.portal.main]
    /**
     * Creates a new IoT SiteWise portal asynchronously.
     *
     * @param portalName  the name of the portal to create
     * @param iamRole     the IAM role ARN to use for the portal
     * @param contactEmail the email address of the portal contact
     * @return a {@link CompletableFuture} that completes with the portal ID when the portal is created successfully, or throws a {@link RuntimeException} if the creation fails
     */
    public CompletableFuture<String> createPortalAsync(String portalName, String iamRole, String contactEmail) {
        CreatePortalRequest createPortalRequest = CreatePortalRequest.builder()
            .portalName(portalName)
            .portalDescription("This is my custom IoT SiteWise portal.")
            .portalContactEmail(contactEmail)
            .roleArn(iamRole)
            .build();

        return getAsyncClient().createPortal(createPortalRequest)
            .thenApply(CreatePortalResponse::portalId)
            .whenComplete((portalId, exception) -> {
                if (exception != null) {
                    throw new RuntimeException("Failed to create portal with name: " + portalName + ". Error: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[sitewise.java2.create.portal.main]

    // snippet-start:[sitewise.java2.delete.portal.main]
    /**
     * Deletes a portal asynchronously.
     *
     * @param portalId the ID of the portal to be deleted
     * @return a {@link CompletableFuture} containing the {@link DeletePortalResponse} when the operation is complete
     * @throws RuntimeException if the portal deletion fails, with the error message and the underlying exception
     */
    public CompletableFuture<DeletePortalResponse> deletePortalAsync(String portalId) {
        DeletePortalRequest deletePortalRequest = DeletePortalRequest.builder()
            .portalId(portalId)
            .build();

        return getAsyncClient().deletePortal(deletePortalRequest)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    throw new RuntimeException("Failed to delete portal with ID: " + portalId + ". Error: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[sitewise.java2.delete.portal.main]

    // snippet-start:[sitewise.java2.list.asset.model.main]
    /**
     * Retrieves the asset model ID asynchronously for the given asset model name.
     *
     * @param assetModelName the name of the asset model to retrieve the ID for
     * @return a {@link CompletableFuture} that, when completed, contains the asset model ID, or {@code null} if the asset model is not found
     */
    public CompletableFuture<String> getAssetModelIdAsync(String assetModelName) {
        ListAssetModelsRequest listAssetModelsRequest = ListAssetModelsRequest.builder().build();
        return getAsyncClient().listAssetModels(listAssetModelsRequest)
            .handle((listAssetModelsResponse, exception) -> {
                if (exception != null) {
                    throw new RuntimeException("Failed to retrieve Asset Model ARN: " + exception.getMessage(), exception);
                }
                for (AssetModelSummary assetModelSummary : listAssetModelsResponse.assetModelSummaries()) {
                    if (assetModelSummary.name().equals(assetModelName)) {
                        return assetModelSummary.id();  // Return the ARN if found.
                    }
                }
                return null;
            });
    }
    // snippet-end:[sitewise.java2.list.asset.model.main]

    // snippet-start:[sitewise.java2.describe.portal.main]
    /**
     * Asynchronously describes a portal.
     *
     * @param portalId the ID of the portal to describe
     * @return a {@link CompletableFuture} that, when completed, will contain the URL of the described portal
     * @throws RuntimeException if the portal description operation fails
     */
    public CompletableFuture<String> describePortalAsync(String portalId) {
        DescribePortalRequest request = DescribePortalRequest.builder()
            .portalId(portalId)
            .build();

        return getAsyncClient().describePortal(request)
            .handle((response, exception) -> {
                if (exception != null) {
                    throw new RuntimeException("Failed to describe portal: " + exception.getMessage(), exception);
                }
                // Return the portal URL if the operation is successful
                return response.portalStartUrl();
            });
    }
    // snippet-start:[sitewise.java2.describe.portal.main]

    // snippet-start:[sitewise.java2.create.gateway.main]
    /**
     * Creates a new IoTSitewise gateway asynchronously.
     *
     * @return a {@link CompletableFuture} containing the {@link CreateGatewayResponse} representing the created gateway
     * @throws RuntimeException if there was an error creating the gateway
     */
    public CompletableFuture<CreateGatewayResponse> createGatewayAsync(String gatewayName, String myThing) {
        GreengrassV2 gg = GreengrassV2.builder()
            .coreDeviceThingName(myThing)
            .build();

        GatewayPlatform platform = GatewayPlatform.builder()
            .greengrassV2(gg)
            .build();

        Map<String, String> tag = new HashMap<>();
        tag.put("Environment", "Production");

        CreateGatewayRequest createGatewayRequest = CreateGatewayRequest.builder()
            .gatewayName(gatewayName)
            .gatewayPlatform(platform)
            .tags(tag)
            .build();

        return getAsyncClient().createGateway(createGatewayRequest)
            .handle((response, exception) -> {
                if (exception != null) {
                    throw new RuntimeException("Error creating gateway", exception);
                }
                System.out.println("The ARN of the gateway is " + response.gatewayArn());
                return response;
            });
    }
    // snippet-end:[sitewise.java2.create.gateway.main]

    // snippet-start:[sitewise.java2.delete.gateway.main]
    public CompletableFuture<DeleteAssetResponse> deleteGatewayAsync(String gatewayARN) {
        DeleteGatewayRequest deleteGatewayRequest = DeleteGatewayRequest.builder()
            .gatewayId(gatewayARN)
            .build();

        getAsyncClient().deleteGateway(deleteGatewayRequest)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    throw new RuntimeException("Error creating gateway", exception);
                } else {
                    System.out.println("The Gateway was deleted successfully");
                }
            });
        return null;
    }
    // snippet-end:[sitewise.java2.delete.gateway.main]

    // snippet-start:[sitewise.java2.describe.gateway.main]
    public CompletableFuture<DescribeGatewayResponse> describeGatewayAsync(String gatewayId) {
        DescribeGatewayRequest request = DescribeGatewayRequest.builder()
            .gatewayId(gatewayId)
            .build();

        return getAsyncClient().describeGateway(request)
            .handle((response, exception) -> {
                if (exception != null) {
                    throw new RuntimeException("Failed to describe the SiteWise gateway", exception);
                }
                System.out.println("Gateway Name: " + response.gatewayName());
                System.out.println("Gateway ARN: " + response.gatewayArn());
                System.out.println("Gateway Platform: " + response.gatewayPlatform().toString());
                System.out.println("Gateway Creation Date: " + response.creationDate());
                return response;
            });
    }
    // snippet-end:[sitewise.java2.describe.gateway.main]

    private static Map<String, Double> generateSampleData() {
        Map<String, Double> data = new HashMap<>();
        data.put("Temperature", 23.5);
        data.put("Humidity", 65.0);
        return data;
    }
}
