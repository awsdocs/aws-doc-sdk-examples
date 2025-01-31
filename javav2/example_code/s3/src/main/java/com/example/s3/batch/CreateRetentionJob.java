// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.batch;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.model.CreateJobRequest;
import software.amazon.awssdk.services.s3control.model.CreateJobResponse;
import software.amazon.awssdk.services.s3control.model.JobManifest;
import software.amazon.awssdk.services.s3control.model.JobManifestLocation;
import software.amazon.awssdk.services.s3control.model.JobManifestSpec;
import software.amazon.awssdk.services.s3control.model.JobOperation;
import software.amazon.awssdk.services.s3control.model.JobReport;
import software.amazon.awssdk.services.s3control.model.JobReportFormat;
import software.amazon.awssdk.services.s3control.model.JobReportScope;
import software.amazon.awssdk.services.s3control.model.S3ObjectLockLegalHold;
import software.amazon.awssdk.services.s3control.model.S3ObjectLockLegalHoldStatus;
import software.amazon.awssdk.services.s3control.model.S3ObjectLockRetentionMode;
import software.amazon.awssdk.services.s3control.model.S3Retention;
import software.amazon.awssdk.services.s3control.model.S3SetObjectLegalHoldOperation;
import software.amazon.awssdk.services.s3control.model.S3SetObjectRetentionOperation;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Before running this example:
 * <p/>
 * The SDK must be able to authenticate AWS requests on your behalf. If you have not configured
 * authentication for SDKs and tools,see https://docs.aws.amazon.com/sdkref/latest/guide/access.html in the AWS SDKs and Tools Reference Guide.
 * <p/>
 * You must have a runtime environment configured with the Java SDK.
 * See https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html in the Developer Guide if this is not set up.
 */
public class CreateRetentionJob {
    private static final String STACK_NAME = "MyS3Stack";

    public static void main(String[] args) throws IOException, ParseException {
        S3Client s3 = S3Client.create();
        S3ControlClient s3ControlClient = S3ControlClient.create();

        // Use CloudFormation to stand up the required resource.
        System.out.println("Use CloudFormation to stand up the resource required for this scenario.");
        CloudFormationHelper.deployCloudFormationStack(STACK_NAME);

        Map<String, String> stackOutputs = CloudFormationHelper.getStackOutputs(STACK_NAME);
        String iamRoleArn = stackOutputs.get("S3BatchRoleArn");
        String accountId = getAccountId();

        // Specify your S3 bucket name.
        String bucketName = "amzn-s3-demo-bucket-" + UUID.randomUUID();  // Change bucket name.
        System.out.println("Populate the bucket with the required files.");
        String[] fileNames = {"job-manifest.csv", "object-key-1.txt", "object-key-2.txt", "object-key-3.txt", "object-key-4.txt"};
        uploadFilesToBucket(s3, bucketName, fileNames);
        String jobId = createComplianceRetentionJob(s3ControlClient, iamRoleArn, bucketName, accountId);
        System.out.println("The job Id is " + jobId);

        // Create a legal Hold Off Job.
        String jobHoldOffId = createLegalHoldOffJob(s3ControlClient, iamRoleArn, bucketName, accountId);
        System.out.println("The id of the hold off job is " + jobHoldOffId);
        CloudFormationHelper.destroyCloudFormationStack(STACK_NAME);
    }

    /**
     * Uploads a set of files to an Amazon S3 bucket.
     *
     * @param s3         the S3 client to use for the file uploads
     * @param bucketName the name of the S3 bucket to upload the files to
     * @param fileNames  an array of file names to be uploaded
     * @throws IOException if an I/O error occurs during the file creation or upload process
     */
    public static void uploadFilesToBucket(S3Client s3, String bucketName, String[] fileNames) throws IOException {
        updateCSV(bucketName);
        createTextFiles(fileNames);
        for (String fileName : fileNames) {
            populateBucket(s3, bucketName, fileName);
        }
        System.out.println("All files are placed in the S3 bucket " + bucketName);
    }

