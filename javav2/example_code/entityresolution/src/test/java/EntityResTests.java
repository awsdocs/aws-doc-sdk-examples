// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import com.example.entity.scenario.EntityResActions;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.entityresolution.model.CreateSchemaMappingResponse;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EntityResTests {
    private static final Logger logger = LoggerFactory.getLogger(EntityResTests.class);
    private static String workflowName = "";
    private static String schemaName = "";

    private static String roleARN = "";
    private static String dataS3bucket = "";
    private static String outputBucket = "";
    private static String inputGlueTableArn = "";

    private static String mappingARN = "";

    private static String jobId = "";

    private static String workflowArn ="";
    private static EntityResActions actions = new EntityResActions();
    @BeforeAll
    public static void setUp() {
        Random random = new Random();
        int randomValue = random.nextInt(10000) + 1;
        workflowName = "MyMatchingWorkflow"+randomValue;
        schemaName = "schema"+randomValue;
        Gson gson = new Gson();
        String jsonVal = getSecretValues();
        SecretValues values = gson.fromJson(jsonVal, SecretValues.class);
        roleARN = values.getRoleARN();
        dataS3bucket = values.getDataS3bucket();
        outputBucket = values.getOutputBucket();
        inputGlueTableArn = values.getInputGlueTableArn();

        String json = """
            [
              {
                "id": "1",
                "name": "Alice Johnson",
                "email": "alice.johnson@example.com"
              },
              {
                "id": "2",
                "name": "Bob Smith",
                "email": "bob.smith@example.com"
              },
              {
                "id": "3",
                "name": "Charlie Black",
                "email": "charlie.black@example.com"
              }
            ]
            """;
        if (!actions.doesObjectExist(dataS3bucket)) {
            actions.uploadInputData(dataS3bucket, json);
        } else {
            System.out.println("The JSON exists in " + dataS3bucket);
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateMapping() {
        assertDoesNotThrow(() -> {
            CreateSchemaMappingResponse response = actions.createSchemaMappingAsync(schemaName).join();
            mappingARN = response.schemaArn();
            assertNotNull(mappingARN);
        });
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testCreateMappingWorkflow() {
        assertDoesNotThrow(() -> {
            workflowArn = actions.createMatchingWorkflowAsync(roleARN, workflowName, outputBucket, inputGlueTableArn, schemaName).join();
            assertNotNull(workflowArn);
        });
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testStartWorkflow() {
        assertDoesNotThrow(() -> {
            jobId = actions.startMatchingJobAsync(workflowName).join();
            assertNotNull(workflowArn);
        });
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testGetJobDetails() {
        assertDoesNotThrow(() -> {
            actions.getMatchingJobAsync(jobId, workflowName).join();
        });
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testtSchemaMappingDetails() {
        assertDoesNotThrow(() -> {
            actions.getSchemaMappingAsync(schemaName).join();
        });
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testListSchemaMappings() {
        assertDoesNotThrow(() -> {
            actions.ListSchemaMappings();
        });
        logger.info("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testLTagResources() {
        assertDoesNotThrow(() -> {
            actions.tagEntityResource(mappingARN).join();
        });
        logger.info("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testLDeleteMapping() {
        assertDoesNotThrow(() -> {
            logger.info("Wait 30 mins for the workflow to complete");
            Thread.sleep(1800000);
            actions.deleteMatchingWorkflowAsync(workflowName).join();
        });
        logger.info("Test 8 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .build();
        String secretName = "test/entity";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/cognito (an AWS Secrets Manager secret)")
    class SecretValues {
        private String roleARN;
        private String dataS3bucket;

        private String outputBucket;

        private String inputGlueTableArn;

        public String getRoleARN() {
            return roleARN;
        }

        public String getDataS3bucket() {
            return dataS3bucket;
        }

        public String getOutputBucket() {
            return outputBucket;
        }

        public String getInputGlueTableArn() {
            return inputGlueTableArn;
        }
    }
}
