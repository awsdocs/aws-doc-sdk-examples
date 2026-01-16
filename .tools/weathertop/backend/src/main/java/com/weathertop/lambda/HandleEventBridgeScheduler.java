// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.weathertop.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.weathertop.service.EventBridgeScheduler;
import java.util.Map;

public class HandleEventBridgeScheduler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        LambdaLogger logger = context.getLogger();
        EventBridgeScheduler schedule = new EventBridgeScheduler();
        try {
            // Extract "language" from query parameters
            Map<String, String> queryParams = event.getQueryStringParameters();
            if (queryParams == null ) {
                return createErrorResponse(400, "Missing required query parameters");
            }

            String taskDefinitionArnVal = queryParams.get("taskDefinitionArnVal").toLowerCase(); // Normalize
            String clusterName = queryParams.get("clusterName").toLowerCase();
            String cron = queryParams.get("cron").toLowerCase();
            String ruleName = "";
            if (clusterName.equals("MyNetWeathertopCluster")) {
                ruleName = "ecs-dotnet-schedule";
            } else if (clusterName.equals("MyJavaWeathertopCluster")) {
                ruleName = "ecs-java-schedule";

            } else if (clusterName.equals("MyRustWeathertopCluster")) {
                ruleName = "ecs-rust-schedule";
            } else {
                ruleName = "ecs-java-schedule";
            }

            String message =  schedule.setScheduler(taskDefinitionArnVal, clusterName, cron, ruleName);

            // Return response
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(Map.of(
                            "Access-Control-Allow-Origin", "*",
                            "Access-Control-Allow-Methods", "GET,OPTIONS",
                            "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                    ))
                    .withBody(message);

        } catch (Exception e) {
            logger.log("Exception: " + e.getMessage());
            return createErrorResponse(500, "Internal server error: " + e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(Map.of("Access-Control-Allow-Origin", "*"))
                .withBody("{\"error\": \"" + message + "\"}");
    }
}