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
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class S3Service {
    // Create the S3Client object.
    private S3Client getClient() {
        return S3Client.builder()
            .region(PhotoApplicationResources.REGION)
            .build();
    }

    public byte[] getObjectBytes(String bucketName, String keyName) {
        S3Client s3 = getClient();
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .key(keyName)
                .bucket(bucketName)
                .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            return objectBytes.asByteArray();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Returns the names of all images in the given Amazon Simple Storage Service (Amazon S3) bucket.
    public List<String> listBucketObjects(String bucketName) {
        S3Client s3 = getClient();
        String keyName;
        List<String> keys = new ArrayList<>();
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(bucketName)
                .build();

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

    // Places an image into an Amazon S3 bucket.
    public void putObject(byte[] data, String bucketName, String objectKey) {
        S3Client s3 = getClient();
        try {
            s3.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build(),
                RequestBody.fromBytes(data));
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

    // Returns true if object exists.
    public boolean checkS3ObjectDoesNotExist(String keyName) {
        S3Client s3 = getClient();
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
            .bucket(PhotoApplicationResources.STORAGE_BUCKET)
            .key(keyName)
            .build();

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

    // New method to sign an object prior to uploading it.
    public String signObjectToDownload(String bucketName, String keyName) {
        S3Presigner presignerOb = S3Presigner.builder()
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

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}