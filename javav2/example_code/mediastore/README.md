# MediaStore code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with AWS Elemental MediaStore.

<!--custom.overview.start-->
<!--custom.overview.end-->

_MediaStore _

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateContainer](src/main/java/com/example/mediastore/CreateContainer.java#L6)
- [DeleteContainer](src/main/java/com/example/mediastore/CreateContainer.java#L6)
- [DeleteObject](src/main/java/com/example/mediastore/DeleteObject.java#L6)
- [DescribeContainer](src/main/java/com/example/mediastore/DescribeContainer.java#L6)
- [GetObject](src/main/java/com/example/mediastore/GetObject.java#L6)
- [ListContainers](src/main/java/com/example/mediastore/ListContainers.java#L6)
- [PutObject](src/main/java/com/example/mediastore/PutObject.java#L6)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->

#### Properties file

Before running the AWS Elemental MediaStore JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a container name used for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **containerName** - The name of a new container.
- **existingContainer** – The name of an existing container.
- **filePath** – The file location of an MP4 file to upload to a container.
- **putPath** – The container and location where a file is uploaded (for example, Videos5/sampleVideo.mp4).
- **getPath** – The container and location where a file is downloaded from (for example, Videos5/sampleVideo.mp4).
- **savePath** – The path on the local drive where the file is saved.

<!--custom.tests.end-->

## Additional resources

- [MediaStore User Guide](https://docs.aws.amazon.com/mediastore/latest/ug/what-is.html)
- [MediaStore API Reference](https://docs.aws.amazon.com/mediastore/latest/apireference/Welcome.html)
- [SDK for Java 2.x MediaStore reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/mediastore/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0