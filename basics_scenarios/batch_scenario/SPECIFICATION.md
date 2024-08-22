# AWS Batch Service Scenario Specification

## Overview
This SDK Basics scenario demonstrates how to interact with AWS Batch using the AWS SDK. It demonstrates various tasks such as creating a compute environment, setting up a job queue, defining a job definition, submitting a job, and so on.  Finally this scenario demonstrates how to clean up resources. Its purpose is to demonstrate how to get up and running with AWS Batch and the AWS SDK.

## Resources
The required resources for this Basics scenario are two IAM roles. The IAM roles must have permission to interact with the AWS Batch service. This Basics scenario uses a CloudFormation template to create the IAM roles and delete the IAM roles at the end of the program.

This scenario submits a job that pulls a Docker image from Amazon ECR to Amazon Fargate. To place a Docker image on Amazon ECR, run the follow Basics scenario. See [Amazon ECR code examples for the SDK for Java 2.x](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/ecr)

## Hello AWS  Batch
This program is intended for users not familiar with the AWS Batch SDK to easily get up and running. The logic is to show use of `listJobsPaginator()`.

## Scenario Program Flow
The AWS Batch Basics scenario executes the following operations.

**Creates two IAM roles using a CloudFormation template**

This operation uses AWS CloudFormation to create two IAM roles that are required for AWS Batch operations. The first role is for the compute environment and grants the necessary permissions for the instances to perform tasks such as reading from Amazon S3 or writing logs to CloudWatch. The second role is for the AWS Batch service to interact with other AWS services.

**Creates a Batch compute environment**

This operation sets up a compute environment where AWS Batch jobs will be executed. The compute environment is essentially a collection of EC2 instances or ECS containers that AWS Batch can scale based on job requirements.


**Checks the status of the compute Environment**

This operation retrieves the current status of the compute environment to ensure it is active and ready to accept jobs.

**Sets up a job queue and job definition**

This operation involves creating a job queue that will manage the submission of jobs and a job definition that specifies how the jobs should be executed.

**Registers a Job Definition**

This operation registers a new job definition with AWS Batch, making it available for job submissions.

**Submits a Batch Job**

This operation submits a job to the AWS Batch job queue for execution.

**Describes a Job**
This operation retrieves detailed information about a specific job, including its status and execution details.

**Deregisters the Job Definition**
his operation removes a job definition from AWS Batch, making it unavailable for future job submissions.

**Describes the Job Queue**

This operation retrieves information about a specific job queue, including its status and associated compute environments.

**Disables and deletes the Job Queue**
This operation disables a job queue to stop it from accepting new jobs and then deletes it from AWS Batch.

**Disables and deletes the Compute Environment**
This operation disables a compute environment to prevent it from executing new jobs and then deletes it from AWS Batch.


### Program execution
The following shows the output of the AWS Batch program in the console. 

