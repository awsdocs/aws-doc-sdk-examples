/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import aws.example.athena.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonAthenaTest {
    private static AthenaClient athenaClient;
    private static final String nameQuery = "sampleQuery";

    @BeforeAll
    public static void setUp(){
        athenaClient = AthenaClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
    }
    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateNamedQueryExample() {
       assertDoesNotThrow(() ->CreateNamedQueryExample.createNamedQuery(athenaClient, nameQuery));
       System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void ListNamedQueryExample() {
       assertDoesNotThrow(() ->ListNamedQueryExample.listNamedQueries(athenaClient));
       System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void ListQueryExecutionsExample() {
        assertDoesNotThrow(() -> ListQueryExecutionsExample.listQueryIds(athenaClient));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void DeleteNamedQueryExample() {
        String sampleNamedQueryId = DeleteNamedQueryExample.getNamedQueryId(athenaClient, nameQuery);
        assertFalse(sampleNamedQueryId.isEmpty());
        assertDoesNotThrow(() ->DeleteNamedQueryExample.deleteQueryName(athenaClient, sampleNamedQueryId));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void StartQueryExample() {
        String queryExecutionId = StartQueryExample.submitAthenaQuery(athenaClient);
        assertFalse(queryExecutionId.isEmpty());
        assertDoesNotThrow(() ->StartQueryExample.waitForQueryToComplete(athenaClient, queryExecutionId));
        assertDoesNotThrow(() ->StartQueryExample.processResultRows(athenaClient, queryExecutionId));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void StopQueryExecutionExample() {
        String sampleQueryExecutionId = StopQueryExecutionExample.submitAthenaQuery(athenaClient);
        assertFalse(sampleQueryExecutionId.isEmpty());
        assertDoesNotThrow(() ->StopQueryExecutionExample.stopAthenaQuery(athenaClient, sampleQueryExecutionId));
        System.out.println("Test 6 passed");
    }
}

