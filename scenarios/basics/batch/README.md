# AWS Batch Service Basic Scenario

## Overview

 This AWS Batch Service basic scenario demonstrates how to interact with the AWS Batch service using an AWS SDK.  The scenario covers various operations such as creating an AWS Batch compute environment, creating a job queue, creating a job defination, and submitting a job, and so on. 
 
 Here are the top six service operations this scenario covers. 

1. **Create an AWS Batch computer environment**: Creates an AWS Batch computer environment. 

2. **Sets up a job queue**: Creates a job queue that will manage the submission of jobs.

3. **Creates a job definition**: Creates a job definition that specifies how the jobs should be executed.

4. **Registers a Job Definition**: Registers a job definition making it available for job submissions.

5. **Submits a Batch Job**: Submits a job.

6. **Checks the status of the job**: Checks the status of the job.

Note: These steps are not the complete program, but summarizes the 5-6 high-level steps. See the Eng Spec for a complete listing of ECR operations. 

### Resources

The basic scenario requires an IAM role that has AWS Batch permissions. A CloudFormation template is used to create the IAM role. 


## Implementations

This scenario example is implemented in the following language:

- Java


## Additional reading

- [AWS Batch](https://docs.aws.amazon.com/batch/latest/userguide/what-is-batch.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
