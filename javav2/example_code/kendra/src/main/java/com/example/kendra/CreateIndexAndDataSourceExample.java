//snippet-sourcedescription:[CreateIndexAndDataSourceExample.java demonstrates how to create an Amazon Kendra index and data source.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Kendra]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kendra;

// snippet-start:[kendra.java2.index.import]
import java.util.concurrent.TimeUnit;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kendra.KendraClient;
import software.amazon.awssdk.services.kendra.model.CreateIndexResponse;
import software.amazon.awssdk.services.kendra.model.DescribeIndexRequest;
import software.amazon.awssdk.services.kendra.model.DescribeIndexResponse;
import software.amazon.awssdk.services.kendra.model.KendraException;
import software.amazon.awssdk.services.kendra.model.IndexStatus;
import software.amazon.awssdk.services.kendra.model.CreateDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.CreateIndexRequest;
import software.amazon.awssdk.services.kendra.model.DataSourceType;
import software.amazon.awssdk.services.kendra.model.DataSourceConfiguration;
import software.amazon.awssdk.services.kendra.model.S3DataSourceConfiguration;
import software.amazon.awssdk.services.kendra.model.CreateDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceRequest;
import software.amazon.awssdk.services.kendra.model.DataSourceStatus;
import software.amazon.awssdk.services.kendra.model.DescribeDataSourceResponse;
import software.amazon.awssdk.services.kendra.model.StartDataSourceSyncJobResponse;
import software.amazon.awssdk.services.kendra.model.StartDataSourceSyncJobRequest;
// snippet-end:[kendra.java2.index.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateIndexAndDataSourceExample {

    public static void main(String[] args) {

         final String usage = "\n" +
                    "Usage:\n" +
                    "    <indexDescription> <indexName> <indexRoleArn> <dataSourceRoleArn> <dataSourceName> <dataSourceDescription> <s3BucketName>\n\n" +
                    "Where:\n" +
                    "    indexDescription - A description for the index.\n" +
                    "    indexName - The name for the new index.\n" +
                    "    indexRoleArn - An Identity and Access Management (IAM) role that gives Amazon Kendra permissions to access your Amazon CloudWatch logs and metrics.\n\n" +
                    "    dataSourceRoleArn - The ARN of am IAM role with permission to access the data source.\n\n" +
                    "    dataSourceName - The name for the new data source.\n\n" +
                    "    dataSourceDescription - A description for the data source.\n\n" +
                    "    s3BucketName - An Amazon S3 bucket used as your data source.\n\n" ;

        if (args.length != 7) {
            System.out.println(usage);
            System.exit(1);
        }

        String indexDescription = args[0];
        String indexName = args[1];
        String indexRoleArn = args[2];
        String dataSourceRoleArn = args[3];
        String dataSourceName = args[4];
        String dataSourceDescription = args[5];
        String s3BucketName = args[6];

        KendraClient kendra = KendraClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String indexId = createIndex(kendra, indexDescription, indexName, indexRoleArn);
        String dataSourceId = CreateIndexAndDataSourceExample.createDataSource(kendra, s3BucketName, dataSourceName, dataSourceDescription, indexId, dataSourceRoleArn);
        startDataSource(kendra, indexId, dataSourceId);
    }

    // snippet-start:[kendra.java2.index.main]
    public static String createIndex(KendraClient kendra, String indexDescription, String indexName, String indexRoleArn) {

    try {
        System.out.println("Creating an index named " +indexName);
        CreateIndexRequest createIndexRequest = CreateIndexRequest.builder()
                .description(indexDescription)
                .name(indexName)
                .roleArn(indexRoleArn)
                .build();

        CreateIndexResponse createIndexResponse = kendra.createIndex(createIndexRequest);
        System.out.println("Index response " +createIndexResponse);
        String indexId = createIndexResponse.id();
        System.out.println("Waiting until the index with index ID "+indexId +" is created.");

        while (true) {
            DescribeIndexRequest describeIndexRequest = DescribeIndexRequest.builder().id(indexId).build();
            DescribeIndexResponse describeIndexResponse = kendra.describeIndex(describeIndexRequest);
            IndexStatus status = describeIndexResponse.status();
            System.out.println("Status is " +status);
            if (status != IndexStatus.CREATING) {
                break;
            }

            TimeUnit.SECONDS.sleep(60);
        }
        return indexId ;
    } catch (KendraException | InterruptedException e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }
    return "" ;
   }
    // snippet-end:[kendra.java2.index.main]

    // snippet-start:[kendra.java2.datasource.main]
    public static String createDataSource(KendraClient kendra, String s3BucketName, String dataSourceName, String dataSourceDescription, String indexId, String dataSourceRoleArn) {

        System.out.println("Creating an S3 data source");

        try {
            CreateDataSourceRequest createDataSourceRequest = CreateDataSourceRequest
                    .builder()
                    .indexId(indexId)
                    .name(dataSourceName)
                    .description(dataSourceDescription)
                    .roleArn(dataSourceRoleArn)
                    .type(DataSourceType.S3)
                    .configuration(
                            DataSourceConfiguration
                                    .builder()
                                    .s3Configuration(
                                            S3DataSourceConfiguration
                                                    .builder()
                                                    .bucketName(s3BucketName)
                                                    .build()
                                    ).build()
                    ).build();

            CreateDataSourceResponse createDataSourceResponse = kendra.createDataSource(createDataSourceRequest);
            System.out.println("Response of creating data source " +createDataSourceResponse);

            String dataSourceId = createDataSourceResponse.id();
            System.out.println("Waiting for Kendra to create the data source " +dataSourceId);
            DescribeDataSourceRequest describeDataSourceRequest = DescribeDataSourceRequest
                    .builder()
                    .indexId(indexId)
                    .id(dataSourceId)
                    .build();

            while (true) {
                DescribeDataSourceResponse describeDataSourceResponse = kendra.describeDataSource(describeDataSourceRequest);
                DataSourceStatus status = describeDataSourceResponse.status();
                System.out.println("Creating data source. Status is " +status);
                if (status != DataSourceStatus.CREATING) {
                    break;
                }

                TimeUnit.SECONDS.sleep(60);
            }

            return dataSourceId;
        } catch (KendraException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "" ;
    }
    // snippet-end:[kendra.java2.datasource.main]

    // snippet-start:[kendra.java2.start.datasource.main]
    public static void startDataSource (KendraClient kendra, String indexId, String dataSourceId) {

     try{
        System.out.println("Synchronize the data source " +dataSourceId);
        StartDataSourceSyncJobRequest startDataSourceSyncJobRequest = StartDataSourceSyncJobRequest
                .builder()
                .indexId(indexId)
                .id(dataSourceId)
                .build();

        StartDataSourceSyncJobResponse startDataSourceSyncJobResponse = kendra.startDataSourceSyncJob(startDataSourceSyncJobRequest);
        System.out.println("Waiting for the data source to sync with the index "+indexId + " for execution ID " +startDataSourceSyncJobResponse.executionId());

    } catch (KendraException e) {
          System.err.println(e.getMessage());
          System.exit(1);
    }

    System.out.println("Index setup is complete");
    }
}
// snippet-end:[kendra.java2.start.datasource.main]
