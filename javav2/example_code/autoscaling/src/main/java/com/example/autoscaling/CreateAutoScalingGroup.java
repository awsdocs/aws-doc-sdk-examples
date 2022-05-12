//snippet-sourcedescription:[CreateAutoScalingGroup.java creates an Auto Scaling group with the specified name and attributes using a waiter.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2 Auto Scaling]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/05/2022]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.autoscaling;

// snippet-start:[autoscale.java2.create_scaling_group.import]
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingException;
import software.amazon.awssdk.services.autoscaling.model.CreateAutoScalingGroupRequest;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsResponse;
import software.amazon.awssdk.services.autoscaling.model.LaunchTemplateSpecification;
import software.amazon.awssdk.services.autoscaling.waiters.AutoScalingWaiter;
// snippet-end:[autoscale.java2.create_scaling_group.import]

/**
 * Before running this SDK for Java (v2) code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateAutoScalingGroup {

    public static void main(String[] args) {

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
                .build();

        createAutoScalingGroup(autoScalingClient, groupName, launchTemplateName, serviceLinkedRoleARN, vpcZoneId);
        autoScalingClient.close();
    }

    // snippet-start:[autoscale.java2.create_scaling_group.main]
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
    // snippet-end:[autoscale.java2.create_scaling_group.main]
}
