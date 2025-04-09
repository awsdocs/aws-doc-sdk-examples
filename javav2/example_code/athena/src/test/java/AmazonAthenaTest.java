// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.example.athena.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;

import java.io.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonAthenaTest {
    private static final Logger logger = LoggerFactory.getLogger(AmazonAthenaTest.class);
    private static AthenaClient athenaClient;
    private static String nameQuery;

    @BeforeAll
    public static void setUp() throws IOException {
        athenaClient = AthenaClient.builder()
            .region(Region.US_WEST_2)
            .build();

        try (InputStream input = AmazonAthenaTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            nameQuery = prop.getProperty("nameQuery");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void testCreateNamedQueryExample() {
        assertDoesNotThrow(() -> CreateNamedQueryExample.createNamedQuery(athenaClient, nameQuery));
        logger.info("Test 1 passed");
    }

    @Test
    @Order(2)
    public void testListNamedQueryExample() {
        assertDoesNotThrow(() -> ListNamedQueryExample.listNamedQueries(athenaClient));
        logger.info("Test 2 passed");
    }

    @Test
    @Order(3)
    public void testListQueryExecutionsExample() {
        assertDoesNotThrow(() -> ListQueryExecutionsExample.listQueryIds(athenaClient));
        logger.info("Test 3 passed");
    }

    @Test
    @Order(4)
    public void testDeleteNamedQueryExample() {
        String sampleNamedQueryId = DeleteNamedQueryExample.getNamedQueryId(athenaClient, nameQuery);
        assertDoesNotThrow(() -> DeleteNamedQueryExample.deleteQueryName(athenaClient, sampleNamedQueryId));
        logger.info("Test 4 passed");

    }

    @Test
    @Order(5)
    public void testStartQueryExample() {
        String queryExecutionId = StartQueryExample.submitAthenaQuery(athenaClient);
        assertDoesNotThrow(() -> StartQueryExample.waitForQueryToComplete(athenaClient, queryExecutionId));
        assertDoesNotThrow(() -> StartQueryExample.processResultRows(athenaClient, queryExecutionId));
        logger.info("Test 5 passed");
    }

    @Test
    @Order(6)
    public void testStopQueryExecutionExample() {
        String sampleQueryExecutionId = StopQueryExecutionExample.submitAthenaQuery(athenaClient);
        assertDoesNotThrow(() -> StopQueryExecutionExample.stopAthenaQuery(athenaClient, sampleQueryExecutionId));
        logger.info("Test 6 passed");
    }
}

