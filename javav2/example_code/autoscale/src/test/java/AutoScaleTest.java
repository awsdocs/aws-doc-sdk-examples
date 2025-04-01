// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.autoscaling.scenario.AutoScalingScenario;
import com.example.autoscaling.CreateAutoScalingGroup;
import com.example.autoscaling.DeleteAutoScalingGroup;
import com.example.autoscaling.DescribeAutoScalingInstances;
import com.example.autoscaling.DetachInstances;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;

import java.io.IOException;
import java.util.Random;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AutoScaleTest {
    private static final Logger logger = LoggerFactory.getLogger(AutoScaleTest.class);
    private static AutoScalingClient autoScalingClient;
    private static String groupName = "";
    private static String groupNameSc = "";
    private static String instanceId = "";
    private static String instanceId2 = "";
    private static String launchTemplateName = "";
    private static String vpcZoneId = "";

    @BeforeAll
    public static void setUp() throws IOException {
        autoScalingClient = AutoScalingClient.builder()
                .region(Region.US_EAST_1)
                .build();

        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        TestValues myValues = gson.fromJson(String.valueOf(getSecretValues()), TestValues.class);
        groupName = myValues.getGroupName() + randomNum;
        launchTemplateName = myValues.getLaunchTemplateName();
        vpcZoneId = myValues.getVpcZoneId();
        groupNameSc = myValues.getGroupNameSc() + randomNum;
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void autoScalingScenario() throws InterruptedException {
        System.out.println("**** Create an Auto Scaling group named " + groupName);
        AutoScalingScenario.createAutoScalingGroup(autoScalingClient, groupNameSc, launchTemplateName, vpcZoneId);

        System.out.println(
                "Wait 1 min for the resources, including the instance. Otherwise, an empty instance Id is returned");
        Thread.sleep(60000);

        System.out.println("**** Get Auto Scale group Id value");
        String instanceId = AutoScalingScenario.getSpecificAutoScalingGroups(autoScalingClient, groupNameSc);
        assertFalse(instanceId.isEmpty());

        System.out.println("**** Describe Auto Scaling with the Id value " + instanceId);
        AutoScalingScenario.describeAutoScalingInstance(autoScalingClient, instanceId);

        System.out.println("**** Enable metrics collection " + instanceId);
        AutoScalingScenario.enableMetricsCollection(autoScalingClient, groupNameSc);

        System.out.println("**** Update an Auto Scaling group to update max size to 3");
        AutoScalingScenario.updateAutoScalingGroup(autoScalingClient, groupNameSc, launchTemplateName);

        System.out.println("**** Describe all Auto Scaling groups to show the current state of the groups");
        AutoScalingScenario.describeAutoScalingGroups(autoScalingClient, groupNameSc);

        System.out.println("**** Describe account details");
        AutoScalingScenario.describeAccountLimits(autoScalingClient);

        System.out.println(
                "Wait 1 min for the resources, including the instance. Otherwise, an empty instance Id is returned");
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
        logger.info("Test 1 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/autoscale";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/autoscale (an AWS Secrets Manager secret)")
    class TestValues {
        private String groupName;
        private String groupNameSc;

        private String launchTemplateName;

        private String vpcZoneId;

        TestValues() {
        }

        String getGroupName() {
            return this.groupName;
        }

        String getGroupNameSc() {
            return this.groupNameSc;
        }

        String getLaunchTemplateName() {
            return this.launchTemplateName;
        }

        String getVpcZoneId() {
            return this.vpcZoneId;
        }
    }
}
