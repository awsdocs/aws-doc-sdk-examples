//snippet-sourcedescription:[UploadADirectory.java demonstrates how to recursively copy a local directory to an Amazon Simple Storage Service (Amazon S3) bucket the Amazon S3 TransferManager.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3.transfermanager;

// snippet-start:[s3.tm.java2.uploadadirectory.import]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedDirectoryUpload;
import software.amazon.awssdk.transfer.s3.model.DirectoryUpload;
import software.amazon.awssdk.transfer.s3.model.UploadDirectoryRequest;

import java.net.URL;
import java.nio.file.Paths;
import java.util.UUID;
// snippet-end:[s3.tm.java2.uploadadirectory.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class UploadADirectory {
    private static final Logger logger = LoggerFactory.getLogger(UploadADirectory.class);
    public final String bucketName = "x-" + UUID.randomUUID();
    public String sourceDirectory;

    public UploadADirectory() {
        setUp();
    }

    public static void main(String[] args) {
        UploadADirectory upload = new UploadADirectory();

        Integer numFailedUploads = upload.uploadDirectory(S3ClientFactory.transferManager, upload.sourceDirectory, upload.bucketName);
        logger.info("Number of failed transfers [{}].", numFailedUploads);
        upload.cleanUp();
    }

    // snippet-start:[s3.tm.java2.uploadadirectory.main]
    public Integer uploadDirectory(S3TransferManager transferManager,
                                   String sourceDirectory, String bucketName){
        DirectoryUpload directoryUpload =
            transferManager.uploadDirectory(UploadDirectoryRequest.builder()
                .source(Paths.get(sourceDirectory))
                .bucket(bucketName)
                .build());

        CompletedDirectoryUpload completedDirectoryUpload = directoryUpload.completionFuture().join();
        completedDirectoryUpload.failedTransfers().forEach(fail ->
            logger.warn("Object [{}] failed to transfer", fail.toString()));
        return completedDirectoryUpload.failedTransfers().size();
    }
    // snippet-end:[s3.tm.java2.uploadadirectory.main]

    private void setUp(){
        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        URL dirResource = UploadADirectory.class.getClassLoader().getResource("uploadDirectory");
        sourceDirectory = dirResource.getPath();
    }

    public void cleanUp(){
        S3ClientFactory.s3Client.deleteObjects(b -> b
            .bucket(bucketName)
            .delete(b1 -> b1
                .objects(
                    ObjectIdentifier.builder().key("file1.txt").build(),
                    ObjectIdentifier.builder().key("file2.txt").build(),
                    ObjectIdentifier.builder().key("file3.txt").build()
                )));
        S3ClientFactory.s3Client.deleteBucket(b -> b.bucket(bucketName));
    }
}
