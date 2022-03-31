// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// This example shows how to list existing Amazon Identity Management
// users. The code was written using the AWS SDK for .NET version 3.7 and
// .NET Core 5.

// snippet-start:[IAM.dotnetv3.ListUsersExample]

using System;
using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;

var client = new AmazonIdentityManagementServiceClient();
var request = new ListUsersRequest
{
    MaxItems = 10,
};
var response = await client.ListUsersAsync(request);

do
{
    response.Users.ForEach(user =>
    {
        Console.WriteLine($"{user.UserName} created on {user.CreateDate}.");
        Console.WriteLine($"ARN: {user.Arn}\n");
    });

    request.Marker = response.Marker;
    response = await client.ListUsersAsync(request);
} while (response.IsTruncated);

// snippet-end:[IAM.dotnetv3.ListUsersExample]
