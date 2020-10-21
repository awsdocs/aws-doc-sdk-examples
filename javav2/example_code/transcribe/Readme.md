# Amazon Transcribe Java code examples

This README discusses how to run and test the Java code examples for Amazon Transcribe.

## Running the Amazon Transcribe Java files

**IMPORTANT**

The Java examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

To run these examples, you'll need the AWS SDK for Java libraries in your **CLASSPATH**.

	export CLASSPATH=target/sdk-examples-1.0.jar:/path/to/aws-java-sdk/<jar-file-name>.jar

Here **/path/to/aws-java-sdk/<jar-file-name>.jar** is the path to where you extracted or built the AWS SDK for Java JAR file.

For systems with Bash support, once you set the **CLASSPATH**, you can run a particular example as follows.

	java com.amazonaws.transcribe.BidirectionalStreaming


 ## Testing the  Amazon Transcribe files

You can test the Java code examples for  Amazon Transcribe by running a test file named **TranscribeTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can execute the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. 

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and may incur charges on your account._

 ### Command line
To execute the JUnit tests from the command line, you can use the following command.

		mvn test

The second test remains running until you stop the test. You can talk into your microphone and see the results written out on the console. You can manually stop the test.  
