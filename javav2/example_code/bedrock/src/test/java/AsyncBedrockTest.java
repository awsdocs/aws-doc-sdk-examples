// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.bedrock.async.GetFoundationModelAsync;
import com.example.bedrock.async.ListFoundationModelsAsync;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrock.BedrockAsyncClient;
import software.amazon.awssdk.services.bedrock.model.FoundationModelDetails;
import software.amazon.awssdk.services.bedrock.model.FoundationModelSummary;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class AsyncBedrockTest {

    private static BedrockAsyncClient client;

    @BeforeAll
    public static void setUp() {
        client = BedrockAsyncClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Test
    @Order(1)
    @Tag("IntegrationTest")
    void listFoundationModels() {
        List<FoundationModelSummary> models = ListFoundationModelsAsync.listFoundationModels(client);
        assertNotEquals(Collections.EMPTY_LIST, models);
        System.out.println("Test 1 passed.");
    }

    @Test
    @Order(2)
    @Tag("IntegrationTest")
    void getFoundationModel() {
        String modelId = "amazon.titan-text-lite-v1";
        FoundationModelDetails model = GetFoundationModelAsync.getFoundationModel(client, modelId);
        assertEquals(model.modelId(), modelId);
        System.out.println("Test 2 passed.");
    }

    @Test
    @Order(3)
    @Tag("IntegrationTest")
    void getFoundationModel_WithNonExistentModelId_ShouldThrow() {
        String modelId = "non-existent.model.id";
        assertThrows(
                IllegalArgumentException.class,
                () -> GetFoundationModelAsync.getFoundationModel(client, modelId)
        );
        System.out.println("Test 3 passed.");
    }
}
