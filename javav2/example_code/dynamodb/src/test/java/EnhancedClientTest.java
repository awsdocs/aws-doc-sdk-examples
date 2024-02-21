// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dynamodb.*;
import com.example.dynamodb.enhanced.*;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EnhancedClientTest {

    private static DynamoDbClient ddb;
    private static DynamoDbEnhancedClient enhancedClient;
    private static String enhancedTableName = "";
    private static String enhancedTableKey  = "";
    private static String enhancedTestRegion = "";

    @BeforeAll
    public static void setUp() {
        try (InputStream input = EnhancedClientTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            //load a properties file from class path, inside static method
            prop.load(input);
            enhancedTableName = prop.getProperty("enhancedTableName");
            enhancedTableKey = prop.getProperty("enhancedTableKey");
            enhancedTestRegion = prop.getProperty("enhancedTestRegion");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //Create a DynamoDbClient object
        Region region = Region.of(enhancedTestRegion);
        ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        // Create a DynamoDbEnhancedClient object
        enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();
    }

    @Test
    @Order(1)
    public void CreateTable() {
        EnhancedCreateTable.createTable(enhancedClient);
        System.out.println("\n Test 2 passed");
    }

    @Test
    @Order(2)
    public void PutItem() {
     EnhancedPutItem.putRecord(enhancedClient);
     System.out.println("\n Test 3 passed");
    }

    @Test
    @Order(3)
    public void PutBatchItems() throws IOException {
       EnhancedBatchWriteItems.putBatchRecords(enhancedClient);
       System.out.println("\n Test 4 passed");
    }

    @Test
    @Order(4)
    public void queryWithFilter(){
        Integer customerCount = EnhancedQueryRecordsWithFilter.queryTableFilter(enhancedClient);
        Assertions.assertEquals(1, customerCount);
        System.out.println("\n Test 5 passed");
    }

    @Test
    @Order(5)
    public void GetItem() {
      String result = EnhancedGetItem.getItem(enhancedClient);
      assertTrue(!result.isEmpty());
      System.out.println("\n Test 6 passed");
    }

    @Test
    @Order(6)
    public void QueryRecords() {
        String result = EnhancedQueryRecords.queryTable(enhancedClient);
        assertTrue(!result.isEmpty());
        System.out.println("\n Test 7passed");
    }

    @Test
    @Order(7)
    public void ScanRecords() {
       EnhancedScanRecords.scan(enhancedClient);
       System.out.println("\n Test 8 passed");
    }

    @Test
    @Order(8)
    public void DeleteTable() {
       DeleteTable.deleteDynamoDBTable(ddb,enhancedTableName);
       System.out.println("\n Test 9 passed");
    }
}