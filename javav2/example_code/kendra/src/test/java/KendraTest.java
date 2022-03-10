/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.kendra.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kendra.KendraClient;

import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class KendraTest {

    private static KendraClient kendra;
    private static String indexName = "";
    private static String indexDescription = "";
    private static String indexRoleArn = "";
    private static String indexId = "";
    private static String s3BucketName = "";
    private static String dataSourceName = "";
    private static String dataSourceDescription = "";
    private static String dataSourceRoleArn = "";
    private static String dataSourceId = "";
    private static String text = "";

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on real AWS resources.
        Region region = Region.US_EAST_1;
        kendra = KendraClient.builder().region(region).build();

        try (InputStream input = KendraTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Load a properties file from the class path.
            prop.load(input);

            // Populate the data members required for all tests.
            indexName = prop.getProperty("indexName");
            indexRoleArn = prop.getProperty("indexRoleArn");
            indexDescription = prop.getProperty("indexDescription");
            s3BucketName = prop.getProperty("s3BucketName");
            dataSourceName = prop.getProperty("dataSourceName");
            dataSourceDescription = prop.getProperty("dataSourceDescription");
            dataSourceRoleArn = prop.getProperty("dataSourceRoleArn");
            text = prop.getProperty("text");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(kendra);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateIndex() {
        indexId = CreateIndexAndDataSourceExample.createIndex(kendra, indexDescription, indexName, indexRoleArn);
        assertTrue(!indexId.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateDataSource() {
        dataSourceId = CreateIndexAndDataSourceExample.createDataSource(kendra, s3BucketName, dataSourceName, dataSourceDescription, indexId, dataSourceRoleArn);
        assertTrue(!dataSourceId.isEmpty());
        System.out.println("Test 3 passed");
    }


    @Test
    @Order(4)
    public void SyncDataSource() {
        CreateIndexAndDataSourceExample.startDataSource(kendra, indexId, dataSourceId);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void ListSyncJobs() {

        ListDataSourceSyncJobs.listSyncJobs(kendra, indexId, dataSourceId);
        System.out.println("Test 5 passed");
    }


    @Test
    @Order(6)
    public void QueryIndex() {
        QueryIndex.querySpecificIndex(kendra, indexId, text);
        System.out.println("Test 6 passed");
    }


    @Test
    @Order(7)
    public void DeleteDataSource() {
        DeleteDataSource.deleteSpecificDataSource(kendra, indexId, dataSourceId);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void DeleteIndex() {
        DeleteIndex.deleteSpecificIndex(kendra, indexId);
        System.out.println("Test 8 passed");
    }
}
