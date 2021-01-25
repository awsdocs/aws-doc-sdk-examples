.. Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
   International License (the "License"). You may not use this file except in compliance with the
   License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions and
   limitations under the License.

###########################################
AWS SDK for Java 2.0 documentation examples
###########################################

These are examples for the `AWS SDK for Java public documentation <javasdk-docs_>`_.

Prerequisites
=============

To build and run these examples, you need the following:

* `Apache Maven <https://maven.apache.org/>`_ (>3.0)
* `AWS SDK for Java <https://aws.amazon.com/sdk-for-java/>`_ (downloaded and extracted somewhere on
  your machine)
* **All Java Version 2 examples assume that you have setup your credentials in the credentials file in the .aws folder**. For information about how to set AWS credentials and the AWS Region, see `Set up AWS credentials and Region for development <http://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/setup-credentials.html>`_ in the *AWS SDK for Java Developer Guide*. You should also set the *AWS Region* within which the operations will be performed. If a Region is not set, the default Region used is **us-east-1**. 
  
  After you set your AWS credentails in the credentials file located in the .aws folder, you can create a service client like this.
  
           Region region = Region.US_WEST_2;
         
           S3Client s3 = S3Client.builder()
             .region(region)
             .build();


**Note**: For more information about setting your AWS credentials, see  `Supplying and retrieving AWS credentials <https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html/>`_.

AWS Java code examples
======================

The **javav2** folder in this repository contains examples of complete use cases, and AWS service-based code examples.

Use cases
---------

In the **use_cases** folder, find step-by-step development tutorials that use multiple AWS services. By following these tutorials, you will gain a deeper understanding of how to create Java-based applications that use the AWS SDK for Java. These tutorials include:

+ **Creating your first AWS Java web application** - A tutorial that discusses using Amazon DynamoDB, Amazon Simple Notification Service (Amazon SNS), and AWS Elastic Beanstalk.

+ **Creating_rds_item_tracker** - A tutorial that discusses using Amazon Relational Database Service (Amazon RDS), Amazon Simple Email Service (Amazon SES), and AWS Elastic Beanstalk to create a web application that tracks work items.

+ **Creating_dynamodb_item_tracker** - A tutorial that discusses using Amazon DynamoDB, Amazon Simple Email Service (Amazon SES), and AWS Elastic Beanstalk to create a web application that tracks work items.

+ **Creating AWS serverless workflows using the AWS SDK for Java** - A tutorial that discusses using the AWS SDK for Java and AWS Step Functions to create a workflow that invokes AWS services. Each workflow step is implemented by using an AWS Lambda function.

+ **Creating a sample AWS photo analyzer application using the AWS SDK for Java** - A tutorial that discusses using the AWS SDK for Java and various AWS services, such as the  Amazon Rekognition service, to analyze images. The application can analyze many images and generate a report that breaks down each image into a series of labels.

+ **Creating_message_application** - A tutorial that discusses using the AWS SDK for Java and the Amazon Simple Queue Service (Amazon SQS) to create a basic messaging web application.

+ **Creating scheduled events to invoke  Lambda functions** - A tutorial that discusses how to create a scheduled event that invokes an AWS Lambda function by using Amazon CloudWatch Events. In addition, the AWS Lambda function is created by using the AWS Lambda Java runtime API and invokes multiple AWS services to perform a specific use case.

+ **Using Amazon Cognito to require a user to log into a web application** - A tutorial that discusses how to use Amazon Cognito to require a web application to authenticate with users defined in an Amazon Cognito User Pool. 

+ **Creating AWS video analyzer applications using the AWS SDK for Java** - A tutorial that discusses using the AWS SDK for Java and various AWS services, such as the  Amazon Rekognition service, to analyze videos. The application analyzes a video located in an Amazon S3 bucket and generates and emails a report that breaks the video into a series of labels.

