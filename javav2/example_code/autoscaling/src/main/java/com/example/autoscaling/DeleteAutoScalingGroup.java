//snippet-sourcedescription:[DeleteAutoScalingGroup.java deletes an Auto Scaling group.]
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

// snippet-start:[autoscale.java2.del_scaling_group.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingException;
import software.amazon.awssdk.services.autoscaling.model.DeleteAutoScalingGroupRequest;
// snippet-end:[autoscale.java2.del_scaling_group.import]

/**
 * Before running this SDK for Java (v2) code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteAutoScalingGroup {

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
                .build();

        deleteAutoScalingGroup(autoScalingClient, groupName);
        autoScalingClient.close();
    }

    // snippet-start:[autoscale.java2.del_scaling_group.main]
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
    // snippet-end:[autoscale.java2.del_scaling_group.main]
}
