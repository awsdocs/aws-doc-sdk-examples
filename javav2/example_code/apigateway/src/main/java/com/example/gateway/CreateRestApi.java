//snippet-sourcedescription:[CreateRestApi.java demonstrates how to create a new RestApi resource.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon API Gateway]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.gateway;

// snippet-start:[apigateway.java2.create_api.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.CreateRestApiRequest;
import software.amazon.awssdk.services.apigateway.model.CreateRestApiResponse;
import software.amazon.awssdk.services.apigateway.model.ApiGatewayException;
// snippet-end:[apigateway.java2.create_api.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateRestApi {

    public static void main(String[] args) {

        final String USAGE = "\n" +
            "Usage:\n" +
            "    CreateRestApi <restApiId> <restApiName>\n\n" +
            "Where:\n" +
            "    restApiId - The string identifier of an existing RestApi. (for example, xxxx99ewyg).\n" +
            "    restApiName - The name to use for the new RestApi. \n" ;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String restApiId = args[0];
        String restApiName = args[1];
        Region region = Region.US_EAST_1;
        ApiGatewayClient apiGateway = ApiGatewayClient.builder()
            .region(region)
            .build();

        createAPI(apiGateway, restApiId, restApiName);
        apiGateway.close();
    }

    // snippet-start:[apigateway.java2.create_api.main]
    public static String createAPI( ApiGatewayClient apiGateway, String restApiId, String restApiName) {

        try {
            CreateRestApiRequest request = CreateRestApiRequest.builder()
                .cloneFrom(restApiId)
                .description("Created using the Gateway Java API")
                .name(restApiName)
                .build();

            CreateRestApiResponse response = apiGateway.createRestApi(request);
            System.out.println("The id of the new api is "+response.id());
            return response.id();

        } catch (ApiGatewayException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[apigateway.java2.create_api.main]
}

