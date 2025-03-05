// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import com.example.entity.scenario.CloudFormationHelper;
import com.example.entity.scenario.EntityResActions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.entityresolution.model.CreateSchemaMappingResponse;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EntityResTests {
    private static final Logger logger = LoggerFactory.getLogger(EntityResTests.class);

    private static String roleARN = "";

    private static String csvMappingARN = "";
    private static String jsonMappingARN = "";

    private static String jobId = "";

    private static String glueBucketName = "";

    private static String csvGlueTableArn = "";

    private static String jsonGlueTableArn = "";

    private static final String STACK_NAME = "EntityResolutionCdkStack";

    private static final String ENTITY_RESOLUTION_ROLE_ARN_KEY = "EntityResolutionRoleArn";
    private static final String GLUE_DATA_BUCKET_NAME_KEY = "GlueDataBucketName";
    private static final String JSON_GLUE_TABLE_ARN_KEY = "JsonErGlueTableArn";
    private static final String CSV_GLUE_TABLE_ARN_KEY = "CsvErGlueTableArn";

    private static String workflowArn = "";
    private static final String jsonSchemaMappingName = "jsonschema-" + UUID.randomUUID();
    private static final String csvSchemaMappingName = "csv-" + UUID.randomUUID();
    private static final String workflowName = "workflow-"+ UUID.randomUUID();
    private static final EntityResActions actions = new EntityResActions();
    @BeforeAll
    public static void setUp() {
        CloudFormationHelper.deployCloudFormationStack(STACK_NAME);
        Map<String, String> outputsMap = CloudFormationHelper.getStackOutputsAsync(STACK_NAME).join();
        roleARN = outputsMap.get(ENTITY_RESOLUTION_ROLE_ARN_KEY);
        glueBucketName = outputsMap.get(GLUE_DATA_BUCKET_NAME_KEY);
        csvGlueTableArn = outputsMap.get(CSV_GLUE_TABLE_ARN_KEY);
        jsonGlueTableArn = outputsMap.get(JSON_GLUE_TABLE_ARN_KEY);

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

        String csv = """
                id,name,email,phone
                1,Alice B. Johnson,alice.johnson@example.com,746-876-9846
                2,Bob Smith Jr.,bob.smith@example.com,987-654-3210
                3,Charlie Black,charlie.black@company.com,345-567-1234
                7,Jane E. Doe,jane_doe@company.com,111-222-3333
                """;

        actions.uploadInputData(glueBucketName, json, csv);
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateMapping() {
        assertDoesNotThrow(() -> {
            CreateSchemaMappingResponse response = actions.createSchemaMappingAsync(jsonSchemaMappingName).join();
            jsonMappingARN = response.schemaArn();
            assertNotNull(jsonMappingARN);
        });

        assertDoesNotThrow(() -> {
            CreateSchemaMappingResponse response = actions.createSchemaMappingAsync(csvSchemaMappingName).join();
            csvMappingARN = response.schemaArn();
            assertNotNull(csvMappingARN);
        });
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testCreateMappingWorkflow() {
        assertDoesNotThrow(() -> {
            workflowArn = actions.createMatchingWorkflowAsync(roleARN, workflowName, glueBucketName, jsonGlueTableArn, jsonSchemaMappingName, csvGlueTableArn, csvSchemaMappingName).join();
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
            actions.getSchemaMappingAsync(jsonSchemaMappingName).join();
        });
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testListSchemaMappings() {
        assertDoesNotThrow(actions::ListSchemaMappings);
        logger.info("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testLTagResources() {
        assertDoesNotThrow(() -> {
            actions.tagEntityResource(csvMappingARN).join();
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
            actions.deleteSchemaMappingAsync(jsonSchemaMappingName).join();
            actions.deleteSchemaMappingAsync(csvSchemaMappingName).join();
            CloudFormationHelper.emptyS3Bucket(glueBucketName);
            CloudFormationHelper.destroyCloudFormationStack(STACK_NAME);
        });
        logger.info("Test 8 passed");
    }
}
