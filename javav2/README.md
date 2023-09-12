# AWS SDK for Java (v2) code examples

To build and run these AWS SDK for Java (v2) code examples, you need the following:

* [Apache Maven](https://maven.apache.org/) (>3.0)
* [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/) (downloaded and extracted somewhere on
  your machine)
* **All Java (v2) examples assume that you have set up your credentials in the credentials file in the .aws folder**. For information about how to set AWS credentials and the AWS Region, see [Set up AWS credentials and Region for development](http://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/setup-credentials.html) in the *AWS SDK for Java Developer Guide*. You should also set the *AWS Region* within which the operations will be performed. If a Region is not set, the default Region used is **us-east-1**. 
  
  After you set your AWS credentials in the credentials file located in the .aws folder, you can create a service client like this.
  
           Region region = Region.US_WEST_2;
           S3Client s3 = S3Client.builder()
             .region(region)
             .build();


**Note**: For more information about setting your AWS credentials, see  [Supplying and retrieving AWS credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

## AWS SDK for Java

The **javav2** folder in this repository contains examples of complete use cases, and AWS service-based code examples.

### Use cases

In the **use_cases** folder, find step-by-step development tutorials that use multiple AWS services. By following these tutorials, you will gain a deeper understanding of how to create Java-based applications that use the AWS SDK for Java. Most of these AWS SDK for Java tutorials use the Synchronous Java client.

If you are interested in using Asynchronous Java service clients, see one of these tutorials:

+ [Creating a Feedback Sentiment Analyzer application using the SDK for Java](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_fsa_app) - Discusses how to develop a Feedback Sentiment Analyzer application using Machine Learning AWS services. The application solves a fictitious use case of a hotel that receives guest feedback on comment cards in a variety of languages. The application is developed by using the AWS SDK for Java (v2) and asynchronous Java clients.

+ [Creating a dynamic web application that asynchronously analyzes photos using the AWS SDK for Java](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_photo_analyzer_async) - Discusses using the AWS SDK for Java (asynchronous client) and various AWS services, such as the Amazon Rekognition service, to analyze images. This web MVC application can analyze many images and generate a report that breaks down each image into a series of labels.

+ [Creating an asynchronous publish/subscription web application that translates messages using the AWS SDK for Java](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_sns_async) - Discusses how to create a web MVC application that has subscription and publish functionality. In this tutorial, the Spring Framework is used with the AWS SDK for Java asynchronous client for Amazon Simple Notification Service (Amazon SNS).

The following tutorials use the synchronous Java client to build sample relational & and non-relational database applications:

+ [Creating your first AWS Java web application](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_first_project) - Discusses using Amazon DynamoDB, Amazon Simple Notification Service (Amazon SNS), and AWS Elastic Beanstalk to create a web application.

+ [Creating a React and Spring REST application that queries Amazon Redshift data](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/CreatingSpringRedshiftRest) - Discusses how to develop a Spring REST API that queries Amazon Redshift data. The Spring REST API uses the AWS SDK for Java to invoke AWS services and is used by a React application that displays the data.

+ [Creating a React and Spring REST application that queries Amazon Aurora Serverless data](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/Creating_Spring_RDS_%20Rest) - Discusses how to develop a Spring REST API that queries Amazon Aurora Serverless data. The Spring REST API uses the AWS SDK for Java to invoke AWS services and is used by a React application that displays the data.

+ [Creating a React and Spring REST application that queries Amazon RDS for MySQL](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/Creating_rds_item_tracker) - Discusses how to develop a Spring REST API that queries Amazon Relational Database Service (Amazon RDS) MySQL data. The Spring REST API uses the Java JDBC API to query MySQL data that is used by a React application that displays the data.

+ [Creating a React and Spring REST application that queries Amazon DynamoDB data](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_dynamodb_web_app) - Discusses how to develop a Spring REST API that queries Amazon DynamoDB data. The Spring REST API uses the AWS SDK for Java to invoke AWS services and is used by a React application that displays the data.

The following tutorials use the synchronous Java client to build sample applications:

+ [Creating a dynamic web application that analyzes photos using the AWS SDK for Java](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_photo_analyzer_app) - Discusses using the AWS SDK for Java and various AWS services, such as Amazon Rekognition, to analyze images. This web MVC applicatio analyzes many images and generate a report that breaks down each image into a series of labels.

+ [Creating a Spring Boot Application that has publish/subscription functionality](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_sns_sample_app) - Discusses how to create a web MVC application that has subscription and publish functionality. In this tutorial, the application is created using the Spring Framework with the AWS SDK for Java API for Amazon SNS. 

+ [Creating an  Amazon Simple Queue Service message application](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_message_application)  - Discusses using the AWS SDK for Java and the Amazon Simple Queue Service (Amazon SQS) to create a basic messaging web application.

+ [Building a Spring Boot web application that Streams Amazon S3 content over HTTP](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/create_spring_stream_app) - Discusses how to create a web MVC application that streams Amazon Simple Storage Service (Amazon S3) video content over HTTP. The video is displayed in the application’s view. In this tutorial, the Spring Framework is used with the AWS SDK for Java API to create the application.

+ [Using Amazon Cognito to require a user to log into a web application](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_amazon_cognito_app) - Discusses how to use Amazon Cognito to require a web MVC application to authenticate with users defined in an Amazon Cognito user pool.

+ [Building an Amazon Lex chatbot that handles multiple languages](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_lex_chatbot) - Discusses how to create an Amazon Lex chatbot within a web application to engage your website visitors. In addition, this chatbot supports multiple languages that lets users interact with the chatbot in the languages of their choice (for example, French).

+ [Create an Amazon personalize app with the AWS SDK for Java](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/create_amazon_personalize_app) -  Discusses how to complete the Amazon personalize workflow from start to finish with the AWS SDK for Java. The project trains two different models with the movie-lens dataset: one with the user-personalization (`aws-user-personalization`) recipe for creating personalized recommendations for your users, and one with the item-to-item similarities (`aws-sims`) recipe to generate recommendations for items that are similar to a given item.

The following tutorials are AWS Lambda use cases: 

+ [Creating a photo asset management application using the AWS SDK for Java](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/pam_source_files) - Discusses how to develop a photo asset management application that lets users manage photos using labels. This serverless application uses many AWS services.

+ [Creating AWS serverless workflows using the AWS SDK for Java](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_workflows_stepfunctions) - Discusses using the AWS SDK for Java and AWS Step Functions to create a workflow that invokes AWS services. Each workflow step is implemented by using an AWS Lambda function.

+ [Building an AWS Workflow that sends notifications over multiple channels](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/workflow_multiple_channels) - Discusses how to develop an AWS serverless workflow that sends notifications over multiple channels. In this AWS tutorial, you create an AWS serverless workflow by using AWS Step Functions, the AWS SDK for Java, and Lambda functions. Each workflow step is implemented by using an AWS Lambda function. 

+ [Creating an AWS serverless workflow that modifies Amazon Redshift data by using the AWS SDK for Java](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/create_workflow_redshift) - Discusses how to develop a workflow using AWS Step Functions that can modify Amazon Redshift data using the AWS SDK for Java.

+ [Creating an ETL workflow by using AWS Step Functions and the AWS SDK for Java](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/Creating_etl_workflow) - Discusses how to develop an AWS serverless workflow that performs an Extract, Transform, and Load (ETL) job. In this AWS tutorial, you create an AWS serverless workflow by using AWS Step Functions, the AWS SDK for Java, and Lambda functions. Each workflow step is implemented by using an AWS Lambda function.

+ [Creating scheduled events to invoke Lambda functions](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_scheduled_events) - Discusses how to create a scheduled event that invokes an AWS Lambda function by using Amazon CloudWatch Events. In addition, the AWS Lambda function is created by using the AWS Lambda Java runtime API and invokes multiple AWS services to perform a specific use case.

+ [Creating an Amazon Web Services Lambda function that detects images with Personal Protective Equipment](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_lambda_ppe) - Discusses how to create a Lambda function that detects personal protective equipment (PPE) in digital assets located in an Amazon S3 bucket. The Lambda function updates an Amazon DynamoDB table with the results and sends an email message. 

+ [Building an AWS workflow that sends notifications over multiple channels](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/workflow_multiple_channels) - Discusses how to develop an AWS serverless workflow that sends notifications over multiple channels. In this AWS tutorial, you create an AWS serverless workflow by using AWS Step Functions, the AWS SDK for Java, and Lambda functions. Each workflow step is implemented by using an AWS Lambda function. 

+ [Creating Lambda functions that tags digital assets located in Amazon S3 buckets](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_lambda_tag_assets) - Discusses how to create a Lambda function that automatically tags digital assets located in an Amazon Simple Storage Service (Amazon S3) bucket.

### AWS service examples

The AWS service-specific Java examples are located in the **example_code** folder. The examples are divided into directories by AWS service (**s3**, **sqs**, and so on). Within each, you'll find a **pom.xml** file used for building the examples with Maven.


## Build and run the service examples

### Build the examples using  Apache Maven or Gradle

To run these examples, you can setup your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information,  
see "Get started with the AWS SDK for Java 2.x" located at https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html.

**Note:** Add the POM file you find in a service-specific folder to the POM file in the project. Then create a package that you find in the examples and you can start adding the Java classes to your project.

### Build the examples from the command line

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


### Run the service examples

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

### Tests

You can test the Java code examples for a given AWS service by running a test file located in this GitHub repository. The test file uses JUnit 5 to run the JUnit tests. It's located in the **src/test/java** folder. For more information, see https://junit.org/junit5/.

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

WARNING: Running these JUnit tests manipulates real Amazon resources and might incur charges on your account.

 ### Properties file

Before running the JUnit tests, you must define required values in the **config.properties** file that's located in the **resources** folder. To learn what the values represent, you can refer to the Java code examples. For example, if you are running Amazon S3 tests, look in the various Amazon S3 code example Java files for an explanation of the values. 

### Docker image (Beta)

This example code will soon be available in a container image
hosted on [Amazon Elastic Container Registry (ECR)](https://docs.aws.amazon.com/AmazonECR/latest/userguide/what-is-ecr.html). This image will be pre-loaded
with all Java v2 examples with dependencies pre-resolved, allowing you to explore
these examples in an isolated environment.

⚠️ As of January 2023, the [SDK for Java v2 image](https://gallery.ecr.aws/b4v4v1s0/javav2) is available on ECR Public but is still
undergoing active development. Refer to
[this GitHub issue](https://github.com/awsdocs/aws-doc-sdk-examples/issues/4128)
for more information.