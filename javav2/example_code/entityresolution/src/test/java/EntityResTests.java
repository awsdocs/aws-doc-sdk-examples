// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import com.example.entity.scenario.EntityResActions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.entityresolution.model.CreateSchemaMappingResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EntityResTests {

    private static String workflowName = "";
    private static String schemaName = "";

    private static String roleARN = "";
    private static String dataS3bucket = "glue-5ffb912c3d534e8493bac675c2a3196d";
    private static String outputBucket = "";
    private static String inputGlueTableArn = "";

    private static String mappingARN = "";

    private static String jobId = "";

    private static String workflowArn ="";
    private static EntityResActions actions = new EntityResActions();
    @BeforeAll
    public static void setUp() {
        workflowName = "MyMatchingWorkflow456";
        schemaName = "schema456";
        roleARN = "arn:aws:iam::814548047983:role/EntityResolutionCdkStack-EntityResolutionRoleB51A51-TSzkkBfrkbfm";
        dataS3bucket = "glue-5ffb912c3d534e8493bac675c2a3196d";
        outputBucket = "s3://entity-resolution-output-entityresolutioncdkstack";
        inputGlueTableArn = "arn:aws:glue:us-east-1:814548047983:table/entity_resolution_db/entity_resolution";

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
            actions.uploadLocalFileAsync(dataS3bucket, json);
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
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testCreateMappingWorkflow() {
        assertDoesNotThrow(() -> {
            workflowArn = actions.createMatchingWorkflowAsync(roleARN, workflowName, outputBucket, inputGlueTableArn, schemaName).join();
            assertNotNull(workflowArn);
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testStartWorkflow() {
        assertDoesNotThrow(() -> {
            jobId = actions.startMatchingJobAsync(workflowName).join();
            assertNotNull(workflowArn);
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testGetJobDetails() {
        assertDoesNotThrow(() -> {
            actions.getMatchingJobAsync(jobId, workflowName).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testtSchemaMappingDetails() {
        assertDoesNotThrow(() -> {
            actions.getSchemaMappingAsync(schemaName).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testListSchemaMappings() {
        assertDoesNotThrow(() -> {
            actions.ListSchemaMappings();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testLTagResources() {
        assertDoesNotThrow(() -> {
            actions.tagEntityResource(mappingARN).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testLDeleteMapping() {
        assertDoesNotThrow(() -> {
            System.out.println("Wait 30 mins for the workflow to complete");
            Thread.sleep(1800000);
            actions.deleteMatchingWorkflowAsync(workflowName).join();
        });
    }


}
