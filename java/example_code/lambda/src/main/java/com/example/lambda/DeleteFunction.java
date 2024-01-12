// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[lambda.java1.DeleteFunction.complete]
package com.example.lambda;

// snippet-start:[lambda.java1.delete.import]
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.ServiceException;
import com.amazonaws.services.lambda.model.DeleteFunctionRequest;
// snippet-end:[lambda.java1.delete.import]

public class DeleteFunction {

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Please specify a function name");
            System.exit(1);
        }

        // snippet-start:[lambda.java1.delete.main]
        String functionName = args[0];
        try {
            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(Regions.US_WEST_2).build();

            DeleteFunctionRequest delFunc = new DeleteFunctionRequest();
            delFunc.withFunctionName(functionName);

            // Delete the function
            awsLambda.deleteFunction(delFunc);
            System.out.println("The function is deleted");

        } catch (ServiceException e) {
            System.out.println(e);
        }
        // snippet-end:[lambda.java1.delete.main]
    }
}
// snippet-end:[lambda.java1.DeleteFunction.complete]
