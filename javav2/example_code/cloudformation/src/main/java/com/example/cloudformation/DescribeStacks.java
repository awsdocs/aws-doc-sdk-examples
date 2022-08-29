// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DescribeStacks.java demonstrates how to obtain information about stacks.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS CloudFormation]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.cloudformation;

// snippet-start:[cf.java2.get_stacks.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import java.util.List;
// snippet-end:[cf.java2.get_stacks.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeStacks {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        CloudFormationClient cfClient = CloudFormationClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        describeAllStacks(cfClient);
        cfClient.close();
    }

    // snippet-start:[cf.java2.get_stacks.main]
    public static void describeAllStacks(CloudFormationClient cfClient) {
        try {
            DescribeStacksResponse stacksResponse = cfClient.describeStacks();
            List<Stack> stacks = stacksResponse.stacks();
            for (Stack stack : stacks) {
                System.out.println("The stack description is " + stack.description());
                System.out.println("The stack Id is " + stack.stackId());
            }

        } catch (CloudFormationException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cf.java2.get_stacks.main]
}
