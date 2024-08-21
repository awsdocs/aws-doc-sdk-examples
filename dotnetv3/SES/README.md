# Amazon SES code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon Simple Email Service (Amazon SES).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon SES is a reliable, scalable, and cost-effective email service._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateTemplate](Actions/SESWrapper.cs#L262)
- [DeleteIdentity](Actions/SESWrapper.cs#L119)
- [DeleteTemplate](Actions/SESWrapper.cs#L343)
- [GetIdentityVerificationAttributes](Actions/SESWrapper.cs#L56)
- [GetSendQuota](Actions/SESWrapper.cs#L212)
- [ListIdentities](Actions/SESWrapper.cs#L27)
- [ListTemplates](Actions/SESWrapper.cs#L237)
- [SendEmail](Actions/SESWrapper.cs#L148)
- [SendTemplatedEmail](Actions/SESWrapper.cs#L301)
- [VerifyEmailIdentity](Actions/SESWrapper.cs#L87)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a web application to track DynamoDB data](../cross-service/DynamoDbItemTracker)
- [Create an Aurora Serverless work item tracker](../cross-service/AuroraItemTracker)
- [Detect objects in images](../cross-service/PhotoAnalyzerApp)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Create a web application to track DynamoDB data

This example shows you how to create a web application that tracks work items in an Amazon DynamoDB table and uses Amazon Simple Email Service (Amazon SES) (Amazon SES) to send reports.


<!--custom.scenario_prereqs.cross_DynamoDBDataTracker.start-->
<!--custom.scenario_prereqs.cross_DynamoDBDataTracker.end-->


<!--custom.scenarios.cross_DynamoDBDataTracker.start-->
<!--custom.scenarios.cross_DynamoDBDataTracker.end-->

#### Create an Aurora Serverless work item tracker

This example shows you how to create a web application that tracks work items in an Amazon Aurora Serverless database and uses Amazon Simple Email Service (Amazon SES) (Amazon SES) to send reports.


<!--custom.scenario_prereqs.cross_RDSDataTracker.start-->
<!--custom.scenario_prereqs.cross_RDSDataTracker.end-->


<!--custom.scenarios.cross_RDSDataTracker.start-->
<!--custom.scenarios.cross_RDSDataTracker.end-->

#### Detect objects in images

This example shows you how to build an app that uses Amazon Rekognition to detect objects by category in images.


<!--custom.scenario_prereqs.cross_RekognitionPhotoAnalyzer.start-->
<!--custom.scenario_prereqs.cross_RekognitionPhotoAnalyzer.end-->


<!--custom.scenarios.cross_RekognitionPhotoAnalyzer.start-->
<!--custom.scenarios.cross_RekognitionPhotoAnalyzer.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SES Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)
- [Amazon SES API Reference](https://docs.aws.amazon.com/ses/latest/APIReference/Welcome.html)
- [SDK for .NET Amazon SES reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/SimpleEmail/NSimpleEmail.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0