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

To build the examples, the following software must be installed and configured.

* AWS SDK for C++
* AWS credentials: Either configured in a local AWS credentials file or by 
  setting the AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY environment variables
* Microsoft Visual Studio (Windows) or a C++11 compiler (Linux/macOS)
* CMake version 3.2 or later

Create a build environment
==========================

Create a build root directory. The directory can be located either within 
the example directory structure or outside of it.

::

    mkdir sdk_example_builds
    cd sdk_example_builds

Create a build directory to store the compiled examples for a particular 
AWS service. Each service should have its own build directory.

::

    mkdir s3
    cd s3

Build the examples on Windows
=============================

Run CMake and MSBuild. The compiled and linked executable files are
located in the Debug directory.

::

    cd \<BUILD_ROOT_DIRECTORY>\<SERVICE>
    cmake -D CMAKE_PREFIX_PATH="C:/Program Files (x86)/aws-cpp-sdk-all/" /awsdocs/aws-doc-sdk-examples/cpp/example_code/<SERVICE>
    msbuild ALL_BUILD.vcxproj

Define the CMAKE_PREFIX_PATH variable to specify the directory 
where the AWS SDK for C++ was installed. Also specify the Code Catalog
directory where the service's examples are located.

On the MSBuild command line, specify the ALL_BUILD.vcxproj argument. 
Alternatively, in Microsoft Visual Studio, open the ALL_BUILD.vcxproj 
project and select Build > Build Solution.

Note: MSBuild is included with Microsoft Visual Studio. Its location 
depends on the Visual Studio version.

* 2019 Community Edition: "\\Program Files (x86)\\Microsoft Visual Studio\\2019\\Community\\MSBuild\Current\Bin\\"
* 2017 Community Edition: "\\Program Files (x86)\\Microsoft Visual Studio\\2017\\Community\\MSBuild\\15.0\\Bin\\amd64\\"

Build the examples on Linux/macOS
=================================

Run CMake and make. The compiled and linked executable files are
located in the build directory.

::

    cd /<BUILD_ROOT_DIRECTORY>/<SERVICE>
    sudo cmake /awsdocs/aws-doc-sdk-examples/cpp/example_code/<SERVICE>
    sudo make

On the CMake command line, specify the Code Catalog directory where
the service's examples are located.

If CMake does not locate the required C++11 compiler, specify its location
by defining CMake variables, as demonstrated below.

::

    sudo cmake -D CMAKE_C_COMPILER=/usr/local/bin/gcc -D CMAKE_CXX_COMPILER=/usr/local/bin/g++ /awsdocs/aws-doc-sdk-examples/cpp/example_code/<SERVICE>
