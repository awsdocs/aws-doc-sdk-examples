// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DescribeStacks.java demonstrates how to obtain information about stacks.]
// snippet-service:[AWS CloudFormation]
// snippet-keyword:[Java]
// snippet-keyword:[AWS CloudFormation]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-10-15]
// snippet-sourceauthor:[AWS-scmacdon]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

package com.example.cloudformation;

// snippet-start:[cf.java2.get_stacks.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import java.util.List;
// snippet-end:[cf.java2.get_stacks.import]

public class DescribeStacks {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        CloudFormationClient cfClient = CloudFormationClient.builder()
                .region(region)
                .build();

        describeAllStacks(cfClient);
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
