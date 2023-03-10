package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.photo.services.S3Service;
import org.json.JSONObject;

import static com.example.photo.PhotoApplicationResources.makeResponse;

public class CopyHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        JSONObject body = new JSONObject(input.getBody());
        context.getLogger().log("Got body: " + body);
        String sourceBucket = body.getString("source");
        context.getLogger().log("Copying files from: " + sourceBucket);

        S3Service s3Service = new S3Service();
        int numFiles = s3Service.copyFiles(sourceBucket);

        CopyHandlerResponse data = CopyHandlerResponse.from(numFiles, sourceBucket);

        return makeResponse(data);
    }
}

class CopyHandlerResponse {
    private final int count;
    private final String source;

    static CopyHandlerResponse from(int count, String source) {
        return new CopyHandlerResponse(count, source);
    }

    private CopyHandlerResponse(int count, String source) {
        this.count = count;
        this.source = source;
    }

    int getCount() {
        return this.count;
    }

    String getSource() {
        return this.source;
    }
}
