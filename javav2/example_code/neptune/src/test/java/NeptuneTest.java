// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.neptune.scenerio.NeptuneActions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NeptuneTest {
    private static String subnetGroupName = "neptuneSubnetGroupTest" ;
    private static String clusterName = "neptuneClusterTest" ;
    private static String dbInstanceId = "neptuneDBTest" ;
    private static NeptuneActions neptuneActions = new NeptuneActions();
    private static String dbClusterId = "";

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateSubnetGroup() {
        assertDoesNotThrow(() -> {
            neptuneActions.createSubnetGroupAsync(subnetGroupName).join();
        });
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testCreateCluster() {
        assertDoesNotThrow(() -> {
            dbClusterId = neptuneActions.createDBClusterAsync(clusterName).join();
            assertFalse(dbClusterId.trim().isEmpty(), "DB Cluster ID should not be empty");
        });
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testCreateDBInstance() {
        assertDoesNotThrow(() -> {
            neptuneActions.createDBInstanceAsync(dbInstanceId, dbClusterId).join();
        });
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testCheckInstance() {
        assertDoesNotThrow(() -> {
            neptuneActions.checkInstanceStatus(dbInstanceId, "available").join();
        });
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testDescribeDBCluster() {
        assertDoesNotThrow(() -> {
            neptuneActions.describeDBClustersAsync(clusterName).join();
        });
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testStopDBCluster() {
        assertDoesNotThrow(() -> {
            neptuneActions.stopDBClusterAsync(dbClusterId);
            neptuneActions.waitForClusterStatus(dbClusterId,"stopped");
        });
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testStartDBCluster() {
        assertDoesNotThrow(() -> {
            neptuneActions.startDBClusterAsync(dbClusterId);
            neptuneActions.waitForClusterStatus(dbClusterId,"available");
            neptuneActions.checkInstanceStatus(dbInstanceId, "available").join();
        });
        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testDeleteResources() {
        assertDoesNotThrow(() -> {
            neptuneActions.deleteNeptuneResourcesAsync(dbInstanceId, clusterName, subnetGroupName);
        });
        System.out.println("Test 8 passed");
    }
}
