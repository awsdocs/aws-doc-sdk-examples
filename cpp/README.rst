.. Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
   International License (the "License"). You may not use this file except in compliance with the
   License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions and
   limitations under the License.

#############################
AWS SDK for C++ Code Examples
#############################

A collection of code examples for the AWS SDK for C++. The examples are grouped
according to the AWS service they demonstrate.

Prerequisites
=============

Before using the Code Examples, first complete the installation and setup steps of `Getting Started  
<https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html>`_ in the AWS SDK for C++
Developer Guide.

The Getting Started section covers how to obtain and build the SDK, and how to build your own
code utilizing the SDK with a sample "Hello World"-style application.  A similar procedure is
applicable to utilizing the code examples in this repository.

Building and running the code examples
=============
 
The example code is organized in subfolders by AWS service.
For example "example_code/s3" contains the Amazon Simple Storage Service (Amazon S3) code examples.

Examples that use multiple services are located in the [example_code/cross-service](example_code/cross-service) folder.

The examples use the CMake build system. Every service subfolder contains a CMakeLists.txt file that builds all the examples for that service. 

To understand more about the example applications, see
[Get started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html).

Hello Service
-----------

Many of the services have a "Hello Service" folder with a starter project and README documentation. 
For example, the Amazon S3 Hello project is located at [example_code/s3/hello_s3](example_code/s3/hello_s3).

Docker image (Beta)
===================

This example code will soon be available in a container image
hosted on [Amazon Elastic Container Registry (ECR)](https://docs.aws.amazon.com/AmazonECR/latest/userguide/what-is-ecr.html). This image will be preloaded
with all C++ examples with dependencies pre-resolved, allowing you to explore
these examples in an isolated environment.

As of January 2023, the [SDK for C++ image](https://gallery.ecr.aws/b4v4v1s0/cpp) is available on ECR Public but is still
undergoing active development. Refer to
[this GitHub issue](https://github.com/awsdocs/aws-doc-sdk-examples/issues/4133)
for more information.

Build steps
-----------
To build the docker image, run the following command from the shell. This command must be run in
the "aws-doc-sdk-examples" directory, the parent directory of "cpp", in order to access the resources folder.

.. code-block:: bash

docker build -f cpp/Dockerfile -t <container_tag> .

The following command will run the docker image, copying your AWS credentials.

.. code-block:: bash

docker run -it --volume ~/.aws/credentials:/home/tests/.aws/credentials <container_tag>

Automated tests
===================

The automated tests for C++ examples can be run using the Python script [run_automated_tests.py](run_automated_tests.py). The script contains instructions for its use.

For information about the dependencies needed to run the examples, see the [DockerFile](Dockerfile).

Additional information
=============

- As an AWS best practice, grant all code least privilege, or only the permissions required to perform a task. For more information, see `Grant Least Privilege
  <https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege>`_ in the *AWS Identity and Access Management User Guide*.

- This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see `Region
  Table <https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/>`_ on the AWS website.

- Running this code might result in charges to your AWS account.

- To propose a new code example for the AWS documentation team to consider producing, create a
  new request. The team is looking to produce code examples that cover broader scenarios and use 
  cases, versus simple code snippets that cover only individual API calls. For instructions, see
  the Proposing new code examples section in the
  `Readme on GitHub <https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/README.rst>`_.

Copyright and License
=============

All content in this repository, unless otherwise stated, is 
Copyright © Amazon Web Services, Inc. or its affiliates. All rights reserved.

Except where otherwise noted, all examples in this collection are licensed under the `Apache
license, version 2.0 <https://www.apache.org/licenses/LICENSE-2.0>`_ (the "License"). The full
license text is provided in the `LICENSE` file accompanying this repository.
