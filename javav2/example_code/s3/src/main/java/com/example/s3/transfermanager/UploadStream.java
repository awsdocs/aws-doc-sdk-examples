package com.example.s3.transfermanager;

// snippet-start:[s3.tm.java2.upload_stream.complete]
// snippet-start:[s3.tm.java2.upload_stream.import]
import com.example.s3.util.AsyncExampleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedUpload;
import software.amazon.awssdk.transfer.s3.model.Upload;

import java.io.ByteArrayInputStream;
import java.util.UUID;
// snippet-end:[s3.tm.java2.upload_stream.import]

public class UploadStream {
    private static final Logger logger = LoggerFactory.getLogger(UploadStream.class);

    public static void main(String[] args) {
        String bucketName = "x-" + UUID.randomUUID();
        String key = UUID.randomUUID().toString();

        AsyncExampleUtils.createBucket(bucketName);
        try {
            UploadStream example = new UploadStream();
            CompletedUpload completedUpload = example.uploadStream(S3TransferManager.create(), bucketName, key);
            logger.info("Object {} etag: {}", key, completedUpload.response().eTag());
            logger.info("Object {} uploaded to bucket {}.", key, bucketName);
        } catch (SdkException e) {
            logger.error(e.getMessage(), e);
        } finally {
            AsyncExampleUtils.deleteObject(bucketName, key);
            AsyncExampleUtils.deleteBucket(bucketName);
        }
    }

// snippet-start:[s3.tm.java2.upload_stream.main]
    /**
     * @param transferManager - To upload content from a stream of unknown size, use the S3TransferManager based on the AWS CRT-based S3 client.
     *                       For more information, see https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/transfer-manager.html.
     * @param bucketName - The name of the bucket.
     * @param key - The name of the object.
     * @return - software.amazon.awssdk.transfer.s3.model.CompletedUpload - The result of the completed upload.
     */
    public CompletedUpload uploadStream(S3TransferManager transferManager, String bucketName, String key) {

        BlockingInputStreamAsyncRequestBody body =
                AsyncRequestBody.forBlockingInputStream(null); // 'null' indicates a stream will be provided later.

        Upload upload = transferManager.upload(builder -> builder
                .requestBody(body)
                .putObjectRequest(req -> req.bucket(bucketName).key(key))
                .build());

        // AsyncExampleUtils.randomString() returns a random string up to 100 characters.
        String randomString = AsyncExampleUtils.randomString();
        logger.info("random string to upload: {}: length={}", randomString, randomString.length());

        // Provide the stream of data to be uploaded.
        body.writeInputStream(new ByteArrayInputStream(randomString.getBytes()));

        return upload.completionFuture().join();
    }
}
// snippet-end:[s3.tm.java2.upload_stream.main]
// snippet-end:[s3.tm.java2.upload_stream.complete]
