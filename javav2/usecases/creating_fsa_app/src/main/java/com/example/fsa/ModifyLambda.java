/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.fsa;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.GetFunctionConfigurationRequest;
import software.amazon.awssdk.services.lambda.model.GetFunctionConfigurationResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;
import software.amazon.awssdk.services.lambda.model.UpdateFunctionCodeRequest;
import software.amazon.awssdk.services.lambda.model.UpdateFunctionCodeResponse;
import software.amazon.awssdk.services.lambda.waiters.LambdaWaiter;

public class ModifyLambda {

    public static void main(String[]args) {
        String analyzeSentimentLambda = "<Enter value>" ;
        String synthesizeAudioLambda = "<Enter value>" ;
        String extractTextLambda = "<Enter value>" ;
        String translateTextLambda = "<Enter value>" ;
        String bucketName = FSAApplicationResources.STORAGE_BUCKET;
        String key = "<Enter value>";

        Region region = Region.US_EAST_1;
        LambdaClient awsLambda = LambdaClient.builder()
            .region(region)
            .build();

        // Update all four Lambda functions.
        updateFunctionCode(awsLambda, analyzeSentimentLambda, bucketName, key);
        updateFunctionCode(awsLambda, synthesizeAudioLambda, bucketName, key);
        updateFunctionCode(awsLambda, extractTextLambda, bucketName, key);
        updateFunctionCode(awsLambda, translateTextLambda, bucketName, key);
        System.out.println("You have successfully updated the AWS Lambda functions");
    }

    public static void updateFunctionCode(LambdaClient awsLambda, String functionName, String bucketName, String key) {
        try {
            LambdaWaiter waiter = awsLambda.waiter();
            UpdateFunctionCodeRequest functionCodeRequest = UpdateFunctionCodeRequest.builder()
                .functionName(functionName)
                .publish(true)
                .s3Bucket(bucketName)
                .s3Key(key)
                .build();

            UpdateFunctionCodeResponse response = awsLambda.updateFunctionCode(functionCodeRequest) ;
            GetFunctionConfigurationRequest getFunctionConfigRequest = GetFunctionConfigurationRequest.builder()
                .functionName(functionName)
                .build();

            WaiterResponse<GetFunctionConfigurationResponse> waiterResponse = waiter.waitUntilFunctionUpdated(getFunctionConfigRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.println("The last modified value is " +response.lastModified());

        } catch(LambdaException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
