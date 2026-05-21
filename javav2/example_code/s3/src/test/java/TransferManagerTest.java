// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.s3.transfermanager.DownloadFile;
import com.example.s3.transfermanager.DownloadToDirectory;
import com.example.s3.transfermanager.ObjectCopy;
import com.example.s3.transfermanager.S3ClientFactory;
import com.example.s3.transfermanager.S3DirectoriesDownloader;
import com.example.s3.transfermanager.UploadADirectory;
import com.example.s3.transfermanager.UploadFile;
import com.example.s3.transfermanager.UploadStream;
import com.example.s3.util.AsyncExampleUtils;
import com.example.s3.util.MemoryLog4jAppender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedUpload;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(S3TestWatcher.class)
class TransferManagerTest {

    private static final Logger logger = LoggerFactory.getLogger(TransferManagerTest.class);
    private static final String LOGGED_STRING_TO_CHECK = "Transfer initiated...";

    @BeforeAll
    public static void beforeAll() {
        logger.info("S3TransferManager tests starting ...");
    }

    @AfterAll
    public static void afterAll() {
        logger.info("... S3TransferManager tests finished");
    }

    @Test
    @Tag("IntegrationTest")
    public void uploadSingleFileWorks() {
        String bucketName = "x-" + UUID.randomUUID();
        String key = UUID.randomUUID().toString();
        URI filePathURI = UploadFile.getFilePathURI();

        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        try {
            UploadFile upload = new UploadFile();
            String etag = upload.uploadFile(S3ClientFactory.transferManager, bucketName, key, filePathURI);
            Assertions.assertNotNull(etag);
        } finally {
            S3ClientFactory.s3Client.deleteObject(b -> b.bucket(bucketName).key(key));
            S3ClientFactory.s3Client.deleteBucket(b -> b.bucket(bucketName));
        }
    }

    @Test
    @Tag("IntegrationTest")
    public void trackUploadFileWorks() {
        String bucketName = "x-" + UUID.randomUUID();
        String key = UUID.randomUUID().toString();
        URI filePathURI = UploadFile.getFilePathURI();

        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        try {
            UploadFile upload = new UploadFile();
            upload.trackUploadFile(S3ClientFactory.transferManager, bucketName, key, filePathURI);
        } catch (SdkException | AssertionFailedError e) {
            logger.error(e.getMessage());
        } finally {
            S3ClientFactory.s3Client.deleteObject(b -> b.bucket(bucketName).key(key));
            S3ClientFactory.s3Client.deleteBucket(b -> b.bucket(bucketName));
        }
        Assertions.assertTrue(getLoggedMessages().contains(LOGGED_STRING_TO_CHECK));
    }

    @Test
    @Tag("IntegrationTest")
    public void downloadSingleFileWorks() {
        String bucketName = "x-" + UUID.randomUUID();
        String key = UUID.randomUUID().toString();
        String downloadedFileWithPath = DownloadFile.getDownloadFilePath("downloaded.pdf");

        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        try {
            S3ClientFactory.s3Client.putObject(builder -> builder
                    .bucket(bucketName).key(key),
                    RequestBody.fromFile(Paths.get(UploadFile.getFilePathURI())));

            DownloadFile download = new DownloadFile();
            Long fileLength = download.downloadFile(S3ClientFactory.transferManager, bucketName, key, downloadedFileWithPath);
            Assertions.assertNotEquals(0L, fileLength);
        } finally {
            DownloadFile.cleanUp(bucketName, key, downloadedFileWithPath);
        }
    }

    @Test
    @Tag("IntegrationTest")
    public void trackDownloadFileWorks() {
        String bucketName = "x-" + UUID.randomUUID();
        String key = UUID.randomUUID().toString();
        String downloadedFileWithPath = DownloadFile.getDownloadFilePath("downloaded.pdf");

        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        try {
            S3ClientFactory.s3Client.putObject(builder -> builder
                    .bucket(bucketName).key(key),
                    RequestBody.fromFile(Paths.get(UploadFile.getFilePathURI())));

            DownloadFile download = new DownloadFile();
            download.trackDownloadFile(S3ClientFactory.transferManager, bucketName, key, downloadedFileWithPath);
        } catch (SdkException e) {
            logger.error(e.getMessage());
        } finally {
            DownloadFile.cleanUp(bucketName, key, downloadedFileWithPath);
        }
        Assertions.assertTrue(getLoggedMessages().contains(LOGGED_STRING_TO_CHECK));
    }

