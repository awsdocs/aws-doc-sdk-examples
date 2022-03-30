/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.appsync.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import software.amazon.awssdk.services.appsync.AppSyncClient;
import java.io.*;
import java.util.*;
import  software.amazon.awssdk.regions.Region;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppSyncTest {

    private static AppSyncClient appSyncClient;
    private static String apiId="";
    private static String dsName="";
    private static String dsRole="";
    private static String tableName="";
    private static String keyId = "";  // Gets dynamically set in a test.
    private static String dsARN = ""; // Gets dynamically set in a test.
    private static String reg = "";

    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_EAST_1;
        reg = region.toString();
        appSyncClient = AppSyncClient.builder()
                .region(region)
                .build();

        try (InputStream input = AppSyncTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            apiId = prop.getProperty("apiId");
            dsName = prop.getProperty("dsName");
            dsRole= prop.getProperty("dsRole");
            tableName= prop.getProperty("tableName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(appSyncClient);
        assertTrue(!apiId.isEmpty());
        assertTrue(!dsName.isEmpty());
        assertTrue(!dsRole.isEmpty());
        assertTrue(!tableName.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateApiKey() {
        keyId = CreateApiKey.createKey(appSyncClient, apiId);
        assertTrue(!keyId.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateDataSource() {
        dsARN = CreateDataSource.createDS(appSyncClient, dsName, reg, dsRole, apiId, tableName);
        assertTrue(!dsARN.isEmpty());
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void GetDataSource() {
        GetDataSource.getDS(appSyncClient, apiId, dsName);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void ListGraphqlApis() {
        ListGraphqlApis.getApis(appSyncClient);
        System.out.println("Test 5 passed");
     }


    @Test
    @Order(6)
    public void ListApiKeys() {
        ListApiKeys.getKeys(appSyncClient,apiId);
        System.out.println("Test 6 passed");

    }

    @Test
    @Order(7)
     public void DeleteDataSource() {
        DeleteDataSource.deleteDS(appSyncClient, apiId, dsName) ;
        System.out.println("Test 7 passed");
     }

    @Test
    @Order(8)
     public void DeleteApiKey() {
         DeleteApiKey.deleteKey(appSyncClient, keyId, apiId) ;
         System.out.println("Test 8 passed");
     }
}
