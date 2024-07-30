// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.batch.scenario;

import java.util.Map;
import java.util.Scanner;
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
            Amazon Batch is a fully managed batch processing service that enables developers, scientists, and engineers to run batch 
            computing workloads of any scale. It dynamically provisions the optimal quantity and type of compute resources 
            (e.g., CPU or memory-optimized instances) based on the volume and specific resource requirements of the batch jobs submitted.
                        
            The Java V2 SDK allows you to interact with various AWS services programmatically. The `BatchAsyncClient` interface 
            in the Java V2 SDK enables developers to automate the submission, monitoring, and management of batch jobs on the Amazon Batch service.
                        
            In this scenario, we'll explore how to use the Java V2 SDK to interact with the Amazon Batch service, including:
                        
            1. **Create an AWS Batch Job Definition**: Define a job definition that is used to run your Docker container on Fargate. This job definition will include the Docker image location (ECR repository), the container properties, and the execution role that grants the necessary permissions.
                        
            2. **Submit an AWS Batch Job**: Submit a job to AWS Batch, specifying the job definition you created in the previous step. This will trigger the process of pulling the Docker image from ECR and launching the container on Fargate.
                        
            3. **Monitor the Job Execution**: AWS Batch will handle the execution of the job on Fargate, including pulling the Docker image from ECR, starting the container, and monitoring its execution.
                        
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
        System.out.println("Use CloudFormation to create two IAM roles that are required for this scenario.");
        CloudFormationHelper.deployCloudFormationStack(STACK_NAME);
        CloudFormationHelper.deployCloudFormationStack(STACK_ECR);

        Map<String, String> stackOutputs = CloudFormationHelper.getStackOutputs(STACK_NAME);
        Map<String, String> stackOutputECR = CloudFormationHelper.getStackOutputs(STACK_ECR);
        String iamRole = stackOutputs.get("BatchRoleArn");
        String executionRoleARN = stackOutputECR.get("EcsRoleArn");

        System.out.println("The IAM role needed to interact wit AWS Batch is "+iamRole);
        System.out.println("The second IAM role needed to interact wit AWS ECR is "+executionRoleARN);
        waitForInputToContinue(scanner);

        System.out.println("1. Create a Batch compute Environment");
        System.out.println("""
            An Amazon Batch compute environment is a resource where you can run your batch jobs. 
            After creating a compute environment, you can define job queues and job definitions to submit jobs for 
            execution. Here’s an overview of what you can do with your compute environment and the types of jobs 
            you can create:
            
            The benefit of creating a compute environment is it allows you to easily configure and manage the compute 
            resources that will be used to run your Batch jobs. By separating the compute environment from the job definitions,
            you can easily scale your compute resources up or down as needed, without having to modify your job definitions. 
            This makes it easier to manage your Batch workloads and ensures that your jobs have the necessary 
            compute resources to run efficiently.
            """);

        waitForInputToContinue(scanner);
        batchActions.createComputeEnvironment(computeEnvironmentName, iamRole, subnet, secGroup);
        System.out.println(DASHES);

        System.out.println("2. Check the status of the "+computeEnvironmentName +" Compute Environment.");
        waitForInputToContinue(scanner);
        batchActions.checkComputeEnvironmentsStatus(computeEnvironmentName).thenAccept(status -> {
            System.out.println("Current Status: " + status);
        }).join();
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println("3. What You Can Do with a Compute Environment?");
        System.out.println("""
            Submit Jobs: You can submit batch jobs to the compute environment for execution. Jobs can be containerized applications or scripts.
            Manage Job Queues: Define job queues to prioritize and manage jobs. Jobs submitted to a queue are evaluated by the scheduler to determine when, where, and how they run.
            Define Job Definitions: Create job definitions that specify how jobs are to be run, including parameters, environment variables, and resource requirements.
            Types of Jobs
            Batch Processing Jobs: Process large volumes of data, such as ETL (Extract, Transform, Load) operations, image processing, and video transcoding.
            Machine Learning Jobs: Train machine learning models or run inference tasks using frameworks like TensorFlow, PyTorch, or scikit-learn.
            Compute-Intensive Jobs: Perform simulations, modeling, and other CPU/GPU-intensive tasks.
            Data Analysis Jobs: Analyze large datasets, run statistical analyses, or perform data mining.
            Setting Up Job Queues and Job Definitions.
            
            This scenario provides an example of setting up a job queue and job definition, and then submitting a job.
            """);
        waitForInputToContinue(scanner);
        String jobQueueArn = batchActions.createJobQueueAsync(jobQueueName, computeEnvironmentName).join();
        System.out.println("Job Queue ARN returned: " + jobQueueArn);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println("4. Register a Job Definition.");
        System.out.println("""
            Registering a job in AWS Batch using the Fargate launch type ensures that all
            necessary parameters, such as the execution role, command to run, and so on 
            are specified and reused across multiple job submissions. 
            
            This promotes a standardized and efficient approach to managing containerized workloads 
            in the cloud.
            """);

        waitForInputToContinue(scanner);
        String[] jobARN = new String[1];
        try {
            jobARN[0] = batchActions.registerJobDefinitionAsync(jobDefinitionName, executionRoleARN, dockerImage)
                .exceptionally(ex -> {
                    System.err.println("Register job definition failed: " + ex.getMessage());
                    return null;
                })
                .join();
            if (jobARN[0] != null) {
                System.out.println("Job ARN: " + jobARN[0]);
            }
        } catch (CompletionException ex) {
            System.err.println("Failed to register job definition: " + ex.getMessage());
        }
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("5. Submit an AWS Batch job from a job definition.");
        waitForInputToContinue(scanner);
        String[] jobId = new String[1];
        try {
            jobId[0] = batchActions.submitJobAsync(jobDefinitionName, jobQueueName, jobARN[0])
                .exceptionally(ex -> {
                    System.err.println("Submit job failed: " + ex.getMessage());
                    return null;
                })
                .join();

            System.out.println("The job id is "+jobId[0]);

        } catch (CompletionException ex) {
            System.err.println("Failed to submit job: " + ex.getMessage());
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6. Get a list of jobs applicable to the job queue.");

        waitForInputToContinue(scanner);
        try {
            batchActions.listJobsAsync(jobDefinitionName, jobQueueName);

        } catch (CompletionException ex) {
            System.err.println("Failed to list jobs: " + ex.getMessage());
        }

        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Check the status of job "+jobId[0]);
        waitForInputToContinue(scanner);
        try {
            String jobStatus = batchActions.describeJobAsync(jobId[0])
                .exceptionally(ex -> {
                    System.err.println("Describe job failed: " + ex.getMessage());
                    return null;
                })
                .join();

            if (jobStatus != null) {
                System.out.println("Job status: " + jobStatus);
            }

        } catch (CompletionException ex) {
            System.err.println("Failed to describe job: " + ex.getMessage());
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println("8. Delete Batch resources");
        System.out.println(
            """
            WHen deleting an AWS Batch compute environment, it does not happen instantaneously. 
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
                batchActions.deregisterJobDefinitionAsync(jobARN[0])
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
                batchActions.deleteComputeEnvironmentAsync(computeEnvironmentName)
                    .exceptionally(ex -> {
                        System.err.println("Delete compute environment failed: " + ex.getMessage());
                        return null;
                    })
                    .join();
            } catch (CompletionException ex) {
                System.err.println("Failed to delete compute environment: " + ex.getMessage());
            }
            waitForInputToContinue(scanner);
            CloudFormationHelper.destroyCloudFormationStack(STACK_NAME);
            CloudFormationHelper.destroyCloudFormationStack(STACK_ECR);
        }

        System.out.println(DASHES);
        System.out.println("This concludes the Amazon Batch SDK scenario");
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

