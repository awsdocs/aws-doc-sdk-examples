// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[lambda.java1.LambdaInvoke.complete]
package com.example.lambda;

// snippet-start:[lambda.java1.invoke.import]
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;

import java.nio.charset.StandardCharsets;
// snippet-end:[lambda.java1.invoke.import]

public class LambdaInvokeFunction {

    public static void main(String[] args) {

        /*
         * Function names appear as
         * arn:aws:lambda:us-west-2:335556330391:function:HelloFunction
         * you can retrieve the value by looking at the function in the AWS Console
         */
        if (args.length < 1) {
            System.out.println("Please specify a function name");
            System.exit(1);
        }

        // snippet-start:[lambda.java1.invoke.main]
        String functionName = args[0];

        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(functionName)
                .withPayload("{\n" +
                        " \"Hello \": \"Paris\",\n" +
                        " \"countryCode\": \"FR\"\n" +
                        "}");
        InvokeResult invokeResult = null;

        try {
            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(Regions.US_WEST_2).build();

            invokeResult = awsLambda.invoke(invokeRequest);

            String ans = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);

            // write out the return value
            System.out.println(ans);

        } catch (ServiceException e) {
            System.out.println(e);
        }

        System.out.println(invokeResult.getStatusCode());
        // snippet-end:[lambda.java1.invoke.main]
    }
}
// snippet-end:[lambda.java1.LambdaInvoke.complete]
