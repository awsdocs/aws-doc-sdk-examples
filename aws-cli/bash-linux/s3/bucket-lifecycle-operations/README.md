<!--
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
This file is licensed under the Apache License, Version 2.0 (the "License").

You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/.

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
-->

# Amazon S3 Bucket Lifecycle Operations

## Files
  * bucket-operations.sh - main script example file
  * bucket-operations-test.sh - unit/integration test file
  * general.sh - common test support function file

## Purpose
This example demonstrates how to interact with some of the basic Amazon S3 operations. The main script file includes functions that perform the following tasks:

 * Creating a bucket and verifying that it exists
 * Copying a file from the local computer to a bucket
 * Copying a file from one bucket location to a different bucket location
 * Listing the contents of a bucket
 * Deleting a file from a bucket
 * Deleting a bucket

## Prerequisites

 * An Amazon Web Services (AWS) account.
 * A shared credentials file with a default profile. The profile that you use must have permissions that allow the AWS operations performed by the script. For more information about how to set up a shared credentials file, see [Configuration and Credential File Settings](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html) in the _AWS CLI User Guide_.

## Running the Code

This example is written as a set of functions in a shell script file (bucket-operations.sh) that can be sourced from another file. The file *`bucket-operations-test.sh`* script demonstrates how to call the functions by sourcing the *`bucket-operations.sh`* file and calling each of the functions.

If all steps work correctly, the test script removes all resources that it created.

To see the intermediate results of each step, run the script with a `-i` parameter. When run this way, you can view the current status of the bucket or its contents using the Amazon S3 console. The script only proceeds to the next step when you press *ENTER* at the prompt.

## Additional Information

* As an AWS best practice, grant this code least privilege, or only the permissions required to perform a task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the _AWS Identity and Access Management (IAM) User Guide_.
* This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions.
* Running this code can result in charges to your AWS account. It is your responsibility to ensure that any resources created by this script are removed when you are done with them.
