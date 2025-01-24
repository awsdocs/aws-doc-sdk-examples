// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.example.athena.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;

import java.io.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonAthenaTest {

    private static AthenaClient athenaClient;
    private static String nameQuery;

    @BeforeAll
    public static void setUp() throws IOException {
        athenaClient = AthenaClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(ProfileCredentialsProvider.create())
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
    public void CreateNamedQueryExample() {
        assertDoesNotThrow(() -> CreateNamedQueryExample.createNamedQuery(athenaClient, nameQuery));
        System.out.println("Test passed");
    }

    @Test
    @Order(2)
    public void ListNamedQueryExample() {
        assertDoesNotThrow(() -> ListNamedQueryExample.listNamedQueries(athenaClient));
        System.out.println("Test passed");
    }

    @Test
    @Order(3)
    public void ListQueryExecutionsExample() {
        assertDoesNotThrow(() -> ListQueryExecutionsExample.listQueryIds(athenaClient));
        System.out.println("Test passed");
    }

    @Test
    @Order(4)
    public void DeleteNamedQueryExample() {
        String sampleNamedQueryId = DeleteNamedQueryExample.getNamedQueryId(athenaClient, nameQuery);
        assertDoesNotThrow(() -> DeleteNamedQueryExample.deleteQueryName(athenaClient, sampleNamedQueryId));
        System.out.println("Test passed");

    }

    @Test
    @Order(5)
    public void StartQueryExample() {
        String queryExecutionId = StartQueryExample.submitAthenaQuery(athenaClient);
        assertDoesNotThrow(() -> StartQueryExample.waitForQueryToComplete(athenaClient, queryExecutionId));
        assertDoesNotThrow(() -> StartQueryExample.processResultRows(athenaClient, queryExecutionId));
        System.out.println("Test passed");
    }

    @Test
    @Order(6)
    public void StopQueryExecutionExample() {
        String sampleQueryExecutionId = StopQueryExecutionExample.submitAthenaQuery(athenaClient);
        assertDoesNotThrow(() -> StopQueryExecutionExample.stopAthenaQuery(athenaClient, sampleQueryExecutionId));
        System.out.println("Test passed");
    }
}

