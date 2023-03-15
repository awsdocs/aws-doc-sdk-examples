/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo.services;

import com.example.photo.PhotoApplicationResources;
<<<<<<< HEAD
import org.apache.logging.log4j.util.Strings;
=======
>>>>>>> 30bc5c02f (added new logic)
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.Tagging;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3control.S3ControlClient;
<<<<<<< HEAD
import software.amazon.awssdk.services.s3control.model.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
=======
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
>>>>>>> 30bc5c02f (added new logic)
import java.time.Duration;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class S3Service {
    // Create the S3Client object.
    private S3Client getClient() {
        return S3Client.builder()
                .region(PhotoApplicationResources.REGION)
                .build();
    }

<<<<<<< HEAD
    private S3ControlClient getControlClient() {
        return S3ControlClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(PhotoApplicationResources.REGION)
                .build();
    }

=======
>>>>>>> 30bc5c02f (added new logic)
    private List<Tag> getObjectTags(String bucketName, String keyName) {
        S3Client s3 = getClient();
        GetObjectTaggingRequest request = GetObjectTaggingRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        GetObjectTaggingResponse response = s3.getObjectTagging(request);
        return response.tagSet();
    }

    private boolean hasBeenRekognized(String keyName) {
        return tagsHasRekognized(getObjectTags(PhotoApplicationResources.STORAGE_BUCKET, keyName));
    }

    public static boolean tagsHasRekognized(Collection<Tag> tags) {
        return tags.stream()
                .filter(tag -> tag.key().equals(PhotoApplicationResources.REKOGNITION_TAG_KEY))
                .findAny()
                .orElseGet(() -> Tag.builder().build())
                .value().equals(PhotoApplicationResources.REKOGNITION_TAG_VALUE);
    }

    // Check for tags on the S3 object.
    public boolean tagCheck(String bucketName, String keyName) {
        List<Tag> tags = getObjectTags(PhotoApplicationResources.STORAGE_BUCKET, keyName);
        return hasRekognizedTag(tags);
    }

    public static boolean hasRekognizedTag(Collection<Tag> tags) {
        for (Tag tag : tags) {
            if (tag.key() == PhotoApplicationResources.REKOGNITION_TAG_KEY
                    && tag.value() == PhotoApplicationResources.REKOGNITION_TAG_VALUE) {
                return true;
            }
        }
        return false;
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
            System.exit(1);
        }
        return null;
    }

    // Tag the image.
    public void markAsRekognized(String objectName) {
        getClient().putObjectTagging(
                PutObjectTaggingRequest.builder()
                        .key(objectName)
                        .bucket(PhotoApplicationResources.STORAGE_BUCKET)
                        .tagging(
                                Tagging.builder()
                                        .tagSet(
                                                Tag.builder()
                                                        .key(PhotoApplicationResources.REKOGNITION_TAG_KEY)
                                                        .value(PhotoApplicationResources.REKOGNITION_TAG_VALUE)
                                                        .build())
                                        .build())
                        .build());
    }

    // Returns the names of all images in the given bucket.
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
            System.exit(1);
        }
        return null;
    }

<<<<<<< HEAD
    public String putManifest(String manifest) {
        UUID uuid = UUID.randomUUID();
        String key = uuid + ".csv";
        getClient().putObject(
                PutObjectRequest.builder()
                        .bucket(PhotoApplicationResources.WORKING_BUCKET)
                        .key(key)
                        .build(),
                RequestBody.fromString(manifest));
        return key;
    }

    public String startRestore(String manifestArn, List<String> labels) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        String label = Strings.join(labels, '-');

        JobManifest jobManifest = JobManifest.builder()
                .location(
                        JobManifestLocation.builder()
                                .objectArn(manifestArn)
                                .build())
                .build();
        JobOperation jobOperation = JobOperation.builder()
                .s3InitiateRestoreObject(
                        S3InitiateRestoreObjectOperation.builder()
                                .expirationInDays(1)
                                .glacierJobTier(S3GlacierJobTier.BULK)
                                .build())
                .build();
        JobReport jobReport = JobReport.builder().bucket(PhotoApplicationResources.WORKING_BUCKET).build();
        CreateJobRequest createJobRequest = CreateJobRequest.builder()
                .description("Restore objects matching " + label + " on " + date + ".")
                .operation(jobOperation)
                .manifest(jobManifest)
                .report(jobReport)
                .clientRequestToken(date + '-' + label)
                .build();
        return getControlClient().createJob(createJobRequest).jobId();
    }

