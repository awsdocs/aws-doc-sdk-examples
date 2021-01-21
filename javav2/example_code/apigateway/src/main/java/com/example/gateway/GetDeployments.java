package com.example.gateway;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

import java.util.List;

public class GetDeployments {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        ApiGatewayClient apiGateway = ApiGatewayClient.builder()
                .region(region)
                .build();

        String restApiId = "inx399ewyg";
        getAllDeployments(apiGateway, restApiId);
        apiGateway.close();
    }

    public static void getAllDeployments(ApiGatewayClient apiGateway,  String restApiId) {

        try {
            GetDeploymentsRequest request = GetDeploymentsRequest.builder()
                    .restApiId(restApiId)
                    .build();

            GetDeploymentsResponse response = apiGateway.getDeployments(request);
            List<Deployment> deployments = response.items();
            for (Deployment deployment: deployments) {
                System.out.println("The deployment id is "+deployment.id());
                System.out.println("The deployment description is "+deployment.description());
            }


        } catch (ApiGatewayException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
