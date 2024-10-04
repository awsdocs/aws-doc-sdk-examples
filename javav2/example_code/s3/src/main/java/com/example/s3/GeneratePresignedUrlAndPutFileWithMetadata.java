// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

// snippet-start:[presigned.java2.generatepresignedurlandputfilewithmetadata.import]
import com.example.s3.util.PresignUrlUtils;
import org.slf4j.Logger;
import software.amazon.awssdk.core.internal.sync.FileContentStreamProvider;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.HttpExecuteResponse;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
// snippet-end:[presigned.java2.generatepresignedurlandputfilewithmetadata.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class GeneratePresignedUrlAndPutFileWithMetadata {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(GeneratePresignedUrlAndPutFileWithMetadata.class);
    private final static S3Client s3Client = S3Client.create();

    public static void main(String[] args) {
        String bucketName = "amzn-s3-demo-bucket" + UUID.randomUUID(); // Change bucket name.
        String keyName = "key" + UUID.randomUUID();
        String resourcePath = "multipartUploadFiles/s3-userguide.pdf";
        // Uncomment the following two lines and comment out the previous two lines to use an image file instead of a PDF file.
        //String resourcePath = "image.png";
        //String contentType = "image/png";

        Map<String, String> metadata = Map.of(
                "author", "Bob",
                "version", "1.0.0.0"
        );

        PresignUrlUtils.createBucket(bucketName, s3Client);
        GeneratePresignedUrlAndPutFileWithMetadata presign = new GeneratePresignedUrlAndPutFileWithMetadata();
        try {
            String presignedUrlString = presign.createPresignedUrl(bucketName, keyName, metadata);

            presign.useHttpUrlConnectionToPut(presignedUrlString, getFileForForClasspathResource(resourcePath), metadata);
            PresignUrlUtils.deleteObject(bucketName, keyName, s3Client);

            presign.useHttpClientToPut(presignedUrlString, getFileForForClasspathResource(resourcePath), metadata);
            PresignUrlUtils.deleteObject(bucketName, keyName, s3Client);

            presign.useSdkHttpClientToPut(presignedUrlString, getFileForForClasspathResource(resourcePath), metadata);


        } finally {
            PresignUrlUtils.deleteObject(bucketName, keyName, s3Client);
            PresignUrlUtils.deleteBucket(bucketName, s3Client);
        }
    }

    // snippet-start:[presigned.java2.generatepresignedurlandputfilewithmetadata.main]
    // snippet-start:[presigned.java2.generatepresignedurlandputfilewithmetadata.createpresignedurl]
    /* Create a presigned URL to use in a subsequent PUT request */
    public String createPresignedUrl(String bucketName, String keyName, Map<String, String> metadata) {
        try (S3Presigner presigner = S3Presigner.create()) {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .metadata(metadata)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))  // The URL expires in 10 minutes.
                    .putObjectRequest(objectRequest)
                    .build();


            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            String myURL = presignedRequest.url().toString();
            logger.info("Presigned URL to upload a file to: [{}]", myURL);
            logger.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

            return presignedRequest.url().toExternalForm();
        }
    }
    // snippet-end:[presigned.java2.generatepresignedurlandputfilewithmetadata.createpresignedurl]

    // snippet-start:[presigned.java2.generatepresignedurlandputfilewithmetadata.basichttpclient]
    /* Use the JDK HttpURLConnection (since v1.1) class to do the upload. */
    public void useHttpUrlConnectionToPut(String presignedUrlString, File fileToPut, Map<String, String> metadata) {
        logger.info("Begin [{}] upload", fileToPut.toString());
        try {
            URL presignedUrl = new URL(presignedUrlString);
            HttpURLConnection connection = (HttpURLConnection) presignedUrl.openConnection();
            connection.setDoOutput(true);
            metadata.forEach((k, v) -> connection.setRequestProperty("x-amz-meta-" + k, v));
            connection.setRequestMethod("PUT");
            OutputStream out = connection.getOutputStream();

            try (RandomAccessFile file = new RandomAccessFile(fileToPut, "r");
                 FileChannel inChannel = file.getChannel()) {
                ByteBuffer buffer = ByteBuffer.allocate(8192); //Buffer size is 8k

                while (inChannel.read(buffer) > 0) {
                    buffer.flip();
                    for (int i = 0; i < buffer.limit(); i++) {
                        out.write(buffer.get());
                    }
                    buffer.clear();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

            out.close();
            connection.getResponseCode();
            logger.info("HTTP response code is " + connection.getResponseCode());

        } catch (S3Exception | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    // snippet-end:[presigned.java2.generatepresignedurlandputfilewithmetadata.basichttpclient]

    // snippet-start:[presigned.java2.generatepresignedurlandputfilewithmetadata.jdkhttpclient]
    /* Use the JDK HttpClient (since v11) class to do the upload. */
    public void useHttpClientToPut(String presignedUrlString, File fileToPut, Map<String, String> metadata) {
        logger.info("Begin [{}] upload", fileToPut.toString());

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        metadata.forEach((k, v) -> requestBuilder.header("x-amz-meta-" + k, v));

        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            final HttpResponse<Void> response = httpClient.send(requestBuilder
                            .uri(new URL(presignedUrlString).toURI())
                            .PUT(HttpRequest.BodyPublishers.ofFile(Path.of(fileToPut.toURI())))
                            .build(),
                    HttpResponse.BodyHandlers.discarding());

            logger.info("HTTP response code is " + response.statusCode());

        } catch (URISyntaxException | InterruptedException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    // snippet-end:[presigned.java2.generatepresignedurlandputfilewithmetadata.jdkhttpclient]

    // snippet-start:[presigned.java2.generatepresignedurlandputfilewithmetadata.sdkhttpclient]
    /* Use the AWS SDK for Java V2 SdkHttpClient class to do the upload. */
    public void useSdkHttpClientToPut(String presignedUrlString, File fileToPut, Map<String, String> metadata) {
        logger.info("Begin [{}] upload", fileToPut.toString());

        try {
            URL presignedUrl = new URL(presignedUrlString);

            SdkHttpRequest.Builder requestBuilder = SdkHttpRequest.builder()
                    .method(SdkHttpMethod.PUT)
                    .uri(presignedUrl.toURI());
            // Add headers
            metadata.forEach((k, v) -> requestBuilder.putHeader("x-amz-meta-" + k, v));
            // Finish building the request.
            SdkHttpRequest request = requestBuilder.build();

            HttpExecuteRequest executeRequest = HttpExecuteRequest.builder()
                    .request(request)
                    .contentStreamProvider(new FileContentStreamProvider(fileToPut.toPath()))
                    .build();

            try (SdkHttpClient sdkHttpClient = ApacheHttpClient.create()) {
                HttpExecuteResponse response = sdkHttpClient.prepareRequest(executeRequest).call();
                logger.info("Response code: {}", response.httpResponse().statusCode());
            }
        } catch (URISyntaxException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    // snippet-end:[presigned.java2.generatepresignedurlandputfilewithmetadata.sdkhttpclient]
    // snippet-end:[presigned.java2.generatepresignedurlandputfilewithmetadata.main]

    public static File getFileForForClasspathResource(String resourcePath) {
        try {
            URL resource = GeneratePresignedUrlAndUploadObject.class.getClassLoader().getResource(resourcePath);
            return Paths.get(resource.toURI()).toFile();
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
