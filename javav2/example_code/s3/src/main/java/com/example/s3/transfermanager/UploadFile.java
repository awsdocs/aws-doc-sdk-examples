//snippet-sourcedescription:[UploadFile.java demonstrates how to upload a file using the Amazon Simple Storage Service (Amazon S3) TransferManager.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3.transfermanager;

// snippet-start:[s3.tm.java2.uploadfile.import]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

import java.net.URL;
import java.nio.file.Paths;
import java.util.UUID;
// snippet-end:[s3.tm.java2.uploadfile.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class UploadFile {
    private static final Logger logger = LoggerFactory.getLogger(UploadFile.class);
    public final String bucketName = "x-" + UUID.randomUUID();
    public final String key = UUID.randomUUID().toString();
    public String filePath;

    public UploadFile() {
        this.setUp();
    }

    public static void main(String[] args) {
        UploadFile upload = new UploadFile();
        upload.uploadFile(S3ClientFactory.transferManager, upload.bucketName, upload.key, upload.filePath);
        upload.cleanUp();
    }

    // snippet-start:[s3.tm.java2.uploadfile.main]
    public String uploadFile(S3TransferManager transferManager, String bucketName,
                             String key, String filePath) {
        UploadFileRequest uploadFileRequest =
            UploadFileRequest.builder()
                .putObjectRequest(b -> b.bucket(bucketName).key(key))
                .addTransferListener(LoggingTransferListener.create())
                .source(Paths.get(filePath))
                .build();

        FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);

        CompletedFileUpload uploadResult = fileUpload.completionFuture().join();
        return uploadResult.response().eTag();
    }
    // snippet-end:[s3.tm.java2.uploadfile.main]

    private void setUp(){
        S3ClientFactory.s3Client.createBucket(b -> b.bucket(bucketName));
        // get the file system path to the provided file to upload
        URL resource = UploadFile.class.getClassLoader().getResource("image.png");
        filePath = resource.getPath();
    }

    public void cleanUp(){
        S3ClientFactory.s3Client.deleteObject(b -> b.bucket(bucketName).key(key));
        S3ClientFactory.s3Client.deleteBucket(b -> b.bucket(bucketName));
    }
}
