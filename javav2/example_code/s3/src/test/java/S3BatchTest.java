// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.s3.batch.CloudFormationHelper;
import com.example.s3.batch.S3BatchActions;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class S3BatchTest {

    private static final String accountId = "814548047983";
    private static final String STACK_NAME = "MyS3Stack";
    private static String bucketName;
    private static String reportBucketName;
    private static String manifestLocation;
    private static S3BatchActions actions;
    private static String jobId;

    private static String iamRoleArn;

    @BeforeAll
    public static void setUp() throws IOException {
        actions = new S3BatchActions();
        CloudFormationHelper.deployCloudFormationStack(STACK_NAME);
        Map<String, String> stackOutputs = CloudFormationHelper.getStackOutputs(STACK_NAME);
        iamRoleArn = stackOutputs.get("S3BatchRoleArn");

        bucketName = "x-" + UUID.randomUUID();
        actions.createBucket(bucketName);
        reportBucketName = "arn:aws:s3:::" + bucketName;
        manifestLocation = "arn:aws:s3:::" + bucketName + "/job-manifest.csv";

        String[] fileNames = {"job-manifest.csv", "object-key-1.txt", "object-key-2.txt", "object-key-3.txt", "object-key-4.txt"};
        actions.uploadFilesToBucket(bucketName, fileNames, actions);
    }

    @AfterAll
    public static void tearDown() throws IOException {
        if (actions != null) {
            String[] fileNames = {"job-manifest.csv", "object-key-1.txt", "object-key-2.txt", "object-key-3.txt", "object-key-4.txt"};
            actions.deleteFilesFromBucket(bucketName, fileNames, actions);
            actions.deleteBucketFolder(bucketName);
            actions.deleteBucket(bucketName);
            CloudFormationHelper.destroyCloudFormationStack(STACK_NAME);
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateAndCancelJob() {
        try {
            jobId = actions.createS3JobAsync(accountId, iamRoleArn, manifestLocation, reportBucketName, UUID.randomUUID().toString()).join();
            assertNotNull(jobId);
        } catch (S3Exception e) {
            fail("S3Exception during job creation: " + e.getMessage());
        } catch (RuntimeException e) {
            fail("Unexpected error during job creation: " + e.getMessage());
        }

        try {
            actions.updateJobPriorityAsync(jobId, accountId)
                .exceptionally(ex -> {
                    fail("Update job priority failed: " + ex.getMessage());
                    return null;
                })
                .join();
        } catch (CompletionException ex) {
            fail("Failed to update job priority: " + ex.getMessage());
        }

        try {
            actions.cancelJobAsync(jobId, accountId)
                .exceptionally(ex -> {
                    fail("Cancel job failed: " + ex.getMessage());
                    return null;
                })
                .join();
        } catch (CompletionException ex) {
            fail("Failed to cancel job: " + ex.getMessage());
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testDescribeJob() {
        try {
            actions.describeJobAsync(jobId, accountId)
                .exceptionally(ex -> {
                    fail("Describe job failed: " + ex.getMessage());
                    return null;
                })
                .join();
        } catch (CompletionException ex) {
            fail("Failed to describe job: " + ex.getMessage());
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testGetAndPutJobTags() {
        try {
            actions.getJobTagsAsync(jobId, accountId)
                .exceptionally(ex -> {
                    fail("Get job tags failed: " + ex.getMessage());
                    return null;
                })
                .join();
        } catch (CompletionException ex) {
            fail("Failed to get job tags: " + ex.getMessage());
        }

        try {
            actions.putJobTaggingAsync(jobId, accountId)
                .exceptionally(ex -> {
                    fail("Put job tagging failed: " + ex.getMessage());
                    return null;
                })
                .join();
        } catch (CompletionException ex) {
            fail("Failed to put job tagging: " + ex.getMessage());
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testListBatchJobs() {
        try {
            actions.listBatchJobsAsync(accountId)
                .exceptionally(ex -> {
                    fail("List batch jobs failed: " + ex.getMessage());
                    return null;
                })
                .join();
        } catch (CompletionException ex) {
            fail("Failed to list batch jobs: " + ex.getMessage());
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testDeleteJobTags() {
        try {
            actions.deleteBatchJobTagsAsync(jobId, accountId)
                .exceptionally(ex -> {
                    fail("Delete batch job tags failed: " + ex.getMessage());
                    return null;
                })
                .join();
        } catch (CompletionException ex) {
            fail("Failed to delete batch job tags: " + ex.getMessage());
        }
    }
}