=======
>>>>>>> 30bc5c02f (added new logic)
    // Places an image into a S3 bucket.
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
            System.exit(1);
        }
    }

<<<<<<< HEAD
    /*
     * // Places an image into a S3 bucket.
     * public void putObject(byte[] data, String bucketName, String objectKey) {
     * S3Client s3 = getClient();
     * try {
     * s3.putObject(PutObjectRequest.builder()
     * .bucket(bucketName)
     * .key(objectKey)
     * .build(),
     * RequestBody.fromBytes(data));
     * } catch (S3Exception e) {
     * System.err.println(e.getMessage());
     * System.exit(1);
     * }
     * }
     * // Pass a map and get back a byte[] that represents a ZIP of all images.
     * public byte[] listBytesToZip(Map<String, byte[]> mapReport) throws
     * IOException {
     * ByteArrayOutputStream baos = new ByteArrayOutputStream();
     * ZipOutputStream zos = new ZipOutputStream(baos);
     * for (Map.Entry<String, byte[]> report : mapReport.entrySet()) {
     * ZipEntry entry = new ZipEntry(report.getKey());
     * entry.setSize(report.getValue().length);
     * zos.putNextEntry(entry);
     * zos.write(report.getValue());
     * }
     * zos.closeEntry();
     * zos.close();
     * return baos.toByteArray();
     * }
     */

=======
>>>>>>> 30bc5c02f (added new logic)
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

    public String putS3Object(String bucketName, String objectKey, byte[] zipContent) {
        S3Client s3 = getClient();
        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal", "test");
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .metadata(metadata)
                    .build();

            s3.putObject(putOb, RequestBody.fromBytes(zipContent));

            // Now lets sign the ZIP
<<<<<<< HEAD
            return signBucket(bucketName, objectKey);
=======
            return signArchive(bucketName, objectKey);
>>>>>>> 30bc5c02f (added new logic)

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return "";
    }

<<<<<<< HEAD
    public static String signBucket(String bucketName, String keyName) {
=======
    public String signArchive(String bucketName, String keyName) {
>>>>>>> 30bc5c02f (added new logic)
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
<<<<<<< HEAD
                    .signatureDuration(Duration.ofMinutes(60))
=======
                    .signatureDuration(Duration.ofMinutes(1440))
>>>>>>> 30bc5c02f (added new logic)
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest = presignerOb.presignGetObject(getObjectPresignRequest);
            return presignedGetObjectRequest.url().toString();

        } catch (S3Exception e) {
            e.getStackTrace();
        }
        return "";
    }

    // Copy objects from the source bucket to storage bucket.
    public int copyFiles(String sourceBucket) {
        S3Client s3 = getClient();

        int count = 0;
        // Only move .jpg images
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(sourceBucket)
                .build();

        ListObjectsV2Response response = s3.listObjectsV2(request);
        for (S3Object s3Object : response.contents()) {

            // Check to make sure the object does not exist in the bucket. If the object
            // exists
            // it will not be copied again.
            if (checkS3ObjectDoesNotExist(s3Object.key())) {
                System.out.println("Object exists in the bucket.");

            } else if ((s3Object.key().endsWith(".jpg")) || (s3Object.key().endsWith(".jpeg"))) {
                System.out.println("JPG object found and will be copied: " + s3Object.key());
                copyS3Object(sourceBucket, s3Object.key());
                count++;
            } else {
                System.out.println("The object is not a JPG");
            }
        }

        return count;
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
        }
        return false;
    }

    public void copyS3Object(String sourceBucket, String objectKey) {
        S3Client s3 = getClient();

        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .sourceBucket(sourceBucket)
                .sourceKey(objectKey)
                .destinationBucket(PhotoApplicationResources.STORAGE_BUCKET)
                .destinationKey(objectKey)
                .build();

        try {
            s3.copyObject(copyReq);

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    // New method to sign an object prior to uploading it
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
            String myURL = presignedRequest.url().toString();
            return presignedRequest.url().toString();

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
<<<<<<< HEAD

=======
>>>>>>> 30bc5c02f (added new logic)
    }
}