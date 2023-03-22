package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.photo.services.S3Service;
import java.util.UUID;
<<<<<<< Updated upstream

import org.json.JSONObject;

=======
import org.json.JSONObject;
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream

        String signedURL = s3Service.signObjectToUpload(uniqueFileName);

=======
        String signedURL = s3Service.signObjectToUpload(uniqueFileName);
>>>>>>> Stashed changes
        UploadResponse data = UploadResponse.from(signedURL);

        return makeResponse(data);
    }
}

class UploadResponse {
    private final String url;
<<<<<<< Updated upstream

    static UploadResponse from(String url) {
        return new UploadResponse(url);
    }

=======
>>>>>>> Stashed changes
    private UploadResponse(String url) {
        this.url = url;
    }

<<<<<<< Updated upstream
=======
    static UploadResponse from(String url) {
        return new UploadResponse(url);
    }
>>>>>>> Stashed changes
    public String getURL() {
        return url;
    }
}