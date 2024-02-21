// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.bedrock.sync.GetFoundationModel;
import com.example.bedrock.sync.ListFoundationModels;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrock.BedrockClient;
import software.amazon.awssdk.services.bedrock.model.FoundationModelSummary;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class SyncBedrockTest {

    private static BedrockClient client;

    @BeforeAll
    public static void setUp() {
        client = BedrockClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Test
    @Order(1)
    @Tag("IntegrationTest")
    void listFoundationModels() {
        List<FoundationModelSummary> models = ListFoundationModels.listFoundationModels(client);
        assertNotEquals(Collections.EMPTY_LIST, models);
        System.out.println("Test 1 passed.");
    }

    @Test
    @Order(2)
    @Tag("IntegrationTest")
    void getFoundationModel() {
        String modelId = "amazon.titan-text-lite-v1";
        var modelDetails = GetFoundationModel.getFoundationModel(client, modelId);
        assertNotNull(modelDetails);
        assertEquals(modelDetails.modelId(), modelId);
        System.out.println("Test 2 passed.");
    }

    @Test
    @Order(3)
    @Tag("IntegrationTest")
    void getFoundationModel_WithNonExistentModelId_ShouldThrow() {
        String modelId = "non-existent.model.id";
        assertThrows(
                IllegalArgumentException.class,
                () -> GetFoundationModel.getFoundationModel(client, modelId)
        );
        System.out.println("Test 3 passed.");
    }
}
