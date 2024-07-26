// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.batch.scenario;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.batch.BatchAsyncClient;
import software.amazon.awssdk.services.batch.BatchClient;
import software.amazon.awssdk.services.batch.model.BatchException;
import software.amazon.awssdk.services.batch.model.ComputeResource;
import software.amazon.awssdk.services.batch.model.ContainerProperties;
import software.amazon.awssdk.services.batch.model.CreateComputeEnvironmentRequest;
import software.amazon.awssdk.services.batch.model.CreateComputeEnvironmentResponse;
import software.amazon.awssdk.services.batch.model.CreateJobQueueRequest;
import software.amazon.awssdk.services.batch.model.DeleteComputeEnvironmentRequest;
import software.amazon.awssdk.services.batch.model.DeleteComputeEnvironmentResponse;
import software.amazon.awssdk.services.batch.model.DescribeComputeEnvironmentsRequest;
import software.amazon.awssdk.services.batch.model.JobDefinitionType;
import software.amazon.awssdk.services.batch.model.RegisterJobDefinitionRequest;
import software.amazon.awssdk.services.batch.model.RegisterJobDefinitionResponse;
import software.amazon.awssdk.services.batch.model.ResourceRequirement;
import software.amazon.awssdk.services.batch.model.SubmitJobRequest;
import software.amazon.awssdk.services.batch.model.SubmitJobResponse;
import software.amazon.awssdk.services.batch.model.CreateJobQueueResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.batch.model.*;

public class BatchActions {
    private static BatchAsyncClient batchClient;

    private static BatchAsyncClient getAsyncClient() {
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
            .maxConcurrency(100)
            .connectionTimeout(Duration.ofSeconds(60))
            .readTimeout(Duration.ofSeconds(60))
            .writeTimeout(Duration.ofSeconds(60))
            .build();

        ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
            .apiCallTimeout(Duration.ofMinutes(2))
            .apiCallAttemptTimeout(Duration.ofSeconds(90))
            .retryPolicy(RetryPolicy.builder()
                .numRetries(3)
                .build())
            .build();

        if (batchClient == null) {
            batchClient = BatchAsyncClient.builder()
                .region(Region.US_EAST_1)
                .httpClient(httpClient)
                .overrideConfiguration(overrideConfig)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        }
        return batchClient;
    }

    public void createComputeEnvironment(String computeEnvironmentName, String batchIAMRole) {
        CreateComputeEnvironmentRequest environmentRequest = CreateComputeEnvironmentRequest.builder()
            .computeEnvironmentName(computeEnvironmentName)
            .type(CEType.MANAGED)
            .state(CEState.ENABLED)
            .computeResources(ComputeResource.builder()
                .type(CRType.FARGATE)
                .maxvCpus(256)
                .subnets(Arrays.asList("subnet-ef28c6b0"))
                .securityGroupIds(Arrays.asList("sg-0d2f3836b8750d1bf"))
                .build())
            .serviceRole(batchIAMRole)
            .build();

        CompletableFuture<CreateComputeEnvironmentResponse> future = getAsyncClient().createComputeEnvironment(environmentRequest);
        future.whenComplete((createComputeEnvironmentResponse, ex) -> {
            if (createComputeEnvironmentResponse != null) {
                System.out.println("Compute environment created: " + createComputeEnvironmentResponse.computeEnvironmentArn());
            } else {
                Throwable cause = ex.getCause();
                if (cause instanceof BatchException) {
                    throw (BatchException) cause;
                } else {
                    throw new RuntimeException("Unexpected error: " + cause.getMessage(), cause);
                }
            }
        });

        future.join();
    }

