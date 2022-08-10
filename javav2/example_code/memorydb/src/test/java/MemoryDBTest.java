/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.memorydb.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.memorydb.MemoryDbClient;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MemoryDBTest {

    private static MemoryDbClient memoryDbClient;
    private static String clusterName="";
    private static String nodeType="";
    private static String subnetGroupName="";
    private static String aclName="";
    private static String snapShotName="";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {
        Region region = Region.US_EAST_1;
        memoryDbClient = MemoryDbClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        try (InputStream input = MemoryDBTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            clusterName = prop.getProperty("clusterName");
            nodeType = prop.getProperty("nodeType");
            subnetGroupName = prop.getProperty("subnetGroupName");
            aclName = prop.getProperty("aclName");
            snapShotName= prop.getProperty("snapShotName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(memoryDbClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void createCluster() {
        assertDoesNotThrow(() ->CreateCluster.createSingleCluster(memoryDbClient, clusterName, nodeType, subnetGroupName, aclName));
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(2)
    public void describeSpecificCluster() {
        assertDoesNotThrow(() ->DescribeSpecificCluster.checkIfAvailable(memoryDbClient, clusterName));
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void createSnapshot() {
        assertDoesNotThrow(() ->CreateSnapshot.createSpecificSnapshot(memoryDbClient, clusterName, snapShotName));
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void describeSnapshot() {
        assertDoesNotThrow(() ->DescribeSnapshots.describeALlSnapshots(memoryDbClient, clusterName));
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void describeAllClusters() {
        assertDoesNotThrow(() ->DescribeClusters.getClusters(memoryDbClient));
        assertDoesNotThrow(() ->DescribeSpecificCluster.checkIfAvailable(memoryDbClient, clusterName));
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(7)
    public void deleteCluster() {
        assertDoesNotThrow(() ->DeleteCluster.deleteSpecificCluster(memoryDbClient, clusterName));
        System.out.println("Test 7 passed");
    }
}
