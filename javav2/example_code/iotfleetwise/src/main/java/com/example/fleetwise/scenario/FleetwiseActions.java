// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.fleetwise.scenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotAsyncClient;
import software.amazon.awssdk.services.iot.model.CreateThingRequest;
import software.amazon.awssdk.services.iot.model.ResourceAlreadyExistsException;
import software.amazon.awssdk.services.iotfleetwise.IoTFleetWiseAsyncClient;
import software.amazon.awssdk.services.iotfleetwise.model.Node;
import software.amazon.awssdk.services.iotfleetwise.model.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

// snippet-start:[iotfleetwise.java2.scenario.actions.main]
public class FleetwiseActions {
    private static final Logger logger = LoggerFactory.getLogger(FleetwiseActions.class);
    private static IoTFleetWiseAsyncClient ioTFleetWiseAsyncClient;

    private static IoTFleetWiseAsyncClient getAsyncClient() {
        if (ioTFleetWiseAsyncClient == null) {
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

            ioTFleetWiseAsyncClient = IoTFleetWiseAsyncClient.builder()
                    .httpClient(httpClient)
                    .overrideConfiguration(overrideConfig)
                    .build();
        }
        return ioTFleetWiseAsyncClient;
    }

    // snippet-start:[iotfleetwise.java2.create.catalog.main]

    /**
     * Creates a signal catalog.
     *
     * @param signalCatalogName the name of the signal catalog to be created
     * @return a {@link CompletableFuture} that completes with the Amazon Resource Name (ARN) of the created signal catalog
     */
    public CompletableFuture<String> createSignalCatalogAsync(String signalCatalogName) {
        return deleteSignalCatalogIfExistsAsync(signalCatalogName)
                .thenCompose(ignored -> delayAsync(2000)) // Wait for 2 seconds
                .thenCompose(ignored -> {
                    List<Node> nodes = List.of(
                            Node.builder().branch(
                                    Branch.builder()
                                            .fullyQualifiedName("Vehicle")
                                            .description("Root branch")
                                            .build()
                            ).build(),
                            Node.builder().branch(
                                    Branch.builder()
                                            .fullyQualifiedName("Vehicle.Powertrain")
                                            .description("Powertrain branch")
                                            .build()
                            ).build(),
                            Node.builder().sensor(
                                    Sensor.builder()
                                            .fullyQualifiedName("Vehicle.Powertrain.EngineRPM")
                                            .description("Engine RPM")
                                            .dataType(NodeDataType.DOUBLE)
                                            .unit("rpm")
                                            .build()
                            ).build(),
                            Node.builder().sensor(
                                    Sensor.builder()
                                            .fullyQualifiedName("Vehicle.Powertrain.VehicleSpeed")
                                            .description("Vehicle Speed")
                                            .dataType(NodeDataType.DOUBLE)
                                            .unit("km/h")
                                            .build()
                            ).build()
                    );

                    CreateSignalCatalogRequest request = CreateSignalCatalogRequest.builder()
                            .name(signalCatalogName)
                            .nodes(nodes)
                            .build();

                    CompletableFuture<String> result = new CompletableFuture<>();

                    getAsyncClient().createSignalCatalog(request)
                            .whenComplete((response, exception) -> {
                                if (exception != null) {
                                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;

                                    if (cause instanceof ValidationException) {
                                        result.completeExceptionally(cause);
                                    } else {
                                        result.completeExceptionally(new RuntimeException("Error creating the catalog", cause));
                                    }
                                } else {
                                    result.complete(response.arn());
                                }
                            });

                    return result;
                });
    }
    // snippet-end:[iotfleetwise.java2.create.catalog.main]

