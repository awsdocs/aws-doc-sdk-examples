# S3 Glacier code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon S3 Glacier.

<!--custom.overview.start-->
<!--custom.overview.end-->

_S3 Glacier provides durable and extremely low-cost storage for infrequently used data with security features for data archiving and backup._

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

- [Hello Amazon S3 Glacier](Actions/HelloGlacier.cs#L4) (`ListVaults`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AddTagsToVault](Actions/GlacierWrapper.cs#L33)
- [CreateVault](Actions/GlacierWrapper.cs#L58)
- [DescribeVault](Actions/GlacierWrapper.cs#L83)
- [InitiateJob](Actions/GlacierWrapper.cs#L112)
- [ListJobs](Actions/GlacierWrapper.cs#L164)
- [ListTagsForVault](Actions/GlacierWrapper.cs#L188)
- [ListVaults](Actions/GlacierWrapper.cs#L213)
- [UploadArchive](Actions/GlacierWrapper.cs#L234)


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

#### Hello Amazon S3 Glacier

This example shows you how to get started using Amazon S3 Glacier.



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [S3 Glacier Developer Guide](https://docs.aws.amazon.com/amazonglacier/latest/dev/introduction.html)
- [S3 Glacier API Reference](https://docs.aws.amazon.com/amazonglacier/latest/dev/amazon-glacier-api.html)
- [SDK for .NET S3 Glacier reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Glacier/NGlacier.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0