//snippet-sourcedescription:[DeleteRestApi.java demonstrates how to delete an existing RestApi resource.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon API Gateway]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.gateway;

// snippet-start:[apigateway.java2.delete_api.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.ApiGatewayException;
import software.amazon.awssdk.services.apigateway.model.DeleteRestApiRequest;
// snippet-end:[apigateway.java2.delete_api.import]


/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DeleteRestApi {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteRestApi <restApiId> \n\n" +
                "Where:\n" +
                "    restApiId - The string identifier of an existing RestApi. (for example, xxxx99ewyg).\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String restApiId = args[0];
        Region region = Region.US_EAST_1;
        ApiGatewayClient apiGateway = ApiGatewayClient.builder()
                .region(region)
                .build();

        deleteAPI(apiGateway, restApiId);
        apiGateway.close();
    }

    // snippet-start:[apigateway.java2.delete_api.main]
    public static void deleteAPI( ApiGatewayClient apiGateway, String restApiId) {

        try {
            DeleteRestApiRequest request = DeleteRestApiRequest.builder()
                .restApiId(restApiId)
                .build();

            apiGateway.deleteRestApi(request);
            System.out.println("The API was successfully deleted");

        } catch (ApiGatewayException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[apigateway.java2.delete_api.main]
}

