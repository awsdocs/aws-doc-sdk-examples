// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GeneratePresignedUrlMetadata.java demonstrates how to use the S3Presigner client to create a presigned URL and upload an object that contains metadata to an Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/18/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3;

// snippet-start:[presigned.java2.generatepresignedurl.metadata.import]
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
// snippet-end:[presigned.java2.generatepresignedurl.metadata.import]

public class GeneratePresignedUrlMetadata {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GeneratePresignedUrlAndUploadObject <bucketName> <keyName> \n\n" +
                "Where:\n" +
                "    bucketName - the name of the Amazon S3 bucket. \n\n" +
                "    keyName - a key name that represents a text file. \n" ;

         if (args.length != 2) {
             System.out.println(USAGE);
             System.exit(1);
         }

        String bucketName = args[0];
        String keyName = args[1];

        Region region = Region.US_EAST_1;
        S3Presigner presigner = S3Presigner.builder()
                .region(region)
                .build();

        signBucket(presigner, bucketName, keyName);
        presigner.close();
    }

    // snippet-start:[presigned.java2.generatepresignedurl.metadata.main]
    public static void signBucket(S3Presigner presigner, String bucketName, String keyName) {

        try {
            // set metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("author", "Mary Doe");
            metadata.put("version", "1.0.0.0");

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .contentType("text/plain")
                    .metadata(metadata)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            System.out.println("Presigned URL to upload a file to: " +
                    presignedRequest.url());
            System.out.println("Which HTTP method needs to be used when uploading a file: " +
                    presignedRequest.httpRequest().method());

            // Upload content to the Amazon S3 bucket by using this URL
            URL url = presignedRequest.url();

            // Create the connection and use it to upload the new object
            // Notice we specify metadata as well
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","text/plain");
            connection.setRequestProperty("x-amz-meta-author","Mary Doe");
            connection.setRequestProperty("x-amz-meta-version","1.0.0.0");
            connection.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write("This text was uploaded as an object by using a presigned URL.");
            out.close();

            connection.getResponseCode();
            System.out.println("HTTP response code is " + connection.getResponseCode());

        } catch (S3Exception e) {
            e.getStackTrace();
        } catch (IOException e) {
            e.getStackTrace();
        }
        // snippet-end:[presigned.java2.generatepresignedurl.metadata.main]
    }
}
