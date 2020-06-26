.. Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
   International License (the "License"). You may not use this file except in compliance with the
   License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions and
   limitations under the License.

###########################################
AWS SDK for Java 2.0 Documentation Examples
###########################################

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
<http://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/setup-credentials.html>`_ in the *AWS
Java Developer Guide*.

AWS Java code examples
======================

The javav2 folder in this repository contains examples of complete use cases, as well as AWS service-based code examples.

Use cases
---------

Under the **use_cases** folder, you will locate step by step development tutorials that use multiple AWS services. By following these tutorials, you will gain a deeper understanding on how to create Java based applications that use the AWS Java SDK. You will find these tutorials:

+ **Creating your first AWS Java Web Application** - A tutorial that discusses using Amazon DynamoDB, Amazon Simple Notification Service (Amazon SNS) and AWS Elastic Beanstalk.
+ **Creating a Secure Spring Application using AWS Services** - A tutorial that discusses using Amazon Relational Database Service (RDS), Amazon Simple Email Service (SES), and AWS Elastic Beanstalk.
+ **Creating AWS Serverless workflows using the Java SDK** - A tutorial that discusses using the AWS Java SDK and AWS Step Functions to create a workflow that invokes AWS services. Each workflow step is implemented by using a Lambda function.

+ **Creating a Sample AWS Photo Analyzer application using the AWS SDK for Java** - A tutorial that discusses using the AWS Java SDK and various AWS Services, such as the  Amazon Rekognition service, to analyze images. The application can analyze many images and generate a report that breaks down each image into a series of labels.

AWS service examples
--------------------

The AWS service specific Java examples are located under the **example_code** folder. The examples are divided into directories by AWS service (``s3``, ``sqs``, and so on). Within
each, you'll find a ``pom.xml`` file used for building the examples with Maven, and a ``Makefile``
that wraps the Maven commands for those of you who also have ``make`` installed.





Build and run the service examples
==================================

Build the examples from a Java IDE 
----------------------------------

You can build the examples in a Java IDE such as IntelliJ. Create a Maven project and be sure to include the POM file you locate in a service directory in your project. This is the easiest way to start building and running the AWS Java examples. The POM file ensures you have access to the Java dependencies. To create an IntelliJ project. 

1. In the IntelliJ IDE, choose **File**, **New**, **Project**. 
2. In the **New Project** dialog box, choose **Maven**. 
3. Choose **Next**.
4. In **GroupId**, enter **aws-project**. 
5. In **ArtifactId**, enter **aws-project**. 
6. Choose **Next**.
7. Choose **Finish**. 

**Note**: Add the POM file you find in a service specific folder to the POM file in the project. Then create a package that you find in the examples and you can start adding the Java classes to your project. 

Build the examples from the command line 
-----------------------------------------

To build any of the service examples, open a command-line (terminal) window and change to the directory containing the examples
you want to build/run. Then type::

   mvn package

You can use the Apache Maven Shade Plugin to package your JAR with the artifacts in an uber-jar, which consists of all dependencies required to run the project. Ensure that the POM file has the required plugin to build the JAR with the dependencies. 

   
    <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       
       <artifactId>maven-shade-plugin</artifactId>
       
       <version>3.0.0</version>
       
       <executions>
       
       <execution>
       
       <phase>package</phase>
       
       <goals>
       
       <goal>shade</goal>
       
       </goals>
       
       </execution>
       
       </executions>
       
       </plugin>
     
    
          
For example, if you execute this command from the S3 directory, you will find a JAR file named **S3J2Project-1.0-SNAPSHOT.jar** in the **target** folder.           

or, if you have ``make``, you can simply type::

   make

to begin the build process. Maven will download any dependencies (such as components of the AWS SDK
for Java) that it needs for building.

Once the examples have been built, you can run them to see them in action.

.. note:: If you are running on a platform with ``make``, you can also use the provided Makefiles to
   build the examples, by running ``make`` in any directory with a ``Makefile`` present. You must
   still have Maven installed, however (the Makefile wraps Maven commands).


Run the service examples
------------------------

**IMPORTANT**

   The examples perform AWS operations for the account and region for which you've specified
   credentials, and you may incur AWS service charges by running them. Please visit the `AWS Pricing
   <https://aws.amazon.com/pricing/>`_ page for details about the charges you can expect for a given
   service and operation.

   Some of these examples perform *destructive* operations on AWS resources, such as deleting an
   Amazon S3 bucket or an Amazon DynamoDB table. **Be very careful** when running an operation that
   may delete or modify AWS resources in your account. It's best to create separate test-only
   resources when experimenting with these examples.

Because you built the JAR that contains the depedencies, you can run an example using the following command. For example, you can run a S3 Java V2 example using this command:

          java -cp target/S3J2Project-1.0-SNAPSHOT.jar com.example.s3.ListObjects mybucket

For systems with bash support
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

As an alternative to setting the ``CLASSPATH`` and specifying the full namespace of the class to
run, we've included a ``bash`` script, ``run_example.sh``, that you can use on Linux, Unix or OS X
(or on Windows by using `Cygwin <https://www.cygwin.com/>`_, `MingW <http://www.mingw.org/>`_, or
`Bash on Ubuntu on Windows <https://msdn.microsoft.com/en-us/commandline/wsl/about>`_).

You can execute ``run_example.sh`` as shown::

    ./run_example.sh S3BucketOps

This will run the `S3BucketOps <example_code/s3/src/main/java/com/example/s3/S3BucketOps.java>`_
example (assuming that you've built the examples first!).

If the example requires arguments, pass the argument list in quotes::

  ./run_example.sh S3BucketOps "<arg1> <arg2> <arg3>"

.. _maven: https://maven.apache.org/
.. _javasdk: https://aws.amazon.com/sdk-for-java/
.. _javasdk-docs: http://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/
