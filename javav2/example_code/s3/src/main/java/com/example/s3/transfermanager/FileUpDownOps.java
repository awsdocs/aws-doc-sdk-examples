//snippet-sourcedescription:[FileUpDownOps.java demonstrates how to upload and download an object to an Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon S3]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3.transfermanager;

// snippet-start:[s3.tm.java2.file_up_down_ops.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.transfer.s3.CompletedFileDownload;
import software.amazon.awssdk.transfer.s3.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.FileDownload;
import software.amazon.awssdk.transfer.s3.FileUpload;
import software.amazon.awssdk.transfer.s3.S3ClientConfiguration;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.nio.file.Paths;

import static software.amazon.awssdk.transfer.s3.SizeConstant.MB;

public class FileUpDownOps {

    private String bucketName;
    private String key;
    private String filePath;
    private String downloadedFileName;

    public FileUpDownOps(String bucketName, String key, String filePath, String downloadedFileName) {
        this.bucketName = bucketName;
        this.key = key;
        this.filePath = filePath;
        this.downloadedFileName = downloadedFileName;
    }

    public String fileUploadDownload() {

// snippet-start:[s3.tm.java2.file_up_down_ops.createclients]
        S3ClientConfiguration s3ClientConfiguration =
                S3ClientConfiguration.builder()
                        .region(Region.US_EAST_1)
                        .minimumPartSizeInBytes(10 * MB)
                        .targetThroughputInGbps(20.0)
                        .build();

        S3TransferManager s3TransferManager = S3TransferManager.builder()
                .s3ClientConfiguration(s3ClientConfiguration)
                .build();
// snippet-end:[s3.tm.java2.file_up_down_ops.createclients]

// snippet-start:[s3.tm.java2.file_up_down_ops.fileupload]
        FileUpload fileUpload = s3TransferManager.uploadFile(uploadFileRequestBuilder ->
                uploadFileRequestBuilder
                        .putObjectRequest(
                                putObjectRequestBuilder -> putObjectRequestBuilder.bucket(bucketName).key(key))
                        .source(Paths.get(filePath)));
// snippet-end:[s3.tm.java2.file_up_down_ops.fileupload]

// snippet-start:[s3.tm.java2.file_up_down_ops.fileuploadresult]
        CompletedFileUpload uploadResult = fileUpload.completionFuture().join();
        System.out.println("Etag: " + uploadResult.response().eTag());
// snippet-end:[s3.tm.java2.file_up_down_ops.fileuploadresult]

// snippet-start:[s3.tm.java2.file_up_down_ops.filedownload]
        FileDownload downloadFile = s3TransferManager.downloadFile(
                downloadFileRequestBuilder -> downloadFileRequestBuilder
                        .getObjectRequest(getObjectRequestBuilder ->
                                getObjectRequestBuilder.bucket(bucketName).key(key))
                        .destination(Paths.get(downloadedFileName)));
// snippet-end:[s3.tm.java2.file_up_down_ops.filedownload]

// snippet-start:[s3.tm.java2.file_up_down_ops.filedownloadresult]
        CompletedFileDownload downloadResult = downloadFile.completionFuture().join();
        System.out.println("Content length: " + downloadResult.response().contentLength());
// snippet-end:[s3.tm.java2.file_up_down_ops.filedownloadresult]

        return uploadResult.response().eTag() + "|" + downloadResult.response().contentType();
    }
}