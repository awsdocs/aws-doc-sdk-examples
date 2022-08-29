// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[LambdaInvoke.java demonstrates how to invoke an AWS Lambda function by using the LambdaClient object]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[AWS Lambda]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lambda.java2.LambdaInvoke.complete]
package com.example.lambda;

// snippet-start:[lambda.java2.invoke.import]
import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;
// snippet-end:[lambda.java2.invoke.import]

public class LambdaInvoke {

   /*
   *  Function names appear as arn:aws:lambda:us-west-2:335556666777:function:HelloFunction
   *  you can retrieve the value by looking at the function in the AWS Console
   *
   * Also, set up your development environment, including your credentials.
   *
   * For information, see this documentation topic:
   *
   * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
   */

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <functionName> \n\n" +
            "Where:\n" +
            "    functionName - The name of the Lambda function \n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String functionName = args[0];
        Region region = Region.US_WEST_2;
        LambdaClient awsLambda = LambdaClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        invokeFunction(awsLambda, functionName);
        awsLambda.close();
    }

    // snippet-start:[lambda.java2.invoke.main]
    public static void invokeFunction(LambdaClient awsLambda, String functionName) {

        InvokeResponse res = null ;
        try {
            // Need a SdkBytes instance for the payload.
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("inputValue", "2000");
            String json = jsonObj.toString();
            SdkBytes payload = SdkBytes.fromUtf8String(json) ;

            // Setup an InvokeRequest.
            InvokeRequest request = InvokeRequest.builder()
                .functionName(functionName)
                .payload(payload)
                .build();

            res = awsLambda.invoke(request);
            String value = res.payload().asUtf8String() ;
            System.out.println(value);

        } catch(LambdaException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[lambda.java2.invoke.main]
}
// snippet-end:[lambda.java2.LambdaInvoke.complete]