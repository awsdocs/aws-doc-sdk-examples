# Amazon S3 code examples for the AWS SDK for .NET (v4)

## Overview

The examples in this section demonstrate how to use the AWS SDK for .NET (v4) with Amazon Simple Storage Service (Amazon S3).

Amazon S3 is an object storage service that offers industry-leading scalability, data availability, security, and performance. You can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web.

## ⚠️ Important

- Running these code examples can result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single-service actions

Code examples that show you how to call individual Amazon S3 service functions.

- [CreatePresignedPost](Actions/CreatePresignedPost.cs) - Shows how to create a basic presigned POST URL.
- [CreatePresignedPostWithMetadata](Actions/CreatePresignedPostWithMetadata.cs) - Shows how to add custom metadata to presigned POST URLs.
- [CreatePresignedPostWithConditions](Actions/CreatePresignedPostWithConditions.cs) - Shows how to add upload restrictions such as content type and file size.
- [CreatePresignedPostWithFilename](Actions/CreatePresignedPostWithFilename.cs) - Shows how to preserve the original filename when using presigned POST URLs.

### Scenarios

Code examples that show you how to accomplish specific tasks by calling multiple Amazon S3 functions.

- [S3 CreatePresignedPost Scenario](Scenarios/S3_CreatePresignedPost/) - Shows how to create and use presigned POST URLs with Amazon S3. The scenario demonstrates:
  1. Creating an S3 bucket
  2. Creating a presigned POST URL
  3. Uploading a file using the presigned POST URL
  4. Cleaning up resources after use

## Running the examples

### Prerequisites

- An AWS account. To create an account, see [AWS Free Tier](https://aws.amazon.com/free/).
- AWS credentials. For details, see the [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- .NET 8.0 or later. For installation instructions, see the [.NET website](https://dotnet.microsoft.com/download).

### Instructions

The example code in this folder uses the AWS SDK for .NET (v4) to interact with Amazon S3.

1. Clone the [AWS SDK Code Examples](https://github.com/awsdocs/aws-doc-sdk-examples) repository.
2. Open a terminal or command prompt and navigate to the `dotnetv4/S3` directory.

#### To build and run the CreatePresignedPost scenario:

1. Navigate to the scenario folder:
   ```
   cd Scenarios/S3_CreatePresignedPost
   ```

2. Build the scenario:
   ```
   dotnet build
   ```

3. Run the scenario in interactive mode (default):
   ```
   dotnet run
   ```
   
   Or run in non-interactive mode:
   ```
   dotnet run -- --non-interactive
   ```

The scenario will:
- Create a temporary S3 bucket
- Create a presigned POST URL
- Upload a test file using the presigned POST URL
- Verify that the file was successfully uploaded to S3
- Clean up the resources it created

In interactive mode, the scenario will pause after each step and wait for you to press Enter to continue.

#### To build and run the standalone examples:

1. Navigate to the Actions folder:
   ```
   cd Actions
   ```

**To run CreatePresignedPost example (the default):**

```bash
dotnet build
dotnet run
```

The project is configured to use S3Actions.CreatePresignedPost as the default startup class when no other is specified.

The standalone example will:
- Create a basic presigned POST URL using the fixed bucket name "amzn-s3-demo-bucket"
- Display the URL and form fields needed for a browser upload

#### To run other examples:

You need to specify the StartupObject during the build phase, not during the run phase. The project will use whatever StartupObject you specify, or fall back to CreatePresignedPost if none is provided:

**To run CreatePresignedPostWithMetadata example:**

```bash
# If switching between examples, clean first to clear previous settings
dotnet clean

# Build with the specific StartupObject
dotnet build /p:StartupObject=S3Actions.CreatePresignedPostWithMetadata

# Run the example
dotnet run
```

**To run CreatePresignedPostWithConditions example:**

```bash
# If switching between examples, clean first to clear previous settings
dotnet clean

# Build with the specific StartupObject
dotnet build /p:StartupObject=S3Actions.CreatePresignedPostWithConditions

# Run the example
dotnet run
```

**To run CreatePresignedPostWithFilename example:**

```bash
# If switching between examples, clean first to clear previous settings
dotnet clean

# Build with the specific StartupObject
dotnet build /p:StartupObject=S3Actions.CreatePresignedPostWithFilename

# Run the example
dotnet run
```

> Note: The cleaning step is important when switching between different examples to ensure the new StartupObject setting takes effect. If you're building a specific example for the first time in a new terminal session, you might be able to skip the clean step.

## Additional resources

- [Amazon S3 Developer Guide](https://docs.aws.amazon.com/AmazonS3/latest/dev/Welcome.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [AWS SDK for .NET (v4) Amazon S3 Reference](https://docs.aws.amazon.com/sdkfornet/v4/apidocs/items/S3/NS3.html)
- [AWS SDK for .NET (v4) Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v4/developer-guide/welcome.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
