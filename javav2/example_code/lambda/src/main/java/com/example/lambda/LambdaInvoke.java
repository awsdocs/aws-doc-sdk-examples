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
// snippet-sourcedescription:[LambdaInvoke.java demonstrates how to invoke an AWS Lambda function by using the LambdaClient object]
// snippet-service:[Lambda]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Lambda]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-11-19]
// snippet-sourceauthor:[AWS-scmacdon]

// snippet-start:[lambda.java2.LambdaInvoke.complete]
package com.example.lambda;

// snippet-start:[lambda.java2.invoke.import]
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
// snippet-end:[lambda.java2.invoke.import]

public class LambdaInvoke {

    public static void main(String[] args) {

      
        if (args.length < 1) {
            System.out.println("Please specify a function name");
            System.exit(1);
        }

        // snippet-start:[lambda.java2.delete.main]
        
        /*
        Function names appear as arn:aws:lambda:us-west-2:335556330391:function:HelloFunction
        you can retrieve the value by looking at the function in the AWS Console
        */
        String functionName = args[0];

        // snippet-start:[lambda.java2.invoke.main]
        InvokeResponse res = null ;
        try
        {
            Region region = Region.US_WEST_2;
            LambdaClient awsLambda = LambdaClient.builder().region(region).build();

            //Need a SdkBytes instance for the payload
            SdkBytes payload = SdkBytes.fromUtf8String("{\n" +
                    " \"Hello \": \"Paris\",\n" +
                    " \"countryCode\": \"FR\"\n" +
                    "}" ) ;

            //Setup an InvokeRequest
            InvokeRequest request =  InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(payload)
                    .build();

            //Invoke the Lambda function
            res = awsLambda.invoke(request);

            //Get the response
            String value = res.payload().asUtf8String() ;

            //write out the response
            System.out.println(value);

        }
        catch(Exception e)
        {
            e.getStackTrace();
        }
        // snippet-end:[lambda.java2.invoke.main]
    }
}
// snippet-end:[lambda.java2.LambdaInvoke.complete]
