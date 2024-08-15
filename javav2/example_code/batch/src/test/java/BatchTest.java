// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.batch.scenario.BatchActions;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.batch.model.CreateComputeEnvironmentResponse;
import software.amazon.awssdk.services.batch.model.JobSummary;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BatchTest {
    private static String computeEnvironmentName = "my-compute-environment" ;
    private static String jobQueueName = "my-job-queue";
    private static String jobDefinitionName = "my-job-definition";
    private static String dockerImage = "dkr.ecr.us-east-1.amazonaws.com/echo-text:echo-text";
    private static String subnet = "" ;
    private static String secGroup = "" ;
    private static BatchActions batchActions = new BatchActions();

    private static String batchIAMRole = "";
    private static String executionRoleARN = "";

    private static String jobId = "";

    static String jobQueueArn ="";

    static String jobARN = "";

    @BeforeAll
    public static void setUp() {
        String[] accId = new String[1];
        CompletableFuture<String> accountIdFuture = batchActions.getAccountId();
        accountIdFuture.thenAccept(accountId -> {
            System.out.println("Account ID: " + accountId);
            accId[0] =  accountId;
        });

        dockerImage = accId[0]+"."+dockerImage;

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        subnet = values.getSubnet();
        secGroup = values.getSecGroup();
        batchIAMRole = values.getBatchIAMRole();
        executionRoleARN = values.getExecutionRoleARN();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateComputeEnvironment() {
        CompletableFuture<CreateComputeEnvironmentResponse> future = batchActions.createComputeEnvironmentAsync(computeEnvironmentName, batchIAMRole, subnet, secGroup);
        CreateComputeEnvironmentResponse response = future.join();
        System.out.println("Compute Environment ARN: " + response.computeEnvironmentArn());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testGetStatus() {
        try {
            Thread.sleep(60_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        CompletableFuture<String> future = batchActions.checkComputeEnvironmentsStatus(computeEnvironmentName);
        String status = future.join();
        System.out.println("Compute Environment Status: " + status);
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testCreateJobQueue() {
        CompletableFuture<String> jobQueueFuture = batchActions.createJobQueueAsync(jobQueueName, computeEnvironmentName);
        jobQueueArn = jobQueueFuture.join();
        assertNotNull(jobQueueArn, "Job Queue ARN should not be null");
        System.out.println("Job Queue ARN: " + jobQueueArn);
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testRegisterJobDefinition() {
        jobARN = batchActions.registerJobDefinitionAsync(jobDefinitionName, executionRoleARN, dockerImage, "X86_64").join();
        assertNotNull(jobARN, "Job ARN should not be null");
        System.out.println("Job ARN: " + jobARN);
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testSubmitJob() {
        try {
            // Pause the execution for 1 minute (60,000 milliseconds)
            Thread.sleep(60_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        jobId = batchActions.submitJobAsync(jobDefinitionName, jobQueueName, jobARN).join();
        System.out.println("Job Id: " + jobId);
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testGetJobs() {
        List<JobSummary> jobs = batchActions.listJobsAsync(jobQueueName);
        jobs.forEach(job ->
            System.out.printf("Job ID: %s, Job Name: %s, Job Status: %s%n",
                job.jobId(), job.jobName(), job.status())
        );
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testJobStatus() {
        CompletableFuture<String> future = batchActions.describeJobAsync(jobId);
        String jobStatus = future.join();
        System.out.println("Job Status: " + jobStatus);
        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testRegisterJobQueue() {
        batchActions.deregisterJobDefinitionAsync(jobARN);
        batchActions.disableJobQueueAsync(jobQueueArn);
        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testDeleteJobQueue() {
        try {
            Thread.sleep(120_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        batchActions.deleteJobQueueAsync(jobQueueArn);
        System.out.println("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testDisableComputeEnvironment() {
        try {
            Thread.sleep(120_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        batchActions.disableComputeEnvironmentAsync(computeEnvironmentName);
        try {
            Thread.sleep(120_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testDeleteComputeEnvironment() {
        try {
            Thread.sleep(120_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        batchActions.deleteComputeEnvironmentAsync(computeEnvironmentName);
        System.out.println("Test 10 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/batch";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/batch (an AWS Secrets Manager secret)")
    class SecretValues {
        private String subnet;
        private String secGroup;
        private String batchIAMRole;

        private String executionRoleARN;

        public String getSubnet() {
            return subnet;
        }

        public String getSecGroup() {
            return secGroup;
        }

        public String getBatchIAMRole() {
            return batchIAMRole;
        }

        public String getExecutionRoleARN() {
            return executionRoleARN;
        }
    }
}
