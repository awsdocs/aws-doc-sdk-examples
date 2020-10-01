// snippet-sourcedescription:[GetAccountSettings.java demonstrates how to obtain information about your account.]
// snippet-service:[Lambda]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Lambda]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-10-02]
// snippet-sourceauthor:[AWS-scmacdon]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
