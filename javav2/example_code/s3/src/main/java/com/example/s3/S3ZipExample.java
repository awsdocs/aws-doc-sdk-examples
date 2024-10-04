// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class S3ZipExample {
    public static void main(String[] args) {
        final String usage = """

            Usage:
              <bucketName> <imageKeys>\s

            Where:
              bucketName - The Amazon S3 bucket where JPG images are located.\s
              keys -  A comma separated list of images (without spaces) located in the S3 bucket and to be placed into a ZIP file. For example,  For example pic1.jpg,pic2.jpg""";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        // Replace with your S3 bucket name.
        String bucketName = args[0];
        String keys = args[1];
        String[] imageKeys = keys.split("[,]", 0);
        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder()
            .region(region)
            .build();

        createZIPFile(s3, bucketName, imageKeys);
    }

    /**
     * Creates a ZIP file containing the specified image keys from an S3 bucket and uploads it to S3.
     *
     * @param s3 the S3Client instance to use for interacting with S3
     * @param bucketName the name of the S3 bucket to use
     * @param imageKeys an array of image keys to include in the ZIP file
     */
    public static void createZIPFile(S3Client s3, String bucketName, String[] imageKeys) {
        String uuid = java.util.UUID.randomUUID().toString();
        String zipName = uuid + ".zip";
        // Create a ByteArrayOutputStream to write the ZIP file to.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

        try {
            for (String imageKey : imageKeys) {
                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(imageKey)
                    .build();
                ResponseBytes<GetObjectResponse> responseBytes = s3.getObjectAsBytes(getObjectRequest);

                // Create a ZipEntry for the object and add it to the ZipOutputStream.
                ZipEntry zipEntry = new ZipEntry(imageKey);
                zipOutputStream.putNextEntry(zipEntry);

                // Write the data to the ZipOutputStream.
                zipOutputStream.write(responseBytes.asByteArray(), 0, responseBytes.asByteArray().length);

                // Close the ZipEntry.
                zipOutputStream.closeEntry();
            }

            // Close the ZipOutputStream.
            zipOutputStream.close();

            // Upload the ZIP file to S3.
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(zipName)
                .build();
            s3.putObject(putObjectRequest, RequestBody.fromBytes(outputStream.toByteArray()));
            String preSignUrl = signObjectToDownload(bucketName, zipName);
            System.out.println("The Presigned URL is " + preSignUrl);

        } catch (S3Exception | IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generates a pre-signed URL for downloading an object from an Amazon S3 bucket.
     *
     * @param bucketName the name of the S3 bucket where the object is stored
     * @param keyName the key (object name) of the object to be downloaded
     * @return the pre-signed URL that can be used to download the object
     * @throws S3Exception if an error occurs while generating the pre-signed URL
     */
    public static String signObjectToDownload(String bucketName, String keyName) {
        S3Presigner presignerOb = S3Presigner.builder()
            .region(Region.US_EAST_1)
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
}
