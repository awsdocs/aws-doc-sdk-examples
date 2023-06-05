/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.photo.WorkCount;
import com.example.photo.services.DynamoDBService;

import java.util.Map;
import java.util.TreeMap;

import static com.example.photo.PhotoApplicationResources.makeResponse;

public class GetHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        context.getLogger().log("In Labels handler");

        DynamoDBService dbService = new DynamoDBService();
        Map<String, WorkCount> map = dbService.scanPhotoTable();

        context.getLogger().log("Retrieved photos: " + map.size());

        Map<String, Map<String, WorkCount>> data = new TreeMap<>();
        data.put("labels", map);

        return makeResponse(data);
    }
}
