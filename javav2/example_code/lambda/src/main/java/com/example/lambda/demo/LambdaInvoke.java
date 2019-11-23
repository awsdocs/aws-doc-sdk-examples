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
// snippet-sourceauthor:[AWS]

package com.example.lambda.demo;

import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;


public class LambdaInvoke {

    public static void main(String[] args) {

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
                    .functionName("arn:aws:lambda:us-west-2:335446330391:function:HelloFunction")
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
    }
}
