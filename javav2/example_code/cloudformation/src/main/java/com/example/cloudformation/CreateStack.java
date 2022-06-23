// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateStack.java demonstrates how to create a stack based on a template and wait until it's ready by using a waiter.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS CloudFormation]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/17/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.cloudformation;

// snippet-start:[cf.java2.create_stack.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
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
 *  To run this example, you must have a valid template that is located in an Amazon S3 bucket.
 *  For example:
 *
 *  https://s3.amazonaws.com/<bucketname>/template.yml
 *
 *  Also, the role that you use must have CloudFormation permissions as well as Amazon S3 and Amazon EC2 permissions. For more information,
 *  see "Getting started with AWS CloudFormation" in the AWS CloudFormation User Guide.
 *
 * Also, before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */

public class CreateStack {

   public static void main(String[] args) {

       final String usage = "\n" +
               "Usage:\n" +
               "    <stackName> <roleARN> <location> <key> <value> \n\n" +
               "Where:\n" +
               "    stackName - The name of the AWS CloudFormation stack. \n" +
               "    roleARN - The ARN of the role that has AWS CloudFormation permissions. \n" +
               "    location - The location of file containing the template body. (for example, https://s3.amazonaws.com/<bucketname>/template.yml). \n"+
               "    key - The key associated with the parameter. \n" +
               "    value - The value associated with the parameter. \n" ;

       if (args.length != 5) {
           System.out.println(usage);
           System.exit(1);
       }

       String stackName = args[0];
       String roleARN = args[1];
       String location = args[2];
       String key = args[3];
       String value = args[4];

       Region region = Region.US_EAST_1;
       CloudFormationClient cfClient = CloudFormationClient.builder()
               .region(region)
               .credentialsProvider(ProfileCredentialsProvider.create())
               .build();

       createCFStack(cfClient, stackName, roleARN, location, key, value);
       cfClient.close();
   }

   // snippet-start:[cf.java2.create_stack.main]
   public static void createCFStack(CloudFormationClient cfClient,
                                    String stackName,
                                    String roleARN,
                                    String location,
                                    String key,
                                    String value){

        try {

            CloudFormationWaiter waiter = cfClient.waiter();
            Parameter myParameter = Parameter.builder()
                    .parameterKey(key)
                    .parameterValue(value)
                    .build();

            CreateStackRequest stackRequest = CreateStackRequest.builder()
                .stackName(stackName)
                .templateURL(location)
                .roleARN(roleARN)
                .onFailure(OnFailure.ROLLBACK)
                .parameters(myParameter)
                .build();

            cfClient.createStack(stackRequest);
            DescribeStacksRequest stacksRequest = DescribeStacksRequest.builder()
                    .stackName(stackName)
                    .build();

            WaiterResponse<DescribeStacksResponse> waiterResponse = waiter.waitUntilStackCreateComplete(stacksRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.println(stackName +" is ready");

        } catch (CloudFormationException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cf.java2.create_stack.main]
}
