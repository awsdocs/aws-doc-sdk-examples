// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

// snippet-start:[presigned.java2.generatepresignedurlandputfilewithqueryparams.import]
import com.example.s3.util.PresignUrlUtils;
import org.slf4j.Logger;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.internal.sync.FileContentStreamProvider;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.HttpExecuteResponse;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
// snippet-end:[presigned.java2.generatepresignedurlandputfilewithqueryparams.import]

/**
 * Before running this Java V2 code example, set up your development environment.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class GeneratePresignedUrlAndPutFileWithQueryParams {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(GeneratePresignedUrlAndPutFileWithQueryParams.class);
    private final static S3Client s3Client = S3Client.create();

    public static void main(String[] args) {
        String bucketName = "amzn-s3-demo-bucket" + UUID.randomUUID(); // Change bucket name.
        String keyName = "key" + UUID.randomUUID();
        String resourcePath = "uploadDirectory/file1.txt";
        // Uncomment the following two lines and comment out the previous two lines to use an image file instead of a PDF file.
        //String resourcePath = "image.png";
        //String contentType = "image/png";

        Map<String, String> queryParams = Map.of(
                "x-amz-meta-author", "Bob",
                "x-amz-meta-version", "1.0.0.0",
                "x-amz-acl", "private",
                "x-amz-server-side-encryption", "AES256"
        );

        PresignUrlUtils.createBucket(bucketName, s3Client);
        GeneratePresignedUrlAndPutFileWithQueryParams presign = new GeneratePresignedUrlAndPutFileWithQueryParams();
        try {
            String presignedUrlString = presign.createPresignedUrl(bucketName, keyName, queryParams);
            presign.useSdkHttpClientToPut(presignedUrlString, getFileForForClasspathResource(resourcePath));

        } finally {
            PresignUrlUtils.deleteObject(bucketName, keyName, s3Client);
            PresignUrlUtils.deleteBucket(bucketName, s3Client);
        }
    }

    // snippet-start:[presigned.java2.generatepresignedurlandputfilewithqueryparams.main]
    // snippet-start:[presigned.java2.generatepresignedurlandputfilewithqueryparams.createpresignedurl]
    /**
     *  Creates a presigned URL to use in a subsequent HTTP PUT request. The code adds query parameters
     *  to the request instead of using headers. By using query parameters, you do not need to add the
     *  the parameters as headers when the PUT request is eventually sent.
     *
     * @param bucketName Bucket name where the object will be uploaded.
     * @param keyName Key name of the object that will be uploaded.
     * @param queryParams Query string parameters to be added to the presigned URL.
     * @return
     */
    public String createPresignedUrl(String bucketName, String keyName, Map<String, String> queryParams) {
        try (S3Presigner presigner = S3Presigner.create()) {
            // Create an override configuration to store the query parameters.
            AwsRequestOverrideConfiguration.Builder overrideConfigurationBuilder = AwsRequestOverrideConfiguration.builder();

            queryParams.forEach(overrideConfigurationBuilder::putRawQueryParameter);

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .overrideConfiguration(overrideConfigurationBuilder.build()) // Add the override configuration.
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
    // snippet-end:[presigned.java2.generatepresignedurlandputfilewithqueryparams.createpresignedurl]

    // snippet-start:[presigned.java2.generatepresignedurlandputfilewithqueryparams.sdkhttpclient]
    /**
     * Use the AWS SDK for Java V2 SdkHttpClient class to execute the PUT request. Since the
     * URL contains the query parameters, no headers are needed for metadata, SSE settings, or ACL settings.
     *
     * @param presignedUrlString The URL for the PUT request.
     * @param fileToPut File to uplaod
     */
    public void useSdkHttpClientToPut(String presignedUrlString, File fileToPut) {
        logger.info("Begin [{}] upload", fileToPut.toString());

        try {
            URL presignedUrl = new URL(presignedUrlString);

            SdkHttpRequest.Builder requestBuilder = SdkHttpRequest.builder()
                    .method(SdkHttpMethod.PUT)
                    .uri(presignedUrl.toURI());

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
    // snippet-end:[presigned.java2.generatepresignedurlandputfilewithqueryparams.sdkhttpclient]
    // snippet-end:[presigned.java2.generatepresignedurlandputfilewithqueryparams.main]

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
