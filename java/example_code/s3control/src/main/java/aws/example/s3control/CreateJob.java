// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[s3.java.create_batch_job.complete]

package aws.example.s3control;

// snippet-start:[s3.java.create_batch_job.import]

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3control.AWSS3Control;
import com.amazonaws.services.s3control.AWSS3ControlClient;
import com.amazonaws.services.s3control.model.*;

import java.util.UUID;
import java.util.ArrayList;

import static com.amazonaws.regions.Regions.US_WEST_2;
// snippet-end:[s3.java.create_batch_job.import]

public class CreateJob {
        public static void main(String[] args) {
                // snippet-start:[s3.java.create_batch_job.main]
                String accountId = "Account ID";
                String iamRoleArn = "IAM Role ARN";
                String reportBucketName = "arn:aws:s3:::bucket-where-completion-report-goes";
                String uuid = UUID.randomUUID().toString();

                ArrayList tagSet = new ArrayList<S3Tag>();
                tagSet.add(new S3Tag().withKey("keyOne").withValue("ValueOne"));

                try {
                        JobOperation jobOperation = new JobOperation()
                                        .withS3PutObjectTagging(new S3SetObjectTaggingOperation()
                                                        .withTagSet(tagSet));

                        JobManifest manifest = new JobManifest()
                                        .withSpec(new JobManifestSpec()
                                                        .withFormat("S3BatchOperations_CSV_20180820")
                                                        .withFields(new String[] {
                                                                        "Bucket", "Key"
                                                        }))
                                        .withLocation(new JobManifestLocation()
                                                        .withObjectArn("arn:aws:s3:::my_manifests/manifest.csv")
                                                        .withETag("60e460c9d1046e73f7dde5043ac3ae85"));
                        JobReport jobReport = new JobReport()
                                        .withBucket(reportBucketName)
                                        .withPrefix("reports")
                                        .withFormat("Report_CSV_20180820")
                                        .withEnabled(true)
                                        .withReportScope("AllTasks");

                        AWSS3Control s3ControlClient = AWSS3ControlClient.builder()
                                        .withCredentials(new ProfileCredentialsProvider())
                                        .withRegion(US_WEST_2)
                                        .build();

                        s3ControlClient.createJob(new CreateJobRequest()
                                        .withAccountId(accountId)
                                        .withOperation(jobOperation)
                                        .withManifest(manifest)
                                        .withReport(jobReport)
                                        .withPriority(42)
                                        .withRoleArn(iamRoleArn)
                                        .withClientRequestToken(uuid)
                                        .withDescription("job description")
                                        .withConfirmationRequired(false));

                        // snippet-end:[s3.java.create_batch_job.main]
                } catch (AmazonServiceException e) {
                        // The call was transmitted successfully, but Amazon S3 couldn't process
                        // it and returned an error response.
                        e.printStackTrace();
                } catch (SdkClientException e) {
                        // Amazon S3 couldn't be contacted for a response, or the client
                        // couldn't parse the response from Amazon S3.
                        e.printStackTrace();
                }
        }
}
// snippet-end:[s3.java.create_batch_job.complete]
