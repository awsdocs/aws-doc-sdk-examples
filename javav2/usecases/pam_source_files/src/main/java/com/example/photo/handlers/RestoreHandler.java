package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.photo.endpoints.RestoreEndpoint;
import com.example.photo.services.DynamoDBService;
import com.example.photo.services.S3Service;
import com.example.photo.services.SnsService;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestoreHandler implements RequestHandler<Map<String, String>, String> {

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        try {
            RestoreEndpoint restoreEndpoint = new RestoreEndpoint(new DynamoDBService(), new S3Service(), new SnsService());
            JSONObject body = new JSONObject(event.get("body"));
            List<String> tags = body.getJSONArray("tags")
                    .toList()
                    .stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .collect(Collectors.toList());
            String notify = body.getString("notify");

            restoreEndpoint.restore(notify, tags);

            return "OK";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
