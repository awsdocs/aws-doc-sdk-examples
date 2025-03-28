# Amazon S3 Batch Basics Scenario

## Introduction
This scenario demonstrates how to use the AWS SDK to interact with Amazon Simple Storage Service (S3) using AWS Batch. The scenario covers various operations such as creating a batch job, modifying the job, and adding additional tags to the job.

## Setting up Resources
To successfully run this basic scenario, the program requires an IAM (Identity and Access Management) role. However, the program creates the IAM role by using an AWS CloudFormation template. 

## Service Operations Invoked
The program performs the following tasks:

1. **Create Batch Job**:
   - Amazon S3 Batch Operation: `CreateJob`

2. **Describe Batch Job**:
  - Amazon S3 Batch Operation: `DescribeJob`

3. **Describe the tags associated with the job**:
   - Amazon S3 Batch Operation: `getJobTagging`

4. **Update Batch Job Tags**:
   - Amazon S3 Batch Operation: `putJobTagging`

5. **List Batch Jobs**:
   - Amazon S3 Batch Operation: `listJobs`
 
6.  **Update an existing S3 Batch Operations job's priority**:
   - Amazon S3 Batch Operation: `updateJobPriority`

7.  **Cancel the S3 Batch job**:
   - Amazon S3 Batch Operation: `updateJobStatus`

8.  **Delete the Amazon S3 Batch job tagging**:
   - Amazon S3 Batch Operation: `deleteJobTagging`


## Usage
1. Clone the repository or download the source code.
2. Open the code in your preferred IDE.
3. Invoke the main method in the `S3BatchScenario` class.

## Code Explanation
The provided code demonstrates the following key features of the AWS SDK and the S3 Batch functionality:

1. **User Interaction**: The program begins with a user prompt to provide the AWS account ID and continues to interact with the user for input to proceed with various steps of the scenario.

2. **Resource Setup**: It uses AWS CloudFormation to set up necessary resources, such as an IAM role required for S3 Batch operations.

3. **Bucket Operations**: Creates an S3 bucket and uploads required files, including a manifest file listing the objects for the batch job.

4. **Job Creation**: Creates an S3 Batch job to tag objects listed in the manifest file with specific tags.

5. **Job Priority Update**: Modifies the priority of the created S3 Batch job.

6. **Job Cancellation**: Provides the option to cancel the S3 Batch job based on user input.

7. **Job Description**: Retrieves and displays details of the created S3 Batch job.

8. **Job Tag Description**: Retrieves and displays tags associated with the S3 Batch job.

9. **Job Tag Update**: Updates the tags of the S3 Batch job.

10. **List Batch Jobs**: Lists all batch jobs in the account.

11. **Delete Job Tags**: Provides the option to delete the tags associated with the S3 Batch job.

12. **Cleanup Resources**: Provides the option to delete the created S3 bucket, its contents, and the CloudFormation stack to clean up resources used in the scenario.
