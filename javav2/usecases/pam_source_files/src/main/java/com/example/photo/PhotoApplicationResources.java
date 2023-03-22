/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import software.amazon.awssdk.regions.Region;

public class PhotoApplicationResources {

    public static final Region REGION = Region.of(System.getenv("AWS_REGION"));

    public static final String STORAGE_BUCKET = System.getenv("STORAGE_BUCKET_NAME");
    public static final String WORKING_BUCKET = System.getenv("WORKING_BUCKET_NAME");

    public static final String LABELS_TABLE = System.getenv("LABELS_TABLE_NAME");
    public static final String JOBS_TABLE = System.getenv("JOBS_TABLE_NAME");

    public static final String TOPIC_ARN = System.getenv("NOTIFICATION_TOPIC");

    public static final Map<String, String> CORS_HEADER_MAP = Map.of(
            "Access-Control-Allow-Origin", "*");
    public static final Gson gson = new Gson();

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static APIGatewayProxyResponseEvent makeResponse(Object src) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withHeaders(CORS_HEADER_MAP)
                .withBody(toJson(src))
                .withIsBase64Encoded(false);
    }
}
