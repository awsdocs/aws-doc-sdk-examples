# AWS Key Management Service (AWS KMS) .NET examples
Purpose

The code examples in this directory demonstrate how to work with AWS KMS using
the AWS SDK for .NET v3.5 or later.

AWS KMS presents a single control point to manage keys and define policies
consistently across integrated AWS services and your own applications. AWS KMS
is integrated with AWS CloudTrail to provide you with logs of all key usage to
help meet your regulatory and compliance needs.

## Code examples

The following AWS KMS code examples use the AWS SDK for .NET version 3.5 or later:

- [CreateAliasExample](CreateAliasExample/) - Create a new alias for an AWS KMS key.
- [CreateGrantExample](CreateGrantExample/) - Create a new AWS KMS grant.
- [CreateKeyExample](CreateKeyExample/) - Create a new AWS KMS key.
- [DescribeKeyExample](DescribeKeyExample/) - Describe an AWS KMS key.
- [DisableKeyExample](DisableKeyExample/) - Disable an AWS KMS key.
- [EnableKeyExample](EnableKeyExample/) - Enable an AWS KMS key.
- [ListAliasesExample](ListAliasesExample/) - List the AWS KMS aliases.
- [ListGrantsExample](ListGrantsExample/) - List AWS KMS grants.
- [ListKeysExample](ListKeysExample/) - List AWS KMS keys.

## ⚠️ Important

- We recommend that you grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide. 
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the examples

The examples in this folder use the default user account. The call to
initialize the AWS KMS client does not specify the AWS Region. Supply
the AWS Region to match your own as a parameter to the client constructor. For
example:

```
var client = new AmazonKeyManagementServicelient(Amazon.RegionEndpoint.USWest2);
```

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- .NET Core 5.0 or later
- AWS SDK for .NET 3.0 or later
- XUnit and Moq (to run unit tests)

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Or you can execute the example from within your IDE.

## Additional information
[AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

[AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

## Contributing

To propose a new code example to the AWS documentation team, see the
[CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/CONTRIBUTING.md).
The team prefers to create code examples that show broad scenarios rather than
individual API calls. 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
