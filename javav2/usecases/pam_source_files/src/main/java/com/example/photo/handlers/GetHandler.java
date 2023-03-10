package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.photo.WorkCount;
import com.example.photo.services.DynamoDBService;
import com.google.gson.Gson;

import java.util.Map;
import java.util.TreeMap;

public class GetHandler implements RequestHandler<Map<String, Object>, APIGatewayProxyResponseEvent> {
  @Override
  public APIGatewayProxyResponseEvent handleRequest(Map<String, Object> event, Context context) {
        context.getLogger().log("In Labels handler");
        DynamoDBService dbService = new DynamoDBService();
        Map<String, WorkCount> map = dbService.scanPhotoTable();
        context.getLogger().log("Retrieved photos: " + map.size());
        Gson gson = new Gson();
        Map<String, Map<String, WorkCount>> m = new TreeMap<>();
        m.put("labels", map);

        Map<String, String> headersMap = Map.of(
            "Access-Control-Allow-Origin", "*"
        );

    return new APIGatewayProxyResponseEvent()
        .withStatusCode(200)
        .withHeaders(headersMap)
        .withBody(gson.toJson(m))
        .withIsBase64Encoded(false);
  }
}
