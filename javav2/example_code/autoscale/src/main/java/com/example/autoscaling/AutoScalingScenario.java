//snippet-sourcedescription:[AutoScalingScenario.java performs multiple Auto Scaling operations.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2 Auto Scaling]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/05/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.autoscaling;

// snippet-start:[autoscale.java2.create_scaling_scenario.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingException;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingGroup;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingInstanceDetails;
import software.amazon.awssdk.services.autoscaling.model.CreateAutoScalingGroupRequest;
import software.amazon.awssdk.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import software.amazon.awssdk.services.autoscaling.model.DescribeAccountLimitsResponse;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsResponse;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingInstancesResponse;
import software.amazon.awssdk.services.autoscaling.model.DisableMetricsCollectionRequest;
import software.amazon.awssdk.services.autoscaling.model.EnableMetricsCollectionRequest;
import software.amazon.awssdk.services.autoscaling.model.Instance;
import software.amazon.awssdk.services.autoscaling.model.LaunchTemplateSpecification;
import software.amazon.awssdk.services.autoscaling.waiters.AutoScalingWaiter;
import software.amazon.awssdk.services.autoscaling.model.UpdateAutoScalingGroupRequest;
import software.amazon.awssdk.services.autoscaling.model.TerminateInstanceInAutoScalingGroupRequest;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingInstancesRequest;
import java.util.List;
// snippet-end:[autoscale.java2.create_scaling_scenario.import]

/**
 *  Before running this SDK for Java (v2) code example, set up your development environment, including your credentials.
 *
 *  For more information, see the following documentation:
 *
 *  https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 *  In addition, create a launch template. For more information, see the following topic:
 *
 *  https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-launch-templates.html#create-launch-template
 *
 * This code example performs the following operations:
 * 1. Creates an Auto Scaling group using an AutoScalingWaiter.
 * 2. Gets all Auto Scaling groups.
 * 3. Gets a specific Auto Scaling group and returns an instance Id value.
 * 4. Describes Auto Scaling with the Id value.
 * 5. Enables metrics collection.
 * 6. Describes Auto Scaling groups.
 * 7. Describes Account details.
 * 8. Updates an Auto Scaling group to use an additional instance.
 * 9. Gets the specific Auto Scaling group and gets the number of instances.
 * 10. Terminates an instance in the Auto Scaling group.
 * 11. Stops the metrics collection.
 * 12. Deletes the Auto Scaling group.
 */

// snippet-start:[autoscale.java2.create_scaling_scenario.main]
public class AutoScalingScenario {

    public static void main(String[] args) throws InterruptedException {
        final String usage = "\n" +
            "Usage:\n" +
            "    <groupName> <launchTemplateName> <serviceLinkedRoleARN> <vpcZoneId>\n\n" +
            "Where:\n" +
            "    groupName - The name of the Auto Scaling group.\n" +
            "    launchTemplateName - The name of the launch template. \n" +
            "    serviceLinkedRoleARN - The Amazon Resource Name (ARN) of the service-linked role that the Auto Scaling group uses.\n" +
            "    vpcZoneId - A subnet Id for a virtual private cloud (VPC) where instances in the Auto Scaling group can be created.\n" ;

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String groupName = args[0];
        String launchTemplateName = args[1];
        String serviceLinkedRoleARN = args[2];
        String vpcZoneId = args[3];
        AutoScalingClient autoScalingClient = AutoScalingClient.builder()
                 .region(Region.US_EAST_1)
                 .credentialsProvider(ProfileCredentialsProvider.create())
                 .build();

        System.out.println("**** Create an Auto Scaling group named "+groupName);
        createAutoScalingGroup(autoScalingClient, groupName, launchTemplateName, serviceLinkedRoleARN, vpcZoneId);

        System.out.println("**** Get Auto Scaling groups");
        getAutoScalingGroups(autoScalingClient);

        System.out.println("Wait 1 min for the resources, including the instance. Otherwise, an empty instance Id is returned");
        Thread.sleep(60000);

        System.out.println("**** Get Auto Scale group Id value");
        String instanceId = getSpecificAutoScalingGroups(autoScalingClient, groupName);
        if (instanceId.compareTo("") ==0) {
            System.out.println("Error - no instance Id value");
            System.exit(1);
        } else {
            System.out.println("The instance Id value is "+instanceId);
        }

        System.out.println("**** Describe Auto Scaling with the Id value "+instanceId);
        describeAutoScalingInstance( autoScalingClient, instanceId);

        System.out.println("**** Enable metrics collection "+instanceId);
        enableMetricsCollection(autoScalingClient, groupName);

        System.out.println("**** Describe Auto Scaling groups");
        describeAutoScalingGroups(autoScalingClient, groupName);

        System.out.println("**** Describe account details");
        describeAccountLimits(autoScalingClient);

        System.out.println("**** Update an Auto Scaling group");
        updateAutoScalingGroup(autoScalingClient, groupName, launchTemplateName, serviceLinkedRoleARN);

        System.out.println("Wait 1 min for the resources, including the instance. Otherwise, an empty instance Id is returned");
        Thread.sleep(60000);
        System.out.println("**** Get the three instance Id values");
        getSpecificAutoScalingGroups(autoScalingClient, groupName);

        System.out.println("**** Terminate an instance in the Auto Scaling group");
        terminateInstanceInAutoScalingGroup(autoScalingClient, instanceId);

        System.out.println("**** Stop the metrics collection");
        disableMetricsCollection(autoScalingClient, groupName);

        System.out.println("**** Delete the Auto Scaling group");
        deleteAutoScalingGroup(autoScalingClient, groupName);
        autoScalingClient.close();
    }

