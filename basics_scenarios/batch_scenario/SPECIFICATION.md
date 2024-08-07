# AWS Batch Service Scenario Specification

## Overview
This SDK Basics scenario demonstrates how to interact with AWS Batch using the AWS SDK. It demonstrates various tasks such as creating a compute environment, setting up a job queue, defining a job definition, submitting a job, and so on.  Finally this scenario demonstrates how to clean up resources. Its purpose is to demonstrate how to get up and running with AWS Batch and the AWS SDK.

## Resources
The required resources for this SDK scenario are an IAM role and a local Docker image. The IAM role must have permission to interact with the Amazon ECR service (for example, ecr:PutImage). 

To create an IAM role, see [Creating IAM roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create.html). 

For more information about using permissions with ECR (for example, how to create Amazon ECR Identity-based policies), see [How Amazon Elastic Container Registry works with IAM](https://docs.aws.amazon.com/AmazonECR/latest/userguide/security_iam_service-with-iam.html).

To see the instructions to create a local docker image, see [README.md](README.md).

This scenario uses the Docker client API for a specific programming language. For more information, see [Develop with Docker Engine SDKs](https://docs.docker.com/engine/api/sdk/)

## Hello Amazon ECR
This program is intended for users not familiar with the Amazon ECR SDK to easily get up and running. The logic is to show use of `ecrClient.listImagesPaginator()`.

### Program execution
The following shows the output of the program in the console. 

```java 
The docker image tag is latest 

```

## Scenario Program Flow
The Amazon ECR SDK getting started scenario executes the following steps:

1. **Parse command-line arguments**: The program checks if the correct number of arguments (2) are provided, which are the IAM role and the account number.

2. **Introduce Amazon ECR**: The program provides a brief introduction to Amazon ECR.

3. **Checks the local machine for a docker image**: The program checks the local mahcine for a docker image named echo-text. If it's not located, the program ends. 

3. **Create an ECR repository**: The program creates the repository using the `createECRRepository` method.

4. **Set an ECR repository policy**: The program sets an ECR repository policy using the `setRepoPolicy` method, which grants the specified IAM role the necessary permissions to access the repository.

5. **Display the ECR repository policy**: The program retrieves and displays the repository policy using the `getRepoPolicy` method.

6. **Retrieve an ECR authorization token**: The program retrieves an ECR authorization token using the `getAuthToken` method, which is required for subsequent operations.

7. **Get the ECR repository URI**: The program retrieves the URI of the ECR repository using the `getRepositoryURI` method.

8. **Set an ECR lifecycle policy**: The program sets an ECR lifecycle policy using the `setLifeCyclePolicy` method, which automatically removes old or unused Docker images from the repository.

9. **Push a Docker image to the ECR repository**: The program pushes a local Docker image to the ECR repository using the Docker Client (its not a recommended way to upload using ECRClient).

10. **Verify the image in the ECR repository**: The program verifies that the Docker image was successfully pushed to the ECR repository using the `verifyImage` method.

11. Provide optional steps on how a user can run the image in the ECR repo. 

12. **Delete the ECR repository**: The program prompts the user to delete the ECR repository and its contents using the `deleteECRRepository` method.


### Program execution
The following shows the output of the Amazon ECR program in the console. 

```
  Amazon Batch is a fully-managed batch processing service that enables developers,
scientists, and engineers to run batch computing workloads of any scale. Amazon Batch
dynamically provisions the optimal quantity and type of compute resources (e.g., CPU or
memory-optimized instances) based on the volume and specific resource requirements of the
batch jobs submitted.

The Java V2 SDK to interact with various AWS services programmatically. The `BatchAsyncClient`
interface in the Java V2 SDK allows developers to automate the submission, monitoring, and
management of batch jobs on the Amazon Batch service.

In this scenario, we'll explore how to use the Java V2 SDK to interact with the Amazon Batch
service and perform key operations such as submitting jobs, monitoring their status, and
managing the compute environment.

Let's get started...

You have two choices:
1 - Run the entire program.
2 - Delete an existing Compute Environment (created from a previous execution of
this program that did not complete).

1
Continuing with the program...

--------------------------------------------------------------------------------
1. Create a Batch compute Environment
An Amazon Batch compute environment is a resource where you can run your batch jobs.
After creating a compute environment, you can define job queues and job definitions to submit jobs for
execution. Here’s an overview of what you can do with your compute environment and the types of jobs
you can create:

The benefit of creating a compute environment is it allows you to easily configure and manage the compute
resources that will be used to run your Batch jobs. By separating the compute environment from the job definitions,
you can easily scale your compute resources up or down as needed, without having to modify your job definitions.
This makes it easier to manage your Batch workloads and ensures that your jobs have the necessary
compute resources to run efficiently.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Compute environment created: arn:aws:batch:us-east-1:814548047983:compute-environment/my-compute-environment
--------------------------------------------------------------------------------
2. Check the status of the my-compute-environment Compute Environment.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Current Status: VALID

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
3. What You Can Do with a Compute Environment?
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


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job Queue ARN returned: arn:aws:batch:us-east-1:814548047983:job-queue/MyJobQueue

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
4. Register a Job Definition.
Registering a job in AWS Batch using the Fargate launch type ensures that all
necessary parameters, such as the execution role, command to run, and so on
are specified and reused across multiple job submissions.

This promotes a standardized and efficient approach to managing containerized workloads
in the cloud.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job definition registered: arn:aws:batch:us-east-1:814548047983:job-definition/MyJobDefinition:35
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
5. Submit an AWS Batch job from a job definition.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Submitted job with ID: cf22904d-9f56-4d70-b050-5d3ff3065a8e

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
6. Get a list of jobs applicable to the job queue.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Batch jobs applicable to the job queue: MyJobQueue

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
7. Check the status of the job.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job status: STARTING

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
8 Delete a Batch compute environment
WHen deleting an AWS Batch compute environment, it does not happen instantaneously.
There is typically a delay, similar to some other AWS resources.
AWS Batch starts the deletion process.

Would you like to delete the AWS Batch resources such as the compute environment? (y/n)
y
You selected to delete the AWS ECR resources.
First, we will deregister the Job Definition.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

MyJobDefinition was successfully deregistered
Second, we will disable and then delete the Job Queue.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job queue update initiated: UpdateJobQueueResponse(JobQueueName=MyJobQueue, JobQueueArn=arn:aws:batch:us-east-1:814548047983:job-queue/MyJobQueue)

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job queue deleted: DeleteJobQueueResponse()
Lets wait 2 mins for the job queue to be deleted
00:00Countdown complete!

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Third, we will delete the Compute Environment.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Compute environment disabled: my-compute-environment
Current State: UPDATING
Lets wait 1 min for the compute environment to be deleted
00:00Countdown complete!
Compute environment was successfully deleted

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
This concludes the Amazon Batch SDK scenario
--------------------------------------------------------------------------------

```

## SOS Tags

The following table describes the metadata used in this Basics Scenario.


| action                       | metadata file                | metadata key                            |
|------------------------------|------------------------------|---------------------------------------- |
| `describeJobs`               | batch_metadata.yaml          | batch_DescribeJobs                      |
| `updateComputeEnvironment`   | batch_metadata.yaml          | batch_UpdateComputeEnvironment          |
| `describeJobQueues`          | batch_metadata.yaml          | batch_DescribeJobQueues                 |
| `DeleteJobQueue`             | batch_metadata.yaml          | batch_DeleteJobQueue                    |
| `updateJobQueue`             | batch_metadata.yaml          | batch_UpdateJobQueue                    |
| `deregisterJobDefinition`    | batch_metadata.yaml          | batch_DeregisterJobDefinition           |
| `registerJobDefinition`      | batch_metadata.yaml          | batch_RegisterJobDefinition             |
| `listJobsPaginator`          | batch_metadata.yaml          | batch_ListJobsPaginator                 |
| `createJobQueue`             | batch_metadata.yaml          | batch_CreateJobQueue                    |
| `DescribeComputeEnvironments`| batch_metadata.yaml          | batch_DescribeComputeEnvironments       |
| `deleteComputeEnvironment`   | batch_metadata.yaml          | batch_DeleteComputeEnvironment          |
| `CreateComputeEnvironment`   | batch_metadata.yaml          | batch_CreateComputeEnvironment          |
| `scenario                    | batch_metadata.yaml          | batch_Scenario                          |

