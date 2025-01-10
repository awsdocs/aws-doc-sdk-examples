// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudformation;

// snippet-start:[cf.java2.create_stack.main]
// snippet-start:[cf.java2.create_stack.import]
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.Parameter;
import software.amazon.awssdk.services.cloudformation.model.CreateStackRequest;
import software.amazon.awssdk.services.cloudformation.model.OnFailure;
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.waiters.CloudFormationWaiter;
// snippet-end:[cf.java2.create_stack.import]

/**
 * To run this example, you must have a valid template that is located in an
 * Amazon S3 bucket.
 * For example:
 *
 * https://s3.amazonaws.com/<bucketname>/template.yml
 *
 * Also, the role that you use must have CloudFormation permissions as well as
 * Amazon S3 and Amazon EC2 permissions. For more information,
 * see "Getting started with AWS CloudFormation" in the AWS CloudFormation User
 * Guide.
 *
 * Also, before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */

public class CreateStack {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <stackName> <roleARN> <location> <key> <value>\s

                Where:
                    stackName - The name of the AWS CloudFormation stack.\s
                    roleARN - The ARN of the role that has AWS CloudFormation permissions.\s
                    location - The location of file containing the template body. (for example, https://s3.amazonaws.com/<bucketname>/template.yml).\s
               """;

        if (args.length != 3) {
           System.out.println(usage);
           System.exit(1);
        }

        String stackName = args[0];
        String roleARN = args[1];
        String location = args[2];
        Region region = Region.US_EAST_1;
        CloudFormationClient cfClient = CloudFormationClient.builder()
                .region(region)
                .build();

        createCFStack(cfClient, stackName, roleARN, location);
        cfClient.close();
    }

    public static void createCFStack(CloudFormationClient cfClient,
            String stackName,
            String roleARN,
            String location) {
        try {
            CloudFormationWaiter waiter = cfClient.waiter();
            CreateStackRequest stackRequest = CreateStackRequest.builder()
                    .stackName(stackName)
                    .templateURL(location)
                    .roleARN(roleARN)
                    .onFailure(OnFailure.ROLLBACK)
                    .build();

            cfClient.createStack(stackRequest);
            System.out.println(stackName + " is ready");

        } catch (CloudFormationException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[cf.java2.create_stack.main]