    /**
     * Delays the execution of the current thread asynchronously for the specified duration.
     *
     * @param millis the duration of the delay in milliseconds
     * @return a {@link CompletableFuture} that completes after the specified delay
     */
    private static CompletableFuture<Void> delayAsync(long millis) {
        return CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw new CompletionException("Sleep interrupted", e);
            }
        });
    }

    /**
     * Deletes the specified signal catalog.
     *
     * @param signalCatalogName the name of the signal catalog to delete
     * @return a {@link CompletableFuture} representing the asynchronous operation.
     */
    public static CompletableFuture<Void> deleteSignalCatalogIfExistsAsync(String signalCatalogName) {
        DeleteSignalCatalogRequest request = DeleteSignalCatalogRequest.builder()
                .name(signalCatalogName)
                .build();

        return getAsyncClient().deleteSignalCatalog(request)
                .handle((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                        if (cause instanceof ResourceNotFoundException) {
                           throw new CompletionException(new RuntimeException("Signal Catalog not found: " + signalCatalogName));
                        }
                        throw new RuntimeException("Failed to delete signal catalog: " + signalCatalogName, cause);
                    }
                    return null;
                });
    }


    // snippet-start:[iotfleetwise.java2.create.decoder.main]
    /**
     * Creates a new decoder manifest.
     *
     * @param name             the name of the decoder manifest
     * @param modelManifestArn the ARN of the model manifest
     * @return a {@link CompletableFuture} that completes with the ARN of the created decoder manifest
     */
    public CompletableFuture<String> createDecoderManifestAsync(String name, String modelManifestArn) {
        String interfaceId = "can0";
        NetworkInterface networkInterface = NetworkInterface.builder()
                .interfaceId(interfaceId)
                .type(NetworkInterfaceType.CAN_INTERFACE)
                .canInterface(CanInterface.builder()
                        .name("canInterface0")
                        .protocolName("CAN")
                        .protocolVersion("1.0")
                        .build())
                .build();

        // Vehicle.Powertrain.EngineRPM decoder.
        SignalDecoder engineRpmDecoder = SignalDecoder.builder()
                .fullyQualifiedName("Vehicle.Powertrain.EngineRPM")
                .interfaceId(interfaceId)
                .type(SignalDecoderType.CAN_SIGNAL)
                .canSignal(CanSignal.builder()
                        .messageId(100)
                        .isBigEndian(false)
                        .isSigned(false)
                        .startBit(0)
                        .length(16)
                        .factor(1.0)
                        .offset(0.0)
                        .build())
                .build();

        // Vehicle.Powertrain.VehicleSpeed decoder.
        SignalDecoder vehicleSpeedDecoder = SignalDecoder.builder()
                .fullyQualifiedName("Vehicle.Powertrain.VehicleSpeed")
                .interfaceId(interfaceId)
                .type(SignalDecoderType.CAN_SIGNAL)
                .canSignal(CanSignal.builder()
                        .messageId(101)
                        .isBigEndian(false)
                        .isSigned(false)
                        .startBit(16)
                        .length(16)
                        .factor(1.0)
                        .offset(0.0)
                        .build())
                .build();

        CreateDecoderManifestRequest request = CreateDecoderManifestRequest.builder()
                .name(name)
                .modelManifestArn(modelManifestArn)
                .networkInterfaces(List.of(networkInterface))
                .signalDecoders(List.of(engineRpmDecoder, vehicleSpeedDecoder))
                .build();

        CompletableFuture<String> result = new CompletableFuture<>();

        getAsyncClient().createDecoderManifest(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null ? exception.getCause() : exception;

                        if (cause instanceof DecoderManifestValidationException) {
                            result.completeExceptionally(new CompletionException("The request contains signal decoders with validation errors: " + cause.getMessage(), cause));
                        } else {
                            result.completeExceptionally(new CompletionException("Failed to create decoder manifest: " + exception.getMessage(), exception));
                        }
                    } else {
                        result.complete(response.arn()); // Complete successfully with the ARN
                    }
                });

        return result;
    }
    // snippet-end:[iotfleetwise.java2.create.decoder.main]

    // snippet-start:[iotfleetwise.java2.delete.decoder.main]
    /**
     * Deletes a decoder manifest.
     *
     * @param name the name of the decoder manifest to delete
     * @return a {@link CompletableFuture} that completes when the decoder manifest has been deleted
     */
    public CompletableFuture<Void> deleteDecoderManifestAsync(String name) {
        return getAsyncClient().deleteDecoderManifest(DeleteDecoderManifestRequest.builder().name(name).build())
                .handle((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                        if (cause instanceof ResourceNotFoundException) {
                            throw (ResourceNotFoundException) cause;
                        }
                        throw new RuntimeException("Failed to delete the decoder manifest: " + cause);
                    }
                    return null;
                });
    }
    // snippet-end:[iotfleetwise.java2.delete.decoder.main]

    // snippet-start:[iotfleetwise.java2.delete.vehicle.main]

    /**
     * Deletes a vehicle with the specified name.
     *
     * @param vecName the name of the vehicle to be deleted
     * @return a {@link CompletableFuture} that completes when the vehicle has been deleted
     */
    public CompletableFuture<Void> deleteVehicleAsync(String vecName) {
        DeleteVehicleRequest request = DeleteVehicleRequest.builder()
                .vehicleName(vecName)
                .build();

        return getAsyncClient().deleteVehicle(request)
                .handle((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                        if (cause instanceof ResourceNotFoundException) {
                            throw (ResourceNotFoundException) cause;
                        }
                        throw new RuntimeException("Failed to delete the vehicle: " + cause);
                    }
                    return null;
                });
    }
    // snippet-end:[iotfleetwise.java2.delete.vehicle.main]

    // snippet-start:[iotfleetwise.java2.update.manifest.main]

    /**
     * Updates the model manifest.
     *
     * @param name the name of the model manifest to update
     */
    public void updateModelManifestAsync(String name) {
        UpdateModelManifestRequest request = UpdateModelManifestRequest.builder()
                .name(name)
                .status(ManifestStatus.ACTIVE)
                .build();

        getAsyncClient().updateModelManifest(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        throw new CompletionException("Failed to update model manifest: " + exception.getMessage(), exception);
                    }
                })
                .thenApply(response -> null);
    }
    // snippet-end:[iotfleetwise.java2.update.manifest.main]

    // snippet-start:[iotfleetwise.java2.update.decoder.main]

    /**
     * Updates the decoder manifest with the given name.
     *
     * @param name the name of the decoder manifest to update
     * @return a {@link CompletableFuture} that completes when the update operation is finished
     */
    public CompletableFuture<Void> updateDecoderManifestAsync(String name) {
        UpdateDecoderManifestRequest request = UpdateDecoderManifestRequest.builder()
                .name(name)
                .status(ManifestStatus.ACTIVE)
                .build();

        return getAsyncClient().updateDecoderManifest(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        throw new CompletionException("Failed to update decoder manifest: " + exception.getMessage(), exception);
                    }
                })
                .thenApply(response -> null);
    }
    // snippet-end:[iotfleetwise.java2.update.decoder.main]

    // snippet-start:[iotfleetwise.java2.create.vehicle.main]

    /**
     * Creates a new vehicle in the system.
     *
     * @param vecName     the name of the vehicle to be created
     * @param manifestArn the Amazon Resource Name (ARN) of the model manifest for the vehicle
     * @param decArn      the Amazon Resource Name (ARN) of the decoder manifest for the vehicle
     * @return a {@link CompletableFuture} that completes when the vehicle has been created, or throws a
     */
    public CompletableFuture<Void> createVehicleAsync(String vecName, String manifestArn, String decArn) {
        CreateVehicleRequest request = CreateVehicleRequest.builder()
                .vehicleName(vecName)
                .modelManifestArn(manifestArn)
                .decoderManifestArn(decArn)
                .build();

        CompletableFuture<Void> result = new CompletableFuture<>();
        getAsyncClient().createVehicle(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception instanceof CompletionException ? exception.getCause() : exception;

                        if (cause instanceof ResourceNotFoundException) {
                            result.completeExceptionally(cause);
                        } else {
                            result.completeExceptionally(new RuntimeException("Failed to create vehicle: " + cause.getMessage(), cause));
                        }
                    } else {
                        logger.info("Vehicle '{}' created successfully.", vecName);
                        result.complete(null); // mark future as complete
                    }
                });

        return result;
    }

    // snippet-end:[iotfleetwise.java2.create.vehicle.main]

    // snippet-start:[iotfleetwise.java2.decoder.active.main]
    /**
     * Waits for the decoder manifest to become active.
     *
     * @param decoderName the name of the decoder to wait for
     * @return a {@link CompletableFuture} that completes when the decoder manifest becomes active, or exceptionally if an error occurs or the manifest becomes invalid
     */
    public CompletableFuture<Void> waitForDecoderManifestActiveAsync(String decoderName) {
        CompletableFuture<Void> result = new CompletableFuture<>();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger secondsElapsed = new AtomicInteger(0);
        AtomicReference<ManifestStatus> lastStatus = new AtomicReference<>(ManifestStatus.DRAFT);

        logger.info(" Elapsed: 0s | Decoder Status: DRAFT");

        final Runnable pollTask = new Runnable() {
            @Override
            public void run() {
                int elapsed = secondsElapsed.incrementAndGet();

                // Check status every 5 seconds
                if (elapsed % 5 == 0) {
                    GetDecoderManifestRequest request = GetDecoderManifestRequest.builder()
                            .name(decoderName)
                            .build();

                    getAsyncClient().getDecoderManifest(request)
                            .whenComplete((response, exception) -> {
                                if (exception != null) {
                                    Throwable cause = exception instanceof CompletionException ? exception.getCause() : exception;

                                    scheduler.shutdown();
                                    if (cause instanceof ResourceNotFoundException) {
                                        result.completeExceptionally(new RuntimeException("Decoder manifest not found: " + cause.getMessage(), cause));
                                    } else {
                                        result.completeExceptionally(new RuntimeException("Error while polling decoder manifest status: " + exception.getMessage(), exception));
                                    }
                                    return;
                                }

                                ManifestStatus status = response.status();
                                lastStatus.set(status);

                                if (status == ManifestStatus.ACTIVE) {
                                    logger.info("\r Elapsed: {}s | Decoder Status: ACTIVE", elapsed);
                                    scheduler.shutdown();
                                    result.complete(null);
                                } else if (status == ManifestStatus.INVALID) {
                                    logger.info("\r Elapsed: {}s | Decoder Status: INVALID", elapsed);
                                    scheduler.shutdown();
                                    result.completeExceptionally(new RuntimeException("Decoder manifest became INVALID. Cannot proceed."));
                                } else {
                                    logger.info("\r⏱ Elapsed: {}s | Decoder Status: {}", elapsed, status);
                                }
                            });
                } else {
                    logger.info("\r Elapsed: {}s | Decoder Status: {}", elapsed, lastStatus.get());
                }
            }
        };

        // Start the task with an initial delay of 1 second, and repeat every second
        scheduler.scheduleAtFixedRate(pollTask, 1, 1, TimeUnit.SECONDS);
        return result;
    }

    // snippet-end:[iotfleetwise.java2.decoder.active.main]

    // snippet-start:[iotfleetwise.java2.get.manifest.main]

    /**
     * Waits for the specified model manifest to become active.
     *
     * @param manifestName the name of the model manifest to wait for
     */
    public CompletableFuture<Void> waitForModelManifestActiveAsync(String manifestName) {
        CompletableFuture<Void> result = new CompletableFuture<>();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger secondsElapsed = new AtomicInteger(0);
        AtomicReference<ManifestStatus> lastStatus = new AtomicReference<>(ManifestStatus.DRAFT);

        logger.info("Elapsed: 0s | Status: DRAFT");

        final Runnable pollTask = new Runnable() {
            @Override
            public void run() {
                int elapsed = secondsElapsed.incrementAndGet();

                // Only check status every 5 seconds
                if (elapsed % 5 == 0) {
                    GetModelManifestRequest request = GetModelManifestRequest.builder()
                            .name(manifestName)
                            .build();

                    getAsyncClient().getModelManifest(request)
                            .whenComplete((response, exception) -> {
                                if (exception != null) {
                                    Throwable cause = exception instanceof CompletionException ? exception.getCause() : exception;

                                    scheduler.shutdown();
                                    if (cause instanceof ResourceNotFoundException) {
                                        result.completeExceptionally(new RuntimeException("Model manifest not found: " + cause.getMessage(), cause));
                                    } else {
                                        result.completeExceptionally(new RuntimeException("Error while polling model manifest status: " + exception.getMessage(), exception));
                                    }
                                    return;
                                }

                                ManifestStatus status = response.status();
                                lastStatus.set(status);

                                if (status == ManifestStatus.ACTIVE) {
                                    logger.info("\rElapsed: {}s | Status: ACTIVE", elapsed);
                                    scheduler.shutdown();
                                    result.complete(null);
                                } else if (status == ManifestStatus.INVALID) {
                                    logger.info("\rElapsed: {}s | Status: INVALID", elapsed);
                                    scheduler.shutdown();
                                    result.completeExceptionally(new RuntimeException("Model manifest became INVALID. Cannot proceed."));
                                } else {
                                    logger.info("\rElapsed: {}s | Status: {}", elapsed, status);
                                }
                            });
                } else {
                    logger.info("\rElapsed: {}s | Status: {}", elapsed, lastStatus.get());
                }
            }
        };

        // Start the task with an initial delay of 1 second, and repeat every second
        scheduler.scheduleAtFixedRate(pollTask, 1, 1, TimeUnit.SECONDS);
        return result;
    }

    // snippet-end:[iotfleetwise.java2.get.manifest.main]

    // snippet-start:[iotfleetwise.java2.get.vehicle.main]

    /**
     * Fetches the details of a vehicle.
     *
     * @param vehicleName the name of the vehicle to fetch details for
     * @return a {@link CompletableFuture} that completes when the vehicle details have been fetched
     */
    public CompletableFuture<Void> getVehicleDetailsAsync(String vehicleName) {
        GetVehicleRequest request = GetVehicleRequest.builder()
                .vehicleName(vehicleName)
                .build();

        CompletableFuture<Void> result = new CompletableFuture<>();

        getAsyncClient().getVehicle(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception instanceof CompletionException ? exception.getCause() : exception;

                        if (cause instanceof ResourceNotFoundException) {
                            result.completeExceptionally(cause); // don't rewrap
                        } else {
                            result.completeExceptionally(new RuntimeException("Failed to fetch vehicle details: " + cause.getMessage(), cause));
                        }
                    } else {
                        Map<String, Object> details = new HashMap<>();
                        details.put("vehicleName", response.vehicleName());
                        details.put("arn", response.arn());
                        details.put("modelManifestArn", response.modelManifestArn());
                        details.put("decoderManifestArn", response.decoderManifestArn());
                        details.put("attributes", response.attributes());
                        details.put("creationTime", response.creationTime().toString());
                        details.put("lastModificationTime", response.lastModificationTime().toString());

                        logger.info("Vehicle Details:");
                        details.forEach((key, value) -> logger.info("• {} : {}", key, value));

                        result.complete(null); // mark as successful
                    }
                });

        return result;
    }

    // snippet-end:[iotfleetwise.java2.get.vehicle.main]

    /**
     * Creates an IoT Thing if it does not already exist.
     *
     * @param thingName the name of the IoT Thing to create
     * @return a {@link CompletableFuture} that completes when the IoT Thing has been created or if it already exists
     */
    public CompletableFuture<Void> createThingIfNotExistsAsync(String thingName) {
        IotAsyncClient iotClient = IotAsyncClient.builder()
                .region(Region.US_EAST_1)
                .build();

        CreateThingRequest request = CreateThingRequest.builder()
                .thingName(thingName)
                .build();

        return iotClient.createThing(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        if (exception instanceof ResourceAlreadyExistsException) {
                            logger.info(" IoT Thing already exists: " + thingName);
                        } else {
                            throw new CompletionException("Failed to create IoT Thing: " + thingName, exception);
                        }
                    } else {
                        logger.info("IoT Thing created: " + response.thingName());
                    }
                })
                .thenApply(response -> null);
    }

    // snippet-start:[iotfleetwise.java2.delete.model.main]

    /**
     * Deletes a model manifest.
     *
     * @param name the name of the model manifest to delete
     * @return a {@link CompletableFuture} that completes when the model manifest has been deleted
     */
    public CompletableFuture<Void> deleteModelManifestAsync(String name) {
        DeleteModelManifestRequest request = DeleteModelManifestRequest.builder()
                .name(name)
                .build();

        return getAsyncClient().deleteModelManifest(request)
                .handle((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                        if (cause instanceof ResourceNotFoundException) {
                            throw (ResourceNotFoundException) cause;
                        }
                        throw new RuntimeException("Failed to delete the model manifest: " + cause);
                    }
                    logger.info("{} was successfully deleted", name);
                    return null;
                });
    }
    // snippet-end:[iotfleetwise.java2.delete.model.main]

    // snippet-start:[iotfleetwise.java2.delete.catalog.main]

    /**
     * Deletes a signal catalog.
     *
     * @param name the name of the signal catalog to delete
     * @return a {@link CompletableFuture} that completes when the signal catalog is deleted
     */
    public CompletableFuture<Void> deleteSignalCatalogAsync(String name) {
        DeleteSignalCatalogRequest request = DeleteSignalCatalogRequest.builder()
                .name(name)
                .build();

        return getAsyncClient().deleteSignalCatalog(request)
                .handle((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                        if (cause instanceof ResourceNotFoundException) {
                            throw (ResourceNotFoundException) cause;
                        }
                        throw new RuntimeException("Failed to delete the signal catalog: " + cause);
                    }
                    logger.info("{} was successfully deleted", name);
                    return null;
                });
    }
    // snippet-end:[iotfleetwise.java2.delete.catalog.main]

    // snippet-start:[iotfleetwise.java2.list.catalogs.main]
    /**
     * Asynchronously retrieves a list of all nodes in the specified signal catalog.
     *
     * @param signalCatalogName the name of the signal catalog to retrieve nodes for
     * @return a {@link CompletableFuture} that, when completed, contains a {@link List} of {@link Node} objects
     * representing all the nodes in the specified signal catalog
     */
    public CompletableFuture<List<Node>> listSignalCatalogNodeAsync(String signalCatalogName) {
        ListSignalCatalogNodesRequest request = ListSignalCatalogNodesRequest.builder()
                .name(signalCatalogName)
                .build();

        List<Node> allNodes = new ArrayList<>();

        return getAsyncClient().listSignalCatalogNodesPaginator(request)
                .subscribe(response -> allNodes.addAll(response.nodes()))
                .thenApply(v -> allNodes);
    }

    // snippet-end:[iotfleetwise.java2.list.catalogs.main]

    // snippet-start:[iotfleetwise.java2.create.model.main]

    /**
     * Creates a model manifest.
     *
     * @param name             the name of the model manifest to create
     * @param signalCatalogArn the Amazon Resource Name (ARN) of the signal catalog
     * @param nodes            a list of nodes to include in the model manifest
     * @return a {@link CompletableFuture} that completes with the ARN of the created model manifest
     */
    public CompletableFuture<String> createModelManifestAsync(String name,
                                                              String signalCatalogArn,
                                                              List<Node> nodes) {
        // Extract the fully qualified names (FQNs) from each Node in the provided list.
        List<String> fqnList = nodes.stream()
                .map(node -> {
                    if (node.sensor() != null) {
                        return node.sensor().fullyQualifiedName();
                    } else if (node.branch() != null) {
                        return node.branch().fullyQualifiedName();
                    } else if (node.attribute() != null) {
                        return node.attribute().fullyQualifiedName();
                    } else {
                        throw new RuntimeException("Unsupported node type");
                    }
                })
                .toList();

        CreateModelManifestRequest request = CreateModelManifestRequest.builder()
                .name(name)
                .signalCatalogArn(signalCatalogArn)
                .nodes(fqnList)
                .build();


        CompletableFuture<String> result = new CompletableFuture<>();
        getAsyncClient().createModelManifest(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null ? exception.getCause() : exception;

                        if (cause instanceof InvalidSignalsException) {
                            result.completeExceptionally(new CompletionException("The request contains signals that aren't valid: " + cause.getMessage(), cause));
                        } else {
                            result.completeExceptionally(new CompletionException("Failed to create model manifest: " + exception.getMessage(), exception));
                        }
                    } else {
                        result.complete(response.arn()); // Complete successfully with the ARN
                    }
                });

        return result;
    }
    // snippet-end:[iotfleetwise.java2.create.model.main]

    // snippet-start:[iotfleetwise.java2.delete.fleet.main]

    /**
     * Deletes a fleet based on the provided fleet ID.
     *
     * @param fleetId the ID of the fleet to be deleted
     */
    public CompletableFuture<Void> deleteFleetAsync(String fleetId) {
        DeleteFleetRequest request = DeleteFleetRequest.builder()
                .fleetId(fleetId)
                .build();

        return getAsyncClient().deleteFleet(request)
                .handle((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                        if (cause instanceof ResourceNotFoundException) {
                            throw (ResourceNotFoundException) cause;
                        }
                        throw new RuntimeException("Failed to delete the fleet: " + cause);
                    }
                    logger.info("{} was successfully deleted", fleetId);
                    return null;
                });
    }
    // snippet-end:[iotfleetwise.java2.delete.fleet.main]


    // snippet-start:[iotfleetwise.java2.create.fleet.main]

    /**
     * Creates a new fleet.
     *
     * @param catARN  the Amazon Resource Name (ARN) of the signal catalog to associate with the fleet
     * @param fleetId the unique identifier for the fleet
     * @return a {@link CompletableFuture} that completes with the ID of the created fleet
     */
    public CompletableFuture<String> createFleetAsync(String catARN, String fleetId) {
        CreateFleetRequest fleetRequest = CreateFleetRequest.builder()
                .fleetId(fleetId)
                .signalCatalogArn(catARN)
                .description("Built using the AWS For Java V2")
                .build();

        CompletableFuture<String> result = new CompletableFuture<>();
        getAsyncClient().createFleet(fleetRequest)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null ? exception.getCause() : exception;

                        if (cause instanceof ResourceNotFoundException) {
                            result.completeExceptionally(cause);
                        } else {
                            result.completeExceptionally(new RuntimeException("An unexpected error occurred", cause));
                        }
                    } else {
                        result.complete(response.id());
                    }
                });

        return result;
    }
    // snippet-end:[iotfleetwise.java2.create.fleet.main]
}
// snippet-end:[iotfleetwise.java2.scenario.actions.main]