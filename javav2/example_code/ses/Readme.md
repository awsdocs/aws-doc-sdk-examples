# Amazon SES code examples for the SDK for Java

## Overview
This README discusses how to run and test the AWS SDK for Java (v2) code examples for Amazon Simple Email Service (Amazon SES).

Amazon SES is a cost-effective, flexible, and scalable email service that developers can use to send email.

## ⚠️ Important
* The AWS SDK for Java examples perform AWS operations for the account and AWS Region for which you've specified credentials. Running these examples might incur charges on your account. For details about the charges you can expect for a given service and API operation, see [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

The credential provider used in all code examples is ProfileCredentialsProvider. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

### Single action

The following examples use the **SesClient** object:

- [Obtains a list of identities for your AWS account](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/ses/src/main/java/com/example/ses/ListIdentities.java) (listIdentities command)
- [Sends an email message](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/ses/src/main/java/com/example/ses/SendMessage.java) (sendRawEmail command)
- [Sends an email message with an attachment](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/ses/src/main/java/com/example/ses/SendMessageAttachment.java) (sendRawEmail command)
- [Sends an email message using a SendEmailRequest object](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/ses/src/main/java/com/example/ses/SendMessageEmailRequest.java) (sendEmail command)


The following examples use the **SesV2Client** object:

- [Obtains a list of identities for your AWS account](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/ses/src/main/java/com/example/sesv2/ListIdentities.java) (listEmailIdentities command)
- [Sends an email message](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/ses/src/main/java/com/example/sesv2/SendEmail.java) (sendEmail command)
- [Sends an email message with an attachment](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/ses/src/main/java/com/example/sesv2/SendMessageAttachment.java) (sendEmail command)

**Note**: Both **SesClient** and **SesV2Client** are part of the AWS SDK for Java (v2). 

## Running the Amazon SES Java files

To run these examples, set up your development environment. For more information, 
see [Get started with the SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html). 


 ## Testing the Amazon SES Java files

You can test the Java code examples for Amazon SES by running a test file named **SESTest**. This file uses JUnit 5 to run the JUnit tests, and the file is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is run, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and may incur charges on your account._

 ### Properties file
Before running the Amazon Simple Email Service JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define an sender email address used for various tests. 
If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **sender** - An email address that represents the sender.   
- **recipient** - An email address that represents the recipient.
- **subject** - The subject line.
- **fileLocation** - The location of an Excel file to use as an attachment (for example, C:/AWS/WorkReport.xls).

**Note**: The email address that you send an email message to must be verified. For information, see [Verifying an email address](https://docs.aws.amazon.com/ses/latest/DeveloperGuide//verify-email-addresses-procedure.html).


## Additional resources
* [Developer Guide - AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)
* [Developer Guide - Amazon SES](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

