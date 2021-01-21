//snippet-sourcedescription:[CreateDeployment.java demonstrates how to create a deployment.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon API Gateway]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[01/21/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.gateway;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

public class CreateDeployment {
    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        ApiGatewayClient apiGateway = ApiGatewayClient.builder()
                .region(region)
                .build();

        String restApiId = "inx399ewyg";
        createNewDeployment(apiGateway, restApiId);
        apiGateway.close();

    }

    public static String createNewDeployment(ApiGatewayClient apiGateway, String restApiId) {

        try {
            CreateDeploymentRequest request = CreateDeploymentRequest.builder()
                    .restApiId(restApiId)
                    .description("Created using the AWS API Gateway Java API")
                    .stageName("MyStage")
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
}
