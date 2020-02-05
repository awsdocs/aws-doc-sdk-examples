.. Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
   International License (the "License"). You may not use this file except in compliance with the
   License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions and
   limitations under the License.

########################################
AWS CLI examples for BASH shell on Linux
########################################

These are examples for the AWS Command Line Interface (CLI) public documentation. All examples have been tested and verified
to work with AWS CLI version 2. The testing platforms include Amazon Linux 2 and MacOS 10.14, both
using a BASH shell.

Prerequisites
=============

To run these examples, you need:

- The AWS CLI, downloaded and running on your machine
- AWS credentials in a shared credentials file

Running the examples
====================

Examples are typically written as functions in shell script files that can be sourced from other 
files. Most are accompanied by a unit test script in the **tests** directory that you can run to validate
that each example works. The test scripts create the setup and teardown functions that create
and destroy any prerequisite resources. We take care that all AWS resources that the example creates are
also destroyed to avoid incurring any unwanted costs. When you're done with an example, we do recommend
that you check the resources in your account to ensure that the teardown function 
worked as expected and didn't accidentally leave anything behind.

To run the examples, you need to create a shared credentials file. For more information about how
to set up a shared credentials file, see `Configuration and Credential File Settings <
https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html>`_
in the *AWS CLI User Guide*.

AWS CLI downloads
=================

For detailed information, see `Installing the AWS CLI <https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html>`_.

Documentation
=============

For detailed documentation for the AWS CLI, see the following:

- `AWS CLI User Guide <https://docs.aws.amazon.com/cli/latest/userguide/>`_
- `AWS CLI Reference Guide <https://docs.aws.amazon.com/cli/latest/reference/>`_

Copyright and License
=====================

All content in this repository, unless otherwise stated, is Copyright © 2010-2020, Amazon Web Services, Inc. or its 
affiliates. All rights reserved.

Except where otherwise noted, all examples in this collection are licensed under the Apache license, version 2.0 (the 
"License"). The full license text is provided in the LICENSE file accompanying this repository.
