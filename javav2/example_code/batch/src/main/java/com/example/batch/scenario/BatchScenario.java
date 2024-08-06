// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.batch.scenario;

// snippet-start:[batch.java2.scenario.main]
import software.amazon.awssdk.services.batch.model.BatchException;
import software.amazon.awssdk.services.batch.model.CreateComputeEnvironmentResponse;
import software.amazon.awssdk.services.batch.model.JobSummary;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class BatchScenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    // Define two stacks used in this Basics Scenario.
    private static final String STACK_NAME = "BatchStack4";

    private static final String STACK_ECR = "EcsStack";
    public static void main(String[] args) throws InterruptedException {
        BatchActions batchActions = new BatchActions();
        Scanner scanner = new Scanner(System.in);
        String computeEnvironmentName = "my-compute-environment" ;
        String jobQueueName = "my-job-queue";
        String jobDefinitionName = "my-job-definition";
        String dockerImage = "dkr.ecr.us-east-1.amazonaws.com/echo-text:echo-text";
        String subnet = "subnet-ef28c6b0" ;
        String secGroup = "sg-0d2f3836b8750d1bf" ;

        // Get an AWS Account id used to retrieve the docker image from Amazon ECR.
        String accId = batchActions.getAccountId();
        dockerImage = accId+"."+dockerImage; // Get the account Id into docker image.

        System.out.println("""
            AWS Batch is a fully managed batch processing service that dynamically provisions the required compute 
            resources for batch computing workloads. The Java V2 `BatchAsyncClient` allows 
            developers to automate the submission, monitoring, and management of batch jobs.
                        
            This scenario provides an example of setting up a compute environment, job queue and job definition, 
            and then submitting a job.
            
            Let's get started...
                        
            You have two choices:
            
            1 - Run the entire program.
            2 - Delete an existing Compute Environment (created from a previous execution of 
            this program that did not complete).
            """);

        while (true) {
            String input = scanner.nextLine();
            if (input.trim().equalsIgnoreCase("1")) {
                System.out.println("Continuing with the program...");
                System.out.println("");
                break;
            } else if (input.trim().equalsIgnoreCase("2")) {
                String jobQueueARN = batchActions.describeJobQueue(computeEnvironmentName);
                if (!jobQueueARN.isEmpty()) {
                    batchActions.disableJobQueueAsync(jobQueueARN);
                    countdown(1);
                    batchActions.deleteJobQueueAsync(jobQueueARN);
                }

                try {
                    batchActions.disableComputeEnvironmentAsync(computeEnvironmentName)
                        .exceptionally(ex -> {
                            System.err.println("Disable compute environment failed: " + ex.getMessage());
                            return null;
                        })
                        .join();
                } catch (CompletionException ex) {
                    System.err.println("Failed to disable compute environment: " + ex.getMessage());
                }
                countdown(2);
                batchActions.deleteComputeEnvironmentAsync(computeEnvironmentName);
                return;
            } else {
                // Handle invalid input.
                System.out.println("Invalid input. Please try again.");
            }
        }
        System.out.println(DASHES);

        waitForInputToContinue(scanner);
        System.out.println("Use AWS CloudFormation to create two IAM roles that are required for this scenario.");
        CloudFormationHelper.deployCloudFormationStack(STACK_NAME);
        CloudFormationHelper.deployCloudFormationStack(STACK_ECR);

        Map<String, String> stackOutputs = CloudFormationHelper.getStackOutputs(STACK_NAME);
        Map<String, String> stackOutputECR = CloudFormationHelper.getStackOutputs(STACK_ECR);
        String batchIAMRole = stackOutputs.get("BatchRoleArn");
        String executionRoleARN = stackOutputECR.get("EcsRoleArn");

        System.out.println("The IAM role needed to interact wit AWS Batch is "+batchIAMRole);
        System.out.println("The second IAM role needed to interact wit AWS ECR is "+executionRoleARN);
        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        System.out.println("1. Create a Batch compute environment");
        System.out.println("""
            A compute environment is a resource where you can run your batch jobs. 
            After creating a compute environment, you can define job queues and job definitions to submit jobs for 
            execution. 
            
            The benefit of creating a compute environment is it allows you to easily configure and manage the compute 
            resources that will be used to run your Batch jobs. By separating the compute environment from the job definitions,
            you can easily scale your compute resources up or down as needed, without having to modify your job definitions. 
            This makes it easier to manage your Batch workloads and ensures that your jobs have the necessary 
            compute resources to run efficiently.
            """);

        waitForInputToContinue(scanner);
        try {
            CompletableFuture<CreateComputeEnvironmentResponse> future = batchActions.createComputeEnvironmentAsync(computeEnvironmentName, batchIAMRole, subnet, secGroup);
            CreateComputeEnvironmentResponse response = future.join();
            System.out.println("Compute Environment ARN: " + response.computeEnvironmentArn());
        } catch (BatchException e) {
            System.err.println("A Batch exception occurred: " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.err.println("An error occurred while creating the compute environment: " + e.getMessage());
            return;
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. Check the status of the "+computeEnvironmentName +" Compute Environment.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<String> future = batchActions.checkComputeEnvironmentsStatus(computeEnvironmentName);
            String status = future.join();
            System.out.println("Compute Environment Status: " + status);

        } catch (BatchException e) {
            System.err.println("A Batch exception occurred: " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.err.println("An error occurred while checking the compute environment status: " + e.getMessage());
            return;
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. Create a job queue");
        System.out.println("""
             A job queue is an essential component that helps manage the execution of your batch jobs. 
             It acts as a buffer, where jobs are placed and then scheduled for execution based on their 
             priority and the available resources in the compute environment. 
             """);
        waitForInputToContinue(scanner);

        String jobQueueArn="";
        try {
            CompletableFuture<String> jobQueueFuture = batchActions.createJobQueueAsync(jobQueueName, computeEnvironmentName);
            jobQueueArn = jobQueueFuture.join();
            System.out.println("Job Queue ARN: " + jobQueueArn);

        } catch (BatchException e) {
            System.err.println("A Batch exception occurred: " + e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.err.println("An error occurred while creating the job queue: " + e.getMessage());
            return;
        }

        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println("4. Register a Job Definition.");
        System.out.println("""
            Registering a job in AWS Batch using the Fargate launch type ensures that all
            necessary parameters, such as the execution role, command to run, and so on
            are specified and reused across multiple job submissions.
            
             The job definition pulls a Docker image from Amazon ECR and executes the Docker image.
            """);

        waitForInputToContinue(scanner);
        String jobARN = null;
        try {
            jobARN = batchActions.registerJobDefinitionAsync(jobDefinitionName, executionRoleARN, dockerImage)
                .exceptionally(ex -> {
                    System.err.println("Register job definition failed: " + ex.getMessage());
                    return null;
                })
                .join();
            if (jobARN != null) {
                System.out.println("Job ARN: " + jobARN);
            }
        } catch (CompletionException ex) {
            System.err.println("Failed to register job definition: " + ex.getMessage());
            return;
        }
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("5. Submit an AWS Batch job from a job definition.");
        waitForInputToContinue(scanner);
        String jobId = null;
        try {
            jobId = batchActions.submitJobAsync(jobDefinitionName, jobQueueName, jobARN)
                .exceptionally(ex -> {
                    System.err.println("Submit job failed: " + ex.getMessage());
                    return null;
                })
                .join();

            System.out.println("The job id is "+jobId);

        } catch (CompletionException ex) {
            System.err.println("Failed to submit job: " + ex.getMessage());
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6. Get a list of jobs applicable to the job queue.");

        waitForInputToContinue(scanner);
        try {
            List<JobSummary> jobs =  batchActions.listJobsAsync(jobQueueName);
            jobs.forEach(job ->
                System.out.printf("Job ID: %s, Job Name: %s, Job Status: %s%n",
                    job.jobId(), job.jobName(), job.status())
            );

        } catch (CompletionException ex) {
            System.err.println("Failed to list jobs: " + ex.getMessage());
        }

        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Check the status of job "+jobId);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<String> future = batchActions.describeJobAsync(jobId);
            String jobStatus = future.join();
            System.out.println("Job Status: " + jobStatus);

        } catch (BatchException e) {
            System.err.println("A Batch exception occurred: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("An error occurred while describing the job: " + e.getMessage());
        }

        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println("8. Delete Batch resources");
        System.out.println(
            """
            When deleting an AWS Batch compute environment, it does not happen instantaneously. 
            There is typically a delay, similar to some other AWS resources. 
            AWS Batch starts the deletion process.
            """);
        System.out.println("Would you like to delete the AWS Batch resources such as the compute environment? (y/n)");
        String delAns = scanner.nextLine().trim();
        if (delAns.equalsIgnoreCase("y")) {
            System.out.println("You selected to delete the AWS ECR resources.");
            System.out.println("First, we will deregister the Job Definition.");
            waitForInputToContinue(scanner);
            try {
                batchActions.deregisterJobDefinitionAsync(jobARN)
                    .exceptionally(ex -> {
                        System.err.println("Deregister job definition failed: " + ex.getMessage());
                        return null;
                    })
                    .join();
            } catch (CompletionException ex) {
                System.err.println("Failed to deregister job definition: " + ex.getMessage());
            }

            System.out.println("Second, we will disable and then delete the Job Queue.");
            waitForInputToContinue(scanner);
            try {
                batchActions.disableJobQueueAsync(jobQueueArn)
                    .exceptionally(ex -> {
                        System.err.println("Disable job queue failed: " + ex.getMessage());
                        return null;
                    })
                    .join();
            } catch (CompletionException ex) {
                System.err.println("Failed to disable job queue: " + ex.getMessage());
            }

            // Wait until the job queue is disabled.
            batchActions.waitForJobQueueToBeDisabled(jobQueueArn);
            waitForInputToContinue(scanner);
            batchActions.deleteJobQueueAsync(jobQueueArn);
            System.out.println("Lets wait 2 mins for the job queue to be deleted");
            countdown(2);
            waitForInputToContinue(scanner);

            System.out.println("Third, we will delete the Compute Environment.");
            waitForInputToContinue(scanner);
            try {
                batchActions.disableComputeEnvironmentAsync(computeEnvironmentName)
                    .exceptionally(ex -> {
                        System.err.println("Disable compute environment failed: " + ex.getMessage());
                        return null;
                    })
                    .join();
            } catch (CompletionException ex) {
                System.err.println("Failed to disable compute environment: " + ex.getMessage());
            }

            batchActions.checkComputeEnvironmentsStatus(computeEnvironmentName).thenAccept(state -> {
                System.out.println("Current State: " + state);
            }).join();

            System.out.println("Lets wait 1 min for the compute environment to be deleted");
            countdown(1);

            try {
                batchActions.deleteComputeEnvironmentAsync(computeEnvironmentName);

            } catch (CompletionException ex) {
                System.err.println("Failed to delete compute environment: " + ex.getMessage());
            }
            waitForInputToContinue(scanner);
            CloudFormationHelper.destroyCloudFormationStack(STACK_NAME);
            CloudFormationHelper.destroyCloudFormationStack(STACK_ECR);
        }

        System.out.println(DASHES);
        System.out.println("This concludes the AWS Batch SDK scenario");
        System.out.println(DASHES);
    }

    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            System.out.println("");
            System.out.println("Enter 'c' followed by <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                System.out.println("Continuing with the program...");
                System.out.println("");
                break;
            } else {
                // Handle invalid input.
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    public static void countdown(int minutes) throws InterruptedException {
        int seconds = 0;
        for (int i = minutes * 60 + seconds; i >= 0; i--) {
            int displayMinutes = i / 60;
            int displaySeconds = i % 60;
            System.out.print(String.format("\r%02d:%02d", displayMinutes, displaySeconds));
            Thread.sleep(1000); // Wait for 1 second
        }
        System.out.println("Countdown complete!");
    }
}
// snippet-end:[batch.java2.scenario.main]