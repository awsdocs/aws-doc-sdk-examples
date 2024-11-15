# MediaConvert code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with AWS Elemental MediaConvert.

<!--custom.overview.start-->
<!--custom.overview.end-->

_MediaConvert is a service that formats and compresses offline video content for delivery to televisions or connected devices._

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

### Get started

- [Hello MediaConvert](Actions/HelloMediaConvert.cs#L4) (`DescribeEndpoints`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateJob](Scenarios/CreateJob/CreateJob.cs#L23)
- [GetJob](Scenarios/CreateJob/CreateJob.cs#L23)
- [ListJobs](Scenarios/CreateJob/CreateJob.cs#L23)


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

##### Configuration settings

The project includes the following settings in `settings.json`:

* `fileInput` - The Amazon Simple Storage Service (Amazon S3) location of the input media file.
* `fileOutput` - The Amazon S3 location for the output media file.
* `mediaConvertEndpoint` - The optional customer-specific endpoint.
* `mediaConvertRoleARN` - The Amazon Resource Name (ARN) of the MediaConvert role, as specified [here](https://docs.aws.amazon.com/mediaconvert/latest/ug/creating-the-iam-role-in-mediaconvert-configured.html).

<!--custom.instructions.end-->

#### Hello MediaConvert

This example shows you how to get started using AWS Elemental MediaConvert.



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [MediaConvert User Guide](https://docs.aws.amazon.com/mediaconvert/latest/ug/what-is.html)
- [MediaConvert API Reference](https://docs.aws.amazon.com/mediaconvert/latest/apireference/custom-endpoints.html)
- [SDK for .NET MediaConvert reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/MediaConvert/NMediaConvert.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0