    public void deleteComputeEnvironment(String computeEnvironmentName) {
        DeleteComputeEnvironmentRequest deleteComputeEnvironment = DeleteComputeEnvironmentRequest.builder()
            .computeEnvironment(computeEnvironmentName)
            .build();

        CompletableFuture<DeleteComputeEnvironmentResponse> future = getAsyncClient().deleteComputeEnvironment(deleteComputeEnvironment);
        future.whenComplete((deleteComputeEnvironmentResponse, ex) -> {
            if (deleteComputeEnvironmentResponse != null) {
                System.out.println("Compute environment was successfully deleted");
            } else {
                Throwable cause = ex.getCause();
                if (cause instanceof BatchException) {
                    throw (BatchException) cause;
                } else {
                    throw new RuntimeException("Unexpected error: " + cause.getMessage(), cause);
                }
            }
        });

        future.join();
    }

    public static CompletableFuture<String> checkComputeEnvironmentsStatus(String computeEnvironmentName) {
        DescribeComputeEnvironmentsRequest environmentsRequest = DescribeComputeEnvironmentsRequest.builder()
            .computeEnvironments(computeEnvironmentName)
            .build();

        return getAsyncClient().describeComputeEnvironments(environmentsRequest)
            .thenApply(response -> {
                String status = response.computeEnvironments().stream()
                    .map(env -> env.statusAsString())
                    .findFirst()
                    .orElse("UNKNOWN");

                return status;
            })
            .exceptionally(ex -> {
                ex.printStackTrace();
                return "ERROR";
            });
    }

    public static CompletableFuture<String> checkComputeEnvironmentsState(String computeEnvironmentName) {
        DescribeComputeEnvironmentsRequest environmentsRequest = DescribeComputeEnvironmentsRequest.builder()
            .computeEnvironments(computeEnvironmentName)
            .build();

        return getAsyncClient().describeComputeEnvironments(environmentsRequest)
            .thenApply(response -> {
                String status = response.computeEnvironments().stream()
                    .map(env -> env.stateAsString())
                    .findFirst()
                    .orElse("UNKNOWN");

                return status;
            })
            .exceptionally(ex -> {
                ex.printStackTrace();
                return "ERROR";
            });
    }


    public CompletableFuture<String> createJobQueueAsync(String jobQueueName, String computeEnvironmentName) {
        CreateJobQueueRequest request = CreateJobQueueRequest.builder()
            .jobQueueName(jobQueueName)
            .priority(1)
            .computeEnvironmentOrder(ComputeEnvironmentOrder.builder()
                .computeEnvironment(computeEnvironmentName)
                .order(1)
                .build())
            .build();

        CompletableFuture<String> future = getAsyncClient().createJobQueue(request)
            .thenApply(CreateJobQueueResponse::jobQueueArn)
            .thenApply(jobQueueArn -> {
                return jobQueueArn;
            });

        future.join();
        return future;
    }

    public String submitFargateJob(String jobName, String jobQueueName, String jobDefinitionName, String executionRoleArn) {
        BatchClient client = BatchClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Define container properties
        ContainerProperties containerProperties = ContainerProperties.builder()
            .image("814548047983.dkr.ecr.us-east-1.amazonaws.com/echo-text:latest")
            .vcpus(1)
            .memory(512)
            .command("echo", "Executing Batch job with input data: ${inputData}")
            .executionRoleArn(executionRoleArn)
            .fargatePlatformConfiguration(FargatePlatformConfiguration.builder()
                .platformVersion("LATEST")
                .build())
            .build();

        // Create job definition request
        RegisterJobDefinitionRequest jobDefinitionRequest = RegisterJobDefinitionRequest.builder()
            .jobDefinitionName(jobDefinitionName)
            .type(JobDefinitionType.CONTAINER)
            .containerProperties(containerProperties)
            .build();

        // Register job definition
        RegisterJobDefinitionResponse jobDefinitionResponse = client.registerJobDefinition(jobDefinitionRequest);
        String jobDefinitionArn = jobDefinitionResponse.jobDefinitionArn();
        System.out.println("Job definition created: " + jobDefinitionArn);

        // Create container overrides
        ContainerOverrides containerOverrides = ContainerOverrides.builder()
            .command("echo", "Executing Batch job with input data: ${inputData}")
            .build();

        // Submit job request
        SubmitJobRequest submitJobRequest = SubmitJobRequest.builder()
            .jobName(jobName)
            .jobQueue(jobQueueName)
            .jobDefinition(jobDefinitionArn)
            .containerOverrides(containerOverrides)
            .build();

        SubmitJobResponse submitJobResponse = client.submitJob(submitJobRequest);
        String jobId = submitJobResponse.jobId();
        System.out.println("Job submitted with ID: " + jobId);
        return jobId;
    }

