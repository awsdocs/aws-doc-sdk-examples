//snippet-sourcedescription:[DownloadFile.java demonstrates how to download a file using the Amazon Simple Storage Service (Amazon S3) TransferManager.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
// snippet-end:[s3.tm.java2.downloadfile.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DownloadFile {
    private static final Logger logger = LoggerFactory.getLogger(UploadFile.class);
    public final String bucketName = "x-" + UUID.randomUUID();
    public final String key = UUID.randomUUID().toString();
    public String downloadedFileWithPath;
    private final String downloadedFileName = "downloaded.png";

    public DownloadFile() {
        setUp();
    }

    public static void main(String[] args) {
        DownloadFile download = new DownloadFile();
        download.downloadFile(S3ClientFactory.transferManager, download.bucketName, download.key, download.downloadedFileWithPath);
        download.cleanUp();
    }

    // snippet-start:[s3.tm.java2.downloadfile.main]
    public Long downloadFile(S3TransferManager transferManager, String bucketName,
                             String key, String downloadedFileWithPath) {
        DownloadFileRequest downloadFileRequest =
            DownloadFileRequest.builder()
                .getObjectRequest(b -> b.bucket(bucketName).key(key))
                .addTransferListener(LoggingTransferListener.create())
                .destination(Paths.get(downloadedFileWithPath))
                .build();

        FileDownload downloadFile = transferManager.downloadFile(downloadFileRequest);

        CompletedFileDownload downloadResult = downloadFile.completionFuture().join();
        logger.info("Content length [{}]", downloadResult.response().contentLength());
        return downloadResult.response().contentLength();
    }
    // snippet-end:[s3.tm.java2.downloadfile.main]

    private void setUp(){
        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        S3ClientFactory.s3Client.putObject(builder -> builder
            .bucket(bucketName)
            .key(key), RequestBody.fromString("Hello World"));
        URL resource = DownloadFile.class.getClassLoader().getResource(".");
        downloadedFileWithPath = resource.getFile() + downloadedFileName;
    }

    public void cleanUp(){
        S3ClientFactory.s3Client.deleteObject(b -> b.bucket(bucketName).key(key));
        S3ClientFactory.s3Client.deleteBucket(b -> b.bucket(bucketName));
        URL url = DownloadFile.class.getClassLoader().getResource(downloadedFileName);
        try {
            Files.delete(Paths.get(url.getPath()));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
