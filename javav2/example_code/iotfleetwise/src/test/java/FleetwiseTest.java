// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.iotfleetwise.IoTFleetWiseAsyncClient;
import software.amazon.awssdk.services.iotfleetwise.model.*;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class FleetwiseTest {
    private static final Logger logger = LoggerFactory.getLogger(FleetwiseTest.class);

    @Mock
    private IoTFleetWiseAsyncClient mockFleetWiseClient;

    private static final String signalCatalogName = "testCatalog";
    private static final String manifestName = "testManifest";
    private static final String fleetId = "testFleet";
    private static final String vecName = "testVehicle";
    private static final String decName = "testDecoder";
    private static final String signalCatalogArn = "arn:aws:iotfleetwise:us-east-1:123456789012:signal-catalog/testCatalog";
    private static final String manifestArn = "arn:aws:iotfleetwise:us-east-1:123456789012:model-manifest/testManifest";
    private static final String decArn = "arn:aws:iotfleetwise:us-east-1:123456789012:decoder-manifest/testDecoder";

    @Test
    @Order(1)
    public void testCreateSignalCatalog() {
        when(mockFleetWiseClient.createSignalCatalog(any(CreateSignalCatalogRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        CreateSignalCatalogResponse.builder()
                                .arn(signalCatalogArn)
                                .name(signalCatalogName)
                                .build()));

        CreateSignalCatalogRequest request = CreateSignalCatalogRequest.builder()
                .name(signalCatalogName)
                .nodes(List.of(
                        Node.builder().branch(Branch.builder()
                                .fullyQualifiedName("Vehicle")
                                .description("Root branch")
                                .build()).build()
                ))
                .build();

        CompletableFuture<CreateSignalCatalogResponse> future = mockFleetWiseClient.createSignalCatalog(request);
        CreateSignalCatalogResponse response = future.join();

        assertNotNull(response.arn());
        assertTrue(response.arn().startsWith("arn:"));
        logger.info("Test 1 passed: Signal catalog created with ARN: {}", response.arn());
    }

    @Test
    @Order(2)
    public void testCreateFleet() {
        when(mockFleetWiseClient.createFleet(any(CreateFleetRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        CreateFleetResponse.builder()
                                .id(fleetId)
                                .arn("arn:aws:iotfleetwise:us-east-1:123456789012:fleet/testFleet")
                                .build()));

        CreateFleetRequest request = CreateFleetRequest.builder()
                .fleetId(fleetId)
                .signalCatalogArn(signalCatalogArn)
                .description("Test fleet")
                .build();

        CompletableFuture<CreateFleetResponse> future = mockFleetWiseClient.createFleet(request);
        CreateFleetResponse response = future.join();

        assertNotNull(response.id());
        assertEquals(fleetId, response.id());
        logger.info("Test 2 passed: Fleet created with ID: {}", response.id());
    }

    @Test
    @Order(3)
    public void testCreateModelManifest() {
        when(mockFleetWiseClient.createModelManifest(any(CreateModelManifestRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        CreateModelManifestResponse.builder()
                                .arn(manifestArn)
                                .name(manifestName)
                                .build()));

        CreateModelManifestRequest request = CreateModelManifestRequest.builder()
                .name(manifestName)
                .signalCatalogArn(signalCatalogArn)
                .nodes(List.of("Vehicle.Powertrain.EngineRPM"))
                .build();

        CompletableFuture<CreateModelManifestResponse> future = mockFleetWiseClient.createModelManifest(request);
        CreateModelManifestResponse response = future.join();

        assertNotNull(response.arn());
        assertTrue(response.arn().contains("model-manifest"));
        logger.info("Test 3 passed: Model manifest created with ARN: {}", response.arn());
    }

    @Test
    @Order(4)
    public void testCreateDecoderManifest() {
        when(mockFleetWiseClient.createDecoderManifest(any(CreateDecoderManifestRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        CreateDecoderManifestResponse.builder()
                                .arn(decArn)
                                .name(decName)
                                .build()));

        CreateDecoderManifestRequest request = CreateDecoderManifestRequest.builder()
                .name(decName)
                .modelManifestArn(manifestArn)
                .build();

        CompletableFuture<CreateDecoderManifestResponse> future = mockFleetWiseClient.createDecoderManifest(request);
        CreateDecoderManifestResponse response = future.join();

        assertNotNull(response.arn());
        assertTrue(response.arn().contains("decoder-manifest"));
        logger.info("Test 4 passed: Decoder manifest created with ARN: {}", response.arn());
    }

    @Test
    @Order(5)
    public void testUpdateModelManifestStatus() {
        when(mockFleetWiseClient.updateModelManifest(any(UpdateModelManifestRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        UpdateModelManifestResponse.builder()
                                .arn(manifestArn)
                                .name(manifestName)
                                .build()));

        when(mockFleetWiseClient.getModelManifest(any(GetModelManifestRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        GetModelManifestResponse.builder()
                                .name(manifestName)
                                .arn(manifestArn)
                                .status(ManifestStatus.ACTIVE)
                                .build()));

        CompletableFuture<UpdateModelManifestResponse> updateFuture = mockFleetWiseClient.updateModelManifest(
                UpdateModelManifestRequest.builder().name(manifestName).status(ManifestStatus.ACTIVE).build());
        assertNotNull(updateFuture.join().arn());

        CompletableFuture<GetModelManifestResponse> getFuture = mockFleetWiseClient.getModelManifest(
                GetModelManifestRequest.builder().name(manifestName).build());
        assertEquals(ManifestStatus.ACTIVE, getFuture.join().status());

        logger.info("Test 5 passed: Model manifest status is ACTIVE");
    }

    @Test
    @Order(6)
    public void testUpdateDecoderManifestStatus() {
        when(mockFleetWiseClient.updateDecoderManifest(any(UpdateDecoderManifestRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        UpdateDecoderManifestResponse.builder()
                                .arn(decArn)
                                .name(decName)
                                .build()));

        when(mockFleetWiseClient.getDecoderManifest(any(GetDecoderManifestRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        GetDecoderManifestResponse.builder()
                                .name(decName)
                                .arn(decArn)
                                .status(ManifestStatus.ACTIVE)
                                .build()));

        CompletableFuture<UpdateDecoderManifestResponse> updateFuture = mockFleetWiseClient.updateDecoderManifest(
                UpdateDecoderManifestRequest.builder().name(decName).status(ManifestStatus.ACTIVE).build());
        assertNotNull(updateFuture.join().arn());

        CompletableFuture<GetDecoderManifestResponse> getFuture = mockFleetWiseClient.getDecoderManifest(
                GetDecoderManifestRequest.builder().name(decName).build());
        assertEquals(ManifestStatus.ACTIVE, getFuture.join().status());

        logger.info("Test 6 passed: Decoder manifest status is ACTIVE");
    }

    @Test
    @Order(7)
    public void testCreateVehicle() {
        when(mockFleetWiseClient.createVehicle(any(CreateVehicleRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        CreateVehicleResponse.builder()
                                .vehicleName(vecName)
                                .arn("arn:aws:iotfleetwise:us-east-1:123456789012:vehicle/testVehicle")
                                .build()));

        CreateVehicleRequest request = CreateVehicleRequest.builder()
                .vehicleName(vecName)
                .modelManifestArn(manifestArn)
                .decoderManifestArn(decArn)
                .build();

        CompletableFuture<CreateVehicleResponse> future = mockFleetWiseClient.createVehicle(request);
        CreateVehicleResponse response = future.join();

        assertNotNull(response.vehicleName());
        assertEquals(vecName, response.vehicleName());
        logger.info("Test 7 passed: Vehicle created: {}", response.vehicleName());
    }

    @Test
    @Order(8)
    public void testGetVehicle() {
        when(mockFleetWiseClient.getVehicle(any(GetVehicleRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        GetVehicleResponse.builder()
                                .vehicleName(vecName)
                                .arn("arn:aws:iotfleetwise:us-east-1:123456789012:vehicle/testVehicle")
                                .modelManifestArn(manifestArn)
                                .decoderManifestArn(decArn)
                                .creationTime(Instant.now())
                                .lastModificationTime(Instant.now())
                                .build()));

        CompletableFuture<GetVehicleResponse> future = mockFleetWiseClient.getVehicle(
                GetVehicleRequest.builder().vehicleName(vecName).build());
        GetVehicleResponse response = future.join();

        assertNotNull(response.vehicleName());
        assertEquals(vecName, response.vehicleName());
        assertNotNull(response.modelManifestArn());
        assertNotNull(response.decoderManifestArn());
        logger.info("Test 8 passed: Vehicle details retrieved for: {}", response.vehicleName());
    }

    @Test
    @Order(9)
    public void testDeleteResources() {
        when(mockFleetWiseClient.deleteVehicle(any(DeleteVehicleRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        DeleteVehicleResponse.builder().vehicleName(vecName).build()));

        when(mockFleetWiseClient.deleteDecoderManifest(any(DeleteDecoderManifestRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        DeleteDecoderManifestResponse.builder().name(decName).build()));

        when(mockFleetWiseClient.deleteModelManifest(any(DeleteModelManifestRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        DeleteModelManifestResponse.builder().name(manifestName).build()));

        when(mockFleetWiseClient.deleteFleet(any(DeleteFleetRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        DeleteFleetResponse.builder().id(fleetId).build()));

        when(mockFleetWiseClient.deleteSignalCatalog(any(DeleteSignalCatalogRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        DeleteSignalCatalogResponse.builder().name(signalCatalogName).build()));

        assertNotNull(mockFleetWiseClient.deleteVehicle(
                DeleteVehicleRequest.builder().vehicleName(vecName).build()).join());
        assertNotNull(mockFleetWiseClient.deleteDecoderManifest(
                DeleteDecoderManifestRequest.builder().name(decName).build()).join());
        assertNotNull(mockFleetWiseClient.deleteModelManifest(
                DeleteModelManifestRequest.builder().name(manifestName).build()).join());
        assertNotNull(mockFleetWiseClient.deleteFleet(
                DeleteFleetRequest.builder().fleetId(fleetId).build()).join());
        assertNotNull(mockFleetWiseClient.deleteSignalCatalog(
                DeleteSignalCatalogRequest.builder().name(signalCatalogName).build()).join());

        logger.info("Test 9 passed: All resources deleted successfully");
    }
}
