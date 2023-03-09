package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.photo.services.S3Service;
import com.google.gson.Gson;

import java.util.Map;

public class CopyHandler implements RequestHandler<Map<String, String>, String> {

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        LambdaLogger logger = context.getLogger();
        String sourceBucket = event.get("source");
        logger.log("Copying files from: " + sourceBucket);

        S3Service s3Service = new S3Service();
        int numFiles = s3Service.copyFiles(sourceBucket);

        CopyHandlerResponse data = CopyHandlerResponse.from(numFiles, sourceBucket);

        Gson gson = new Gson();
        return gson.toJson(data);
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
