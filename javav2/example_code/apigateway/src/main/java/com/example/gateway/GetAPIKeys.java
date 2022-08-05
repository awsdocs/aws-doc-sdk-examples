//snippet-sourcedescription:[GetAPIKeys.java demonstrates how to obtain information about the current ApiKeys resource.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon API Gateway]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.gateway;

// snippet-start:[apigateway.java2.get_apikeys.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.GetApiKeysResponse;
import software.amazon.awssdk.services.apigateway.model.ApiKey;
import software.amazon.awssdk.services.apigateway.model.ApiGatewayException;
import java.util.List;
// snippet-end:[apigateway.java2.get_apikeys.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class GetAPIKeys {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        ApiGatewayClient apiGateway = ApiGatewayClient.builder()
                .region(region)
                .build();

        getKeys(apiGateway);
        apiGateway.close();
    }

    // snippet-start:[apigateway.java2.get_apikeys.main]
    public static void getKeys(ApiGatewayClient apiGateway) {

        try {
            GetApiKeysResponse response = apiGateway.getApiKeys();
            List<ApiKey> keys = response.items();
            for (ApiKey key: keys) {
                System.out.println("key id is: "+key.id());
                System.out.println("key name is: "+key.name());
            }

        } catch (ApiGatewayException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[apigateway.java2.get_apikeys.main]
}
