# Amazon S3 code examples for the SDK for Swift

## Overview

Shows how to use the AWS SDK for Swift to work with Amazon Simple Storage Service (Amazon S3).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
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

### Single actions

Code excerpts that show you how to call individual service functions.

- [Copy an object from one bucket to another](basics/Sources/ServiceHandler/ServiceHandler.swift#L164) (`CopyObject`)
- [Create a bucket](basics/Sources/ServiceHandler/ServiceHandler.swift#L41) (`CreateBucket`)
- [Delete an empty bucket](basics/Sources/ServiceHandler/ServiceHandler.swift#L56) (`DeleteBucket`)
- [Delete an object](basics/Sources/ServiceHandler/ServiceHandler.swift#L183) (`DeleteObject`)
- [Delete multiple objects](DeleteObjects/Sources/ServiceHandler/ServiceHandler.swift#L54) (`DeleteObjects`)
- [Get an object from a bucket](basics/Sources/ServiceHandler/ServiceHandler.swift#L112) (`GetObject`)
- [List buckets](ListBuckets/Sources/ListBuckets/S3Session.swift#L68) (`ListBuckets`)
- [List objects in a bucket](basics/Sources/ServiceHandler/ServiceHandler.swift#L204) (`ListObjectsV2`)
- [Upload an object to a bucket](basics/Sources/ServiceHandler/ServiceHandler.swift#L70) (`PutObject`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with buckets and objects](basics/Sources/ServiceHandler/ServiceHandler.swift)


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



#### Get started with buckets and objects

This example shows you how to do the following:

- Create a bucket and upload a file to it.
- Download an object from a bucket.
- Copy an object to a subfolder in a bucket.
- List the objects in a bucket.
- Delete the bucket objects and the bucket.

<!--custom.scenario_prereqs.s3_Scenario_GettingStarted.start-->
<!--custom.scenario_prereqs.s3_Scenario_GettingStarted.end-->


<!--custom.scenarios.s3_Scenario_GettingStarted.start-->
<!--custom.scenarios.s3_Scenario_GettingStarted.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `swift` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [SDK for Swift Amazon S3 reference](https://awslabs.github.io/aws-sdk-swift/reference/0.x/AWSS3/Home)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0