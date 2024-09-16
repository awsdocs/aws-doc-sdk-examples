// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.iotsitewise;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iotsitewise.IoTSiteWiseClient;
import software.amazon.awssdk.services.iotsitewise.model.ColumnName;
import software.amazon.awssdk.services.iotsitewise.model.CreateBulkImportJobRequest;
import software.amazon.awssdk.services.iotsitewise.model.CreateBulkImportJobResponse;
import software.amazon.awssdk.services.iotsitewise.model.Csv;
import software.amazon.awssdk.services.iotsitewise.model.CustomerManagedS3Storage;
import software.amazon.awssdk.services.iotsitewise.model.DescribeStorageConfigurationRequest;
import software.amazon.awssdk.services.iotsitewise.model.DescribeStorageConfigurationResponse;
import software.amazon.awssdk.services.iotsitewise.model.ErrorReportLocation;
import software.amazon.awssdk.services.iotsitewise.model.File;
import software.amazon.awssdk.services.iotsitewise.model.FileFormat;
import software.amazon.awssdk.services.iotsitewise.model.InvalidRequestException;
import software.amazon.awssdk.services.iotsitewise.model.IoTSiteWiseException;
import software.amazon.awssdk.services.iotsitewise.model.JobConfiguration;
import software.amazon.awssdk.services.iotsitewise.model.MultiLayerStorage;
import software.amazon.awssdk.services.iotsitewise.model.PutStorageConfigurationRequest;
import software.amazon.awssdk.services.iotsitewise.model.PutStorageConfigurationResponse;
import software.amazon.awssdk.services.iotsitewise.model.StorageType;

import java.util.ArrayList;
import java.util.List;

public class CreateBulkImportJob {

    public static void main(String[]args){
       setConfig();
       checkStorageConfig();
       createBulkImportJob();
    }

    private static IoTSiteWiseClient getClient() {
        IoTSiteWiseClient client = IoTSiteWiseClient.builder()
            .region(Region.US_EAST_1)
            .build();

        return client;
    }

    public static void createBulkImportJob() {
        List<File> files = new ArrayList<>();
        files.add(software.amazon.awssdk.services.iotsitewise.model.File.builder()
            .bucket("scottsitewise")
            .key("data.csv")
            .build());

        Csv csvFormat = Csv.builder()
            .columnNames(
                ColumnName.ASSET_ID,   // Correct column names
                ColumnName.PROPERTY_ID,   // Correct column names
                ColumnName.DATA_TYPE,
                ColumnName.TIMESTAMP_SECONDS,
                ColumnName.VALUE
            )
            .build();

        FileFormat fileFormat = FileFormat.builder()
            .csv(csvFormat) // Set headers
            .build();

        JobConfiguration job = JobConfiguration.builder()
            .fileFormat(fileFormat)
            .build();

        ErrorReportLocation errorReportLocation = ErrorReportLocation.builder()
            .bucket("scottsitewise")
            .prefix("")
            .build();

        CreateBulkImportJobRequest bulkImportJobRequest = CreateBulkImportJobRequest.builder()
            .jobName("BulkImportJob")
            .errorReportLocation(errorReportLocation)
            .jobRoleArn("arn:aws:iam::814548047983:role/AWSIoTSiteWiseMonitorServiceRole_8jLqg43nJ")
            .files(files)
            .jobConfiguration(job)
            .build();

        try {
            CreateBulkImportJobResponse res =  getClient().createBulkImportJob(bulkImportJobRequest);
            System.out.println("It worked");

        } catch (InvalidRequestException e) {
            System.err.println("Error creating bulk import job: " + e.getMessage());
            e.printStackTrace();
        } catch (IoTSiteWiseException e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void checkStorageConfig() {
        DescribeStorageConfigurationRequest request = DescribeStorageConfigurationRequest.builder().build();

        try {
            // Pass the request object to the describeStorageConfiguration method
            DescribeStorageConfigurationResponse response = getClient().describeStorageConfiguration(request);

            // Output the current storage configuration
            System.out.println("Current Storage Type: " + response.storageType());

            // Check if Multi-Layer Storage is enabled
            if (response.multiLayerStorage() != null) {
                System.out.println("Multi-Layer Storage is enabled: " + response.multiLayerStorage());
            } else {
                System.out.println("Multi-Layer Storage is not enabled.");
            }
        } catch (IoTSiteWiseException e) {
            System.err.println("Failed to describe storage configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void setConfig() {
        CustomerManagedS3Storage managedS3Storage = CustomerManagedS3Storage.builder()
            .roleArn("arn:aws:iam::814548047983:role/service-role/AWSIoTSiteWiseMonitorServiceRole_8jLqg43nJ")
            .s3ResourceArn("arn:aws:s3:::scottsitewise")
            .build();

        // Configure the S3 bucket for cold tier storage in multi-layer storage
        MultiLayerStorage multiLayerStorage = MultiLayerStorage.builder()
            .customerManagedS3Storage(managedS3Storage)
            .build();

        PutStorageConfigurationRequest storageConfigurationRequest = PutStorageConfigurationRequest.builder()
            .storageType(StorageType.MULTI_LAYER_STORAGE)  // Specify the storage type
            .multiLayerStorage(multiLayerStorage)
            .build();

        try {
            // Call the API to set the storage configuration
            PutStorageConfigurationResponse response = getClient().putStorageConfiguration(storageConfigurationRequest);
            System.out.println("Multi-layer storage configuration updated: " + response);

            // Optional: Check if cold storage is ready for bulk imports
            checkStorageReadyForBulkImports();

        } catch (InvalidRequestException e) {
            System.err.println("Error creating bulk import job: " + e.getMessage());
            e.printStackTrace();
        } catch (IoTSiteWiseException e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Additional method to check if storage is ready for bulk imports
    private static void checkStorageReadyForBulkImports() {
        DescribeStorageConfigurationRequest request = DescribeStorageConfigurationRequest.builder().build();
        try {
            DescribeStorageConfigurationResponse response = getClient().describeStorageConfiguration(request);
            if (response.multiLayerStorage() != null) {
                System.out.println("Cold tier storage is configured for bulk imports.");
            } else {
                System.out.println("Cold tier storage is NOT configured for bulk imports.");
            }
        } catch (IoTSiteWiseException e) {
            System.err.println("Failed to describe storage configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
