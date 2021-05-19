//snippet-sourcedescription:[CreateDatasetImportJob.java demonstrates how to create an
// Amazon Personalize dataset import job that imports data into an Amazon Personalize dataset.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[5/11/2021]
//snippet-sourceauthor:[seashman - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.personalize;

//snippet-start:[personalize.java2.create_dataset_import_job.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import software.amazon.awssdk.services.personalize.model.CreateDatasetImportJobRequest;
import software.amazon.awssdk.services.personalize.model.DataSource;
import software.amazon.awssdk.services.personalize.model.DatasetImportJob;
import software.amazon.awssdk.services.personalize.model.DescribeDatasetImportJobRequest;

import java.time.Instant;
//snippet-end:[personalize.java2.create_dataset_import_job.import]

public class CreateDatasetImportJob {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateDatasetImportJob <datasetArn, s3BucketPath, jobName, roleArn>\n\n" +
                "Where:\n" +
                "    datasetArn - The Amazon Resource Name (ARN) of the destination dataset.\n" +
                "    s3BucketPath - The path to the Amazon S3 bucket where the data that you " +
                "want to upload to your dataset is stored.\n" +
                "    jobName - The name for the dataset import job.\n" +
                "    roleArn - The ARN of the IAM service-linked role that"
                + "has permissions to add data to your output Amazon S3 bucket.\n\n";

        if (args.length != 4) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String datasetArn = args[0];
        String s3BucketPath = args[1];
        String jobName = args[2];
        String roleArn = args[3];

        // Change to the region where your resources are located
        Region region = Region.US_WEST_2;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        String datasetImportJobArn = createPersonalizeDatasetImportJob(personalizeClient, jobName,
                datasetArn, s3BucketPath, roleArn);
        System.out.println("Dataset import job ARN: " + datasetImportJobArn);
        personalizeClient.close();
    }

    //snippet-start:[personalize.java2.create_dataset_import_job.main]
    public static String createPersonalizeDatasetImportJob(PersonalizeClient personalizeClient,
                                                           String jobName,
                                                           String datasetArn,
                                                           String s3BucketPath,
                                                           String roleArn) {

        long waitInMilliseconds = 60 * 1000;
        String status;
        String datasetImportJobArn;

        try {
            DataSource importDataSource = DataSource.builder()
                    .dataLocation(s3BucketPath)
                    .build();

            CreateDatasetImportJobRequest createDatasetImportJobRequest = CreateDatasetImportJobRequest.builder()
                    .datasetArn(datasetArn)
                    .dataSource(importDataSource)
                    .jobName(jobName)
                    .roleArn(roleArn)
                    .build();

            datasetImportJobArn = personalizeClient.createDatasetImportJob(createDatasetImportJobRequest)
                    .datasetImportJobArn();
            DescribeDatasetImportJobRequest describeDatasetImportJobRequest = DescribeDatasetImportJobRequest.builder()
                    .datasetImportJobArn(datasetImportJobArn)
                    .build();

            long maxTime = Instant.now().getEpochSecond() + 3 * 60 * 60;

            while (Instant.now().getEpochSecond() < maxTime) {

                DatasetImportJob datasetImportJob = personalizeClient
                        .describeDatasetImportJob(describeDatasetImportJobRequest)
                        .datasetImportJob();

                status = datasetImportJob.status();
                System.out.println("Dataset import job status: " + status);

                if (status.equals("ACTIVE") || status.equals("CREATE FAILED")) {
                    break;
                }
                try {
                    Thread.sleep(waitInMilliseconds);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            return datasetImportJobArn;

        } catch (PersonalizeException e) {
            System.out.println(e.awsErrorDetails().errorMessage());
        }
        return "";
    }
    //snippet-end:[personalize.java2.create_dataset_import_job.main]
}
