package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.photo.Job;
import com.example.photo.endpoints.RestoreEndpoint;
import com.example.photo.services.DynamoDBService;
import com.example.photo.services.S3Service;
import com.example.photo.services.SnsService;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestoreHandler implements RequestHandler<Map<String, String>, String> {

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        try {
            RestoreEndpoint restoreEndpoint = new RestoreEndpoint(new DynamoDBService(), new S3Service(),
                    new SnsService());
            JSONObject body = new JSONObject(event.get("body"));
            List<String> tags = body.getJSONArray("tags")
                    .toList()
                    .stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .collect(Collectors.toList());
            String notify = body.getString("notify");

            context.getLogger().log("Restoring labels " + tags.stream().collect(Collectors.joining(" ")));
            context.getLogger().log("Notifying " + notify);

            Job job = restoreEndpoint.restore(notify, tags);

            context.getLogger().log("Started job " + job.getJobId() + " which will notify " + job.getTopicArn());

            return "OK";
        } catch (Exception e) {
            String st = Arrays.stream(e.getStackTrace()).map(t -> t.toString()).collect(Collectors.joining("\n"));
            context.getLogger().log("Error starting restore " + e.getMessage() + "\n" + st);
            throw new RuntimeException(e);
        }
    }
}
