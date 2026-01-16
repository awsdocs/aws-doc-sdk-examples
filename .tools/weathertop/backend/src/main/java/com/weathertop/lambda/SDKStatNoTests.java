// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.weathertop.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.weathertop.service.SDKStats;
import java.util.Map;

public class SDKStatNoTests  implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    /**
     * Handles the incoming API Gateway request to get the latest summaries for
     * a set of SDK languages for the Coverage by Language component.
     *
     * @param event the {@link APIGatewayProxyRequestEvent} containing the request details
     * @param context the {@link Context} object providing information about the current execution environment
     * @return a {@link APIGatewayProxyResponseEvent} containing the response to be returned to the API Gateway
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        LambdaLogger logger = context.getLogger();

        try {
            // Extract "language" from query parameters
            Map<String, String> queryParams = event.getQueryStringParameters();

            // Test with hard coding lanhs
            String[] langs = {"dotnetv4", "dotnetv3", "php", "python", "ruby", "javascriptv3"};
            // String language = queryParams.get("language").toLowerCase(); // Normalize
            logger.log("Received languages: " + langs);

            // Fetch latest JSON from S3
            SDKStats langStats = new SDKStats();
            String JSON = langStats.getNoTestsBySDK(langs);

            // Return response
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(Map.of(
                            "Access-Control-Allow-Origin", "*",
                            "Access-Control-Allow-Methods", "GET,OPTIONS",
                            "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token"
                    ))
                    .withBody(JSON);

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


