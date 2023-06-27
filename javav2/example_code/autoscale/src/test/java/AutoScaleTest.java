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
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

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
    private static String vpcZoneId="";

    @BeforeAll
    public static void setUp() throws IOException {
        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;

        autoScalingClient = AutoScalingClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        try (InputStream input = AutoScaleTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            groupName = prop.getProperty("groupName")+randomNum;
            launchTemplateName = prop.getProperty("launchTemplateName");
            vpcZoneId = prop.getProperty("vpcZoneId");
            groupNameSc = prop.getProperty("groupNameSc")+randomNum;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @Test
    @Order(2)
    public void createAutoScalingGroup() {
        assertDoesNotThrow(() -> CreateAutoScalingGroup.createAutoScalingGroup(autoScalingClient, groupName, launchTemplateName, vpcZoneId));
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
        AutoScalingScenario.createAutoScalingGroup(autoScalingClient, groupNameSc, launchTemplateName, vpcZoneId);

        System.out.println("Wait 1 min for the resources, including the instance. Otherwise, an empty instance Id is returned");
        Thread.sleep(60000);

        System.out.println("**** Get Auto Scale group Id value");
        String instanceId = AutoScalingScenario.getSpecificAutoScalingGroups(autoScalingClient, groupNameSc);
        assertTrue(!instanceId.isEmpty());

        System.out.println("**** Describe Auto Scaling with the Id value "+instanceId);
        AutoScalingScenario.describeAutoScalingInstance( autoScalingClient, instanceId);

        System.out.println("**** Enable metrics collection "+instanceId);
        AutoScalingScenario.enableMetricsCollection(autoScalingClient, groupNameSc);

        System.out.println("**** Update an Auto Scaling group to update max size to 3");
        AutoScalingScenario.updateAutoScalingGroup(autoScalingClient, groupNameSc, launchTemplateName);

        System.out.println("**** Describe all Auto Scaling groups to show the current state of the groups");
        AutoScalingScenario.describeAutoScalingGroups(autoScalingClient, groupNameSc);

        System.out.println("**** Describe account details");
        AutoScalingScenario.describeAccountLimits(autoScalingClient);

        System.out.println("Wait 1 min for the resources, including the instance. Otherwise, an empty instance Id is returned");
        Thread.sleep(60000);

        System.out.println("**** Set desired capacity to 2");
        AutoScalingScenario.setDesiredCapacity(autoScalingClient, groupNameSc);

        System.out.println("**** Get the two instance Id values and state");
        AutoScalingScenario.getSpecificAutoScalingGroups(autoScalingClient, groupNameSc);

        System.out.println("**** List the scaling activities that have occurred for the group");
        AutoScalingScenario.describeScalingActivities(autoScalingClient, groupNameSc);

        System.out.println("**** Terminate an instance in the Auto Scaling group");
        AutoScalingScenario.terminateInstanceInAutoScalingGroup(autoScalingClient, instanceId);

        System.out.println("**** Stop the metrics collection");
        AutoScalingScenario.disableMetricsCollection(autoScalingClient, groupNameSc);

        System.out.println("**** Delete the Auto Scaling group");
        AutoScalingScenario.deleteAutoScalingGroup(autoScalingClient, groupNameSc);
    }
}

