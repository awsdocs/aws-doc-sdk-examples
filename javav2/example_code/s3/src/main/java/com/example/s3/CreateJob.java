//snippet-sourcedescription:[CreateJob.java demonstrates how to create an Amazon Simple Storage Service (Amazon S3) batch job.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/16/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.s3;

// snippet-start:[s3.java2.create_job.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.model.S3Tag;
import software.amazon.awssdk.services.s3control.model.S3SetObjectTaggingOperation;
import software.amazon.awssdk.services.s3control.model.JobOperation;
import software.amazon.awssdk.services.s3control.model.JobManifestLocation;
import software.amazon.awssdk.services.s3control.model.JobManifestSpec;
import software.amazon.awssdk.services.s3control.model.JobManifest;
import software.amazon.awssdk.services.s3control.model.JobReport;
import software.amazon.awssdk.services.s3control.model.CreateJobRequest;
import software.amazon.awssdk.services.s3control.model.S3ControlException;
// snippet-end:[s3.java2.create_job.import]

import java.util.ArrayList;

/**
 * To run this code example, ensure that you have followed the documentation provided here:
 *
 * https://docs.aws.amazon.com/AmazonS3/latest/dev/batch-ops-create-job.html
 *
 * In addition, before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateJob {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <accountId> <iamRoleArn> <manifestLocation> <reportBucketName>>\n\n" +
                "Where:\n" +
                "    accountId - The account id value that owns the Amazon S3 bucket.\n\n" +
                "    iamRoleArn - The ARN of the AWS Identity and Access Management (IAM) role that has permissions to create a batch job.\n" +
                "    manifestLocation - The location where the manaifest file required for the job (for example, arn:aws:s3:::<BUCKETNAME>/manifest.csv).\n" +
                "    reportBucketName - The Amazon S3 bucket where the report is written to  (for example, arn:aws:s3:::<BUCKETNAME>).\n";

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String accountId = args[0];
        String iamRoleArn = args[1];
        String manifestLocation = args[2];
        String reportBucketName = args[3];
        String uuid = java.util.UUID.randomUUID().toString();

        S3ControlClient s3ControlClient = S3ControlClient.builder()
                .region(Region.US_EAST_1)
                .build();

        createS3Job(s3ControlClient, accountId, iamRoleArn, manifestLocation, reportBucketName, uuid);
        s3ControlClient.close();
    }

    // snippet-start:[s3.java2.create_job.main]
    public static void createS3Job( S3ControlClient s3ControlClient,
                                    String accountId,
                                    String iamRoleArn,
                                    String manifestLocation,
                                    String reportBucketName,
                                    String uuid) {

       try {
           ArrayList<S3Tag> tagSet = new ArrayList<>();

           S3Tag s3Tag = S3Tag.builder()
                .key("keyOne")
                .value("ValueOne")
                .build();

            tagSet.add(s3Tag);

            S3SetObjectTaggingOperation objectTaggingOperation = S3SetObjectTaggingOperation.builder()
                .tagSet(s3Tag)
                .build();

            JobOperation jobOperation = JobOperation.builder()
                .s3PutObjectTagging(objectTaggingOperation)
                .build();

            JobManifestLocation jobManifestLocation = JobManifestLocation.builder()
                .objectArn(manifestLocation)
                .eTag("60e460c9d1046e73f7dde5043ac3ae85")
                .build();

            JobManifestSpec manifestSpec = JobManifestSpec.builder()
                .fieldsWithStrings(new String[]{"Bucket", "Key"})
                .format("S3BatchOperations_CSV_20180820")
                .build();

            JobManifest jobManifest = JobManifest.builder()
                .spec(manifestSpec)
                .location(jobManifestLocation)
                .build();

            JobReport jobReport = JobReport.builder()
                .bucket(reportBucketName)
                .prefix("reports")
                .format("Report_CSV_20180820")
                .enabled(true)
                .reportScope("AllTasks")
                .build();

            CreateJobRequest jobRequest = CreateJobRequest.builder()
                .accountId(accountId)
                .description("Job created using the AWS Java SDK")
                .manifest(jobManifest)
                .operation(jobOperation)
                .report(jobReport)
                .priority(42)
                .roleArn(iamRoleArn)
                .clientRequestToken(uuid)
                .confirmationRequired(false)
                .build();

            s3ControlClient.createJob(jobRequest);

       } catch (S3ControlException e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
       }
    }
    // snippet-end:[s3.java2.create_job.main]
}
