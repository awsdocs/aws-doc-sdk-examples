//snippet-sourcedescription:[CheckingObjectIntegrity.java demonstrates how to check data integrity in Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/15/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3;

// snippet-start:[s3.java2.s3_object_check_integrity.import]
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.ChecksumMode;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectAttributes;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
// snippet-end:[s3.java2.s3_object_check_integrity.import]

/**
 * To run this AWS code example, ensure that you have setup your development environment, including your AWS credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CheckingObjectIntegrity {
    private final static int CHUNK_SIZE = 5 * 1024 * 1024;

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "  <bucketName> <objectKey> <objectPath> \n\n" +
                "Where:\n" +
                "  bucketName - the Amazon S3 bucket to upload an object into.\n" +
                "  objectKey - the object to upload (for example, book.pdf).\n" +
                "  objectPath - the path where the file is located (for example, C:/AWS/book2.pdf). \n\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucketName = args[0];
        String objectKey = args[1];
        String objectPath = args[2];

        System.out.println("Putting object " + objectKey + " into bucket " + bucketName + " with checksum in algorithm sha256.");
        System.out.println("  in bucket: " + bucketName);

        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        putS3MultipartObjectBracketedByChecksum(s3, bucketName, objectKey, objectPath);
        downloadS3MultipartObjectBracketedByChecksum(s3, bucketName, objectKey);
        validateExistingFileAgainstS3Checksum(s3, bucketName, objectKey, objectPath);
    }

    // snippet-start:[s3.java2.s3_object_check_integrity.main]
    public static void putS3MultipartObjectBracketedByChecksum(S3Client s3, String bucketName, String objectKey, String objectPath) {
        System.out.println("Starting uploading file with additional checksum.");
        File file = new File(objectPath);
        try (InputStream in = new FileInputStream(file)) {

            CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .checksumAlgorithm(ChecksumAlgorithm.SHA256)
                    .build();
            CreateMultipartUploadResponse createdUpload = s3.createMultipartUpload(createMultipartUploadRequest);
            List<CompletedPart> completedParts = new ArrayList<CompletedPart>();
            int partNumber = 1;
            byte[] buffer = new byte[CHUNK_SIZE];
            int read = in.read(buffer);
            while (read != -1) {
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .partNumber(partNumber)
                        .uploadId(createdUpload.uploadId())
                        .bucket(bucketName)
                        .key(objectKey)
                        .checksumAlgorithm(ChecksumAlgorithm.SHA256).build();
                UploadPartResponse uploadedPart = s3.uploadPart(uploadPartRequest, RequestBody.fromByteBuffer(ByteBuffer.wrap(buffer, 0, read)));
                CompletedPart part = CompletedPart.builder().
                        partNumber(partNumber)
                        .checksumSHA256(uploadedPart.checksumSHA256())
                        .eTag(uploadedPart.eTag()).build();
                completedParts.add(part);
                read = in.read(buffer);
                partNumber++;
            }

            CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder().parts(completedParts).build();
            CompleteMultipartUploadResponse completedUploadResponse = s3.completeMultipartUpload(
                    CompleteMultipartUploadRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .uploadId(createdUpload.uploadId())
                            .multipartUpload(completedMultipartUpload).build());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadS3MultipartObjectBracketedByChecksum(S3Client s3, String bucketName, String objectKey) {
        System.out.println("Starting downloading file and doing validation");
        File file = new File("DOWNLOADED_" + objectKey);
        try (OutputStream out = new FileOutputStream(file)) {
            GetObjectAttributesResponse
                    objectAttributes = s3.getObjectAttributes(GetObjectAttributesRequest.builder().bucket(bucketName).key(objectKey)
                    .objectAttributes(ObjectAttributes.OBJECT_PARTS, ObjectAttributes.CHECKSUM).build());
            //Initialize the digester to calculate checksum of checksums.
            MessageDigest sha256ChecksumOfChecksums = MessageDigest.getInstance("SHA-256");

            //If you retrieve the object in parts, and set the ChecksumMode to enabled, the SDK will automatically validate the part checksum
            for (int partNumber = 1; partNumber <= objectAttributes.objectParts().totalPartsCount(); partNumber++) {
                MessageDigest sha256PartChecksum = MessageDigest.getInstance("SHA-256");
                ResponseInputStream<GetObjectResponse> response = s3.getObject(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .partNumber(partNumber)
                        .checksumMode(ChecksumMode.ENABLED).build());
                GetObjectResponse getObjectResponse = response.response();
                byte[] buffer = new byte[CHUNK_SIZE];
                int read = response.read(buffer);
                while (read != -1) {
                    out.write(buffer, 0, read);
                    sha256PartChecksum.update(buffer, 0, read);
                    read = response.read(buffer);
                }
                byte[] sha256PartBytes = sha256PartChecksum.digest();
                sha256ChecksumOfChecksums.update(sha256PartBytes);

                String base64PartChecksum = Base64.getEncoder().encodeToString(sha256PartBytes);
                String base64PartChecksumFromObjectAttributes = objectAttributes.objectParts().parts().get(partNumber - 1).checksumSHA256();
                if (!base64PartChecksum.equals(getObjectResponse.checksumSHA256()) || !base64PartChecksum.equals(base64PartChecksumFromObjectAttributes)) {
                    throw new IOException("Part checksum didn't match for the part");
                }
                System.out.println(partNumber + " " + base64PartChecksum);
            }

            //Before finalizing, do the final checksum validation.
            String base64ChecksumOfChecksums = Base64.getEncoder().encodeToString(sha256ChecksumOfChecksums.digest());
            String base64ChecksumOfChecksumFromAttributes = objectAttributes.checksum().checksumSHA256();
            if (base64ChecksumOfChecksumFromAttributes != null && !base64ChecksumOfChecksums.equals(base64ChecksumOfChecksumFromAttributes)) {
                throw new IOException("Failed checksum validation for full object checksum of checksums");
            }
            System.out.println("Checksum of checksums: " + base64ChecksumOfChecksumFromAttributes);
            out.flush();
        } catch (IOException | NoSuchAlgorithmException e) {
            //Cleanup bad file
            file.delete();
            e.printStackTrace();
        }
    }

    public static void validateExistingFileAgainstS3Checksum(S3Client s3, String bucketName, String objectKey, String objectPath) {
        System.out.println("Starting validating the locally persisted file.");
        File file = new File(objectPath);
        GetObjectAttributesResponse
                objectAttributes = s3.getObjectAttributes(GetObjectAttributesRequest.builder().bucket(bucketName).key(objectKey)
                .objectAttributes(ObjectAttributes.OBJECT_PARTS, ObjectAttributes.CHECKSUM).build());
        try (InputStream in = new FileInputStream(file)) {
            MessageDigest sha256ChecksumOfChecksums = MessageDigest.getInstance("SHA-256");
            MessageDigest sha256Part = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[CHUNK_SIZE];
            int currentPart = 0;
            int partBreak = objectAttributes.objectParts().parts().get(currentPart).size();
            int totalRead = 0;
            int read = in.read(buffer);
            while (read != -1) {
                totalRead += read;
                if (totalRead >= partBreak) {
                    int difference = totalRead - partBreak;
                    byte[] partChecksum;
                    if (totalRead != partBreak) {
                        sha256Part.update(buffer, 0, read - difference);
                        partChecksum = sha256Part.digest();
                        sha256ChecksumOfChecksums.update(partChecksum);
                        sha256Part.reset();
                        sha256Part.update(buffer, read - difference, difference);
                    } else {
                        sha256Part.update(buffer, 0, read);
                        partChecksum = sha256Part.digest();
                        sha256ChecksumOfChecksums.update(partChecksum);
                        sha256Part.reset();
                    }
                    String base64PartChecksum = Base64.getEncoder().encodeToString(partChecksum);
                    if (!base64PartChecksum.equals(objectAttributes.objectParts().parts().get(currentPart).checksumSHA256())) {
                        throw new IOException("Part checksum didn't match what persisted in S3.");
                    }
                    currentPart++;
                    System.out.println(currentPart + " " + base64PartChecksum);
                    if (currentPart < objectAttributes.objectParts().totalPartsCount()) {
                        partBreak += objectAttributes.objectParts().parts().get(currentPart - 1).size();
                    }
                } else {
                    sha256Part.update(buffer, 0, read);
                }
                read = in.read(buffer);
            }
            if (currentPart != objectAttributes.objectParts().totalPartsCount()) {
                currentPart++;
                byte[] partChecksum = sha256Part.digest();
                sha256ChecksumOfChecksums.update(partChecksum);
                String base64PartChecksum = Base64.getEncoder().encodeToString(partChecksum);
                System.out.println(currentPart + " " + base64PartChecksum);
            }

            String base64CalculatedChecksumOfChecksums = Base64.getEncoder().encodeToString(sha256ChecksumOfChecksums.digest());
            System.out.println("Calculated checksum of checksums: " + base64CalculatedChecksumOfChecksums);
            System.out.println("S3 persisted checksum of checksums: " + objectAttributes.checksum().checksumSHA256());
            if (!base64CalculatedChecksumOfChecksums.equals(objectAttributes.checksum().checksumSHA256())) {
                throw new IOException("Full object checksum of checksums don't match S3");
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    // snippet-end:[s3.java2.s3_object_check_integrity.main]
}
