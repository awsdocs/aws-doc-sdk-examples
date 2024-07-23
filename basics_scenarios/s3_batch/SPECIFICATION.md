# Amazon S3 Batch Basics Scenario Specification

## Overview

This document outlines the technical specifications for the _Amazon S3 Batch Basic Scenario_, a scenario designed to demonstrate Amazon S3 Batch functionality and SDKs. It is primarily intended for the AWS code examples team to use while developing this example in additional languages.


## Resources
The required resources for this basic scenario include an IAM role and an S3 bucket. The IAM role is created using an AWS CloudFormation template, and the S3 bucket is created and populated with files using the AWS SDK. The scenario automatically deploys the CloudFormation stack, so the user running the program does not need to take any action.

## Scenario Program Flow
The Amazon S3 Batch basic scenario executes the following steps:

1. **Introduce Amazon S3 Batch**: The program provides a brief introduction to Amazon S3 Batch functionality.

2. **Stand up the resources**: Uses Amazon CloudFormation to create the resources.

3. **Create Batch Job**: The program creates an S3 Batch job.

4. **Cancel the S3 Batch job**: The program cancels the S3 Batch job if the user requests that action.

5. **Describe the job**: The program describes the job that was just created. Details such as the status and the job priority are displayed.

6. **Describe the tags**: The program describes the tags associated with the job.

7. **Update Batch Job Tags**: The program adds additional tags to the job.

8. **List Batch Jobs**: The program lists all batch jobs under the account.

9. **Delete the Amazon S3 Batch job tagging**: The program deletes the job tagging.

10. **Delete the resources**: The program deletes the CloudFormation stack and the S3 bucket used in the scenario.


### Program execution
The following shows the output of the Amazon S3 Batch program in the console. 

