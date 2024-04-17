// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.transfermanager;

// snippet-start:[s3.tm.java2.uploadfile.import]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.UUID;
// snippet-end:[s3.tm.java2.uploadfile.import]

/**
 * Before running this example:
 * <p/>
 * The SDK must be able to authenticate AWS requests on your behalf. If you have not configured
 * authentication for SDKs and tools,see https://docs.aws.amazon.com/sdkref/latest/guide/access.html in the AWS SDKs and Tools Reference Guide.
 * <p/>
 * You must have a runtime environment configured with the Java SDK.
 * See https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html in the Developer Guide if this is not set up.
 */

public class UploadFile {
    private static final Logger logger = LoggerFactory.getLogger(UploadFile.class);
    public final String bucketName = "x-" + UUID.randomUUID();
    public final String key = UUID.randomUUID().toString();
    public URI filePathURI;

    public UploadFile() {
        this.setUp();
    }

    public static void main(String[] args) {
        UploadFile upload = new UploadFile();
        upload.uploadFile(S3ClientFactory.transferManager, upload.bucketName, upload.key, upload.filePathURI);
        upload.trackUploadFile(S3ClientFactory.transferManager, upload.bucketName, upload.key, upload.filePathURI);
        upload.cleanUp();
    }

    // snippet-start:[s3.tm.java2.uploadfile.main]
    public String uploadFile(S3TransferManager transferManager, String bucketName,
                             String key, URI filePathURI) {
        UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
            .putObjectRequest(b -> b.bucket(bucketName).key(key))
            .source(Paths.get(filePathURI))
            .build();

        FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);

        CompletedFileUpload uploadResult = fileUpload.completionFuture().join();
        return uploadResult.response().eTag();
    }
    // snippet-end:[s3.tm.java2.uploadfile.main]

    // snippet-start:[s3.tm.java2.trackuploadfile.main]
    public void trackUploadFile(S3TransferManager transferManager, String bucketName,
                             String key, URI filePathURI) {
        UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                .putObjectRequest(b -> b.bucket(bucketName).key(key))
                .addTransferListener(LoggingTransferListener.create())  // Add listener.
                .source(Paths.get(filePathURI))
                .build();

        FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);

        fileUpload.completionFuture().join();
        /*
            The SDK provides a LoggingTransferListener implementation of the TransferListener interface.
            You can also implement the interface to provide your own logic.

            Configure log4J2 with settings such as the following.
                <Configuration status="WARN">
                    <Appenders>
                        <Console name="AlignedConsoleAppender" target="SYSTEM_OUT">
                            <PatternLayout pattern="%m%n"/>
                        </Console>
                    </Appenders>

                    <Loggers>
                        <logger name="software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener" level="INFO" additivity="false">
                            <AppenderRef ref="AlignedConsoleAppender"/>
                        </logger>
                    </Loggers>
                </Configuration>

            Log4J2 logs the progress. The following is example output for a 21.3 MB file upload.
                Transfer initiated...
                |                    | 0.0%
                |====                | 21.1%
                |============        | 60.5%
                |====================| 100.0%
                Transfer complete!
        */
    }
    // snippet-end:[s3.tm.java2.trackuploadfile.main]

    private void setUp() {
        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        // get the file system path to the provided file to upload
        URL resource = UploadFile.class.getClassLoader().getResource("multipartUploadFiles/s3-userguide.pdf");
        try {
            filePathURI = resource.toURI();
        } catch (URISyntaxException | NullPointerException e) {
            logger.error("Error getting file path URI: {}", e.getMessage());
            System.exit(1);
        }
    }

    public void cleanUp() {
        S3ClientFactory.s3Client.deleteObject(b -> b.bucket(bucketName).key(key));
        S3ClientFactory.s3Client.deleteBucket(b -> b.bucket(bucketName));
    }
}