# Amazon S3 code examples for the SDK for Swift

## Overview

Shows how to use the AWS SDK for Swift to work with Amazon Simple Storage Service (Amazon S3).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `swift` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](basics/Sources/ServiceHandler/ServiceHandler.swift)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CopyObject](basics/Sources/ServiceHandler/ServiceHandler.swift#L232)
- [CreateBucket](basics/Sources/ServiceHandler/ServiceHandler.swift#L56)
- [DeleteBucket](basics/Sources/ServiceHandler/ServiceHandler.swift#L87)
- [DeleteObject](basics/Sources/ServiceHandler/ServiceHandler.swift#L257)
- [DeleteObjects](DeleteObjects/Sources/ServiceHandler/ServiceHandler.swift#L59)
- [GetObject](basics/Sources/ServiceHandler/ServiceHandler.swift#L163)
- [ListBuckets](ListBuckets/Sources/ListBuckets/S3Session.swift#L106)
- [ListObjectsV2](basics/Sources/ServiceHandler/ServiceHandler.swift#L280)
- [PutObject](basics/Sources/ServiceHandler/ServiceHandler.swift#L107)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Download stream of unknown size](binary-streaming/Sources/streamdown/streamdown.swift)
- [Upload stream of unknown size](binary-streaming/Sources/streamup/streamup.swift)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

To build any of these examples from a terminal window, navigate into its
directory, then use the following command:

```
$ swift build
```

To build one of these examples in Xcode, navigate to the example's directory
(such as the `ListUsers` directory, to build that example). Then type `xed.`
to open the example directory in Xcode. You can then use standard Xcode build
and run commands.

<!--custom.instructions.start-->
<!--custom.instructions.end-->


#### Learn the basics

This example shows you how to do the following:

- Create a bucket and upload a file to it.
- Download an object from a bucket.
- Copy an object to a subfolder in a bucket.
- List the objects in a bucket.
- Delete the bucket objects and the bucket.

<!--custom.basic_prereqs.s3_Scenario_GettingStarted.start-->
<!--custom.basic_prereqs.s3_Scenario_GettingStarted.end-->


<!--custom.basics.s3_Scenario_GettingStarted.start-->
<!--custom.basics.s3_Scenario_GettingStarted.end-->


#### Download stream of unknown size

This example shows you how to download a stream of unknown size from an Amazon S3 object.


<!--custom.scenario_prereqs.s3_Scenario_DownloadStream.start-->
<!--custom.scenario_prereqs.s3_Scenario_DownloadStream.end-->


<!--custom.scenarios.s3_Scenario_DownloadStream.start-->
<!--custom.scenarios.s3_Scenario_DownloadStream.end-->

#### Upload stream of unknown size

This example shows you how to upload a stream of unknown size to an Amazon S3 object.


<!--custom.scenario_prereqs.s3_Scenario_UploadStream.start-->
<!--custom.scenario_prereqs.s3_Scenario_UploadStream.end-->


<!--custom.scenarios.s3_Scenario_UploadStream.start-->
<!--custom.scenarios.s3_Scenario_UploadStream.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `swift` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [SDK for Swift Amazon S3 reference](https://sdk.amazonaws.com/swift/api/awss3/latest/documentation/awss3)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
