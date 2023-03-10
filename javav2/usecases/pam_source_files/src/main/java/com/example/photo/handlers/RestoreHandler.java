package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.photo.Job;
import com.example.photo.endpoints.RestoreEndpoint;
import com.example.photo.services.DynamoDBService;
import com.example.photo.services.S3Service;
import com.example.photo.services.SnsService;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.photo.PhotoApplicationResources.toJson;
import static com.example.photo.PhotoApplicationResources.CORS_HEADER_MAP;

public class RestoreHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            RestoreEndpoint restoreEndpoint = new RestoreEndpoint(new DynamoDBService(), new S3Service(),
                    new SnsService());
            JSONObject body = new JSONObject(input.getBody());
            List<String> tags = body.getJSONArray("labels")
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

            Map<String, String> headersMap = Map.of(
                    "Access-Control-Allow-Origin", "*");

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(headersMap)
                    .withBody("{}")
                    .withIsBase64Encoded(false);
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withHeaders(CORS_HEADER_MAP)
                    .withBody(toJson(e))
                    .withIsBase64Encoded(false);
        }
    }
}
