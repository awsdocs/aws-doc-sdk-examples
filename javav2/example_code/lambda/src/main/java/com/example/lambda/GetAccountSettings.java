// snippet-sourcedescription:[GetAccountSettings.java demonstrates how to obtain information about your account.]
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

package com.example.lambda;

// snippet-start:[lambda.java2.account.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.GetAccountSettingsResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;
// snippet-end:[lambda.java2.account.import]

public class GetAccountSettings {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        LambdaClient awsLambda = LambdaClient.builder()
                .region(region)
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
