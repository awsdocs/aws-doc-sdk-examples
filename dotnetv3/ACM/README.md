# ACM code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET with AWS Certificate Manager (ACM)
to request, import, and manage certificates.

ACM makes it easy to provision, manage, and deploy SSL/TLS certificates on AWS
managed resources.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Describe a certificate](DescribeCertificates/DescribeCertificate/DescribeCertificate.cs) (`DescribeCertificateAsync`)
* [List certificates](ListCertificates/ListCertificates/ListCertificates.cs) (`ListCertificatesAsync`)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

The examples in this folder use the default user account. The call to
initialize the client object does not specify the AWS region. Supply
the AWS region to match your own as a parameter to the client constructor. For
example:

```
var client = new AmazonCertificateManagerClient(Amazon.RegionEndpoint.USWest2);
```

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [ACM User Guide](https://docs.aws.amazon.com/acm/latest/userguide/acm-overview.html)
* [ACM API Reference](https://docs.aws.amazon.com/acm/latest/APIReference/Welcome.html)
* [AWS SDK for .NET ACM](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/CertificateManager/NCertificateManager.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0