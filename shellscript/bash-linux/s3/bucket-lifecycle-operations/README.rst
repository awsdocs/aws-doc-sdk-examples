.. Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
   International License (the "License"). You may not use this file except in compliance with the
   License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions and
   limitations under the License.

#####################################
Amazon S3 Bucket Lifecycle Operations
#####################################

This example demonstrates how to interact with some of the basic Amazon S3 operations. It
includes functions that perform the following tasks:

- Creating a bucket and verifying its existence
- Copying a file from the local computer to a bucket
- Copying a file from one bucket location to another
- Listing the contents of bucket
- Deleting a file from bucket
- Deleting the bucket

Running the example
===================

This example is written as functions in a shell script file (bucket-operations.sh) that 
can be sourced from another file. The file **bucket-operations-test.sh** script 
demonstrates how to call the functions by sourcing the **bucket-operations.sh** file and 
calling each of the functions.

If all steps work correctly, the test script removes all resources that it created.

To see the intermediate results of each step, remove the comment mark (#) in front of 
each of the lines that begins with a ``read`` command. When you then run the script, you 
can view the current status of the bucket or its contents using the Amazon S3 console.

This example assumes that you have a shared credentials file with a default profile. For
more information about how to set up a shared credentials file, see `Configuration and 
Credential File Settings <https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html>`_
in the *AWS CLI User Guide*.

Copyright and License
=====================

All content in this repository, unless otherwise stated, is Copyright © 2010-2020, Amazon 
Web Services, Inc. or its affiliates. All rights reserved.

Except where otherwise noted, all examples in this collection are licensed under the 
Apache license, version 2.0 (the "License"). The full license text is provided in the 
LICENSE file accompanying this repository.