    public void ListJobs(String jobDefinitionName, String jobQueueName){
        BatchClient client = BatchClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();


        // Describe the job definitions
        ListJobsRequest jobRequest = ListJobsRequest.builder()
            .jobQueue(jobQueueName)
            .jobStatus("STARTING")
            .build();

        ListJobsResponse result = client.listJobs(jobRequest);
        List<JobSummary> jobs = result.jobSummaryList();
        System.out.println("Batch jobs applicable to the job queue: " + jobQueueName);
        for (JobSummary jobSummary : jobs) {
              System.out.println("Job Definition Name: " + jobSummary.jobName());
            System.out.println("Job Id: " + jobSummary.jobId());
        }
    }

    public String registerJobDefinition(String jobDefinitionName, String executionRoleARN) {
        BatchClient client = BatchClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        ContainerProperties containerProperties = ContainerProperties.builder()
            .image("814548047983.dkr.ecr.us-east-1.amazonaws.com/echo-text:echo-text")
            .executionRoleArn(executionRoleARN)
            .command(Arrays.asList("echo", "Hello World"))
            .resourceRequirements(
                Arrays.asList(
                    ResourceRequirement.builder()
                        .type(ResourceType.VCPU)
                        .value("1")
                        .build(),
                    ResourceRequirement.builder()
                        .type(ResourceType.MEMORY)
                        .value("2048")
                        .build()
                )
            )
            .build();


        RegisterJobDefinitionRequest request = RegisterJobDefinitionRequest.builder()
            .jobDefinitionName(jobDefinitionName)
            .type(JobDefinitionType.CONTAINER)
            .containerProperties(containerProperties)
            .platformCapabilities(PlatformCapability.FARGATE)
            .build();

        RegisterJobDefinitionResponse response = client.registerJobDefinition(request);
        String jobDefinitionArn = response.jobDefinitionArn();
        System.out.println("Job definition registered: " + jobDefinitionArn);
        return jobDefinitionArn;
    }

    public void deregisterJobDefinition(String jobDefinition) {
        BatchClient client = BatchClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        DeregisterJobDefinitionRequest jobDefinitionRequest = DeregisterJobDefinitionRequest.builder()
            .jobDefinition(jobDefinition)
            .build();

        DeregisterJobDefinitionResponse response = client.deregisterJobDefinition(jobDefinitionRequest);
        System.out.println(jobDefinition + " was successfully deregistered");
    }

    public static void disableJobQueue(String jobQueueArn) {
        BatchClient client = BatchClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Disable the job queue
        UpdateJobQueueRequest updateRequest = UpdateJobQueueRequest.builder()
            .jobQueue(jobQueueArn)
            .state(JQState.DISABLED)
            .build();

        UpdateJobQueueResponse updateResponse = client.updateJobQueue(updateRequest);
        System.out.println("Job queue update initiated: " + updateResponse);
    }

    public void deleteJobQueue(String jobQueueArn) {
        BatchClient client = BatchClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        DeleteJobQueueRequest deleteRequest = DeleteJobQueueRequest.builder()
            .jobQueue(jobQueueArn)
            .build();

        DeleteJobQueueResponse deleteResponse = client.deleteJobQueue(deleteRequest);
        System.out.println("Job queue deleted: " + deleteResponse);
    }

