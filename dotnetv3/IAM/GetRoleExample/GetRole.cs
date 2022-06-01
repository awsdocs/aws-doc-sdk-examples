// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// This example shows how to retrieve information about an AWS Identity and
// Access Management (IAM) role. The example was created using the AWS SDK for
// .NET version 3.7 and .NET Core 5.

// snippet-start:[IAM.dotnetv3.GetRoleExample]

using System;
using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;

var client = new AmazonIdentityManagementServiceClient();

var response = await client.GetRoleAsync(new GetRoleRequest
{
    RoleName = "LambdaS3Role",
});

if (response.Role is not null)
{
    Console.WriteLine($"{response.Role.RoleName} with ARN: {response.Role.Arn}");
    Console.WriteLine($"{response.Role.Description}");
    Console.WriteLine($"Created: {response.Role.CreateDate} Last used on: { response.Role.RoleLastUsed}");
}

// snippet-end:[IAM.dotnetv3.GetRoleExample]
