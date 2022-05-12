/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.autoscaling.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import java.io.*;
import java.util.*;
import  software.amazon.awssdk.regions.Region;

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
        public void whenInitializingAWSService_thenNotNull() {
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
        public void CreateAutoScalingGroup() {

            CreateAutoScalingGroup.createAutoScalingGroup(autoScalingClient, groupName, launchTemplateName, serviceLinkedRoleARN, vpcZoneId);
            System.out.println("Test 2 passed");
        }

        @Test
        @Order(3)
        public void DescribeAutoScalingInstances() throws InterruptedException {
            System.out.println("Wait 1 min for the resources");
            Thread.sleep(60000);
            instanceId2 =  DescribeAutoScalingInstances.getAutoScaling(autoScalingClient, groupName);
            assertTrue(!instanceId2.isEmpty());
            System.out.println(instanceId2);
            System.out.println("Test 3 passed");
        }

        @Test
        @Order(4)
        public void DetachInstances()throws InterruptedException {
            System.out.println("Wait 1 min for the resources, including the instance");
            Thread.sleep(60000);
            DetachInstances.detachInstance(autoScalingClient, groupName, instanceId2);
            System.out.println("Test 4 passed");
        }

        @Test
        @Order(5)
        public void DeleteAutoScalingGroup() {
            DeleteAutoScalingGroup.deleteAutoScalingGroup(autoScalingClient, groupName);
            System.out.println("Test 5 passed");
        }

        @Test
        @Order(6)
        public void AutoScalingScenario() throws InterruptedException {

            AutoScalingScenario.createAutoScalingGroup(autoScalingClient, groupNameSc, launchTemplateName, serviceLinkedRoleARN, vpcZoneId);
            AutoScalingScenario.getAutoScalingGroups(autoScalingClient);

            System.out.println("Wait 1 min for the resources, including the instance");
            Thread.sleep(60000);
            String instanceId =  AutoScalingScenario.getSpecificAutoScalingGroups(autoScalingClient, groupNameSc);
            if (instanceId.compareTo("") ==0) {
                System.out.println("Error - no instance Id value");
                System.exit(1);
            }
            else {
                System.out.println("The instance Id value is "+instanceId);
            }

            AutoScalingScenario.describeAccountLimits(autoScalingClient);
            AutoScalingScenario.updateAutoScalingGroup(autoScalingClient, groupNameSc, launchTemplateName, serviceLinkedRoleARN);
            AutoScalingScenario.terminateInstanceInAutoScalingGroup(autoScalingClient, instanceId);
            AutoScalingScenario.deleteAutoScalingGroup(autoScalingClient, groupNameSc);
        }
  }

