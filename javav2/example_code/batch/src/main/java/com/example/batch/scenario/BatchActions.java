// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.batch.scenario;
// snippet-start:[batch.java2.actions.main]
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
import software.amazon.awssdk.services.batch.model.DescribeComputeEnvironmentsRequest;
import software.amazon.awssdk.services.batch.model.JobDefinitionType;
import software.amazon.awssdk.services.batch.model.RegisterJobDefinitionRequest;
import software.amazon.awssdk.services.batch.model.ResourceRequirement;
import software.amazon.awssdk.services.batch.model.SubmitJobRequest;
import software.amazon.awssdk.services.batch.model.CreateJobQueueResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.batch.model.*;
import software.amazon.awssdk.services.batch.paginators.ListJobsPublisher;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;

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

    // snippet-start:[batch.java2.create_compute.main]
    /**
     * Asynchronously creates a new compute environment in AWS Batch.
     *
     * @param computeEnvironmentName the name of the compute environment to create
     * @param batchIAMRole the IAM role to be used by the compute environment
     * @param subnet the subnet ID to be used for the compute environment
     * @param secGroup the security group ID to be used for the compute environment
     * @return a {@link CompletableFuture} representing the asynchronous operation, which will complete with the
     *         {@link CreateComputeEnvironmentResponse} when the compute environment has been created
     * @throws BatchException if there is an error creating the compute environment
     * @throws RuntimeException if there is an unexpected error during the operation
     */
    public CompletableFuture<CreateComputeEnvironmentResponse> createComputeEnvironmentAsync(
        String computeEnvironmentName, String batchIAMRole, String subnet, String secGroup) {
        CreateComputeEnvironmentRequest environmentRequest = CreateComputeEnvironmentRequest.builder()
            .computeEnvironmentName(computeEnvironmentName)
            .type(CEType.MANAGED)
            .state(CEState.ENABLED)
            .computeResources(ComputeResource.builder()
                .type(CRType.FARGATE)
                .maxvCpus(256)
                .subnets(Collections.singletonList(subnet))
                .securityGroupIds(Collections.singletonList(secGroup))
                .build())
            .serviceRole(batchIAMRole)
            .build();

        CompletableFuture<CreateComputeEnvironmentResponse> response = getAsyncClient().createComputeEnvironment(environmentRequest);
        response.whenComplete((resp, ex) -> {
            if (resp != null) {
                System.out.println("Compute environment created successfully.");
            } else {
                if (ex.getCause() instanceof BatchException) {
                    throw (BatchException) ex.getCause();
                } else {
                    String errorMessage = "Unexpected error occurred: " + ex.getMessage();
                    throw new RuntimeException(errorMessage, ex);
                }
            }
        });

        return response;
    }
    // snippet-end:[batch.java2.create_compute.main]

    public void deleteComputeEnvironmentAsync(String computeEnvironmentName) {
        DeleteComputeEnvironmentRequest deleteComputeEnvironment = DeleteComputeEnvironmentRequest.builder()
            .computeEnvironment(computeEnvironmentName)
            .build();

        getAsyncClient().deleteComputeEnvironment(deleteComputeEnvironment)
            .whenComplete((response, ex) -> {
                if (ex == null) {
                    System.out.println("Compute environment was successfully deleted");
                } else {
                    Throwable cause = ex.getCause();
                    if (cause instanceof BatchException) {
                        throw new RuntimeException(cause);
                    } else {
                        throw new RuntimeException("Unexpected error: " + cause.getMessage(), cause);
                    }
                }
            })
            .join();
    }

    // snippet-start:[batch.java2.check.status.main]
    /**
     * Checks the status of the specified compute environment.
     *
     * @param computeEnvironmentName the name of the compute environment to check
     * @return a CompletableFuture containing the status of the compute environment, or "ERROR" if an exception occurs
     */
    public CompletableFuture<String> checkComputeEnvironmentsStatus(String computeEnvironmentName) {
        if (computeEnvironmentName == null || computeEnvironmentName.isEmpty()) {
            throw new IllegalArgumentException("Compute environment name cannot be null or empty");
        }

        DescribeComputeEnvironmentsRequest environmentsRequest = DescribeComputeEnvironmentsRequest.builder()
            .computeEnvironments(computeEnvironmentName)
            .build();

        CompletableFuture<DescribeComputeEnvironmentsResponse> response = getAsyncClient().describeComputeEnvironments(environmentsRequest);
        response.whenComplete((resp, ex) -> {
            if (resp != null) {
                System.out.println("Compute environment status retrieved successfully.");
            } else {
                if (ex.getCause() instanceof BatchException) {
                    throw (BatchException) ex.getCause();
                } else {
                    String errorMessage = "Unexpected error occurred: " + ex.getMessage();
                    throw new RuntimeException(errorMessage, ex);
                }
            }
        });

        return response.thenApply(resp -> resp.computeEnvironments().stream()
            .map(env -> env.statusAsString())
            .findFirst()
            .orElse("UNKNOWN"));
    }
    // snippet-end:[batch.java2.check.status.main]

    // snippet-start:[batch.java2.create.job.queue.main]
    /**
     * Creates a job queue asynchronously.
     *
     * @param jobQueueName the name of the job queue to create
     * @param computeEnvironmentName the name of the compute environment to associate with the job queue
     * @return a CompletableFuture that completes with the Amazon Resource Name (ARN) of the job queue
     */
    public CompletableFuture<String> createJobQueueAsync(String jobQueueName, String computeEnvironmentName) {
        if (jobQueueName == null || jobQueueName.isEmpty()) {
            throw new IllegalArgumentException("Job queue name cannot be null or empty");
        }
        if (computeEnvironmentName == null || computeEnvironmentName.isEmpty()) {
            throw new IllegalArgumentException("Compute environment name cannot be null or empty");
        }

        CreateJobQueueRequest request = CreateJobQueueRequest.builder()
            .jobQueueName(jobQueueName)
            .priority(1)
            .computeEnvironmentOrder(ComputeEnvironmentOrder.builder()
                .computeEnvironment(computeEnvironmentName)
                .order(1)
                .build())
            .build();

        CompletableFuture<CreateJobQueueResponse> response = getAsyncClient().createJobQueue(request);
        response.whenComplete((resp, ex) -> {
            if (resp != null) {
                System.out.println("Job queue created successfully.");
            } else {
                if (ex.getCause() instanceof BatchException) {
                    throw (BatchException) ex.getCause();
                } else {
                    String errorMessage = "Unexpected error occurred: " + ex.getMessage();
                    throw new RuntimeException(errorMessage, ex);
                }
            }
        });

        return response.thenApply(CreateJobQueueResponse::jobQueueArn);
    }
    // snippet-end:[batch.java2.create.job.queue.main]

    /**
     * Asynchronously lists the jobs in the specified job queue with the given job status.
     *
     * @param jobQueue the name of the job queue to list jobs from
     * @return a List<JobSummary> that contains the jobs that succeeded
     */
    public List<JobSummary> listJobsAsync(String jobQueue) {
        if (jobQueue == null || jobQueue.isEmpty()) {
            throw new IllegalArgumentException("Job queue cannot be null or empty");
        }

        ListJobsRequest listJobsRequest = ListJobsRequest.builder()
            .jobQueue(jobQueue)
            .jobStatus(JobStatus.SUCCEEDED)  // Filter jobs by status
            .build();

        List<JobSummary> jobSummaries = new ArrayList<>();
        ListJobsPublisher listJobsPaginator = getAsyncClient().listJobsPaginator(listJobsRequest);
        CompletableFuture<Void> future = listJobsPaginator.subscribe(response -> {
            jobSummaries.addAll(response.jobSummaryList());
        });
        future.join();
        return jobSummaries;
    }

    // snippet-start:[batch.java2.register.job.main]
    /**
     * Registers a new job definition asynchronously in AWS Batch.
     * <p>
     * When using Fargate as the compute environment, it is crucial to set the
     * {@link NetworkConfiguration} with {@link AssignPublicIp#ENABLED} to
     * ensure proper networking configuration for the Fargate tasks. This
     * allows the tasks to communicate with external services, access the
     * internet, or communicate within a VPC.
     *
     * @param jobDefinitionName the name of the job definition to be registered
     * @param executionRoleARN the ARN (Amazon Resource Name) of the execution role
     *                         that provides permissions for the containers in the job
     * @return a CompletableFuture that completes with the ARN of the registered
     *         job definition upon successful execution, or completes exceptionally with
     *         an error if the registration fails
     */
    public CompletableFuture<String> registerJobDefinitionAsync(String jobDefinitionName, String executionRoleARN, String image) {
        NetworkConfiguration networkConfiguration = NetworkConfiguration.builder()
            .assignPublicIp(AssignPublicIp.ENABLED)
            .build();

        ContainerProperties containerProperties = ContainerProperties.builder()
            .image(image)
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
            .networkConfiguration(networkConfiguration)
            .build();

        RegisterJobDefinitionRequest request = RegisterJobDefinitionRequest.builder()
            .jobDefinitionName(jobDefinitionName)
            .type(JobDefinitionType.CONTAINER)
            .containerProperties(containerProperties)
            .platformCapabilities(PlatformCapability.FARGATE)
            .build();

        CompletableFuture<String> future = new CompletableFuture<>();
        getAsyncClient().registerJobDefinition(request)
            .thenApply(RegisterJobDefinitionResponse::jobDefinitionArn)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    future.completeExceptionally(ex);
                } else {
                    future.complete(result);
                }
            });

        return future;
    }
    // snippet-end:[batch.java2.register.job.main]

    // snippet-start:[batch.java2.deregister.job.main]
    /**
     * Deregisters a job definition asynchronously.
     *
     * @param jobDefinition the name of the job definition to be deregistered
     * @return a CompletableFuture that completes when the job definition has been deregistered
     *         or an exception has occurred
     */
    public CompletableFuture<Void> deregisterJobDefinitionAsync(String jobDefinition) {
        DeregisterJobDefinitionRequest jobDefinitionRequest = DeregisterJobDefinitionRequest.builder()
            .jobDefinition(jobDefinition)
            .build();

        CompletableFuture<Void> future = new CompletableFuture<>();
        getAsyncClient().deregisterJobDefinition(jobDefinitionRequest)
            .thenAccept(response -> {
                System.out.println(jobDefinition + " was successfully deregistered");
                future.complete(null);
            })
            .exceptionally(ex -> {
                System.err.println("Failed to deregister job definition: " + ex.getMessage());
                future.completeExceptionally(ex);
                return null;
            });

        return future;
    }
    // snippet-start:[batch.java2.deregister.job.main]

    // snippet-start:[batch.java2.disable.job.queue.main]
    /**
     * Disables the specified job queue asynchronously.
     *
     * @param jobQueueArn the Amazon Resource Name (ARN) of the job queue to be disabled
     * @return a {@link CompletableFuture} that completes when the job queue update operation is complete,
     *         or completes exceptionally if an error occurs during the operation
     */
    public CompletableFuture<Void> disableJobQueueAsync(String jobQueueArn) {
        UpdateJobQueueRequest updateRequest = UpdateJobQueueRequest.builder()
            .jobQueue(jobQueueArn)
            .state(JQState.DISABLED)
            .build();

        CompletableFuture<Void> future = new CompletableFuture<>();
        getAsyncClient().updateJobQueue(updateRequest)
            .thenAccept(updateResponse -> {
                System.out.println("Job queue update initiated: " + updateResponse);
                future.complete(null); // Complete the CompletableFuture on successful execution
            })
            .exceptionally(ex -> {
                System.err.println("Failed to update job queue: " + ex.getMessage());
                future.completeExceptionally(ex); // Complete the CompletableFuture exceptionally on error
                return null;
            });

        return future;
    }
    // snippet-end:[batch.java2.disable.job.queue.main]

    // snippet-start:[batch.java2.delete.job.queue.main]
    /**
     * Deletes a Batch job queue asynchronously.
     *
     * @param jobQueueArn The Amazon Resource Name (ARN) of the job queue to delete.
     * @return A CompletableFuture that represents the asynchronous deletion of the job queue.
     *         The future completes when the job queue has been successfully deleted or if an error occurs.
     *         If successful, the future will be completed with a {@code Void} value.
     *         If an error occurs, the future will be completed exceptionally with the thrown exception.
     */
    public CompletableFuture<Void> deleteJobQueueAsync(String jobQueueArn) {
        DeleteJobQueueRequest deleteRequest = DeleteJobQueueRequest.builder()
            .jobQueue(jobQueueArn)
            .build();

        return getAsyncClient().deleteJobQueue(deleteRequest)
            .thenAccept(deleteResponse -> {
                System.out.println("Job queue deleted: " + deleteResponse);
            })
            .exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
    }
    // snippet-end:[batch.java2.delete.job.queue.main]

    // snippet-start:[batch.java2.describe.job.queue.main]
    public String describeJobQueue(String computeEnvironmentName) {
        BatchClient batchClient = BatchClient.builder()
            .region(Region.US_EAST_1)
            .build();

        // Create a DescribeJobQueuesRequest.
        DescribeJobQueuesRequest describeJobQueuesRequest = DescribeJobQueuesRequest.builder()
            .build();

        // Describe the job queues
        DescribeJobQueuesResponse describeJobQueuesResponse = batchClient.describeJobQueues(describeJobQueuesRequest);
        String jobQueueARN = "";
        for (JobQueueDetail jobQueueDetail : describeJobQueuesResponse.jobQueues()) {
            for (ComputeEnvironmentOrder computeEnvironmentOrder : jobQueueDetail.computeEnvironmentOrder()) {
                String computeEnvironment = computeEnvironmentOrder.computeEnvironment();
                String name = getComputeEnvironmentName(computeEnvironment);
                if (name.equals(computeEnvironmentName)) {
                    jobQueueARN = jobQueueDetail.jobQueueArn();
                    System.out.println("Job queue ARN associated with the compute environment: " + jobQueueARN);
                }
            }
        }
        return jobQueueARN;
    }
    // snippet-end:[batch.java2.describe.job.queue.main]

    // snippet-start:[batch.java2.disable.compute.environment.main]
    /**
     * Disables the specified compute environment asynchronously.
     *
     * @param computeEnvironmentName the name of the compute environment to disable
     * @return a CompletableFuture that completes when the compute environment is disabled
     * @throws Exception if an error occurs while disabling the compute environment
     */
    public CompletableFuture<Void> disableComputeEnvironmentAsync(String computeEnvironmentName) {
        UpdateComputeEnvironmentRequest updateRequest = UpdateComputeEnvironmentRequest.builder()
            .computeEnvironment(computeEnvironmentName)
            .state(CEState.DISABLED)
            .build();

        CompletableFuture<Void> future = new CompletableFuture<>();
        getAsyncClient().updateComputeEnvironment(updateRequest)
            .thenAccept(updateResponse -> {
                System.out.println("Compute environment disabled: " + updateResponse.computeEnvironmentName());
                future.complete(null);
            })
            .exceptionally(ex -> {
                System.err.println("Failed to disable compute environment: " + ex.getMessage());
                future.completeExceptionally(ex);
                return null;
            });

        return future;
    }
    // snippet-end:[batch.java2.describe.job.queue.main]

    // snippet-start:[batch.java2.submit.job.main]
    /**
     * Submits a job asynchronously to the AWS Batch service.
     *
     * @param jobDefinitionName the name of the job definition to use
     * @param jobQueueName the name of the job queue to submit the job to
     * @param jobARN the Amazon Resource Name (ARN) of the job definition
     * @return a CompletableFuture that, when completed, contains the job ID of the submitted job
     */
    public CompletableFuture<String> submitJobAsync(String jobDefinitionName, String jobQueueName, String jobARN) {
        SubmitJobRequest jobRequest = SubmitJobRequest.builder()
            .jobDefinition(jobARN)
            .jobName(jobDefinitionName)
            .jobQueue(jobQueueName)
            .build();

        CompletableFuture<SubmitJobResponse> responseFuture = getAsyncClient().submitJob(jobRequest);
        return responseFuture.thenApply(response -> {
            String jobId = response.jobId();
            System.out.println("Job submitted successfully. Job ID: " + jobId);
            return jobId;
        }).exceptionally(ex -> {
            if (ex.getCause() instanceof BatchException) {
                throw (BatchException) ex.getCause();
            } else {
                String errorMessage = "Unexpected error occurred: " + ex.getMessage();
                throw new RuntimeException(errorMessage, ex);
            }
        });
    }
    // snippet-end:[batch.java2.submit.job.main]

    // snippet-start:[batch.java2.retrieve.job.main]
    /**
     * Asynchronously retrieves the status of a specific job.
     *
     * @param jobId the ID of the job to retrieve the status for
     * @return a CompletableFuture that completes with the job status
     */
    public CompletableFuture<String> describeJobAsync(String jobId) {
        DescribeJobsRequest describeJobsRequest = DescribeJobsRequest.builder()
            .jobs(jobId)
            .build();

        CompletableFuture<String> future = new CompletableFuture<>();
        getAsyncClient().describeJobs(describeJobsRequest)
            .whenComplete((response, ex) -> {
                if (response != null) {
                    JobDetail jobDetail = response.jobs().get(0);
                    String jobStatus = String.valueOf(jobDetail.status());
                    System.out.println("Job status retrieved successfully. Status: " + jobStatus);
                    future.complete(jobStatus);
                } else {
                    // Handle the exception
                    if (ex.getCause() instanceof BatchException) {
                        future.completeExceptionally((BatchException) ex.getCause());
                    } else {
                        String errorMessage = "Unexpected error occurred: " + ex.getMessage();
                        future.completeExceptionally(new RuntimeException(errorMessage, ex));
                    }
                }
            });

        return future;
    }
    // snippet-start:[batch.java2.retrieve.job.main]

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
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted while waiting for job queue to be disabled", e);
                }
            }
        }
    }


    private static String getComputeEnvironmentName(String computeEnvironment) {
        String[] parts = computeEnvironment.split("/");
        if (parts.length == 2) {
            return parts[1];
        }
        return null;
    }

    public String getAccountId() {
        StsClient stsClient = StsClient.builder()
            .region(Region.US_EAST_1)
            .build();

        GetCallerIdentityResponse callerIdentityResponse = stsClient.getCallerIdentity();
        return callerIdentityResponse.account();
    }
}
// snippet-end:[batch.java2.actions.main]



