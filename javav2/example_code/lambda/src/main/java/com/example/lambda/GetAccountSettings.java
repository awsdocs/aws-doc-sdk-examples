// snippet-sourcedescription:[GetAccountSettings.java demonstrates how to obtain information about your account.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[AWS Lambda]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.lambda;

// snippet-start:[lambda.java2.account.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.GetAccountSettingsResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;
// snippet-end:[lambda.java2.account.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetAccountSettings {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        LambdaClient awsLambda = LambdaClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        getSettings(awsLambda);
        awsLambda.close();
    }

    // snippet-start:[lambda.java2.account.main]
    public static void getSettings(LambdaClient awsLambda) {

        try {
            GetAccountSettingsResponse response = awsLambda.getAccountSettings();
            System.out.println("Total code size for your account is "+response.accountLimit().totalCodeSize() +" bytes");

        } catch(LambdaException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[lambda.java2.account.main]
}
