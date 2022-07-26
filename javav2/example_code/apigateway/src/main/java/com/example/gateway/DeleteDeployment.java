//snippet-sourcedescription:[DeleteDeployment.java demonstrates how to delete a deployment.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon API Gateway]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.gateway;

// snippet-start:[apigateway.java2.delete_deployment.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.ApiGatewayException;
import software.amazon.awssdk.services.apigateway.model.DeleteDeploymentRequest;
// snippet-end:[apigateway.java2.delete_deployment.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DeleteDeployment {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteDeployment <restApiId> <deploymentId>\n\n" +
                "Where:\n" +
                "    restApiId - The string identifier of an existing RestApi. (for example, xxxx99ewyg).\n" +
                "    deploymentId - The string identifier of an existing deployment. \n" ;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String restApiId = args[0];
        String deploymentId =  args[1];
        Region region = Region.US_EAST_1;
        ApiGatewayClient apiGateway = ApiGatewayClient.builder()
                .region(region)
                .build();

        deleteSpecificDeployment(apiGateway, restApiId, deploymentId);
        apiGateway.close();
     }

    // snippet-start:[apigateway.java2.delete_deployment.main]
    public static void deleteSpecificDeployment(ApiGatewayClient apiGateway, String restApiId, String deploymentId) {

        try {
            DeleteDeploymentRequest request = DeleteDeploymentRequest.builder()
                .restApiId(restApiId)
                .deploymentId(deploymentId)
                .build();

            apiGateway.deleteDeployment(request);
            System.out.println("Deployment was deleted" );

        } catch (ApiGatewayException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[apigateway.java2.delete_deployment.main]
}
