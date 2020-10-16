// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateStack.java demonstrates how to create a stack based on a template and wait until the status is CREATE_COMPLETE.]
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

// snippet-start:[cf.java2.create_stack.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.Parameter;
import software.amazon.awssdk.services.cloudformation.model.CreateStackRequest;
import software.amazon.awssdk.services.cloudformation.model.OnFailure;
import software.amazon.awssdk.services.cloudformation.model.CreateStackResponse;
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import java.util.List;
import java.util.concurrent.TimeUnit;
// snippet-end:[cf.java2.create_stack.import]

/**
 *  To run this example, you must have a valid template that is located in a S3 bucket.
 *  For example:
 *
 *  https://s3.amazonaws.com/<bucketname>/template.yml
 *
 *  Also, the IAM role that you use must have CloudFormation permissions as well as S3 and EC2 permissions. For more information,
 *  see "Getting started with AWS CloudFormation" in the AWS CloudFormation User Guide.
 *
 */

public class CreateStack {

   public static void main(String[] args) {

       final String USAGE = "\n" +
               "Usage:\n" +
               "    CreateStack <stackName><roleARN><location><key><value> \n\n" +
               "Where:\n" +
               "    stackName - the name of the stack \n" +
               "    roleARN - the ARN of the role that has CloudFormation permissions \n" +
               "    location - the location of file containing the template body that is located in an Amazon S3 bucket (ie, https://s3.amazonaws.com/<bucketname>/template.yml) \n"+
               "    key - the key associated with the parameter \n" +
               "    value - the input value associated with the parameter. \n" ;

       if (args.length < 5) {
           System.out.println(USAGE);
           System.exit(1);
       }

       /* Read the name from command args*/
       String stackName = args[0];
       String roleARN = args[1];
       String location = args[2];
       String key = args[3];
       String value = args[4];

       Region region = Region.US_EAST_1;
       CloudFormationClient cfClient = CloudFormationClient.builder()
               .region(region)
               .build();

       createCFStack(cfClient, stackName, roleARN, location, key, value);
   }

   // snippet-start:[cf.java2.create_stack.main]
   public static void createCFStack(CloudFormationClient cfClient,
                                    String stackName,
                                    String roleARN,
                                    String location,
                                    String key,
                                    String value){

        try {
            // Create a Parameter object that contains the key/value
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

            // Create the CloudFormation Stack
            CreateStackResponse stackResponse = cfClient.createStack(stackRequest);
            Boolean complete = false;

            // Wait until the Status is complete
            String id = stackResponse.stackId();
            String status = "";

            while (!complete) {

                TimeUnit.SECONDS.sleep(5);
                //  check status
                status = getStackStatus(cfClient, stackName);

                if (status.compareTo("CREATE_COMPLETE")==0)
                    complete = true;
                else
                    System.out.print(".");
            }
            System.out.println("The status of "+stackName +" is "+status);

        } catch (CloudFormationException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // Return the status of the stack
    private static String getStackStatus(CloudFormationClient cfClient, String stackName) {

       try {
            String status = "";
            DescribeStacksRequest stacksRequest = DescribeStacksRequest.builder()
                .stackName(stackName)
                .build();

            // Get the status by invoking describeStacks
            DescribeStacksResponse describeStacksResponse = cfClient.describeStacks(stacksRequest);

            // Only 1 stack is returned
            List<Stack> stacks = describeStacksResponse.stacks();
            for (Stack stack: stacks) {
                status = stack.stackStatus().toString();
            }
            return status;

       } catch (CloudFormationException e) {
           System.err.println(e.getMessage());
           System.exit(1);
       }
       return "";
    }
    // snippet-end:[cf.java2.create_stack.main]
}
