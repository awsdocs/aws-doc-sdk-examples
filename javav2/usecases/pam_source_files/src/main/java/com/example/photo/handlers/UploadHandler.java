package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.photo.services.S3Service;
import com.google.gson.Gson;

import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

public class UploadHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        JSONObject body = new JSONObject(input.getBody());
        context.getLogger().log("Got body: " + body);
        String fileName = body.getString("file_name");
        context.getLogger().log("Building URL for " + fileName);
        if (fileName == null || fileName.equals("")) {
            throw new RuntimeException("Missing filename");
        }
        UUID uuid = UUID.randomUUID();
        String uniqueFileName = uuid + "-" + fileName;

        S3Service s3Service = new S3Service();

        String signedURL = s3Service.signObjectToUpload(uniqueFileName);

        UploadResponse data = UploadResponse.from(signedURL);

        Gson gson = new Gson();
        Map<String, String> headersMap = Map.of(
                "Access-Control-Allow-Origin", "*");

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withHeaders(headersMap)
                .withBody(gson.toJson(data))
                .withIsBase64Encoded(false);
    }
}

class UploadResponse {
    private final String url;

    static UploadResponse from(String url) {
        return new UploadResponse(url);
    }

    private UploadResponse(String url) {
        this.url = url;
    }

    public String getURL() {
        return url;
    }
}