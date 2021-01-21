package com.example.gateway;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.ApiGatewayException;
import software.amazon.awssdk.services.apigateway.model.GetMethodRequest;
import software.amazon.awssdk.services.apigateway.model.GetMethodResponse;
import software.amazon.awssdk.services.apigateway.model.MethodResponse;
import java.util.Map;

public class GetMethod {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        ApiGatewayClient apiGateway = ApiGatewayClient.builder()
                .region(region)
                .build();

        String restApiId = "inx399ewyg";
        String resourceId = "d41817";
        String httpMethod = "GET";

        getSpecificMethod(apiGateway, restApiId, resourceId, httpMethod);
        apiGateway.close();

    }

    public static void getSpecificMethod(ApiGatewayClient apiGateway,
                                         String restApiId,
                                         String resourceId,
                                         String httpMethod) {

        try {
            GetMethodRequest methodRequest = GetMethodRequest.builder()
                .httpMethod(httpMethod)
                .restApiId(restApiId)
                .resourceId(resourceId)
                .build();

            GetMethodResponse response = apiGateway.getMethod(methodRequest);

            // Retrieve a method response associated with a given HTTP status code.
            Map<String, MethodResponse> details = response.methodResponses();
            for (Map.Entry<String,MethodResponse> entry : details.entrySet())
                System.out.println("Key = " + entry.getKey() +
                        ", Value = " + entry.getValue());

        } catch (ApiGatewayException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
      }
  }
