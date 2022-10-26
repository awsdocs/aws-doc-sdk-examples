# Amazon Rekognition code examples for the AWS SDK for Go (v2)

## Overview

The examples in this directory demonstrate how to perform Amazon Rekognition
actions using the AWS SDK for Go (v2).

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only
  the minimum permissions required to perform the task. For more information,
  see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

- [Detect faces in an image](DetectFaces/DetectFaces.go) (`DetectFaces`)
- [Detect labels in an image](DetectLabels/DetectLabels.go) (`DetectLabels`)

## Run the examples

### Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go V2](https://aws.github.io/aws-sdk-go-v2/docs/configuring-sdk/)
in the AWS SDK for Go V2 Developer Guide.

### Instructions

Go to the directory where you want to run the sample, and do the following:

```
go mod tidy
go run .
```

## Tests

⚠️ Running the tests might result in charges to your AWS account.

To run a unit test, enter:

`go test`

You should see something like the following,
where PATH is the path to the folder containing the Go files:

```sh
PASS
ok      PATH 6.593s
```

If you want to see any log messages, enter:

`go test -v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

## Additional resources

[Rekognition developer guide](https://docs.aws.amazon.com/rekognition/latest/dg/what-is.html)
[Rekognition API reference](https://docs.aws.amazon.com/rekognition/latest/APIReference/Welcome.html)
[AWS SDK for Go (v2) API reference guide](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/rekognition)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
