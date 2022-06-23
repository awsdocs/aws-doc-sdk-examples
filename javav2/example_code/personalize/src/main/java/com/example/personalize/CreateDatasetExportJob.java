//snippet-sourcedescription:[CreateDatasetExportJob.java demonstrates how to create
// an Amazon Personalize dataset export job that exports data from a dataset to an Amazon S3 bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/13/2021]
//snippet-sourceauthor:[seashman - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.personalize;

//snippet-start:[personalize.java2.create_dataset_export_job.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.CreateDatasetExportJobRequest;
import software.amazon.awssdk.services.personalize.model.DatasetExportJob;
import software.amazon.awssdk.services.personalize.model.DatasetExportJobOutput;
import software.amazon.awssdk.services.personalize.model.S3DataConfig;
import software.amazon.awssdk.services.personalize.model.DescribeDatasetExportJobRequest;
import software.amazon.awssdk.services.personalize.model.IngestionMode;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;

import java.time.Instant;

//snippet-end:[personalize.java2.create_dataset_export_job.import]

public class CreateDatasetExportJob {

    public static void main(String [] args) {

        final String USAGE = "\n" +
        "Usage:\n" +
        "    CreateDatasetExportJob <name, datasetArn, ingestionMode, roleArn, s3BucketPath>\n\n" +
        "Where:\n" +
        "    jobName - The name for the dataset export job.\n" +
        "    datasetArn - The Amazon Resource Name (ARN) of the dataset that contains the data to export.\n" +
        "    ingestionMode - The data to export, based on how you imported the data.\n" +
        "    roleArn - The Amazon Resource Name (ARN) of the IAM service role that"
                + "has permissions to add data to your output Amazon S3 bucket.\n" +
        "    s3BucketPath - The path to your output bucket\n" +
        "    kmsKeyArn - The ARN for your KMS key\n\n";
        
        if (args.length != 6) {
            System.out.println(USAGE);
            System.exit(1);
        }

        IngestionMode ingestionMode = IngestionMode.ALL;
        String jobName = args[0];
        String datasetArn = args[1];
        
        if (args[2].toLowerCase().equals("put")) {
            ingestionMode = IngestionMode.PUT;
        }
        else if (args[2].toLowerCase().equals("bulk")) {
            ingestionMode = IngestionMode.BULK;
        }
        String roleArn = args[3];
        String s3BucketPath = args[4];
        String kmsKeyArn = args[5];


        Region region = Region.US_WEST_2;
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();
        createDatasetExportJob(personalizeClient, jobName, datasetArn, ingestionMode, roleArn, s3BucketPath, kmsKeyArn);
        personalizeClient.close();
    }
    //snippet-start:[personalize.java2.create_dataset_export_job.main]
    public static String createDatasetExportJob(PersonalizeClient personalizeClient,
                                            String jobName,
                                            String datasetArn, 
                                            IngestionMode ingestionMode, 
                                            String roleArn,
                                            String s3BucketPath,
                                            String kmsKeyArn) {
        
        long waitInMilliseconds = 30 * 1000; // 30 seconds
        String status = null;

        try {

            S3DataConfig exportS3DataConfig = S3DataConfig.builder().path(s3BucketPath).kmsKeyArn(kmsKeyArn).build();
            DatasetExportJobOutput jobOutput = DatasetExportJobOutput.builder().s3DataDestination(exportS3DataConfig).build();

            CreateDatasetExportJobRequest createRequest = CreateDatasetExportJobRequest.builder()
                    .jobName(jobName)
                    .datasetArn(datasetArn)
                    .ingestionMode(ingestionMode)
                    .jobOutput(jobOutput)
                    .roleArn(roleArn)
                    .build();

            String datasetExportJobArn = personalizeClient.createDatasetExportJob(createRequest).datasetExportJobArn();
            
            DescribeDatasetExportJobRequest describeDatasetExportJobRequest = DescribeDatasetExportJobRequest.builder()
                .datasetExportJobArn(datasetExportJobArn)
                .build();

            long maxTime = Instant.now().getEpochSecond() + 3 * 60 * 60;

            while (Instant.now().getEpochSecond() < maxTime) {

                DatasetExportJob datasetExportJob = personalizeClient.describeDatasetExportJob(describeDatasetExportJobRequest)
                        .datasetExportJob();

                status = datasetExportJob.status();
                System.out.println("Export job status: " + status);
                
                if (status.equals("ACTIVE") || status.equals("CREATE FAILED")) {
                   return status;
                }
                try {
                    Thread.sleep(waitInMilliseconds);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (PersonalizeException e) {
            System.out.println(e.awsErrorDetails().errorMessage());
        }
        return "";
    }
    //snippet-end:[personalize.java2.create_dataset_export_job.main]
}
