.. Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
   International License (the "License"). You may not use this file except in compliance with the
   License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions and
   limitations under the License.

######################################
AWS SDK for C++ Documentation Examples
######################################

This is a collection of examples for the AWS SDK for C++ public documentation.


Prerequisites
=============

To build and run these examples, you'll need:

* CMake (version 3.2+)
* AWS SDK for C++ (downloaded, extracted, built and installed on your machine)
* AWS credentials, either configured in a local AWS credentials file or by setting the
  AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY environment variables


Building the examples
=====================

To build the examples, create a build directory::

    mkdir s3_build

Go into the directory and run ``cmake``, providing it with the path to where you install the SDK if the path is not CMake awareness, and
the path to the examples directory that you want to build. For example::

    cd s3_build
    cmake -DCMAKE_PREFIX_PATH=/path/to/aws_sdk_installed/ ../example_code/s3

Finally, run ``make`` in the build directory:

    make


Running the examples
====================

The examples are built right into the build directory, so you can run them by name. On Unix, Linux
and OS X, make sure you specify the full path to the file (since you unlikely added the current
directory to your ``PATH``)::

    ./list_buckets

On **Windows**, you would run the same example as::

    list_buckets.exe

