package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.photo.endpoints.DownloadEndpoint;
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
      context.getLogger().log("RestoreHandler handleRequest" + toJson(input));
      JSONObject body = new JSONObject(input.getBody());
      List<String> labels = body.getJSONArray("tags")
          .toList()
          .stream()
          .filter(String.class::isInstance)
          .map(String.class::cast)
          .collect(Collectors.toList());
      context.getLogger().log("Restoring labels " + toJson(labels));

      DownloadEndpoint restoreEndpoint = new DownloadEndpoint(new DynamoDBService(), new S3Service(), new SnsService());
      String url = restoreEndpoint.download("notify", labels);

      context.getLogger().log("Labels archived to URL " + url);

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
