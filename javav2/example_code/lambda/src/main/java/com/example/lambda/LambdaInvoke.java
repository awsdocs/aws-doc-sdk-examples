// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[LambdaInvoke.java demonstrates how to invoke an AWS Lambda function by using the LambdaClient object]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[AWS Lambda]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/11/2020]
// snippet-sourceauthor:[AWS-scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lambda.java2.LambdaInvoke.complete]
package com.example.lambda;

// snippet-start:[lambda.java2.invoke.import]
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;
// snippet-end:[lambda.java2.invoke.import]

public class LambdaInvoke {

    /*
     Function names appear as arn:aws:lambda:us-west-2:335556666777:function:HelloFunction
     you can retrieve the value by looking at the function in the AWS Console
   */
   public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    LambdaInvoke <functionName> \n\n" +
                "Where:\n" +
                "    functionName - the name of the Lambda function \n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

       /* Read the name from command args*/
        String functionName = args[0];

        Region region = Region.US_EAST_1;
        LambdaClient awsLambda = LambdaClient.builder()
                .region(region)
                .build();

        invokeFunction(awsLambda, functionName);
        awsLambda.close();
    }

    // snippet-start:[lambda.java2.invoke.main]
    public static void invokeFunction(LambdaClient awsLambda, String functionName) {

         InvokeResponse res = null ;
        try {
            //Need a SdkBytes instance for the payload
            SdkBytes payload = SdkBytes.fromUtf8String("{\n" +
                    " \"Hello \": \"Paris\",\n" +
                    " \"countryCode\": \"FR\"\n" +
                    "}" ) ;

            //Setup an InvokeRequest
            InvokeRequest request = InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(payload)
                    .build();

            //Invoke the Lambda function
            res = awsLambda.invoke(request);
            String value = res.payload().asUtf8String() ;
            System.out.println(value);

        } catch(LambdaException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[lambda.java2.invoke.main]
    }
}
// snippet-end:[lambda.java2.LambdaInvoke.complete]