```
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

1
Continuing with the program...

--------------------------------------------------------------------------------

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Use AWS CloudFormation to create two IAM roles that are required for this scenario.
Stack creation requested, ARN is arn:aws:cloudformation:us-east-1:814548047983:stack/BatchStack4/a3f75c50-54d1-11ef-b797-0e1c1efd9fe3
Stack created successfully
Stack creation requested, ARN is arn:aws:cloudformation:us-east-1:814548047983:stack/EcsStack/c8f19bb0-54d1-11ef-8454-0eebf7f4997f
Stack created successfully
The IAM role needed to interact wit AWS Batch is arn:aws:iam::814548047983:role/BatchStack4-RoleBatch15CD9C03-KWhgI3rvSIbY
The second IAM role needed to interact wit AWS ECR is arn:aws:iam::814548047983:role/EcsStack-RoleEcsB0CD5AAE-RaUPyHZuoa1L

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
1. Create a Batch compute environment
A compute environment is a resource where you can run your batch jobs.
After creating a compute environment, you can define job queues and job definitions to submit jobs for
execution.

The benefit of creating a compute environment is it allows you to easily configure and manage the compute
resources that will be used to run your Batch jobs. By separating the compute environment from the job definitions,
you can easily scale your compute resources up or down as needed, without having to modify your job definitions.
This makes it easier to manage your Batch workloads and ensures that your jobs have the necessary
compute resources to run efficiently.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Compute environment created successfully.
Compute Environment ARN: arn:aws:batch:us-east-1:814548047983:compute-environment/my-compute-environment

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
2. Check the status of the my-compute-environment Compute Environment.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Compute environment status retrieved successfully.
Compute Environment Status: VALID

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
3. Create a job queue
A job queue is an essential component that helps manage the execution of your batch jobs.
It acts as a buffer, where jobs are placed and then scheduled for execution based on their
priority and the available resources in the compute environment.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job queue created successfully.
Job Queue ARN: arn:aws:batch:us-east-1:814548047983:job-queue/my-job-queue

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
4. Register a Job Definition.
Registering a job in AWS Batch using the Fargate launch type ensures that all
necessary parameters, such as the execution role, command to run, and so on
are specified and reused across multiple job submissions.

 The job definition pulls a Docker image from Amazon ECR and executes the Docker image.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job ARN: arn:aws:batch:us-east-1:814548047983:job-definition/my-job-definition:63
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
5. Submit an AWS Batch job from a job definition.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job submitted successfully. Job ID: 5a7af03d-2659-4e96-a714-94beaacce600
The job id is 5a7af03d-2659-4e96-a714-94beaacce600

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
6. Get a list of jobs applicable to the job queue.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job ID: e3a2b1de-01b6-4330-8a2e-f1f921a87acb, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: e7618984-49a0-44b8-85e3-e331e0cdca9a, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: b3c19bb0-65db-4097-8871-c95544a4efdb, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 611b04a3-6c1e-415d-a733-83798c8b9d40, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: c81941d3-b3ba-4b2a-8bf2-470ff8f467aa, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 353e105d-ca38-43cc-b4f8-52bd75418b5b, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 77571838-dd64-454f-8b34-73fcb41ffa7c, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 2b80159f-46d5-4d8c-ac73-6a94f19a7c74, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: fad2a2e6-a66a-4b93-ab7b-e81b833d2f6f, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 358747fe-2c50-4d85-a965-dccd221791b7, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 6eb60b65-5039-4da4-ae74-c5fa1d2ac633, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 87e8798f-1104-4d8b-b9dc-fee65413bc64, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: c834a6dc-133d-47ef-80f8-f1b3b0f35408, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 45ed52c7-4caf-462f-b713-378892b7ec77, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 5983149a-a735-4b17-8fc3-addc7f7dee1f, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 480c1331-af47-4485-9162-c60c241873e7, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: bb04c5b1-0913-408e-b09b-f6dbdd731d12, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: b41d72d8-f241-4335-88cc-0adf531670e8, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 4782196d-1749-483c-b367-86281cbaf310, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 10d617d7-b769-476e-a1be-16a68f11ee83, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: dd9b05d0-755c-4f9b-a4e4-d92bbc85b415, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: a67cd644-8d9f-4847-8192-78e9e018e020, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: aea93597-cbb3-447c-8174-c83307847b58, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 0f4c6dce-0498-4c7d-85a0-31517634bd7f, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 7173bf10-4c58-4d05-8028-620d71a89ff8, Job Name: my-job-definition, Job Status: SUCCEEDED
Job ID: 40354da2-7f7d-42df-9e84-888b1e4e5007, Job Name: my-job-definition, Job Status: SUCCEEDED

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
7. Check the status of job 5a7af03d-2659-4e96-a714-94beaacce600

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job status retrieved successfully. Status: STARTING
Job Status: STARTING

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
8. Delete Batch resources
When deleting an AWS Batch compute environment, it does not happen instantaneously.
There is typically a delay, similar to some other AWS resources.
AWS Batch starts the deletion process.

Would you like to delete the AWS Batch resources such as the compute environment? (y/n)
y
You selected to delete the AWS ECR resources.
First, we will deregister the Job Definition.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

arn:aws:batch:us-east-1:814548047983:job-definition/my-job-definition:63 was successfully deregistered
Second, we will disable and then delete the Job Queue.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job queue update initiated: UpdateJobQueueResponse(JobQueueName=my-job-queue, JobQueueArn=arn:aws:batch:us-east-1:814548047983:job-queue/my-job-queue)
Job queue is now disabled.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Lets wait 2 mins for the job queue to be deleted
02:00Job queue deleted: DeleteJobQueueResponse()
00:00Countdown complete!

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Third, we will delete the Compute Environment.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Compute environment disabled: my-compute-environment
Compute environment status retrieved successfully.
Current State: UPDATING
Lets wait 1 min for the compute environment to be deleted
00:00Countdown complete!
Compute environment was successfully deleted

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Delete stack requested ....
Stack deleted successfully.
Delete stack requested ....
Stack deleted successfully.
--------------------------------------------------------------------------------
This concludes the AWS Batch SDK scenario
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