    public static void createAutoScalingGroup(AutoScalingClient autoScalingClient,
                                              String groupName,
                                              String launchTemplateName,
                                              String serviceLinkedRoleARN,
                                              String vpcZoneId) {
        try {
            AutoScalingWaiter waiter = autoScalingClient.waiter();
            LaunchTemplateSpecification templateSpecification = LaunchTemplateSpecification.builder()
                    .launchTemplateName(launchTemplateName)
                    .build();

            CreateAutoScalingGroupRequest request = CreateAutoScalingGroupRequest.builder()
                    .autoScalingGroupName(groupName)
                    .availabilityZones("us-east-1a")
                    .launchTemplate(templateSpecification)
                    .maxSize(1)
                    .minSize(1)
                    .vpcZoneIdentifier(vpcZoneId)
                    .serviceLinkedRoleARN(serviceLinkedRoleARN)
                    .build();

            autoScalingClient.createAutoScalingGroup(request);
            DescribeAutoScalingGroupsRequest groupsRequest = DescribeAutoScalingGroupsRequest.builder()
                    .autoScalingGroupNames(groupName)
                    .build();

            WaiterResponse<DescribeAutoScalingGroupsResponse> waiterResponse = waiter.waitUntilGroupExists(groupsRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.println("Auto Scaling Group created");

        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void describeAutoScalingInstance( AutoScalingClient autoScalingClient, String id) {

        try {
            DescribeAutoScalingInstancesRequest describeAutoScalingInstancesRequest = DescribeAutoScalingInstancesRequest.builder()
                    .instanceIds(id)
                    .build();

            DescribeAutoScalingInstancesResponse response = autoScalingClient.describeAutoScalingInstances(describeAutoScalingInstancesRequest);
            List<AutoScalingInstanceDetails> instances = response.autoScalingInstances();
            for (AutoScalingInstanceDetails instance:instances ) {
                System.out.println("The instance lifecycle state is: "+instance.lifecycleState());
            }

        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void describeAutoScalingGroups(AutoScalingClient autoScalingClient, String groupName) {

        try {
            DescribeAutoScalingGroupsRequest groupsRequest = DescribeAutoScalingGroupsRequest.builder()
                    .autoScalingGroupNames(groupName)
                    .maxRecords(10)
                    .build();

            DescribeAutoScalingGroupsResponse response = autoScalingClient.describeAutoScalingGroups(groupsRequest);
            List<AutoScalingGroup> groups = response.autoScalingGroups();
            for (AutoScalingGroup group: groups) {
                System.out.println("*** The service to use for the health checks: "+ group.healthCheckType() );
            }

        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void getAutoScalingGroups( AutoScalingClient autoScalingClient) {

        try{
            DescribeAutoScalingGroupsResponse response = autoScalingClient.describeAutoScalingGroups();
            List<AutoScalingGroup> groups = response.autoScalingGroups();
            for (AutoScalingGroup group: groups) {
                System.out.println("The group name is " + group.autoScalingGroupName());
                System.out.println("The group ARN is " + group.autoScalingGroupARN());
            }

        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static String getSpecificAutoScalingGroups(AutoScalingClient autoScalingClient, String groupName) {

        try{
            String instanceId = "";
            DescribeAutoScalingGroupsRequest scalingGroupsRequest = DescribeAutoScalingGroupsRequest.builder()
                    .autoScalingGroupNames(groupName)
                    .build();

            DescribeAutoScalingGroupsResponse response = autoScalingClient.describeAutoScalingGroups(scalingGroupsRequest);
            List<AutoScalingGroup> groups = response.autoScalingGroups();
            for (AutoScalingGroup group: groups) {
                System.out.println("The group name is " + group.autoScalingGroupName());
                System.out.println("The group ARN is " + group.autoScalingGroupARN());
                List<Instance> instances = group.instances();

                for (Instance instance : instances) {
                    instanceId = instance.instanceId();
                    System.out.println("The instance id is " + group.autoScalingGroupARN());
                }
            }

            return instanceId ;
        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "" ;
    }

    public static void enableMetricsCollection(AutoScalingClient autoScalingClient, String groupName) {

        try {

            EnableMetricsCollectionRequest collectionRequest = EnableMetricsCollectionRequest.builder()
                    .autoScalingGroupName(groupName)
                    .metrics("GroupMaxSize")
                    .granularity("1Minute")
                    .build();

            autoScalingClient.enableMetricsCollection(collectionRequest);
            System.out.println("The enable metrics collection operation was successful");

        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

    }

    public static void disableMetricsCollection(AutoScalingClient autoScalingClient, String groupName) {

        try {
            DisableMetricsCollectionRequest disableMetricsCollectionRequest = DisableMetricsCollectionRequest.builder()
                    .autoScalingGroupName(groupName)
                    .metrics("GroupMaxSize")
                    .build();

            autoScalingClient.disableMetricsCollection(disableMetricsCollectionRequest);
            System.out.println("The disable metrics collection operation was successful");

        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }


    public static void describeAccountLimits(AutoScalingClient autoScalingClient) {

        try {
            DescribeAccountLimitsResponse response = autoScalingClient.describeAccountLimits();
            System.out.println("The max number of auto scaling groups is "+response.maxNumberOfAutoScalingGroups());
            System.out.println("The current number of auto scaling groups is "+response.numberOfAutoScalingGroups());

        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void updateAutoScalingGroup(AutoScalingClient autoScalingClient, String groupName, String launchTemplateName, String serviceLinkedRoleARN) {

        try {
            AutoScalingWaiter waiter = autoScalingClient.waiter();
            LaunchTemplateSpecification templateSpecification = LaunchTemplateSpecification.builder()
                    .launchTemplateName(launchTemplateName)
                    .build();

            UpdateAutoScalingGroupRequest groupRequest = UpdateAutoScalingGroupRequest.builder()
                    .maxSize(3)
                    .desiredCapacity(3)
                    .serviceLinkedRoleARN(serviceLinkedRoleARN)
                    .autoScalingGroupName(groupName)
                    .launchTemplate(templateSpecification)
                    .build();

            autoScalingClient.updateAutoScalingGroup(groupRequest);
            DescribeAutoScalingGroupsRequest groupsRequest = DescribeAutoScalingGroupsRequest.builder()
                    .autoScalingGroupNames(groupName)
                    .build();

            WaiterResponse<DescribeAutoScalingGroupsResponse> waiterResponse = waiter.waitUntilGroupInService(groupsRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.println("You successfully updated the auto scaling group  "+groupName);

        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void terminateInstanceInAutoScalingGroup(AutoScalingClient autoScalingClient, String instanceId){

        try {
            TerminateInstanceInAutoScalingGroupRequest request = TerminateInstanceInAutoScalingGroupRequest.builder()
                    .instanceId(instanceId)
                    .shouldDecrementDesiredCapacity(false)
                    .build();

            autoScalingClient.terminateInstanceInAutoScalingGroup(request);
            System.out.println("You have terminated instance "+instanceId);

        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void deleteAutoScalingGroup(AutoScalingClient autoScalingClient, String groupName) {

        try {
            DeleteAutoScalingGroupRequest deleteAutoScalingGroupRequest = DeleteAutoScalingGroupRequest.builder()
                    .autoScalingGroupName(groupName)
                    .forceDelete(true)
                    .build() ;

            autoScalingClient.deleteAutoScalingGroup(deleteAutoScalingGroupRequest) ;
            System.out.println("You successfully deleted "+groupName);

        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[autoscale.java2.create_scaling_scenario.main]