    /**
     * Uploads a file to an Amazon S3 bucket.
     *
     * @param s3         The {@link S3Client} instance used to interact with the Amazon S3 service.
     * @param bucketName The name of the Amazon S3 bucket where the file will be uploaded.
     * @param fileName   The name of the file to be uploaded.
     */
    public static void populateBucket(S3Client s3, String bucketName, String fileName) {
        Path filePath = Paths.get("src/main/resources/batch/", fileName).toAbsolutePath();
        PutObjectRequest putOb = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .build();

        s3.putObject(putOb, RequestBody.fromFile(filePath));
        System.out.println("Successfully placed " + fileName + " into bucket " + bucketName);
    }

    /**
     * Updates the first value in each line of a CSV file located at the specified path.
     *
     * @param newValue the new value to be set for the first field in each line of the CSV file
     * @throws IOException          if an I/O error occurs while reading or writing the CSV file
     * @throws NullPointerException if the {@code newValue} parameter is {@code null}
     */
    public static void updateCSV(String newValue) {
        Path csvFilePath = Paths.get("src/main/resources/batch/job-manifest.csv").toAbsolutePath();
        try {
            // Read all lines from the CSV file.
            List<String> lines = Files.readAllLines(csvFilePath);

            // Update the first value in each line.
            List<String> updatedLines = lines.stream()
                .map(line -> {
                    String[] parts = line.split(",");
                    parts[0] = newValue;
                    return String.join(",", parts);
                })
                .collect(Collectors.toList());

            // Write the updated lines back to the CSV file.
            Files.write(csvFilePath, updatedLines);
            System.out.println("CSV file updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // snippet-start:[s3control.java2.create_job.compliance.main]

    /**
     * Creates a compliance retention job in Amazon S3 Control.
     * <p>
     * A compliance retention job in Amazon S3 Control is a feature that allows you to
     * set a retention period for objects stored in an S3 bucket.
     * This feature is particularly useful for organizations that need to comply with
     * regulatory requirements or internal policies that mandate the retention of data for
     * a specific duration.
     *
     * @param s3ControlClient The S3ControlClient instance to use for the API call.
     * @return The job ID of the created compliance retention job.
     */
    public static String createComplianceRetentionJob(final S3ControlClient s3ControlClient, String roleArn, String bucketName, String accountId) {
        final String manifestObjectArn = "arn:aws:s3:::amzn-s3-demo-manifest-bucket/compliance-objects-manifest.csv";
        final String manifestObjectVersionId = "your-object-version-Id";

        Instant jan2025 = Instant.parse("2025-01-01T00:00:00Z");
        JobOperation jobOperation = JobOperation.builder()
            .s3PutObjectRetention(S3SetObjectRetentionOperation.builder()
                .retention(S3Retention.builder()
                    .mode(S3ObjectLockRetentionMode.COMPLIANCE)
                    .retainUntilDate(jan2025)
                    .build())
                .build())
            .build();

        JobManifestLocation manifestLocation = JobManifestLocation.builder()
            .objectArn(manifestObjectArn)
            .eTag(manifestObjectVersionId)
            .build();

        JobManifestSpec manifestSpec = JobManifestSpec.builder()
            .fieldsWithStrings("Bucket", "Key")
            .format("S3BatchOperations_CSV_20180820")
            .build();

        JobManifest manifestToPublicApi = JobManifest.builder()
            .location(manifestLocation)
            .spec(manifestSpec)
            .build();

        // Report details.
        final String jobReportBucketArn = "arn:aws:s3:::" + bucketName;
        final String jobReportPrefix = "reports/compliance-objects-bops";

        JobReport jobReport = JobReport.builder()
            .enabled(true)
            .reportScope(JobReportScope.ALL_TASKS)
            .bucket(jobReportBucketArn)
            .prefix(jobReportPrefix)
            .format(JobReportFormat.REPORT_CSV_20180820)
            .build();

        final Boolean requiresConfirmation = true;
        final int priority = 10;
        CreateJobRequest request = CreateJobRequest.builder()
            .accountId(accountId)
            .description("Set compliance retain-until to 1 Jan 2025")
            .manifest(manifestToPublicApi)
            .operation(jobOperation)
            .priority(priority)
            .roleArn(roleArn)
            .report(jobReport)
            .confirmationRequired(requiresConfirmation)
            .build();

        // Create the job and get the result.
        CreateJobResponse result = s3ControlClient.createJob(request);
        return result.jobId();
    }
    // snippet-end:[s3control.java2.create_job.compliance.main]

    // snippet-start:[s3control.java2.create_job.legal.off.main]

    /**
     * Creates a legal hold off job in an S3 bucket.
     *
     * @param s3ControlClient the S3 Control client used to create the job
     * @param roleArn         the ARN of the IAM role to use for the job
     * @param bucketName      the name of the S3 bucket to create the job report in
     * @param accountId       the AWS account ID to create the job in
     * @return the job ID of the created job
     */
    public static String createLegalHoldOffJob(final S3ControlClient s3ControlClient, String roleArn, String bucketName, String accountId) {
        final String manifestObjectArn = "arn:aws:s3:::amzn-s3-demo-manifest-bucket/compliance-objects-manifest.csv";
        final String manifestObjectVersionId = "your-object-version-Id";
        JobOperation jobOperation = JobOperation.builder()
            .s3PutObjectLegalHold(S3SetObjectLegalHoldOperation.builder()
                .legalHold(S3ObjectLockLegalHold.builder()
                    .status(S3ObjectLockLegalHoldStatus.OFF)
                    .build())
                .build())
            .build();

        JobManifestLocation manifestLocation = JobManifestLocation.builder()
            .objectArn(manifestObjectArn)
            .eTag(manifestObjectVersionId)
            .build();

        JobManifestSpec manifestSpec = JobManifestSpec.builder()
            .fieldsWithStrings("Bucket", "Key")
            .format("S3BatchOperations_CSV_20180820")
            .build();

        JobManifest manifestToPublicApi = JobManifest.builder()
            .location(manifestLocation)
            .spec(manifestSpec)
            .build();

        // Report details.
        final String jobReportBucketArn = "arn:aws:s3:::" + bucketName;
        final String jobReportPrefix = "reports/compliance-objects-bops";

        JobReport jobReport = JobReport.builder()
            .enabled(true)
            .reportScope(JobReportScope.ALL_TASKS)
            .bucket(jobReportBucketArn)
            .prefix(jobReportPrefix)
            .format(JobReportFormat.REPORT_CSV_20180820)
            .build();

        final Boolean requiresConfirmation = true;
        final int priority = 10;
        CreateJobRequest request = CreateJobRequest.builder()
            .accountId(accountId)
            .description("Set compliance retain-until to 1 Jan 2025")
            .manifest(manifestToPublicApi)
            .operation(jobOperation)
            .priority(priority)
            .roleArn(roleArn)
            .report(jobReport)
            .confirmationRequired(requiresConfirmation)
            .build();

        // Create the job and get the result.
        CreateJobResponse result = s3ControlClient.createJob(request);
        return result.jobId();
    }
    // snippet-end:[s3control.java2.create_job.legal.off.main]

    /**
     * Creates text files with the given file names in the "batch" directory within the "src/main/resources" folder.
     *
     * @param fileNames an array of file names to be created, each with a ".txt" extension
     */
    public static void createTextFiles(String[] fileNames) {
        String currentDirectory = System.getProperty("user.dir");
        String directoryPath = currentDirectory + "\\src\\main\\resources\\batch";
        Path path = Paths.get(directoryPath);

        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path);
                System.out.println("Created directory: " + path.toString());
            } else {
                System.out.println("Directory already exists: " + path.toString());
            }

            for (String fileName : fileNames) {
                // Check if the file is a .txt file.
                if (fileName.endsWith(".txt")) {
                    // Define the path for the new file.
                    Path filePath = path.resolve(fileName);
                    System.out.println("Attempting to create file: " + filePath.toString());

                    // Create and write content to the new file.
                    Files.write(filePath, "This is a test".getBytes());

                    // Verify the file was created.
                    if (Files.exists(filePath)) {
                        System.out.println("Successfully created file: " + filePath.toString());
                    } else {
                        System.out.println("Failed to create file: " + filePath.toString());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the account ID of the current AWS user or role.
     *
     * @return the account ID as a String
     */
    public static String getAccountId() {
        StsClient stsClient = StsClient.create();
        GetCallerIdentityResponse callerIdentityResponse = stsClient.getCallerIdentity();
        return callerIdentityResponse.account();
    }
}
