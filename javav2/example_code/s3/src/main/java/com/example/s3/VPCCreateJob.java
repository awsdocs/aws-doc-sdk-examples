// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

// snippet-start:[s3.java2.create_job.vpc.main]
// snippet-start:[s3.java2.create_job.vpc.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.model.S3SetObjectTaggingOperation;
import software.amazon.awssdk.services.s3control.model.JobOperation;
import software.amazon.awssdk.services.s3control.model.S3Tag;
import software.amazon.awssdk.services.s3control.model.JobManifestLocation;
import software.amazon.awssdk.services.s3control.model.JobManifestSpec;
import software.amazon.awssdk.services.s3control.model.JobManifest;
import software.amazon.awssdk.services.s3control.model.JobManifestFormat;
import software.amazon.awssdk.services.s3control.model.JobReport;
import software.amazon.awssdk.services.s3control.model.JobReportFormat;
import software.amazon.awssdk.services.s3control.model.CreateJobRequest;
import software.amazon.awssdk.services.s3control.model.S3ControlException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
// snippet-end:[s3.java2.create_job.vpc.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class VPCCreateJob {
    public static void main(String[] args) throws URISyntaxException {
        final String usage = """

            Usage:
                <accountId> <iamRoleArn> <manifestLocation> <reportBucketName> <tagKey> <tagValue> <eTag> <vpcBucketURL>

            Where:
                accountId - The account id value that owns the Amazon S3 bucket.
                iamRoleArn - The ARN of the AWS Identity and Access Management (IAM) role that has permissions to create a batch job.
                manifestLocation - The location where the manaifest file required for the job (for example, arn:aws:s3:::<BUCKETNAME>/manifest.csv).
                reportBucketName - The Amazon S3 bucket where the report is written to  (for example, arn:aws:s3:::<BUCKETNAME>).
                tagKey - The key used for a tag (for example,  keyOne).
                tagValue - The value for the key (for example,  ValueOne).
                eTag - The ETag for the specified manifest object (for example, 000000c9d1046e73f7dde5043ac3ae85).
                vpcBucketURL - The URL of the bucket located in your virtual private cloud (VPC) (for example,  https://bucket.vpce-xxxxxc4d-5e6f.s3.us-east-1.vpce.amazonaws.com)
                """;

        if (args.length != 8) {
            System.out.println(usage);
            System.exit(1);
        }

        String accountId = args[0];
        String iamRoleArn = args[1];
        String manifestLocation = args[2];
        String reportBucketName = args[3];
        String tagKey = args[4];
        String tagValue = args[5];
        String eTag = args[6];
        String vpcBucketURL = args[7];
        String uuid = java.util.UUID.randomUUID().toString();
        URI myURI = new URI(vpcBucketURL);
        S3ControlClient s3ControlClient = S3ControlClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(myURI)
            .build();

        createS3Job(s3ControlClient, accountId, iamRoleArn, manifestLocation, reportBucketName, tagKey,
            tagValue, eTag,
            uuid);
        s3ControlClient.close();
    }

    /**
     * Creates an S3 Batch Operations job using the AWS Java SDK.
     *
     * @param s3ControlClient the S3 Control client used to create the job
     * @param accountId the AWS account ID associated with the job
     * @param iamRoleArn the ARN of the IAM role to be used for the job
     * @param manifestLocation the Amazon S3 object location of the job manifest
     * @param reportBucketName the name of the Amazon S3 bucket to store the job report
     * @param tagKey the key of the tag to be added to the objects
     * @param tagValue the value of the tag to be added to the objects
     * @param eTag the ETag of the job manifest object
     * @param uuid a unique identifier for the job request
     */
    public static void createS3Job(S3ControlClient s3ControlClient, String accountId, String iamRoleArn,
                                   String manifestLocation, String reportBucketName, String tagKey, String tagValue, String eTag,
                                   String uuid) {
        try {
            ArrayList<S3Tag> tagSet = new ArrayList<>();
            S3Tag s3Tag = S3Tag.builder()
                .key(tagKey)
                .value(tagValue)
                .build();

            tagSet.add(s3Tag);
            S3SetObjectTaggingOperation objectTaggingOperation = S3SetObjectTaggingOperation.builder()
                .tagSet(tagSet)
                .build();

            JobOperation jobOperation = JobOperation.builder()
                .s3PutObjectTagging(objectTaggingOperation)
                .build();

            JobManifestLocation jobManifestLocation = JobManifestLocation.builder()
                .objectArn(manifestLocation)
                .eTag(eTag)
                .build();

            JobManifestSpec manifestSpec = JobManifestSpec.builder()
                .fieldsWithStrings(new String[]{"Bucket", "Key"})
                .format(JobManifestFormat.S3_BATCH_OPERATIONS_CSV_20180820)
                .build();

            JobManifest jobManifest = JobManifest.builder()
                .spec(manifestSpec)
                .location(jobManifestLocation)
                .build();

            JobReport jobReport = JobReport.builder()
                .bucket(reportBucketName)
                .prefix("reports")
                .format(JobReportFormat.REPORT_CSV_20180820)
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
}
// snippet-end:[s3.java2.create_job.vpc.main]
