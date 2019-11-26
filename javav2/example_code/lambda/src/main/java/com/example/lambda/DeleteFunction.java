/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DynamoDBAsyncGetItem.java demonstrates how to get an item by using the DynamoDbAsyncClient object]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-11-19]
// snippet-sourceauthor:[AWS-scmacdon]


// snippet-start:[lambda.Java.DeleteFunction.complete]
package com.example.lambda;

// snippet-start:[lambda.java2.delete.import]
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.model.DeleteFunctionRequest;
import software.amazon.awssdk.services.lambda.model.ServiceException;
// snippet-end:[lambda.java2.delete.import]

public class DeleteFunction {

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Please specify a function name");
            System.exit(1);
        }

        // snippet-start:[lambda.java2.delete.main]
        String functionName = args[0];
        try
        {
            Region region = Region.US_WEST_2;
            LambdaClient awsLambda = LambdaClient.builder().region(region).build();

            //Setup an DeleteFunctionRequest
            DeleteFunctionRequest request =  DeleteFunctionRequest.builder()
                    .functionName(functionName)
                    .build();

            //Invoke the Lambda deleteFunction method
            awsLambda.deleteFunction(request);
            System.out.println("Done");
        }
        catch(ServiceException e)
        {
            e.getStackTrace();
        }

        // snippet-end:[lambda.java2.delete.main]
    }
}
// snippet-end:[lambda.Java.DeleteFunction.complete]
