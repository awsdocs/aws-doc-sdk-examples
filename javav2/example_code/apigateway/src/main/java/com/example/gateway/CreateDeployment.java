//snippet-sourcedescription:[CreateDeployment.java demonstrates how to create a deployment resource.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon API Gateway]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.gateway;

// snippet-start:[apigateway.java2.create_deployment.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.CreateDeploymentRequest;
import software.amazon.awssdk.services.apigateway.model.CreateDeploymentResponse;
import software.amazon.awssdk.services.apigateway.model.ApiGatewayException;
// snippet-end:[apigateway.java2.create_deployment.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateDeployment {
    public static void main(String[] args) {

        final String USAGE = "\n" +
            "Usage:\n" +
            "    <restApiId> <stageName>\n\n" +
            "Where:\n" +
            "    restApiId - The string identifier of the associated RestApi. (for example, xxxx99ewyg).\n" +
            "    stageName - The name of the stage. \n" ;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String restApiId = args[0];
        String stageName = args[1];
        Region region = Region.US_EAST_1;
        ApiGatewayClient apiGateway = ApiGatewayClient.builder()
            .region(region)
            .build();

        createNewDeployment(apiGateway, restApiId, stageName);
        apiGateway.close();
    }

    // snippet-start:[apigateway.java2.create_deployment.main]
    public static String createNewDeployment(ApiGatewayClient apiGateway, String restApiId, String stageName) {

        try {
            CreateDeploymentRequest request = CreateDeploymentRequest.builder()
                .restApiId(restApiId)
                .description("Created using the AWS API Gateway Java API")
                .stageName(stageName)
                .build();

            CreateDeploymentResponse response = apiGateway.createDeployment(request);
            System.out.println("The id of the deployment is "+response.id());
            return response.id();

        } catch (ApiGatewayException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return ""  ;
    }
    // snippet-end:[apigateway.java2.create_deployment.main]
}