// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.fleetwise.scenario;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotAsyncClient;
import software.amazon.awssdk.services.iot.model.CreateThingRequest;
import software.amazon.awssdk.services.iot.model.ResourceAlreadyExistsException;
import software.amazon.awssdk.services.iotfleetwise.IoTFleetWiseAsyncClient;
import software.amazon.awssdk.services.iotfleetwise.model.*;
import java.time.Duration;
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

public class FleetwiseActions {
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

    /**
     * Creates a signal catalog asynchronously.
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

                    return getAsyncClient().createSignalCatalog(request)
                            .whenComplete((response, exception) -> {
                                if (exception != null) {
                                    throw new CompletionException("Failed to create signal catalog: " + exception.getMessage(), exception);
                                }
                            })
                            .thenApply(CreateSignalCatalogResponse::arn);
                });
    }

    /**
     * Delays the execution of the current thread asynchronously for the specified duration.
     *
     * @param millis the duration of the delay in milliseconds
     * @return a {@link CompletableFuture} that completes after the specified delay
     * @throws CompletionException if the sleep operation is interrupted
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
     * Deletes the specified signal catalog asynchronously, if it exists.
     *
     * @param signalCatalogName the name of the signal catalog to delete
     * @return a {@link CompletableFuture} representing the asynchronous operation.
     *         The future will complete without a result if the signal catalog was successfully
     *         deleted or if the signal catalog does not exist. If an exception occurs during
     *         the deletion, the future will complete exceptionally with the corresponding exception.
     */
    public static CompletableFuture<Void> deleteSignalCatalogIfExistsAsync(String signalCatalogName) {
        DeleteSignalCatalogRequest request = DeleteSignalCatalogRequest.builder()
                .name(signalCatalogName)
                .build();

        return getAsyncClient().deleteSignalCatalog(request)
                .handle((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause();
                        if (cause instanceof ResourceNotFoundException) {
                            return null; // Signal catalog doesn't exist — ignore
                        }
                        throw new CompletionException("Failed to delete signal catalog: " + exception.getMessage(), exception);
                    }
                    return null;
                });
    }

    /**
     * Creates a new decoder manifest asynchronously.
     *
     * @param name             the name of the decoder manifest
     * @param modelManifestArn the ARN of the model manifest
     * @return a {@link CompletableFuture} that completes with the ARN of the created decoder manifest
     * @throws CompletionException if there is an error creating the decoder manifest
     */
    public CompletableFuture<String> createDecoderManifestAsync(String name, String modelManifestArn) {
        String interfaceId = "can0";

        // Define CAN interface.
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

        // Create decoder manifest request.
        CreateDecoderManifestRequest request = CreateDecoderManifestRequest.builder()
                .name(name)
                .modelManifestArn(modelManifestArn)
                .networkInterfaces(List.of(networkInterface))
                .signalDecoders(List.of(engineRpmDecoder, vehicleSpeedDecoder))
                .build();

        return getAsyncClient().createDecoderManifest(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        throw new CompletionException("Failed to create decoder manifest: " + exception.getMessage(), exception);
                    }
                })
                .thenApply(CreateDecoderManifestResponse::arn);
    }

    public CompletableFuture<Void> deleteDecoderManifestAsync(String name) {
        DeleteDecoderManifestRequest request = DeleteDecoderManifestRequest.builder()
                .name(name)
                .build();

        return getAsyncClient().deleteDecoderManifest(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        throw new RuntimeException("Failed to delete decoder manifest: " + name, exception);
                    } else {
                        System.out.println("✅ "+ name + " was successfully deleted");
                    }
                })
                .thenApply(response -> null); // Return CompletableFuture<Void>
    }

    /**
     * Asynchronously deletes a vehicle with the specified name.
     *
     * @param vecName the name of the vehicle to be deleted
     * @return a {@link CompletableFuture} that completes when the vehicle has been deleted
     * @throws RuntimeException if the deletion of the vehicle fails
     */
    public CompletableFuture<Void> deleteVehicleAsync(String vecName) {
        DeleteVehicleRequest request = DeleteVehicleRequest.builder()
                .vehicleName(vecName)
                .build();

        return getAsyncClient().deleteVehicle(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        throw new RuntimeException("Failed to delete vehicle: " + vecName, exception);
                    } else {
                        System.out.println("✅ "+ vecName + " was successfully deleted");
                    }
                })
                .thenApply(response -> null); // Return CompletableFuture<Void>
    }


    /**
     * Updates the model manifest asynchronously.
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

    /**
     * Asynchronously updates the decoder manifest with the given name.
     *
     * @param name the name of the decoder manifest to update
     * @return a {@link CompletableFuture} that completes when the update operation is finished
     * @throws CompletionException if the update operation fails
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
                .thenApply(response -> null); // Return void-equivalent
    }

    /**
     * Asynchronously creates a new vehicle in the system.
     *
     * @param vecName      the name of the vehicle to be created
     * @param manifestArn  the Amazon Resource Name (ARN) of the model manifest for the vehicle
     * @param decArn       the Amazon Resource Name (ARN) of the decoder manifest for the vehicle
     * @return a {@link CompletableFuture} that completes when the vehicle has been created, or throws a
     *         {@link CompletionException} if there was an error during the creation process
     */
    public CompletableFuture<Void> createVehicleAsync(String vecName, String manifestArn, String decArn) {
        CreateVehicleRequest request = CreateVehicleRequest.builder()
                .vehicleName(vecName)
                .modelManifestArn(manifestArn)
                .decoderManifestArn(decArn)
                .build();

        return getAsyncClient().createVehicle(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        throw new CompletionException("Failed to create vehicle: " + exception.getMessage(), exception);
                    } else {
                        System.out.println("✅ Vehicle '" + vecName + "' created successfully.");
                    }
                })
                .thenApply(response -> null); // Void return type
    }

    /**
     * Waits for the decoder manifest to become active asynchronously.
     *
     * @param decoderName the name of the decoder to wait for
     * @return a {@link CompletableFuture} that completes when the decoder manifest becomes active, or exceptionally if an error occurs or the manifest becomes invalid
     */
    public CompletableFuture<Void> waitForDecoderManifestActiveAsync(String decoderName) {
        CompletableFuture<Void> result = new CompletableFuture<>();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger secondsElapsed = new AtomicInteger(0);
        AtomicReference<ManifestStatus> lastStatus = new AtomicReference<>(ManifestStatus.DRAFT);

        System.out.print("⏳ Elapsed: 0s | Decoder Status: DRAFT");

        final Runnable pollTask = new Runnable() {
            @Override
            public void run() {
                int elapsed = secondsElapsed.incrementAndGet();

                // Only check status every 5 seconds
                if (elapsed % 5 == 0) {
                    GetDecoderManifestRequest request = GetDecoderManifestRequest.builder()
                            .name(decoderName)
                            .build();

                    getAsyncClient().getDecoderManifest(request)
                            .whenComplete((response, exception) -> {
                                if (exception != null) {
                                    scheduler.shutdown();
                                    result.completeExceptionally(new RuntimeException("❌ Error while polling decoder manifest status: "
                                            + exception.getMessage(), exception));
                                    return;
                                }

                                ManifestStatus status = response.status();
                                lastStatus.set(status);

                                if (status == ManifestStatus.ACTIVE) {
                                    System.out.print("\r⏱️ Elapsed: " + elapsed + "s | Decoder Status: ACTIVE ✅\n");
                                    scheduler.shutdown();
                                    result.complete(null);
                                } else if (status == ManifestStatus.INVALID) {
                                    System.out.print("\r⏱️ Elapsed: " + elapsed + "s | Decoder Status: INVALID ❌\n");
                                    scheduler.shutdown();
                                    result.completeExceptionally(
                                            new RuntimeException("Decoder manifest became INVALID. Cannot proceed."));
                                } else {
                                    // Just update the status text
                                    System.out.print("\r⏱️ Elapsed: " + elapsed + "s | Decoder Status: " + status);
                                }
                            });
                } else {
                    // Still print even if not polling yet
                    System.out.print("\r⏱️ Elapsed: " + elapsed + "s | Decoder Status: " + lastStatus.get());
                }
            }
        };

        scheduler.scheduleAtFixedRate(pollTask, 1, 1, TimeUnit.SECONDS);
        return result;
    }


    /**
     * Waits for the specified model manifest to become active.
     *
     * @param manifestName the name of the model manifest to wait for
     * @throws RuntimeException if the model manifest does not become active within the timeout period, or if an error occurs while polling the manifest status
     */
    public CompletableFuture<Void> waitForModelManifestActiveAsync(String manifestName) {
        CompletableFuture<Void> result = new CompletableFuture<>();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger secondsElapsed = new AtomicInteger(0);
        AtomicReference<ManifestStatus> lastStatus = new AtomicReference<>(ManifestStatus.DRAFT);

        System.out.print("⏳ Elapsed: 0s | Status: DRAFT");

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
                                    scheduler.shutdown();
                                    result.completeExceptionally(new RuntimeException("❌ Error while polling model manifest status: "
                                            + exception.getMessage(), exception));
                                    return;
                                }

                                ManifestStatus status = response.status();
                                lastStatus.set(status);

                                if (status == ManifestStatus.ACTIVE) {
                                    System.out.print("\r⏱️ Elapsed: " + elapsed + "s | Status: ACTIVE ✅\n");
                                    scheduler.shutdown();
                                    result.complete(null);
                                } else if (status == ManifestStatus.INVALID) {
                                    System.out.print("\r⏱️ Elapsed: " + elapsed + "s | Status: INVALID ❌\n");
                                    scheduler.shutdown();
                                    result.completeExceptionally(
                                            new RuntimeException("Model manifest became INVALID. Cannot proceed."));
                                } else {
                                    // Just update the status text
                                    System.out.print("\r⏱️ Elapsed: " + elapsed + "s | Status: " + status);
                                }
                            });
                } else {
                    // Still print even if not polling yet
                    System.out.print("\r⏱️ Elapsed: " + elapsed + "s | Status: " + lastStatus.get());
                }
            }
        };

        scheduler.scheduleAtFixedRate(pollTask, 1, 1, TimeUnit.SECONDS);
        return result;
    }

    /**
     * Asynchronously fetches the details of a vehicle.
     *
     * @param vehicleName the name of the vehicle to fetch details for
     * @return a {@link CompletableFuture} that completes when the vehicle details have been fetched
     */
    public CompletableFuture<Void> getVehicleDetailsAsync(String vehicleName) {
        GetVehicleRequest request = GetVehicleRequest.builder()
                .vehicleName(vehicleName)
                .build();

        return getAsyncClient().getVehicle(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        throw new CompletionException("Failed to fetch vehicle details: " + exception.getMessage(), exception);
                    } else {
                        Map<String, Object> details = new HashMap<>();
                        details.put("vehicleName", response.vehicleName());
                        details.put("arn", response.arn());
                        details.put("modelManifestArn", response.modelManifestArn());
                        details.put("decoderManifestArn", response.decoderManifestArn());
                        details.put("attributes", response.attributes());
                        details.put("creationTime", response.creationTime().toString());
                        details.put("lastModificationTime", response.lastModificationTime().toString());

                        // Print details in a readable format
                        System.out.println("🚗 Vehicle Details:");
                        details.forEach((key, value) -> {
                            System.out.printf("• %-20s : %s%n", key, value);
                        });
                    }
                })
                .thenApply(response -> null); // returning CompletableFuture<Void>
    }

    /**
     * Asynchronously creates an IoT Thing if it does not already exist.
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
                            System.out.println("ℹ️ IoT Thing already exists: " + thingName);
                        } else {
                            throw new CompletionException("Failed to create IoT Thing: " + thingName, exception);
                        }
                    } else {
                        System.out.println("✅ IoT Thing created: " + response.thingName());
                    }
                })
                .thenApply(response -> null);
    }

    /**
     * Deletes a model manifest asynchronously.
     *
     * @param name the name of the model manifest to delete
     * @return a {@link CompletableFuture} that completes when the model manifest has been deleted
     * @throws CompletionException if there was an error deleting the model manifest
     */
    public CompletableFuture<Void> deleteModelManifestAsync(String name) {
        DeleteModelManifestRequest request = DeleteModelManifestRequest.builder()
                .name(name)
                .build();

        return getAsyncClient().deleteModelManifest(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        throw new CompletionException("Failed to delete model manifest: " + exception.getMessage(), exception);
                    } else {
                        System.out.println("✅ "+ name + " was successfully deleted");
                    }
                })
                .thenApply(response -> null); // return type is CompletableFuture<Void>
    }

    /**
     * Deletes a signal catalog asynchronously.
     *
     * @param name the name of the signal catalog to delete
     * @return a {@link CompletableFuture} that completes when the signal catalog is deleted
     * @throws CompletionException if the deletion of the signal catalog fails
     */
    public CompletableFuture<Void> deleteSignalCatalogAsync(String name) {
        DeleteSignalCatalogRequest request = DeleteSignalCatalogRequest.builder()
                .name(name)
                .build();

        return getAsyncClient().deleteSignalCatalog(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        throw new CompletionException("Failed to delete signal catalog: " + exception.getMessage(), exception);
                    } else {
                        System.out.println("✅ "+name + " was successfully deleted");
                    }
                })
                .thenApply(response -> null); // return type is CompletableFuture<Void>
    }

    /**
     * Lists the signal catalog nodes asynchronously.
     *
     * @param signalCatalogName the name of the signal catalog
     * @return a CompletableFuture that, when completed, contains a list of nodes in the specified signal catalog
     * @throws CompletionException if an exception occurs during the asynchronous operation
     */
    public CompletableFuture<List<Node>> listSignalCatalogNodeAsync(String signalCatalogName) {
        ListSignalCatalogNodesRequest request = ListSignalCatalogNodesRequest.builder()
                .name(signalCatalogName)
                .build();

        return getAsyncClient().listSignalCatalogNodes(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        throw new CompletionException("Failed to list signal catalog nodes: " + exception.getMessage(), exception);
                    }
                })
                .thenApply(ListSignalCatalogNodesResponse::nodes); // Return the nodes
    }

    /**
     * Creates a model manifest asynchronously.
     *
     * @param name              the name of the model manifest to create
     * @param signalCatalogArn  the Amazon Resource Name (ARN) of the signal catalog
     * @param nodes             a list of nodes to include in the model manifest
     * @return a {@link CompletableFuture} that completes with the ARN of the created model manifest
     * @throws RuntimeException if an unsupported node type is encountered
     * @throws CompletionException if there is a failure during the model manifest creation
     */

    public CompletableFuture<String> createModelManifestAsync(String name,
                                                                     String signalCatalogArn,
                                                                     List<Node> nodes) {
        // Extract fully qualified names from each Node
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

        return getAsyncClient().createModelManifest(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        throw new CompletionException("Failed to create model manifest: " + exception.getMessage(), exception);
                    }
                })
                .thenApply(CreateModelManifestResponse::arn); // Return the ARN
    }

    /**
     * Deletes a fleet based on the provided fleet ID.
     *
     * @param fleetId the ID of the fleet to be deleted
     * @throws IoTFleetWiseException if an error occurs during the deletion process
     */
    public CompletableFuture<Void> deleteFleetAsync(String fleetId) {
        DeleteFleetRequest request = DeleteFleetRequest.builder()
                .fleetId(fleetId)
                .build();

        return getAsyncClient().deleteFleet(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        throw new CompletionException("Failed to delete fleet: " + exception.getMessage(), exception);
                    } else {
                        System.out.println("✅ "+ fleetId + " was successfully deleted");
                    }
                })
                .thenApply(response -> null); // Returning Void
    }


    /**
     * Creates a new fleet asynchronously using the AWS SDK for Java V2.
     *
     * @param catARN the Amazon Resource Name (ARN) of the signal catalog to associate with the fleet
     * @param fleetId the unique identifier for the fleet
     * @return a {@link CompletableFuture} that completes with the ID of the created fleet
     * @throws RuntimeException if there was an error creating the fleet
     */
    public CompletableFuture<String> createFleetAsync(String catARN, String fleetId) {
        CreateFleetRequest fleetRequest = CreateFleetRequest.builder()
                .fleetId(fleetId)
                .signalCatalogArn(catARN)
                .description("Built using the AWS For Java V2")
                .build();

        return getAsyncClient().createFleet(fleetRequest)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        throw new RuntimeException("Failed to create fleet: " + fleetId, exception);
                    }
                })
                .thenApply(CreateFleetResponse::id); // Extract fleet ID on success
    }
}
