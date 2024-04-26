// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.transfermanager;

// snippet-start:[s3.tm.java2.downloadfile.import]

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileDownload;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import software.amazon.awssdk.transfer.s3.model.FileDownload;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
// snippet-end:[s3.tm.java2.downloadfile.import]

/**
 * Before running this example:
 * <p/>
 * The SDK must be able to authenticate AWS requests on your behalf. If you have not configured
 * authentication for SDKs and tools,see https://docs.aws.amazon.com/sdkref/latest/guide/access.html in the AWS SDKs and Tools Reference Guide.
 * <p/>
 * You must have a runtime environment configured with the Java SDK.
 * See https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html in the Developer Guide if this is not set up.
 */

public class DownloadFile {
    private static final Logger logger = LoggerFactory.getLogger(UploadFile.class);
    public final String bucketName = "x-" + UUID.randomUUID();
    public final String key = UUID.randomUUID().toString();
    private final String downloadedFileName = "downloaded.pdf";
    public String downloadedFileWithPath;

    public DownloadFile() {
        setUp();
    }

    public static void main(String[] args) {
        DownloadFile download = new DownloadFile();
        download.downloadFile(S3ClientFactory.transferManager, download.bucketName, download.key,
                download.downloadedFileWithPath);
        download.cleanUp();
    }

    // snippet-start:[s3.tm.java2.downloadfile.main]
    public Long downloadFile(S3TransferManager transferManager, String bucketName,
                             String key, String downloadedFileWithPath) {
        DownloadFileRequest downloadFileRequest = DownloadFileRequest.builder()
                .getObjectRequest(b -> b.bucket(bucketName).key(key))
                .destination(Paths.get(downloadedFileWithPath))
                .build();

        FileDownload downloadFile = transferManager.downloadFile(downloadFileRequest);

        CompletedFileDownload downloadResult = downloadFile.completionFuture().join();
        logger.info("Content length [{}]", downloadResult.response().contentLength());
        return downloadResult.response().contentLength();
    }
    // snippet-end:[s3.tm.java2.downloadfile.main]

    // snippet-start:[s3.tm.java2.trackdownloadfile.main]
    public void trackDownloadFile(S3TransferManager transferManager, String bucketName,
                             String key, String downloadedFileWithPath) {
        DownloadFileRequest downloadFileRequest = DownloadFileRequest.builder()
                .getObjectRequest(b -> b.bucket(bucketName).key(key))
                .addTransferListener(LoggingTransferListener.create())  // Add listener.
                .destination(Paths.get(downloadedFileWithPath))
                .build();

        FileDownload downloadFile = transferManager.downloadFile(downloadFileRequest);

        CompletedFileDownload downloadResult = downloadFile.completionFuture().join();
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

            Log4J2 logs the progress. The following is example output for a 21.3 MB file download.
                Transfer initiated...
                |=======             | 39.4%
                |===============     | 78.8%
                |====================| 100.0%
                Transfer complete!
        */
    }
    // snippet-end:[s3.tm.java2.trackdownloadfile.main]


    private void setUp() {
        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        try {
            S3ClientFactory.s3Client.putObject(builder -> builder
                    .bucket(bucketName)
                    .key(key), RequestBody.fromFile(Paths.get(
                    DownloadFile.class.getClassLoader().getResource("multipartUploadFiles/s3-userguide.pdf").toURI())));
            URL resource = DownloadFile.class.getClassLoader().getResource(".");
            Path basePath = Paths.get(resource.toURI());
            Path fullPath = basePath.resolve(downloadedFileName);
            downloadedFileWithPath = fullPath.toString();
        } catch (URISyntaxException | NullPointerException e) {
            logger.error("Exception creating URI [{}]", e.getMessage());
            System.exit(1);
        }
    }

    public void cleanUp() {
        S3ClientFactory.s3Client.deleteObject(b -> b.bucket(bucketName).key(key));
        S3ClientFactory.s3Client.deleteBucket(b -> b.bucket(bucketName));
        URL url = DownloadFile.class.getClassLoader().getResource(downloadedFileName);
        if (url != null) {
            try {
                Files.delete(Paths.get(url.toURI().getPath()));
                System.out.println("File deleted successfully");
            } catch (URISyntaxException e) {
                System.err.println("Error converting URL to URI: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error deleting file " + e.getMessage());
            }
        } else {
            System.err.println("The file wasn't found");
        }
    }
}