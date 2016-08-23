.. Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
   International License (the "License"). You may not use this file except in compliance with the
   License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions and
   limitations under the License.

#######################################
AWS SDK for Java Documentation Examples
#######################################

These are examples for the AWS SDK for Java public documentation.

Prerequsites
============

To build and run these examples, you'll need:

* Apache Maven (>3.0)
* AWS SDK for Java (downloaded and extracted somewhere on your machine)
* AWS credentials, either configured in a local AWS credentials file or by setting the
  AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY environment variables

Building the examples
=====================

To build, go to the directory with ``pom.xml`` in it and type::

    mvn package

It will download the dependencies it needs for building.

Running the examples
====================

To run the examples, you'll need the AWS SDK for Java libraries in your CLASSPATH. You can set them
directly, such as::

    export CLASSPATH=/path/to/aws-java-sdk/lib/*:/path/to/aws-java-sdk/third-party/lib/*

Where ``/path/to/aws-java-sdk`` is the path to where you extracted the AWS Java SDK download (it
should contain the ``lib`` and ``third-party/lib`` directories).

You can then run it like this::

    java aws.example.s3.ListBuckets -cp target/s3examples-1.0.jar:$CLASSPATH

I've included a ``bash`` script that you can run on most systems that assumes you've set the path to
the Java SDK directory in the ``JAVA_SDK_HOME`` environment variable. For example::

    export JAVA_SDK_HOME=/path/to/aws-java-sdk

Once you've set the variable, you can execute ``run_example.sh`` as shown::

    ./run_example.sh ListBuckets

and it will run the ListBuckets example (assuming that you've built the examples first!).

