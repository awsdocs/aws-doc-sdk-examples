/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo.services;

import com.example.photo.PhotoApplicationResources;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
<<<<<<< Updated upstream
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.Tagging;
=======
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
>>>>>>> Stashed changes
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
<<<<<<< Updated upstream
import java.util.*;
=======
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
>>>>>>> Stashed changes
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class S3Service {
    // Create the S3Client object.
    private S3Client getClient() {
        return S3Client.builder()
<<<<<<< Updated upstream
                .region(PhotoApplicationResources.REGION)
                .build();
=======
            .region(PhotoApplicationResources.REGION)
            .build();
>>>>>>> Stashed changes
    }

    public byte[] getObjectBytes(String bucketName, String keyName) {
        S3Client s3 = getClient();
        try {
            GetObjectRequest objectRequest = GetObjectRequest
<<<<<<< Updated upstream
                    .builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();
=======
                .builder()
                .key(keyName)
                .bucket(bucketName)
                .build();
>>>>>>> Stashed changes

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            return objectBytes.asByteArray();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Returns the names of all images in the given bucket.
    public List<String> listBucketObjects(String bucketName) {
        S3Client s3 = getClient();
        String keyName;
        List<String> keys = new ArrayList<>();
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
<<<<<<< Updated upstream
                    .builder()
                    .bucket(bucketName)
                    .build();
=======
                .builder()
                .bucket(bucketName)
                .build();
>>>>>>> Stashed changes

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                keyName = myValue.key();
                keys.add(keyName);
            }

            return keys;
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Places an image into a S3 bucket.
    public void putObject(byte[] data, String bucketName, String objectKey) {
        S3Client s3 = getClient();
        try {
            s3.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build(),
<<<<<<< Updated upstream
                    RequestBody.fromBytes(data));
=======
                RequestBody.fromBytes(data));
>>>>>>> Stashed changes
        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Pass a map and get back a byte[] that represents a ZIP of all images.
    public byte[] listBytesToZip(Map<String, byte[]> mapReport) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        for (Map.Entry<String, byte[]> report : mapReport.entrySet()) {
            ZipEntry entry = new ZipEntry(report.getKey());
            entry.setSize(report.getValue().length);
            zos.putNextEntry(entry);
            zos.write(report.getValue());
        }
        zos.closeEntry();
        zos.close();
        return baos.toByteArray();
    }

    // Copy objects from the source bucket to storage bucket.
    public int copyFiles(String sourceBucket) {
        S3Client s3 = getClient();

        int count = 0;
        // Only move .jpg images
        ListObjectsV2Request request = ListObjectsV2Request.builder()
<<<<<<< Updated upstream
                .bucket(sourceBucket)
                .build();
=======
            .bucket(sourceBucket)
            .build();
>>>>>>> Stashed changes

        ListObjectsV2Response response = s3.listObjectsV2(request);
        for (S3Object s3Object : response.contents()) {
            // Check to make sure the object does not exist in the bucket. If the object
            // exists it will not be copied again.
            String key = s3Object.key();
            if (checkS3ObjectDoesNotExist(key)) {
                System.out.println("Object exists in the bucket.");
            } else if ((key.endsWith(".jpg")) || (key.endsWith(".jpeg"))) {
                System.out.println("JPG object found and will be copied: " + key);
                copyS3Object(sourceBucket, key);
                count++;
            }
        }

        return count;
    }

    // Returns true if object exists.
    public boolean checkS3ObjectDoesNotExist(String keyName) {
        S3Client s3 = getClient();
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
<<<<<<< Updated upstream
                .bucket(PhotoApplicationResources.STORAGE_BUCKET)
                .key(keyName)
                .build();
=======
            .bucket(PhotoApplicationResources.STORAGE_BUCKET)
            .key(keyName)
            .build();
>>>>>>> Stashed changes

        try {
            HeadObjectResponse response = s3.headObject(headObjectRequest);
            String contentType = response.contentType();
            if (contentType.length() > 0)
                return true;
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    public void copyS3Object(String sourceBucket, String objectKey) {
        S3Client s3 = getClient();

        CopyObjectRequest copyReq = CopyObjectRequest.builder()
<<<<<<< Updated upstream
                .sourceBucket(sourceBucket)
                .sourceKey(objectKey)
                .destinationBucket(PhotoApplicationResources.STORAGE_BUCKET)
                .destinationKey(objectKey)
                .build();
=======
            .sourceBucket(sourceBucket)
            .sourceKey(objectKey)
            .destinationBucket(PhotoApplicationResources.STORAGE_BUCKET)
            .destinationKey(objectKey)
            .build();
>>>>>>> Stashed changes

        try {
            s3.copyObject(copyReq);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // New method to sign an object prior to uploading it
    public String signObjectToDownload(String bucketName, String keyName) {
        S3Presigner presignerOb = S3Presigner.builder()
<<<<<<< Updated upstream
                .region(PhotoApplicationResources.REGION)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(1440))
                    .getObjectRequest(getObjectRequest)
                    .build();
=======
            .region(PhotoApplicationResources.REGION)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(1440))
                .getObjectRequest(getObjectRequest)
                .build();
>>>>>>> Stashed changes

            PresignedGetObjectRequest presignedGetObjectRequest = presignerOb.presignGetObject(getObjectPresignRequest);

            return presignedGetObjectRequest.url().toString();
        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String signObjectToUpload(String keyName) {
        S3Presigner presigner = S3Presigner.builder()
<<<<<<< Updated upstream
                .region(PhotoApplicationResources.REGION)
                .build();

        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(PhotoApplicationResources.STORAGE_BUCKET)
                    .key(keyName)
                    .contentType("image/jpeg")
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5))
                    .putObjectRequest(objectRequest)
                    .build();
=======
            .region(PhotoApplicationResources.REGION)
            .build();

        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(PhotoApplicationResources.STORAGE_BUCKET)
                .key(keyName)
                .contentType("image/jpeg")
                .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(objectRequest)
                .build();
>>>>>>> Stashed changes

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}