    public void waitForJobQueueToBeDisabled(String jobQueueArn) {
        BatchClient client = BatchClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        boolean isDisabled = false;
        while (!isDisabled) {
            DescribeJobQueuesRequest describeRequest = DescribeJobQueuesRequest.builder()
                .jobQueues(jobQueueArn)
                .build();

            DescribeJobQueuesResponse describeResponse = client.describeJobQueues(describeRequest);
            for (JobQueueDetail jobQueue : describeResponse.jobQueues()) {
                if (jobQueue.jobQueueArn().equals(jobQueueArn) && jobQueue.state() == JQState.DISABLED) {
                    isDisabled = true;
                    break;
                }
            }

            if (!isDisabled) {
                try {
                    System.out.println("Waiting for job queue to be disabled...");
                    Thread.sleep(5000); // Wait for 5 seconds before checking again
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted while waiting for job queue to be disabled", e);
                }
            }
        }
    }

    public static void disableComputeEnvironment(String computeEnvironmentName) {
        BatchClient client = BatchClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        UpdateComputeEnvironmentRequest updateRequest = UpdateComputeEnvironmentRequest.builder()
            .computeEnvironment(computeEnvironmentName)
            .state(CEState.DISABLED)
            .build();

        UpdateComputeEnvironmentResponse updateResponse = client.updateComputeEnvironment(updateRequest);
        System.out.println("Compute environment disabled: " + updateResponse.computeEnvironmentName());
    }

    public String submitJob(String jobDefinitionName, String jobQueueName, String jobARN) {
        BatchClient client = BatchClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        SubmitJobRequest jobRequest = SubmitJobRequest.builder()
            .jobDefinition(jobARN)
            .jobName(jobDefinitionName)
            .jobQueue(jobQueueName)
            .build();

        SubmitJobResponse response = client.submitJob(jobRequest);
        String jobId = response.jobId();
        System.out.println("Submitted job with ID: " + jobId);
        return jobId;
    }

    public void describeJob(String jobId) throws InterruptedException {
        BatchClient client = BatchClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        DescribeJobsRequest describeJobsRequest = DescribeJobsRequest.builder()
            .jobs(jobId)
            .build();

       // while (true) {
            DescribeJobsResponse describeJobsResult = client.describeJobs(describeJobsRequest);

            // Get the job details
            JobDetail jobDetail = describeJobsResult.jobs().get(0);

            // Check the job status
            String jobStatus = String.valueOf(jobDetail.status());
            System.out.println("Job status: " + jobStatus);

            /*
            // Check if the job was successful
            if (jobStatus.equals("SUCCEEDED")) {
                System.out.println("Job was successful!");
                break;
            } else {
                Thread.sleep(1000);
            }
             */


       // }
    }

    public String describeJobQueue(String computeEnvironmentName) {
        // Create a Batch client
        BatchClient batchClient = BatchClient.builder()
            .region(Region.US_EAST_1)
            .build();

        // Create a DescribeJobQueuesRequest.
        DescribeJobQueuesRequest describeJobQueuesRequest = DescribeJobQueuesRequest.builder()
            .build();

        // Describe the job queues
        DescribeJobQueuesResponse describeJobQueuesResponse = batchClient.describeJobQueues(describeJobQueuesRequest);
        String jobQueueARN = "";
        // Iterate through the job queues and find the one associated with the compute environment

        for (JobQueueDetail jobQueueDetail : describeJobQueuesResponse.jobQueues()) {
            for (ComputeEnvironmentOrder computeEnvironmentOrder : jobQueueDetail.computeEnvironmentOrder()) {
                String computeEnvironment = computeEnvironmentOrder.computeEnvironment();
                String name = getComputeEnvironmentName(computeEnvironment);
                if (name.equals(computeEnvironmentName)) {
                    // Found the job queue associated with the compute environment
                    jobQueueARN = jobQueueDetail.jobQueueArn();
                    System.out.println("Job queue ARN associated with the compute environment: " + jobQueueARN);
                    // You can now delete the job queue before deleting the compute environment
                    // ...
                }
            }
       }
        return jobQueueARN;
    }

    private static String getComputeEnvironmentName(String computeEnvironment) {
        String[] parts = computeEnvironment.split("/");
        if (parts.length == 2) {
            return parts[1];
        }
        return null;
    }
}




