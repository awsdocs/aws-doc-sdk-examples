// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import com.example.autoscaling.scenario.AutoScalingScenario;
import com.example.autoscaling.CreateAutoScalingGroup;
import com.example.autoscaling.DeleteAutoScalingGroup;
import com.example.autoscaling.DescribeAutoScalingInstances;
import com.example.autoscaling.DetachInstances;
import com.example.autoscaling.scenario.CloudFormationHelper;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
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
    private static AutoScalingClient autoScalingClient;

    private static Ec2Client ec2;
    private static String groupNameSc = "";
    private static String launchTemplateName = "";
    private static String vpcZoneId = "";
    private static final String ROLES_STACK = "MyCdkAutoScaleStack";

    @BeforeAll
    public static void setUp() throws IOException {
        autoScalingClient = AutoScalingClient.builder()
                .region(Region.US_WEST_2)
                .build();

        ec2 = Ec2Client.builder()
                .region(Region.US_WEST_2)
                .build();

        Random random = new Random();
        int randomNum = random.nextInt((1000 - 1) + 1) + 1;

        CloudFormationHelper.deployCloudFormationStack(ROLES_STACK);
        Map<String, String> stackOutputs = CloudFormationHelper.getStackOutputsAsync(ROLES_STACK).join();
        launchTemplateName = stackOutputs.get("LaunchTemplateNameOutput");

        // Get the values to run these tests from AWS Secrets Manager.
        groupNameSc = "MyAutoScalingGroup" + randomNum;
        vpcZoneId = AutoScalingScenario.getVPC(ec2);
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void autoScalingScenario() throws InterruptedException {
        AutoScalingScenario.updateTemlate(ec2, launchTemplateName );
        System.out.println("**** Create an Auto Scaling group named " + groupNameSc);
        AutoScalingScenario.createAutoScalingGroup(autoScalingClient, ec2, groupNameSc, launchTemplateName, vpcZoneId);
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
        CloudFormationHelper.destroyCloudFormationStack(ROLES_STACK);
    }
}
