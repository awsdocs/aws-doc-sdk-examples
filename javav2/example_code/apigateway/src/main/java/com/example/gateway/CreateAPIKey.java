//snippet-sourcedescription:[CreateAPIKeys.java demonstrates how to create Api keys.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon API Gateway]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[12/16/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.gateway;

// snippet-start:[apigateway.java2.createapikeys.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;
// snippet-end:[apigateway.java2.createapikeys.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateAPIKey {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        ApiGatewayClient apiGateway = ApiGatewayClient.builder()
                .region(region)
                .build();

        createApiKey(apiGateway);
        apiGateway.close();
    }

    // snippet-start:[apigateway.java2.createapikeys.main]
    public static void createApiKey(ApiGatewayClient apiGateway) {

        try {

            CreateApiKeyRequest apiKeyRequest = CreateApiKeyRequest.builder()
                    .name("Key Name")
                    .description("Key description")
                    .enabled(true)
                    .generateDistinctId(true)
                    .build();

            //Creating a api key
            CreateApiKeyResponse response = apiGateway.createApiKey(apiKeyRequest);
         /*
          If we have a plan for the api keys, we can set it for the created api key.
         */
            CreateUsagePlanKeyRequest planRequest = CreateUsagePlanKeyRequest.builder()
                    .usagePlanId("<Enter Value>")
                    .keyId(response.id())
                    .keyType("API_KEY")
                    .build();

            apiGateway.createUsagePlanKey(planRequest);
            apiGateway.close();

        } catch (ApiGatewayException e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
    }
}
// snippet-end:[apigateway.java2.createapikeys.main]
}
