// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.weathertop.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.weathertop.service.FargateTaskRunner;
import java.util.Map;

public class HandleRunFargateTask implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        LambdaLogger logger = context.getLogger();

        try {
            // Extract "language" from query parameters
            Map<String, String> queryParams = event.getQueryStringParameters();
            if (queryParams == null || !queryParams.containsKey("language")) {
                return createErrorResponse(400, "Missing required query parameter: language");
            }

            String language = queryParams.get("language").toLowerCase(); // Normalize
            logger.log("Received language: " + language);

            FargateTaskRunner runner = new FargateTaskRunner();
            String taskRunId = "";
            if (language.compareTo("java") == 0) {
                String clusteerName = "MyJavaWeathertopCluster";
                String defName = "WeathertopJava";
                taskRunId = runner.runFargateTask(defName, clusteerName);

            } else if (language.compareTo("rustv1") == 0) {
                String clusteerName = "MyRustWeathertopCluster";
                String defName = "WeathertopRust";
                taskRunId = runner.runFargateTask(defName, clusteerName);

            } else if (language.compareTo("ruby") == 0) {
                String clusteerName = "MyRubyWeathertopCluster";
                String defName = "WeathertopRuby";
                taskRunId = runner.runFargateTask(defName, clusteerName);

            } else if (language.compareTo("kotlin") == 0) {
                String clusteerName = "MyKotlinWeathertopCluster";
                String defName = "WeathertopKotlin";
                taskRunId = runner.runFargateTask(defName, clusteerName);

            } else if (language.compareTo("dotnetv4") == 0) {
                String clusteerName = "MyNetWeathertopCluster";
                String defName = "WeathertopNet";
                taskRunId = runner.runFargateTask(defName, clusteerName);

            } else if (language.compareTo("dotnetv3") == 0) {
                String clusteerName = "MyNet3WeathertopCluster";
                String defName = "WeathertopNet3";
                taskRunId = runner.runFargateTask(defName, clusteerName);

            } else if (language.compareTo("javascriptv3") == 0) {

                String clusteerName = "MyJSWeathertopCluster";
                String defName = "WeathertopJS";
                taskRunId = runner.runFargateTask(defName, clusteerName);

            } else if (language.compareTo("php") == 0) {
                String clusterName = "MyPHPWeathertopCluster";
                String defName = "WeathertopPhp";  // Your PHP task definition family name
                taskRunId = runner.runFargateTask(defName, clusterName);

            } else if (language.compareTo("python") == 0) {
                String clusterName = "MyPythonWeathertopCluster";
                String defName = "WeathertopPython";  // Your PHP task definition family name
                taskRunId = runner.runFargateTask(defName, clusterName);

            } else if (language.compareTo("gov2") == 0) {
                String clusteerName = "MyGoWeathertopCluster";
                String defName = "WeathertopGo";
                taskRunId = runner.runFargateTask(defName, clusteerName);

            } else {
                logger.log("Unsupported language: " + language);
                // Optionally handle default case
            }


            // Return response
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(Map.of(
                            "Access-Control-Allow-Origin", "*",
                            "Access-Control-Allow-Methods", "GET,OPTIONS",
                            "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                    ))
                    .withBody("{\"taskArn\": \"" + taskRunId.replace("\"", "\\\"") + "\"}");

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