    @Test
    @Tag("IntegrationTest")
    public void copyObjectWorks() {
        String bucketName = "x-" + UUID.randomUUID();
        String key = UUID.randomUUID().toString();
        String destinationBucket = "x-" + UUID.randomUUID();
        String destinationKey = UUID.randomUUID().toString();

        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        S3ClientFactory.s3Client.putObject(builder -> builder
                .bucket(bucketName).key(key), RequestBody.fromString("Hello World"));
        S3ClientFactory.s3Client.createBucket(b -> b.bucket(destinationBucket));

        try {
            ObjectCopy copy = new ObjectCopy();
            String etag = copy.copyObject(S3ClientFactory.transferManager, bucketName, key, destinationBucket, destinationKey);
            Assertions.assertNotNull(etag);
        } finally {
            ObjectCopy.cleanUp(bucketName, key, destinationBucket, destinationKey);
        }
    }

    @Test
    @Tag("IntegrationTest")
    public void directoryUploadWorks() {
        String bucketName = "x-" + UUID.randomUUID();
        URI sourceDirectory = UploadADirectory.getSourceDirectoryURI();

        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        try {
            UploadADirectory upload = new UploadADirectory();
            Integer numFailedUploads = upload.uploadDirectory(S3ClientFactory.transferManager, sourceDirectory, bucketName);
            Assertions.assertNotNull(numFailedUploads, "Bucket download failed to complete.");
        } finally {
            UploadADirectory.cleanUp(bucketName);
        }
    }

    @Test
    @Tag("IntegrationTest")
    public void directoryDownloadWorks() {
        String bucketName = "x-" + UUID.randomUUID();
        URI destinationPathURI = DownloadToDirectory.getDestinationURI();
        Set<String> uploadedFiles = new HashSet<>();

        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        RequestBody requestBody = RequestBody.fromString("Hello World.");
        java.util.stream.IntStream.rangeClosed(1, 3).forEach(i -> {
            String fileName = "downloadedFile" + i + ".txt";
            uploadedFiles.add(fileName);
            S3ClientFactory.s3Client.putObject(b -> b.bucket(bucketName).key(fileName), requestBody);
        });

        try {
            DownloadToDirectory download = new DownloadToDirectory();
            Integer numFilesFailedToDownload = download.downloadObjectsToDirectory(
                    S3ClientFactory.transferManager, destinationPathURI, bucketName);
            Assertions.assertNotNull(numFilesFailedToDownload, "Bucket download failed to complete.");
        } finally {
            DownloadToDirectory.cleanUp(bucketName, uploadedFiles, destinationPathURI);
        }
    }

    @Test
    @Tag("IntegrationTest")
    public void uploadStreamWorks() {
        String bucketName = "x-" + UUID.randomUUID();
        String key = UUID.randomUUID().toString();
        AsyncExampleUtils.createBucket(bucketName);
        try {
            UploadStream example = new UploadStream();
            CompletedUpload completedUpload = example.uploadStream(S3TransferManager.create(), bucketName, key);
            logger.info("Object {} etag: {}", key, completedUpload.response().eTag());
            logger.info("Object {} uploaded to bucket {}.", key, bucketName);
            Assertions.assertTrue(completedUpload.response().sdkHttpResponse().isSuccessful());
        } catch (SdkException e) {
            logger.error(e.getMessage(), e);
        } finally {
            AsyncExampleUtils.deleteObject(bucketName, key);
            AsyncExampleUtils.deleteBucket(bucketName);
        }
    }

    @Test
    @Tag("IntegrationTest")
    public void s3DirectoriesDownloadWorks() {
        String bucketName = "x-" + UUID.randomUUID();
        URI destinationPathURI = S3DirectoriesDownloader.getDestinationURI();
        Set<String> uploadedFiles = new HashSet<>();

        S3DirectoriesDownloader.setUp(bucketName, uploadedFiles);
        try {
            S3DirectoriesDownloader downloader = new S3DirectoriesDownloader();
            Integer numFilesDownloaded = downloader.downloadS3Directories(
                    S3ClientFactory.transferManager, destinationPathURI, bucketName);
            Assertions.assertEquals(6, numFilesDownloaded);
        } finally {
            S3DirectoriesDownloader.cleanUp(bucketName, uploadedFiles, destinationPathURI);
        }
    }

    private String getLoggedMessages() {
        final LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        final Configuration configuration = context.getConfiguration();
        final MemoryLog4jAppender memoryLog4jAppender = configuration.getAppender("MemoryLog4jAppender");
        return memoryLog4jAppender.getEventsAsString();
    }
}
