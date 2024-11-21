// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.transfermanager;

// snippet-start:[s3.tm.java2.download-s3-directories.import]

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.config.DownloadFilter;
import software.amazon.awssdk.transfer.s3.model.CompletedDirectoryDownload;
import software.amazon.awssdk.transfer.s3.model.DirectoryDownload;
import software.amazon.awssdk.transfer.s3.model.DownloadDirectoryRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
// snippet-end:[s3.tm.java2.download-s3-directories.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class S3DirectoriesDownloader {
    private static final Logger logger = LoggerFactory.getLogger(S3DirectoriesDownloader.class);
    public final String bucketName = "junk-s3-demo-bucket" + UUID.randomUUID(); // Change bucket name.
    public URI destinationPathURI;
    private final Set<String> downloadedFileNameSet = new HashSet<>();
    private final String destinationDirName = "downloadDirectory";
    private final List<String> folderNames = List.of("folder1", "folder2", "folder3");
    private final List<String> filterFolderNames = List.of("folder1",  "folder3");

    public S3DirectoriesDownloader() {
        setUp();
    }

    public static void main(String[] args) {
        S3DirectoriesDownloader downloader = new S3DirectoriesDownloader();
        Integer numFilesFailedToDownload = null;
        try {
            numFilesFailedToDownload = downloader.downloadS3Directories(S3ClientFactory.transferManager,
                    downloader.destinationPathURI, downloader.bucketName);
            logger.info("Number of files that failed to download [{}].", numFilesFailedToDownload);
        } catch (Exception e) {
            logger.error("Exception [{}]", e.getMessage(), e);
        } finally {
            downloader.cleanUp();
        }
    }

    // snippet-start:[s3.tm.java2.download-s3-directories.main]
    /**
     * For standard buckets, S3 provides the illusion of a directory structure through the use of keys. When you upload
     * an object to an S3 bucket, you specify a key, which is essentially the "path" to the object. The key can contain
     * forward slashes ("/") to make it appear as if the object is stored in a directory structure, but this is just a
     * logical representation, not an actual directory.
     * <p><pre>
     * In this example, our S3 bucket contains the following objects:
     *
     * folder1/file1.txt
     * folder1/file2.txt
     * folder1/file3.txt
     * folder2/file1.txt
     * folder2/file2.txt
     * folder2/file3.txt
     * folder3/file1.txt
     * folder3/file2.txt
     * folder3/file3.txt
     *
     * When method `downloadS3Directories` is invoked with
     * `destinationPathURI` set to `/test`, the downloaded
     * directory looks like:
     *
     * |- test
     *    |- folder1
     *    	  |- file1.txt
     *    	  |- file2.txt
     *    	  |- file3.txt
     *    |- folder3
     *    	  |- file1.txt
     *    	  |- file2.txt
     *    	  |- file3.txt
     * </pre>
     *
     * @param transferManager    An S3TransferManager instance.
     * @param destinationPathURI local directory to hold the downloaded S3 'directories' and files.
     * @param bucketName         The S3 bucket that contains the 'directories' to download.
     * @return The number of objects (files, in this case) that were downloaded.
     */
    public Integer downloadS3Directories(S3TransferManager transferManager,
                                         URI destinationPathURI, String bucketName) {

        // Define the filters for which 'directories' we want to download.
        DownloadFilter folder1Filter = (S3Object s3Object) -> s3Object.key().startsWith("folder1/");
        DownloadFilter folder3Filter = (S3Object s3Object) -> s3Object.key().startsWith("folder3/");
        DownloadFilter folderFilter = s3Object -> folder1Filter.or(folder3Filter).test(s3Object);

        DirectoryDownload directoryDownload = transferManager.downloadDirectory(DownloadDirectoryRequest.builder()
                .destination(Paths.get(destinationPathURI))
                .bucket(bucketName)
                .filter(folderFilter)
                .build());
        CompletedDirectoryDownload completedDirectoryDownload = directoryDownload.completionFuture().join();

        Integer numFilesInFolder1 = Paths.get(destinationPathURI).resolve("folder1").toFile().list().length;
        Integer numFilesInFolder3 = Paths.get(destinationPathURI).resolve("folder3").toFile().list().length;

        try {
            assert numFilesInFolder1 == 3;
            assert numFilesInFolder3 == 3;
            assert !Paths.get(destinationPathURI).resolve("folder2").toFile().exists(); // `folder2` was not downloaded.
        } catch (AssertionError e) {
            logger.error("An assertion failed.");
        }

        completedDirectoryDownload.failedTransfers()
                .forEach(fail -> logger.warn("Object failed to transfer  [{}]", fail.exception().getMessage()));
        return numFilesInFolder1 + numFilesInFolder3;
    }
    // snippet-end:[s3.tm.java2.download-s3-directories.main]

    private void setUp() {
        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        S3ClientFactory.s3Waiter.waitUntilBucketExists(r -> r.bucket(bucketName));

        RequestBody requestBody = RequestBody.fromString("Hello World.");

        folderNames.forEach(folderName ->
                IntStream.rangeClosed(1, 3).forEach(i -> {
                    String fileName = folderName + "/" + "file" + i + ".txt";
                    downloadedFileNameSet.add(fileName);
                    S3ClientFactory.s3Client.putObject(b -> b
                                    .bucket(bucketName)
                                    .key(fileName),
                            requestBody);
                }));
        try {
            destinationPathURI = S3DirectoriesDownloader.class.getClassLoader().getResource(destinationDirName).toURI();
        } catch (URISyntaxException | NullPointerException e) {
            logger.error("Exception creating URI [{}]", e.getMessage());
            System.exit(1);
        }
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
        Predicate<String> filterFolder1 = key -> key.startsWith("folder1");
        Predicate<String> filterFolder3 = key -> key.startsWith("folder3");
        Predicate<String> filterForFolders = filterFolder1.or(filterFolder3);

        downloadedFileNameSet.stream()
                .filter(filterForFolders)
                .forEach(fileName -> {
                    try {
                        Path basePath = Paths.get(destinationPathURI);
                        Path fullPath = basePath.resolve(fileName);
                        Files.delete(fullPath);
                    } catch (IOException e) {
                        logger.error("Exception deleting file [{}]", fileName);
                    }
                });
        filterFolderNames.forEach(folderName -> {
            try {
                Path basePath = Paths.get(destinationPathURI);
                Path fullPath = basePath.resolve(folderName);
                Files.delete(fullPath);
            } catch (IOException e) {
                logger.error("Exception deleting folder [{}]", folderName);
            }
        });
    }
}
