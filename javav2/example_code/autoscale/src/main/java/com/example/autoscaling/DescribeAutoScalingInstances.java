//snippet-sourcedescription:[DescribeAutoScalingInstances.java gets information about the Auto Scaling groups in the account and Region.]
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

// snippet-start:[autoscale.java2.describe_instances.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingException;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingGroup;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsResponse;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import software.amazon.awssdk.services.autoscaling.model.Instance;
import java.util.List;
// snippet-end:[autoscale.java2.describe_instances.import]

/**
 * Before running this SDK for Java (v2) code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeAutoScalingInstances {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <groupName>\n\n" +
                "Where:\n" +
                "    groupName - The name of the Auto Scaling group.\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String groupName = args[0];
        AutoScalingClient autoScalingClient = AutoScalingClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

       String instanceId = getAutoScaling(autoScalingClient, groupName);
       System.out.println(instanceId);
        autoScalingClient.close();
    }

    // snippet-start:[autoscale.java2.describe_instances.main]
    public static String getAutoScaling( AutoScalingClient autoScalingClient, String groupName) {

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
                }
            }
            return instanceId;
        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[autoscale.java2.describe_instances.main]
}
