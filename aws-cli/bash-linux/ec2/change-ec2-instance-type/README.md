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
  * change_ec2_instance_type.sh - main script example file
  * test_change_ec2_instance_type.sh - unit/integration test file
  * general.sh - common test support function file

## Purpose
The main script file contains the function `change_ec2_instance_type()` that perform the following tasks:

 * Verifies that the specified EC2 instance exists.
 * Warns the user (unless -f was selected) before stopping the instance.
 * Changes the instance type.
 * If requested (by selecting -r), restarts the instance and confirms that the instance is running.

## Prerequisites

 * An Amazon Web Services (AWS) account.
 * A shared credentials file with a default profile. The profile that you use must have permissions that allow the AWS operations performed by the script. For more information about how to set up a shared credentials file, see [Configuration and Credential File Settings](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html) in the _AWS CLI User Guide_.
 * A running EC2 instance in the account for which you have permission to stop and modify. If you run the test script, it launches an instance for you, tests changing the type, and then terminates the instance. 

## Running the Code
This example is written as a function in a shell script file (*`change_ec2_instance_type.sh`*) that you can `source` from another script or from the command line. Once the function is in memory, you can invoke it from the command line. For example, the following commands change type of the specified instance to `t2.nano`:

```
source ./change_ec2_instance_type.sh
./change_ec2_instance_type -i *instance-id* -t new-type
```

## Parameters

**-i** *(string)* Specifies the instance ID to modify.

**-t** *(string)* Specifies the EC2 instance type to switch to.

**-r** *(switch)* If set, the function restarts the instance after the type switch. Default: the function doesn't restart the instance.

**-f** *(switch)* If set, the function doesn't prompt the user before shutting down the instance to make the type switch. Default: the script prompts the user to confirm shutting down the instance before making the switch.

**-v** *(switch)* If set, the function displays status throughout its operation. Default: the script operates silently and displays output only in the event of an error.

## Testing the example
The file *change_ec2_instance_type_test.sh* script tests the various code paths for the `change_ec2_instance_type` function.

If all steps in the test script work correctly, the test script removes all resources that it created.

You can run the test script with the following parameters:

**-v** *(switch)* The tests each show a pass/failure status as they run. Default: the tests runs silently and the output includes only the final overall pass/failure status.

**-i** *(switch)* The script pauses after each test to enable you to browse the intermediate results of each step. When run this way, you can examine the current status of the instance using the Amazon EC2 console. The script  proceeds to the next step after you press *ENTER* at the prompt.

## Additional Information

 * As an AWS best practice, grant this code least privilege, or only the permissions required to perform a task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the _AWS Identity and Access Management (IAM) User Guide_.
 * This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see [Service Endpoints and Quotas](https://docs.aws.amazon.com/general/latest/gr/aws-service-information.html) in the _AWS General Reference Guide_.
 * Running this code can result in charges to your AWS account. It is your responsibility to ensure that any resources created by this script are removed when you are done with them.
