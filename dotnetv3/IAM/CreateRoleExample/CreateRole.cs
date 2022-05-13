// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// This example shows how to create an Amazon Identity and Access Management
// (IAM) service role. The example was created using the AWS SDK for .NET
// version 3.7 and .NET 5.

// snippet-start:[IAM.dotnetv3.CreateRoleExample]

using System;
using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;

var client = new AmazonIdentityManagementServiceClient();
string assumeRolePolicy = @"{
    'Version': '2012-10-17',
    'Statement':
    [
        {
            'Effect': 'Allow',
            'Principal':
            {
                'AWS': 'USER_ARN', // The ARN of the user.
            },
            'Action': 'sts:AssumeRole',
        },
    ],
}";

var request = new CreateRoleRequest
{
    RoleName = "IAMExampleRole",
    AssumeRolePolicyDocument = assumeRolePolicy,
};

try
{
    var response = await client.CreateRoleAsync(request);
    Console.WriteLine($"Successfully created {response.Role.RoleName} with ARN: {response.Role.Arn}");
}
catch (ConcurrentModificationException)
{
    Console.WriteLine("Multiple requests to change this object were submitted simultaneously. Wait a few minutes and submit your request again.");
}
catch (EntityAlreadyExistsException)
{
    Console.WriteLine("The IAM role you are trying to create already exists.");
}
catch (LimitExceededException ex)
{
    Console.WriteLine($"Error: {ex.Message}");
}

// snippet-end:[IAM.dotnetv3.CreateRoleExample]