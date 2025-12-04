// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.weathertop.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weathertop.service.EcsEventBridgeInspector;
import java.util.Map;

public class HandleEventBridgeInspector implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(Map.of(
                        "Access-Control-Allow-Origin", "*",
                        "Access-Control-Allow-Methods", "GET,OPTIONS",
                        "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                ))
                .withBody("{\"error\": \"" + message.replace("\"", "\\\"") + "\"}");
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        LambdaLogger logger = context.getLogger();

        try {
            Map<String, String> queryParams = event.getQueryStringParameters();
            if (queryParams == null || !queryParams.containsKey("language")) {
                return createErrorResponse(400, "Missing required query parameter: language");
            }

            String language = queryParams.get("language").toLowerCase();
            logger.log("Received language: " + language);

            switch (language) {
                case "java": {
                    logger.log("Processing Java SDK EventBridge Inspector");

                    EcsEventBridgeInspector inspector = new EcsEventBridgeInspector(
                            "MyJavaWeathertopCluster",
                            "WeathertopJava",
                            "WeathertopJavaLogs",
                            "ecs-java-schedule"
                    );

                    Map<String, Object> data = inspector.inspect();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(data);

                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withHeaders(Map.of(
                                    "Access-Control-Allow-Origin", "*",
                                    "Access-Control-Allow-Methods", "GET,OPTIONS",
                                    "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                            ))
                            .withBody(json);
                }

                case "dotnetv4": {
                    logger.log("Processing .NET EventBridge Inspector");

                    String clusterName = "MyNetWeathertopCluster";      // Must match an actual ECS cluster
                    String serviceName = "WeathertopNet";               // Must be a service running in that cluster
                    String logGroup = "WeathertopDotNetLogs";              // Must be a CloudWatch log group
                    String eventBridgeRulePrefix = "ecs-dotnet-schedule";  // Must match existing EventBridge rules

                    // Call your inspector (assuming your EcsEventBridgeInspector works with these values)
                    EcsEventBridgeInspector inspector = new EcsEventBridgeInspector(
                            clusterName,
                            serviceName,
                            logGroup,
                            eventBridgeRulePrefix
                    );


                    Map<String, Object> data = inspector.inspect();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(data);

                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withHeaders(Map.of(
                                    "Access-Control-Allow-Origin", "*",
                                    "Access-Control-Allow-Methods", "GET,OPTIONS",
                                    "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                            ))
                            .withBody(json);
                }
                case "dotnetv3": {
                    logger.log("Processing .NET EventBridge Inspector");

                    String clusterName = "MyNet3WeathertopCluster";      // Must match an actual ECS cluster
                    String serviceName = "WeathertopNet3";               // Must be a service running in that cluster
                    String logGroup = "WeathertopDotNet3Logs";              // Must be a CloudWatch log group
                    String eventBridgeRulePrefix = "ecs-dotnet3-schedule";  // Must match existing EventBridge rules

                    // Call your inspector (assuming your EcsEventBridgeInspector works with these values)
                    EcsEventBridgeInspector inspector = new EcsEventBridgeInspector(
                            clusterName,
                            serviceName,
                            logGroup,
                            eventBridgeRulePrefix
                    );


                    Map<String, Object> data = inspector.inspect();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(data);

                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withHeaders(Map.of(
                                    "Access-Control-Allow-Origin", "*",
                                    "Access-Control-Allow-Methods", "GET,OPTIONS",
                                    "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                            ))
                            .withBody(json);
                }

                case "javascriptv3": {
                    logger.log("Processing JS EventBridge Inspector");

                    String clusterName = "MyJSWeathertopCluster";      // Must match an actual ECS cluster
                    String serviceName = "WeathertopJS";               // Must be a service running in that cluster
                    String logGroup = "WeathertopJSLogs";              // Must be a CloudWatch log group
                    String eventBridgeRulePrefix = "ecs-js-schedule";  // Must match existing EventBridge rules

                    // Call your inspector (assuming your EcsEventBridgeInspector works with these values)
                    EcsEventBridgeInspector inspector = new EcsEventBridgeInspector(
                            clusterName,
                            serviceName,
                            logGroup,
                            eventBridgeRulePrefix
                    );


                    Map<String, Object> data = inspector.inspect();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(data);

                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withHeaders(Map.of(
                                    "Access-Control-Allow-Origin", "*",
                                    "Access-Control-Allow-Methods", "GET,OPTIONS",
                                    "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                            ))
                            .withBody(json);
                }

                case "kotlin": {
                    logger.log("Processing JS EventBridge Inspector");

                    String clusterName = "MyKotlinWeathertopCluster";
                    String taskDefinitionFamily = "WeathertopKotlin";
                    String logGroup = "WeathertopKotlinContainerLogs";
                    String ruleName = "ecs-kotlin-schedule";           // Must be a CloudWatch log group

                    EcsEventBridgeInspector inspector = new EcsEventBridgeInspector(
                            clusterName,
                            taskDefinitionFamily,
                            logGroup,
                            ruleName
                    );

                    Map<String, Object> data = inspector.inspect();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(data);

                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withHeaders(Map.of(
                                    "Access-Control-Allow-Origin", "*",
                                    "Access-Control-Allow-Methods", "GET,OPTIONS",
                                    "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                            ))
                            .withBody(json);

                }

                case "python": {
                    logger.log("Processing JS EventBridge Inspector");

                    String clusterName = "MyPythonWeathertopCluster";
                    String taskDefinitionFamily = "WeathertopPython";
                    String logGroup = "WeathertopPythonContainerLogs";
                    String ruleName = "ecs-python-schedule";           // Must be a CloudWatch log group
                    String eventBridgeRulePrefix = "ecs-python-schedule";  // Must match existing EventBridge rules

                    EcsEventBridgeInspector inspector = new EcsEventBridgeInspector(
                            clusterName,
                            taskDefinitionFamily,
                            logGroup,
                            ruleName
                    );

                    Map<String, Object> data = inspector.inspect();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(data);

                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withHeaders(Map.of(
                                    "Access-Control-Allow-Origin", "*",
                                    "Access-Control-Allow-Methods", "GET,OPTIONS",
                                    "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                            ))
                            .withBody(json);
                }

                case "php": {
                    logger.log("Processing JS EventBridge Inspector");

                    String clusterName = "MyPHPWeathertopCluster";
                    String taskDefinitionFamily = "WeathertopPhp";
                    String logGroup = "WeathertopPhpContainerLogs";
                    String ruleName = "ecs-php-schedule";           // Must be a CloudWatch log group
                    String eventBridgeRulePrefix = "ecs-js-schedule";  // Must match existing EventBridge rules

                    EcsEventBridgeInspector inspector = new EcsEventBridgeInspector(
                            clusterName,
                            taskDefinitionFamily,
                            logGroup,
                            ruleName
                    );

                    Map<String, Object> data = inspector.inspect();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(data);

                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withHeaders(Map.of(
                                    "Access-Control-Allow-Origin", "*",
                                    "Access-Control-Allow-Methods", "GET,OPTIONS",
                                    "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                            ))
                            .withBody(json);
                }

                case "gov2": {
                    logger.log("Processing JS EventBridge Inspector");

                    String clusterName = "MyGoWeathertopCluster";
                    String taskDefinitionFamily = "WeathertopGo";
                    String logGroup = "WeathertopGoContainerLogs";
                    String ruleName = "ecs-go-schedule";           // Must be a CloudWatch log group
                    String eventBridgeRulePrefix = "ecs-js-schedule";  // Must match existing EventBridge rules

                    EcsEventBridgeInspector inspector = new EcsEventBridgeInspector(
                            clusterName,
                            taskDefinitionFamily,
                            logGroup,
                            ruleName
                    );

                    Map<String, Object> data = inspector.inspect();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(data);

                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withHeaders(Map.of(
                                    "Access-Control-Allow-Origin", "*",
                                    "Access-Control-Allow-Methods", "GET,OPTIONS",
                                    "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                            ))
                            .withBody(json);
                }

                case "rustv1": {
                    logger.log("Processing RUST EventBridge Inspector");

                    String clusterName = "MyRustWeathertopCluster";
                    String taskDefinitionFamily = "WeathertopRust";
                    String logGroup = "WeathertopRustContainerLogs";
                    String ruleName = "ecs-rust-schedule";           // Must be a CloudWatch log group
                    String eventBridgeRulePrefix = "ecs-rust-schedule";  // Must match existing EventBridge rules

                    EcsEventBridgeInspector inspector = new EcsEventBridgeInspector(
                            clusterName,
                            taskDefinitionFamily,
                            logGroup,
                            ruleName
                    );

                    Map<String, Object> data = inspector.inspect();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(data);

                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withHeaders(Map.of(
                                    "Access-Control-Allow-Origin", "*",
                                    "Access-Control-Allow-Methods", "GET,OPTIONS",
                                    "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                            ))
                            .withBody(json);
                }

                case "cpp": {
                    logger.log("Processing JS EventBridge Inspector");

                    String clusterName = "MyCPPWeathertopCluster";
                    String taskDefinitionFamily = "WeathertopCPP";
                    String logGroup = "WeathertopCPPContainerLogs";
                    String ruleName = "ecs-cpp-schedule";           // Must be a CloudWatch log group

                    EcsEventBridgeInspector inspector = new EcsEventBridgeInspector(
                            clusterName,
                            taskDefinitionFamily,
                            logGroup,
                            ruleName
                    );

                    Map<String, Object> data = inspector.inspect();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(data);

                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withHeaders(Map.of(
                                    "Access-Control-Allow-Origin", "*",
                                    "Access-Control-Allow-Methods", "GET,OPTIONS",
                                    "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                            ))
                            .withBody(json);
                }

                case "ruby": {
                    logger.log("Processing JS EventBridge Inspector");

                    String clusterName = "MyRubyWeathertopCluster";
                    String taskDefinitionFamily = "WeathertopRuby";
                    String logGroup = "WeathertopRubyContainerLogs";
                    String ruleName = "ecs-ruby-schedule";           // Must be a CloudWatch log group

                    EcsEventBridgeInspector inspector = new EcsEventBridgeInspector(
                            clusterName,
                            taskDefinitionFamily,
                            logGroup,
                            ruleName
                    );

                    Map<String, Object> data = inspector.inspect();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(data);

                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withHeaders(Map.of(
                                    "Access-Control-Allow-Origin", "*",
                                    "Access-Control-Allow-Methods", "GET,OPTIONS",
                                    "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                            ))
                            .withBody(json);
                }


                default:
                    String unknownMsg = "Unknown language parameter: '" + language + "'";
                    logger.log(unknownMsg);
                    return createErrorResponse(400, unknownMsg);
            }

        } catch (Exception e) {
            logger.log("Exception: " + e.getMessage());
            return createErrorResponse(500, "Internal server error: " + e.getMessage());
        }
    }
}
