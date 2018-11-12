.. Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
   International License (the "License"). You may not use this file except in compliance with the
   License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions and
   limitations under the License.

#######################################
AWS SDK for Java Documentation Examples
#######################################

These are examples for the `AWS SDK for Java public documentation <javasdk-docs_>`_.

Prerequisites
=============

To build and run these examples, you'll need:

* `Apache Maven <https://maven.apache.org/>`_ (>3.0)
* `AWS SDK for Java <https://aws.amazon.com/sdk-for-java/>`_ (downloaded and extracted somewhere on
  your machine)
* AWS credentials, either configured in a local AWS credentials file or by setting the
  ``AWS_ACCESS_KEY_ID`` and ``AWS_SECRET_ACCESS_KEY`` environment variables.
* You should also set the *AWS region* within which the operations will be performed. If a region is
  not set, the default region used will be ``us-east-1``.

For information about how to set AWS credentials and the region for use with the AWS SDK for Java,
see `Set up AWS Credentials and Region for Development
<http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html>`_ in the *AWS
Java Developer Guide*.

Building the examples
=====================

The examples are divided into directories by AWS service (``s3``, ``dynamodb``, and so on). Within
each, you'll find a ``pom.xml`` file used for building the examples with Maven, and a ``Makefile``
that wraps the Maven commands for those of you who also have ``make`` installed.

To build, open a command-line (terminal) window and change to the directory containing the examples
you want to build/run. Then type::

   mvn package

or, if you have ``make``, you can simply type::

   make

to begin the build process. Maven will download any dependencies (such as components of the AWS SDK
for Java) that it needs for building.

Once the examples have been built, you can run them to see them in action.

.. note:: If you are running on a platform with ``make``, you can also use the provided Makefiles to
   build the examples, by running ``make`` in any directory with a ``Makefile`` present. You must
   still have Maven installed, however (the Makefile wraps Maven commands).


Running the examples
====================

**IMPORTANT**

   The examples perform AWS operations for the account and region for which you've specified
   credentials, and you may incur AWS service charges by running them. Please visit the `AWS Pricing
   <https://aws.amazon.com/pricing/>`_ page for details about the charges you can expect for a given
   service and operation.

   Some of these examples perform *destructive* operations on AWS resources, such as deleting an
   Amazon S3 bucket or an Amazon DynamoDB table. **Be very careful** when running an operation that
   may delete or modify AWS resources in your account. It's best to create separate test-only
   resources when experimenting with these examples.

To run these examples, you'll need the AWS SDK for Java libraries in your ``CLASSPATH``::

    On Unix, run open the command line and execute:
    export CLASSPATH="/path/to/aws-java-sdk/lib/*:/path/to/aws-java-sdk/third-party/lib/*:$CLASSPATH"

    On Windows, open cmd and execute:
    set CLASSPATH="path/to/aws-java-sdk/lib/*;path/to/aws-java-sdk/third-party/lib/*;%CLASSPATH%"

Where ``/path/to/aws-java-sdk`` is the path to where you extracted the AWS Java SDK download (it
contains the ``lib`` and ``third-party/lib`` directories). For example, the path can be
``/tmp/aws-java-sdk``, or ``C:\aws-java-sdk``.

Once you set the ``CLASSPATH``, you can run a particular example like this::

    java aws.example.s3.ListBuckets -cp target/s3examples-1.0.jar:$CLASSPATH

For systems with bash support
-----------------------------

As an alternative to setting the ``CLASSPATH`` and specifying the full namespace of the class to
run, we've included a ``bash`` script, ``run_example.sh``, that you can use on Linux, Unix or OS X
(or on Windows by using `Cygwin <https://www.cygwin.com/>`_, `MingW <http://www.mingw.org/>`_, or
`Bash on Ubuntu on Windows <https://msdn.microsoft.com/en-us/commandline/wsl/about>`_).

To use the script, set the path to the Java SDK directory in the ``JAVA_SDK_HOME`` environment
variable. For example::

    export JAVA_SDK_HOME=/path/to/aws-java-sdk

Once you've set the variable, you can execute ``run_example.sh`` as shown::

    ./run_example.sh ListBuckets

This will run the `ListBuckets <example_code/s3/src/main/java/aws/example/s3/ListBuckets.java>`_
example (assuming that you've built the examples first!).

.. _maven: https://maven.apache.org/
.. _javasdk: https://aws.amazon.com/sdk-for-java/
.. _javasdk-docs: http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/
