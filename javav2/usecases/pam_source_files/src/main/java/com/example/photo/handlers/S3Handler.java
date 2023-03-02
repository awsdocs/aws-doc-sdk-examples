package com.example.photo.handlers;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.photo.endpoints.UploadEndpoint;
import com.example.photo.services.AnalyzePhotos;
import com.example.photo.services.DynamoDBService;
import com.example.photo.services.S3Service;

// Tags the object after it's uploaded into the storage bucket.
public class S3Handler implements RequestHandler<S3Event, String> {

    @Override
    public String handleRequest(S3Event event, Context context) {
        // Get the S3 bucket and object key from the S3 event.
        String bucketName = event.getRecords().get(0).getS3().getBucket().getName();
        String objectKey = event.getRecords().get(0).getS3().getObject().getKey();

        // Log the S3 bucket and object key in the log file.
        context.getLogger().log("S3 object name: s3://" + bucketName + "/" + objectKey);

        AnalyzePhotos photos = new AnalyzePhotos();
        DynamoDBService dbService = new DynamoDBService();
        S3Service s3Service = new S3Service();

        // Tag the file
        UploadEndpoint endpoint = new UploadEndpoint(photos, dbService, s3Service);
        endpoint.tagAfterUpload(objectKey);

        context.getLogger().log("Tagged image");
        return "OK"; // Unused response
    }
}
