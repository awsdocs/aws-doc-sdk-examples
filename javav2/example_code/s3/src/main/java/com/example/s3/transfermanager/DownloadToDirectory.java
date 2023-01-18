//snippet-sourcedescription:[DownloadToDirectory.java demonstrates how to copy all objects in an Amazon Simple Storage Service (Amazon S3) bucket to a local directory using the Amazon S3 TransferManager.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3.transfermanager;

// snippet-start:[s3.tm.java2.downloadtodirectory.import]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedDirectoryDownload;
import software.amazon.awssdk.transfer.s3.model.DirectoryDownload;
import software.amazon.awssdk.transfer.s3.model.DownloadDirectoryRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
// snippet-end:[s3.tm.java2.downloadtodirectory.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DownloadToDirectory {
    private static final Logger logger = LoggerFactory.getLogger(DownloadToDirectory.class);
    public final String bucketName = "x-" + UUID.randomUUID();
    public String destinationPath;
    private final Set<String> downloadedFileNameSet = new HashSet<>();
    private final String destinationDirName = "downloadDirectory";

    public DownloadToDirectory() {
        setUp();
    }

    public static void main(String[] args) {
        DownloadToDirectory download = new DownloadToDirectory();
        Integer numFilesFailedToDownload = download.downloadObjectsToDirectory(S3ClientFactory.transferManager, download.destinationPath, download.bucketName);
        logger.info("Number of files that failed to download [{}].", numFilesFailedToDownload);
        download.cleanUp();
    }

    // snippet-start:[s3.tm.java2.downloadtodirectory.main]
    public Integer downloadObjectsToDirectory(S3TransferManager transferManager,
                                              String destinationPath, String bucketName) {
        DirectoryDownload directoryDownload =
            transferManager.downloadDirectory(DownloadDirectoryRequest.builder()
                .destination(Paths.get(destinationPath))
                .bucket(bucketName)
                .build());
        CompletedDirectoryDownload completedDirectoryDownload = directoryDownload.completionFuture().join();

        completedDirectoryDownload.failedTransfers().forEach(fail ->
            logger.warn("Object [{}] failed to transfer", fail.toString()));
        return completedDirectoryDownload.failedTransfers().size();
    }
    // snippet-end:[s3.tm.java2.downloadtodirectory.main]

    private void setUp() {
        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));

        RequestBody requestBody = RequestBody.fromString("Hello World.");
        java.util.stream.IntStream.rangeClosed(1, 3).forEach(i -> {
                String fileName = "downloadedFile" + i + ".txt";
                downloadedFileNameSet.add(fileName);
                S3ClientFactory.s3Client.putObject(b -> b
                        .bucket(bucketName)
                        .key(fileName),
                    requestBody);
            }
        );
        destinationPath = DownloadToDirectory.class.getClassLoader().getResource(destinationDirName).getPath();
    }


    public void cleanUp() {
        // Delete items uploaded to bucket for download.
        Set<ObjectIdentifier> items = downloadedFileNameSet
            .stream()
            .map(name -> ObjectIdentifier.builder().key(name).build())
            .collect(Collectors.toSet());

        S3ClientFactory.s3Client.deleteObjects(b -> b
            .bucket(bucketName)
            .delete(b1 -> b1.objects(items)));
        // Delete bucket.
        S3ClientFactory.s3Client.deleteBucket(b -> b.bucket(bucketName));

        // Delete files downloaded.
        downloadedFileNameSet.stream().forEach(fileName -> {
            try {
                Files.delete(Paths.get(destinationPath + "/" + fileName));
            } catch (IOException e) {
                logger.error("Exception deleting file [{}]", fileName);
                throw new RuntimeException(e);
            }
        });
    }
}
