package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.photo.services.S3Service;
import java.util.UUID;
import org.json.JSONObject;
import static com.example.photo.PhotoApplicationResources.makeResponse;
import static com.example.photo.PhotoApplicationResources.CORS_HEADER_MAP;

public class UploadHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        JSONObject body = new JSONObject(input.getBody());
        context.getLogger().log("Got body: " + body);
        String fileName = body.getString("file_name");
        context.getLogger().log("Building URL for " + fileName);

        if (fileName == null || fileName.equals("")) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withHeaders(CORS_HEADER_MAP)
                    .withBody("{\"error\":\"Missing filename\"}")
                    .withIsBase64Encoded(false);
        }
        UUID uuid = UUID.randomUUID();
        String uniqueFileName = uuid + "-" + fileName;

        S3Service s3Service = new S3Service();
        String signedURL = s3Service.signObjectToUpload(uniqueFileName);
        UploadResponse data = UploadResponse.from(signedURL);

        return makeResponse(data);
    }
}

class UploadResponse {
    private final String url;
    private UploadResponse(String url) {
        this.url = url;
    }

    static UploadResponse from(String url) {
        return new UploadResponse(url);
    }
    public String getURL() {
        return url;
    }
}