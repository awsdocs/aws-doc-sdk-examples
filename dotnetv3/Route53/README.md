# Route 53 code examples for the SDK for .NET

## Overview
The examples in this section show how to use the AWS SDK for .NET with Amazon Route 53.

Route 53 is a highly available and scalable Domain Name System (DNS) web service.

## ⚠️ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

* [Hello Route 53 domain registration](Actions/HelloRoute53.cs)

### Single actions

Code excerpts that show you how to call individual service functions.

* [Check domain availability](Actions/Route53Wrapper.cs)(`CheckDomainAvailabilityAsync`)
* [Check domain transferability](Actions/Route53Wrapper.cs)(`CheckDomainTransferabilityAsync`)
* [Get domain details](Actions/Route53Wrapper.cs)(`GetDomainDetailAsync`)
* [Get operation details](Actions/Route53Wrapper.cs)(`GetOperationDetailAsync`)
* [Get suggested domain names](Actions/Route53Wrapper.cs)(`GetDomainSuggestionsAsync`)
* [List domains](Actions/Route53Wrapper.cs)(`ListDomainsAsync`)
* [List operations](Actions/Route53Wrapper.cs)(`ListOperationsAsync`)
* [List prices](Actions/Route53Wrapper.cs)(`ListPricesAsync`)
* [Register a domain](Actions/Route53Wrapper.cs)(`RegisterDomainAsync`)
* [View billing](Actions/Route53Wrapper.cs)(`ViewBillingAsync`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

* [Get started with domains](Scenarios/Route53DomainScenario.cs)

## Run the examples

### Prerequisites

* To find prerequisites for running these examples, see the
[README](../README.md#Prerequisites) in the dotnetv3 folder.

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Tests

⚠️ Running the tests might result in charges to your AWS account.

The solution includes a test project. To run the tests, navigate to the folder that contains the test project and then issue the following command:

```
dotnet test
```

Alternatively, you can open the example solution and use the Visual Studio Test Runner to run the tests.

## Additional resources
* [Amazon Route 53 Developer Guide](https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/index.html)
* [Amazon Route 53 API Reference](https://docs.aws.amazon.com/Route53/latest/APIReference/index.html)
* [AWS SDK for .NET Amazon Route 53 Domains](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Route53Domains/NRoute53Domains.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

