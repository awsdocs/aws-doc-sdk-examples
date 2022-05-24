/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.ecs.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import java.io.*;
import java.util.*;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EcsTest {

    private static  EcsClient ecsClient;
    private static Region region;
    private static String clusterName = "";
    private static String clusterARN = "";
    private static String taskId = "";
    private static String securityGroups = "";
    private static String subnet = "";
    private static String serviceName = "";
    private static String serviceArn = "";
    private static String taskDefinition = "";

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
       region = Region.US_EAST_1;
        ecsClient = EcsClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = EcsTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            clusterName = prop.getProperty("clusterName");
            taskId = prop.getProperty("taskId");
            subnet = prop.getProperty("subnet");
            securityGroups = prop.getProperty("securityGroups");
            serviceName = prop.getProperty("serviceName");
            taskDefinition = prop.getProperty("taskDefinition");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(ecsClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateCluster() {
        clusterARN = CreateCluster.createGivenCluster(ecsClient, clusterName);
        assertTrue(!clusterARN.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void ListClusters() {
        ListClusters.listAllClusters(ecsClient);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DescribeClusters() {
        DescribeClusters.descCluster(ecsClient, clusterARN);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void ListTaskDefinitions() {
        ListTaskDefinitions.getAllTasks(ecsClient, clusterARN, taskId);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void CreateService() {

        serviceArn  = CreateService.CreateNewService(ecsClient, clusterName, serviceName, securityGroups, subnet, taskDefinition);
        assertTrue(!serviceArn.isEmpty());
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void UpdateService() throws InterruptedException {
        Thread.sleep(20000);
        UpdateService.updateSpecificService(ecsClient, clusterName, serviceArn);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void DeleteService() {
        DeleteService.deleteSpecificService(ecsClient, clusterName, serviceArn);
        System.out.println("Test 8 passed");
    }
}
