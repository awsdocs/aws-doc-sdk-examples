package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.photo.services.S3Service;
import java.util.Map;

public class CopyHandler implements RequestHandler<Map<String, String>, String>{

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        LambdaLogger logger = context.getLogger();
        String sourceBucket = event.get("source");
        logger.log("Bucket name is: " + sourceBucket);

        S3Service s3Service = new S3Service();
        int numFiles = s3Service.copyFiles(sourceBucket);
        return "You copied " + numFiles + " files from " + sourceBucket;
    }
}