```
--------------------------------------------------------------------------------
Welcome to the Amazon S3 Batch basics scenario.
S3 Batch is a powerful service provided by AWS that enables efficient and
cost-effective processing of large-scale data stored in Amazon S3.
It automatically scales resources to handle varying workloads without the need for manual
intervention.

One of the key features of S3 Batch is its ability to perform tagging operations on objects stored in
S3 buckets. Users can leverage S3 Batch to apply, update, or remove tags on thousands or millions of
objects in a single operation, streamlining the management and organization of their data.

This can be particularly useful for tasks such as cost allocation, lifecycle management, or
metadata-driven workflows, where consistent and accurate tagging is essential.
S3 Batch's scalability and serverless nature make it an ideal solution for organizations with
growing data volumes and complex data management requirements.

This Java program walks you through Amazon S3 Batch operations.

Let's get started...



Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Use CloudFormation to stand up the resource required for this scenario.
Stack creation requested, ARN is arn:aws:cloudformation:us-east-1:814548047983:stack/MyS3Stack/b8ae9450-482a-11ef-ab7a-0affc0d36e9d
Stack created successfully
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Setup the required bucket for this scenario.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

HeadBucketResponse(BucketRegion=us-east-1, AccessPointAlias=false)
x-fc084273-7496-4528-bd74-172a6141ae02 is ready
Populate the bucket with the required files.
CSV file updated successfully.
Successfully placed job-manifest.csv into bucket x-fc084273-7496-4528-bd74-172a6141ae02
Successfully placed object-key-1.txt into bucket x-fc084273-7496-4528-bd74-172a6141ae02
Successfully placed object-key-2.txt into bucket x-fc084273-7496-4528-bd74-172a6141ae02
Successfully placed object-key-3.txt into bucket x-fc084273-7496-4528-bd74-172a6141ae02
Successfully placed object-key-4.txt into bucket x-fc084273-7496-4528-bd74-172a6141ae02
All files are placed in bucket x-fc084273-7496-4528-bd74-172a6141ae02

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
1. Create a S3 Batch Job
This job tags all objects listed in the manifest file with tags

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The Job id is 391171f8-d831-4931-a7eb-d56c43610e4b

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
2. Update an existing S3 Batch Operations job's priority
In this step, we modify the job priority value. The higher the number, the higher the priority.
So, a job with a priority of `30` would have a higher priority than a job with
a priority of `20`. This is a common way to represent the priority of a task
or job, with higher numbers indicating a higher priority.

Ensure that the job status allows for priority updates. Jobs in certain
states (e.g., Cancelled, Failed, or Completed) cannot have their priorities
updated. Only jobs in the Active or Suspended state typically allow priority
updates.

The job priority was updated

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
3. Cancel the S3 Batch job
Do you want to cancel the Batch job? (y/n): n
Job 391171f8-d831-4931-a7eb-d56c43610e4b was not canceled.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
4. Describe the job that was just created

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job ID: 391171f8-d831-4931-a7eb-d56c43610e4b
Description: Job created using the AWS Java SDK
Status: Active
Role ARN: arn:aws:iam::1234567890983:role/MyS3Stack-S3BatchRole8238262D-Vi5aVsQaZ776
Priority: 60
Progress Summary: JobProgressSummary(TotalNumberOfTasks=4, NumberOfTasksSucceeded=0, NumberOfTasksFailed=0, Timers=JobTimers(ElapsedTimeInActiveSeconds=5))
Manifest Location: arn:aws:s3:::x-fc084273-7496-4528-bd74-172a6141ae02/job-manifest.csv
Manifest ETag: "a7709805d2162c992ad79219340af58b"
Operation: S3 Put Object Tagging
Tag Set: [S3Tag(Key=keyOne, Value=ValueOne), S3Tag(Key=keyTwo, Value=ValueTwo)]
Report Bucket: arn:aws:s3:::x-fc084273-7496-4528-bd74-172a6141ae02
Report Prefix: reports
Report Format: Report_CSV_20180820
Report Enabled: true
Report Scope: AllTasks
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
5. Describe the tags associated with the job

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

No tags found for job ID: 391171f8-d831-4931-a7eb-d56c43610e4b
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
6. Update Batch Job Tags

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Additional Tags were added to job 391171f8-d831-4931-a7eb-d56c43610e4b
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
7. List Batch Jobs

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The job id is eb27cb53-7793-4d5d-95f0-fa3226f25540
The job priority is 60
The job id is 66ea3d82-936e-4c9c-90dc-f0bb5aaf315e
The job priority is 42
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
8. Delete the Amazon S3 Batch job tagging.
Do you want to delete Batch job tagging? (y/n)y
You have successfully deleted 391171f8-d831-4931-a7eb-d56c43610e4b tagging.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Do you want to delete the AWS resources used in this scenario? (y/n)y
All files have been deleted from the bucket x-fc084273-7496-4528-bd74-172a6141ae02
The object was deleted!
The object was deleted!
The object was deleted!
The object was deleted!
The object was deleted!
Deleted object: reports/job-391171f8-d831-4931-a7eb-d56c43610e4b/manifest.json
Deleted folder: reports/
x-fc084273-7496-4528-bd74-172a6141ae02 was deleted
Delete stack requested ....
Stack deleted successfully.
The Amazon S3 Batch scenario has successfully completed.
--------------------------------------------------------------------------------

```

## SOS Tags

The following table describes the metadata used in this Basics Scenario.


| action                       | metadata file                | metadata key                            |
|------------------------------|------------------------------|---------------------------------------- |
| `createjob`                  | s3-control_metadata.yaml     | s3-control_CreateJob                    |
| `updateJobPriority`          | s3-control_metadata.yaml     | s3-control_UpdateJobPriority            |
| `updateJobStatus`            | s3-control_metadata.yaml     | s3-control_UpdateJobStatus              |
| `listJobs`                   | s3-control_metadata.yaml     | s3-control_ListJobs                     |
| `getJobTagging`              | s3-control_metadata.yaml     | s3-control_GetJobTagging                |
| `deleteJobTagging`           | s3-control_metadata.yaml     | s3-control_DeleteJobTagging             |
| `describeJob`                | s3-control_metadata.yaml     | s3-control_DescribeJob                  |
| `putJobTagging`              | s3-control_metadata.yaml     | s3-control_PutJobTagging                |
| `scenario                    | s3-control_metadata.yaml     | s3-control_Basics                           |

