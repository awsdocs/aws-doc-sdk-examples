package com.example.photo.endpoints;

import com.example.photo.PhotoApplicationResources;
import com.example.photo.WorkItem;
import com.example.photo.services.AnalyzePhotos;
import com.example.photo.services.DynamoDBService;
import com.example.photo.services.S3Service;

import java.util.List;

public class UploadEndpoint {
    final AnalyzePhotos analyzePhotos;
    final DynamoDBService dbService;
    final S3Service s3Service;

    public UploadEndpoint(AnalyzePhotos analyzePhotos, DynamoDBService dynamoDBService, S3Service s3Service) {
        this.analyzePhotos = analyzePhotos;
        this.dbService = dynamoDBService;
        this.s3Service = s3Service;
    }

    // This method has changed as the JPG has been placed into the Bucket via a Presigned URL
    public void tagAfterUpload(String name) {
        List<WorkItem> labels = analyzePhotos.detectLabels(PhotoApplicationResources.STORAGE_BUCKET, name);
        s3Service.markAsRekognized(name);
        dbService.putRecord(List.of(labels));
    }

    // This method is only invoked from the Spring Controller and not the AWS Lambda handler.
    public void upload(byte[] bytes, String name) {
        // Put the file into the bucket.
        s3Service.putObject(bytes, PhotoApplicationResources.STORAGE_BUCKET, name);
        List<WorkItem> labels = analyzePhotos.detectLabels(PhotoApplicationResources.STORAGE_BUCKET, name);
        s3Service.markAsRekognized(name);
        dbService.putRecord(List.of(labels));
    }

    // Copy every object in source bucket with suffix .jpe?g to Storage Bucket.
    public int copyFiles(String source) {
        return s3Service.copyFiles(source);
    }
}
