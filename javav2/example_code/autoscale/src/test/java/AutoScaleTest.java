/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.example.autoscaling.AutoScalingScenario;
import com.example.autoscaling.CreateAutoScalingGroup;
import com.example.autoscaling.DeleteAutoScalingGroup;
import com.example.autoscaling.DescribeAutoScalingInstances;
import com.example.autoscaling.DetachInstances;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import software.amazon.awssdk.regions.Region;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AutoScaleTest {

    private static AutoScalingClient autoScalingClient;
    private static String groupName="";
    private static String groupNameSc="";
    private static String instanceId="";
    private static String instanceId2="";
    private static String launchTemplateName="";
    private static String serviceLinkedRoleARN="";
    private static String vpcZoneId="";

    @BeforeAll
    public static void setUp() throws IOException {

        autoScalingClient = AutoScalingClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = AutoScaleTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            groupName = prop.getProperty("groupName");
            launchTemplateName = prop.getProperty("launchTemplateName");
            serviceLinkedRoleARN = prop.getProperty("serviceLinkedRoleARN");
            vpcZoneId = prop.getProperty("vpcZoneId");
            groupNameSc = prop.getProperty("groupNameSc");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @Test
    @Order(1)
    public void checkValues() {
        assertNotNull(autoScalingClient);
        assertNotNull(groupName);
        assertNotNull(instanceId);
        assertNotNull(launchTemplateName);
        assertNotNull(serviceLinkedRoleARN);
        assertNotNull(vpcZoneId);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void createAutoScalingGroup() {
        assertDoesNotThrow(() -> CreateAutoScalingGroup.createAutoScalingGroup(autoScalingClient, groupName, launchTemplateName, serviceLinkedRoleARN, vpcZoneId));
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void describeAutoScalingInstances() throws InterruptedException {
        System.out.println("Wait 1 min for the resources");
        Thread.sleep(60000);
        instanceId2 = DescribeAutoScalingInstances.getAutoScaling(autoScalingClient, groupName);
        assertTrue(!instanceId2.isEmpty());
        System.out.println(instanceId2);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void detachInstances()throws InterruptedException {
        System.out.println("Wait 1 min for the resources, including the instance");
        Thread.sleep(60000);
        assertDoesNotThrow(() -> DetachInstances.detachInstance(autoScalingClient, groupName, instanceId2));
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void deleteAutoScalingGroup() {
        assertDoesNotThrow(() -> DeleteAutoScalingGroup.deleteAutoScalingGroup(autoScalingClient, groupName));
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void autoScalingScenario() throws InterruptedException {
        System.out.println("**** Create an Auto Scaling group named "+groupName);
        assertDoesNotThrow(() -> AutoScalingScenario.createAutoScalingGroup(autoScalingClient, groupNameSc, launchTemplateName, serviceLinkedRoleARN, vpcZoneId));
        System.out.println("**** Get Auto Scaling groups");
        assertDoesNotThrow(() -> AutoScalingScenario.getAutoScalingGroups(autoScalingClient));
        System.out.println("Wait 1 min for the resources, including the instance. Otherwise, an empty instance Id is returned");
        Thread.sleep(60000);
        System.out.println("**** Get Auto Scale group Id value");
        String instanceId = AutoScalingScenario.getSpecificAutoScalingGroups(autoScalingClient, groupNameSc);
        assertTrue(!instanceId.isEmpty());
        System.out.println("**** Describe Auto Scaling with the Id value "+instanceId);
        assertDoesNotThrow(() -> AutoScalingScenario.describeAutoScalingInstance( autoScalingClient, instanceId));
        System.out.println("**** Enable metrics collection "+instanceId);
        assertDoesNotThrow(() -> AutoScalingScenario.enableMetricsCollection(autoScalingClient, groupNameSc));
        System.out.println("**** Describe Auto Scaling groups");
        assertDoesNotThrow(() -> AutoScalingScenario.describeAutoScalingGroups(autoScalingClient, groupNameSc));
        System.out.println("**** Describe Account details");
        assertDoesNotThrow(() -> AutoScalingScenario.describeAccountLimits(autoScalingClient));
        System.out.println("**** Update an Auto Scaling group");
        assertDoesNotThrow(() -> AutoScalingScenario.updateAutoScalingGroup(autoScalingClient, groupNameSc, launchTemplateName, serviceLinkedRoleARN));
        System.out.println("Wait 1 min for the resources, including the instance. Otherwise, an empty instance Id is returned");
        Thread.sleep(60000);
        System.out.println("**** Get the three instance Id values");
        assertDoesNotThrow(() -> AutoScalingScenario.getSpecificAutoScalingGroups(autoScalingClient, groupNameSc));
        System.out.println("**** Terminate an instance in the Auto Scaling group");
        assertDoesNotThrow(() -> AutoScalingScenario.terminateInstanceInAutoScalingGroup(autoScalingClient, instanceId));
        System.out.println("**** Disable the metrics collection");
        assertDoesNotThrow(() -> AutoScalingScenario.disableMetricsCollection(autoScalingClient, groupNameSc));
        System.out.println("**** Delete the Auto Scaling group");
        assertDoesNotThrow(() -> AutoScalingScenario.deleteAutoScalingGroup(autoScalingClient, groupNameSc));
    }
}

