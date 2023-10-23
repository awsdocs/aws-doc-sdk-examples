/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3;

// snippet-start:[presigned.java2.generatepresignedurlandputfilewithmetadata.import]

import com.example.s3.util.PresignUrlUtils;
import org.slf4j.Logger;
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
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(GeneratePresignedUrlAndUploadObject.class);
    private final static S3Client s3Client = S3Client.create();

    public static void main(String[] args) {
        String bucketName = "b-" + UUID.randomUUID();
        String keyName = "k-" + UUID.randomUUID();
        String resourcePath = "multipartUploadFiles/s3-userguide.pdf";
        String contentType = "application/pdf";
        // Uncomment the following two lines and comment out the previous two lines to use an image file instead of a PDF file.
        //String resourcePath = "image.png";
        //String contentType = "image/png";

        Map<String, String> metadata = Map.of(
                "author", "Mary Doe",
                "version", "1.0.0.0"
        );


        PresignUrlUtils.createBucket(bucketName, s3Client);
        GeneratePresignedUrlAndPutFileWithMetadata presign = new GeneratePresignedUrlAndPutFileWithMetadata();
        try {
            URL presignedUrl = presign.createPresignedUrl(bucketName, keyName, contentType, metadata);
            presign.useHttpUrlConnectionToPut(presignedUrl, getFileForForClasspathResource(resourcePath), contentType, metadata);
            presign.useHttpClientToPut(presignedUrl, getFileForForClasspathResource(resourcePath), contentType, metadata);
        } finally {
            PresignUrlUtils.deleteObject(bucketName, keyName, s3Client);
            PresignUrlUtils.deleteBucket(bucketName, s3Client);
        }
    }

    // snippet-start:[presigned.java2.generatepresignedurlandputfilewithmetadata.main]
    /**
     * Create a presigned URL for uploading with a PUT request.
     * @param bucketName  - The name of the bucket.
     * @param keyName     - The name of the object.
     * @param contentType - The content type of the object.
     * @param metadata    - The metadata to store with the object.
     * @return - The presigned URL for an HTTP PUT.
     */
    public URL createPresignedUrl(String bucketName, String keyName, String contentType, Map<String, String> metadata) {
        try (S3Presigner presigner = S3Presigner.create()) {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .contentType(contentType)
                    .metadata(metadata)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            String myURL = presignedRequest.url().toString();
            logger.info("Presigned URL to upload a file to: [{}]", myURL);
            logger.info("Which HTTP method needs to be used when uploading a file: [{}]", presignedRequest.httpRequest().method());

            return presignedRequest.url();
        }
    }

    /**
     * Use the JDK HttpURLConnection (since v1.1) class to do the upload, but you can
     * use any HTTP client.
     *
     * @param presignedUrl - The presigned URL.
     * @param fileToPut    - The file to upload.
     * @param contentType  - The content type of the file.
     * @param metadata    - The metadata to store with the object.
     */
    public void useHttpUrlConnectionToPut(URL presignedUrl, File fileToPut, String contentType, Map<String, String> metadata) {
        logger.info("Begin [{}] upload", contentType);
        try {
            HttpURLConnection connection = (HttpURLConnection) presignedUrl.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", contentType);
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

    /**
     * Use the JDK HttpClient (since v11) class to do the upload, but you can
     * use any HTTP client.
     *
     * @param presignedUrl - The presigned URL.
     * @param fileToPut    - The file to upload.
     * @param contentType  - The content type of the file.
     * @param metadata    - The metadata to store with the object.
     */
    public void useHttpClientToPut(URL presignedUrl, File fileToPut, String contentType, Map<String, String> metadata) {
        logger.info("Begin [{}] upload", contentType);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        metadata.forEach((k, v) -> requestBuilder.header("x-amz-meta-" + k, v));

        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            final HttpResponse<Void> response = httpClient.send(requestBuilder
                            .uri(presignedUrl.toURI())
                            .header("Content-Type", contentType)
                            .PUT(HttpRequest.BodyPublishers.ofFile(Path.of(fileToPut.toURI())))
                            .build(),
                    HttpResponse.BodyHandlers.discarding());

            logger.info("HTTP response code is " + response.statusCode());

        } catch (URISyntaxException | InterruptedException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
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
