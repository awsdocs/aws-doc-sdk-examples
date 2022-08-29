// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DeleteFunction.java demonstrates how to delete an AWS Lambda function by using the LambdaClient object]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[AWS Lambda]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lambda.java2.DeleteFunction.complete]
package com.example.lambda;

// snippet-start:[lambda.java2.delete.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.model.DeleteFunctionRequest;
import software.amazon.awssdk.services.lambda.model.LambdaException;
// snippet-end:[lambda.java2.delete.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteFunction {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <functionName> \n\n" +
            "Where:\n" +
            "    functionName - The name of the Lambda function. \n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String functionName = args[0];
        Region region = Region.US_EAST_1;
        LambdaClient awsLambda = LambdaClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        deleteLambdaFunction(awsLambda, functionName);
        awsLambda.close();
    }

    // snippet-start:[lambda.java2.delete.main]
    public static void deleteLambdaFunction(LambdaClient awsLambda, String functionName) {
        try {
            DeleteFunctionRequest request = DeleteFunctionRequest.builder()
                .functionName(functionName)
                .build();

            awsLambda.deleteFunction(request);
            System.out.println("The "+functionName +" function was deleted");

        } catch(LambdaException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[lambda.java2.delete.main]
}
// snippet-end:[lambda.java2.DeleteFunction.complete]