+ **Using Amazon API Gateway to invoke Lambda functions** - A tutorial that discusses how to use Amazon API Gateway to invoke a Lambda function. In addition, the AWS Lambda function is created by using the AWS Lambda Java runtime API and invokes multiple AWS services to perform a specific use case.

+ **Building an Amazon multiple language Chatbot** - A tutorial that discusses how to create an Amazon Lex chatbot within a web application to engage your web site visitors. In addition, this chatbot supports multiple languages that lets users communiciate with the chatbot in the languages of their choice (for example, French).  


AWS service examples
--------------------

The AWS service-specific Java examples are located in the **example_code** folder. The examples are divided into directories by AWS service (**s3**, **sqs**, and so on). Within
each, you'll find a **pom.xml** file used for building the examples with Maven, and a **Makefile**
that wraps the Maven commands for those of you who also have **make** installed.


Build and run the service examples
==================================

Build the examples using  Apache Maven or Gradle
------------------------------------------------

To run these examples, you can setup your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information,  
see "Get started with the AWS SDK for Java 2.x" located at https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html.

**Note:** Add the POM file you find in a service-specific folder to the POM file in the project. Then create a package that you find in the examples and you can start adding the Java classes to your project.

Build the examples from the command line
-----------------------------------------

To build any of the service examples, open a command-prompt (terminal) window and change to the directory containing the examples
you want to build or run. Then type::

   mvn package

You can use the Apache Maven Shade Plugin to package your JAR file with the artifacts in an uber JAR, which consists of all dependencies required to run the project. Ensure that the POM file has the required plugin to build the JAR with the dependencies.


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



For example, if you execute this command from the **s3** directory, you will find a JAR file named **S3J2Project-1.0-SNAPSHOT.jar** in the **target** folder.

Or, if you have **make**, you can begin the build process by typing::

   make

Maven will download any dependencies (such as components of the AWS SDK
for Java) that it needs for building.

Once the examples are built, you can run them to see them in action.

.. note:: If you are running on a platform with **make**, you can also use the provided Makefiles to
   build the examples, by running **make** in any directory with a **Makefile** present. You must
   still have Maven installed, however (the Makefile wraps Maven commands).


Run the service examples
------------------------

**IMPORTANT**

   The examples perform AWS operations for the account and AWS Region for which you've specified
   credentials, and you may incur AWS service charges by running them. See the `AWS Pricing
   <https://aws.amazon.com/pricing/>`_ page for details about the charges you can expect for a given
   service and operation.

   Some of these examples perform *destructive* operations on AWS resources, such as deleting an
   Amazon S3 bucket or an Amazon DynamoDB table. **Be very careful** when running an operation that
   may delete or modify AWS resources in your account. It's best to create separate test-only
   resources when experimenting with these examples.

Because you built the JAR file that contains the dependencies, you can run an example using the following command. For example, you can run an S3 Java V2 example using this command:

          java -cp target/S3J2Project-1.0-SNAPSHOT.jar com.example.s3.ListObjects mybucket

For systems with Bash support
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

As an alternative to setting the **CLASSPATH** and specifying the full namespace of the class to
run, we've included a **bash** script, **run_example.sh**, that you can use on Linux, Unix, or OS X
(or on Windows by using `Cygwin <https://www.cygwin.com/>`_, `MingW <http://www.mingw.org/>`_, or
`Bash on Ubuntu on Windows <https://msdn.microsoft.com/en-us/commandline/wsl/about>`_).

You can execute **run_example.sh** as shown::

    ./run_example.sh S3BucketOps

This runs the `S3BucketOps <example_code/s3/src/main/java/com/example/s3/S3BucketOps.java>`_
example (assuming that you've built the examples first).

If the example requires arguments, pass the argument list in quotation marks::

  ./run_example.sh S3BucketOps "<arg1> <arg2> <arg3>"

.. _maven: https://maven.apache.org/
.. _javasdk: https://aws.amazon.com/sdk-for-java/
.. _javasdk-docs: http://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/
