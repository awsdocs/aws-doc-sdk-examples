<!--
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
This file is licensed under the Apache License, Version 2.0 (the "License").

You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/.

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
-->
# AWS CLI examples

These are examples for the AWS Command Line Interface (AWS CLI) public
documentation. All examples have been tested and verified to work with
the AWS CLI version 2.

## Prerequisites

To run these examples, you'll need:

 * The AWS CLI, downloaded and running on your machine
 * AWS credentials in a shared credentials file

## Running the examples

Examples are typically written as functions in shell script files that can be
sourced from other files.  When you're done with an example, we do
recommend that you check the resources in your account to ensure that the
teardown worked as expected and didn't accidentally leave any resources behind.

To run the examples, you need to create a shared credentials file. For more
information about how to set up a shared credentials file, see [Configuration
and Credential File Settings](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html)
in the _AWS CLI User Guide_.

### Tests

Most examples are accompanied by a unit or integration test
script that you can run to validate that each example works. The test scripts
include setup and teardown to create and destroy any prerequisite resources.
We take care that all AWS resources that the example creates are also destroyed
to avoid incurring any unwanted costs.


## AWS CLI downloads

For information about how to download and install the AWS CLI, see [Installing
the AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html)
in the _AWS CLI User Guide_.

## Documentation

For detailed documentation for the AWS CLI, see the following:

 * [AWS CLI User Guide](https://docs.aws.amazon.com/cli/latest/userguide/)
 * [AWS CLI Reference Guide](https://docs.aws.amazon.com/cli/latest/reference/)
