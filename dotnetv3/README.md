# README for AWS SDK for .NET 3.x documentation examples


This workspace contains both single service and cross-service examples for the AWS SDK for .NET.

All of the examples run on version 3.5 or later of the AWS SDK for .NET using .NET Core 5.0.
Each service directory also contains a **Readme.md** file with information about the code examples within that directory.

## AWS single-service examples

The AWS single-service C# .NET examples demonstrate how to perform simple API calls. They are divided into folders by the AWS service that they illustrate.

## AWS cross-service examples

AWS cross-service examples demonstrate how to combine multiple AWS services into a cohesive application. By following these examples, you will gain a deeper understanding of how to create C# .NET applications using .NET Core 5.0.

Each example in the cross-services example directory includes a README.md file that describes how to create and run the example.

## Prerequisites

To build and run the code examples for the AWS SDK for .NET, you need the following:

- The AWS SDK for .NET. For more information, see the [AWS SDK for .NET
Developer Guide](https://docs.aws.amazon.com/sdk-for-net/latest/developer-guide/welcome.html).

- AWS credentials, either configured in a local AWS credentials file, or by
setting the AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY environment variables.
For more information, see the [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/overview.html).

## Building and running the code examples

To build and run a code example, 
follow the instructions in the README file for the service.
In general, follow these steps:

1. Navigate to the directory containing a **.sln** file.
2. Build the solution using ```dotnet build SOLUTION.sln```, 
   where SOLUTION.sln is the name of the **.sln** file.
3. Navigate to the directory containing the code example
   and a **.csproj** file.
4. Run the project using the ```dotnet run``` command.

## Running the unit tests

All of the code example projects have a companion unit test,
where the name of the unit test project is the same as the tested project,
with a **Test** suffix.

We use [Xunit](https://xunit.net/) as the framework for our unit tests and
in most cases use [moq4](https://github.com/moq/moq4) to create unit tests with mocked objects.
You can create an Xunit project and install the **moq** Nuget package in that project with
the following commands:

```
dotnet new xunit -o MyTestProject
cd MyTestProject
dotnet add package moq
```

A typical unit test looks something like the following,
which tests a call to **CreateTableAsync** in the
**CreateTable** project:

```
using System.Net;
using System.Threading;
using System.Threading.Tasks;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

using Moq;

using Xunit;

namespace DynamoDBCRUD
{
    public class UnitTest1
    {
        private static readonly string _tableName = "testtable";

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.CreateTableAsync(
                It.IsAny<CreateTableRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<CreateTableRequest, CancellationToken>((request, token) =>
                {
                    if (!string.IsNullOrEmpty(_tableName))
                    {
                        bool areEqual = _tableName == request.TableName;
                        Assert.True(areEqual, "The provided table name is not the one used to create the table");
                    }
                })
                .Returns((CreateTableRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new CreateTableResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDBClient.Object;
        }

        [Fact]
        public async void CheckCreateTable()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();

            var result = await CreateTable.MakeTableAsync(client, _tableName);

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT create table " + _tableName);
        }
    }
}
```

To run this test,
navigate to the **CreateTableTest** folder and run:

```
dotnet test
```

If you want more information, run:

```
dotnet test -l "console;verbosity=detailed"
```

## Additional resources

- As an AWS best practice, grant all code least privilege, or only the permissions required to perform a task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the *AWS Identity and Access Management User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see [Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/) on the AWS website.
- Running this code might result in charges to your AWS account.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
