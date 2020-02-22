<!--
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
This file is licensed under the Apache License, Version 2.0 (the "License").

You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/.

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
-->

# Change Amazon EC2 Instance Type

This example demonstrates how to change the instance type of an Amazon EC2
instance type. It stops the instance if it is running, changes the instance
type, then, if requested, restarts the instance.

## Files
  * change-ec2-instance-type.sh - main script example file
  * change-ec2-instance-type-test.sh - unit/integration test file
  * general.sh - common test support function file

## Purpose
The main script file includes functions that perform the following tasks:

 * Verifies that the specified EC2 instance exists
 * Warns the user (unless -f/--force was selected)
 * Stops the instance
 * Changes the instance type
 * If requested, restarts the instance
 * Confirms that the instance is running

## Prerequisites

 * An Amazon Web Services (AWS) account.
 * A shared credentials file with a default profile. The profile that you use must have permissions that allow the AWS operations performed by the script. For more information about how to set up a shared credentials file, see [Configuration and Credential File Settings](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html) in the _AWS CLI User Guide_.
 * A running EC2 instance in the account for which you have permission to stop and modify. If you run the test script, it launches an instance for you of one type, changes the type, then terminates the instance. 

## Running the Code

This example is written as a set of functions in a shell script file (*`change-ec2-instance-type.sh`*) that can be sourced from another file. The file *`bucket-operations-test.sh`* script demonstrates how to invoke the function..

If all steps in the test script work correctly, the test script removes all resources that it created.

To see the intermediate results of each step, run the test script with a `-i` parameter. When run this way, you can view the current status of the instance using the Amazon EC2 console. The script  proceeds to the next step when you press *ENTER* at the prompt.

## Additional Information

 * As an AWS best practice, grant this code least privilege, or only the permissions required to perform a task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the _AWS Identity and Access Management (IAM) User Guide_.
 * This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see [Service Endpoints and Quotas](https://docs.aws.amazon.com/general/latest/gr/aws-service-information.html) in the _AWS General Reference Guide_.
 * Running this code can result in charges to your AWS account. It is your responsibility to ensure that any resources created by this script are removed when you are done with them.
