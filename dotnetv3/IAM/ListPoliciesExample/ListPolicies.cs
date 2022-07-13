// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// This example shows how to list list the Amazon Identity Management
// policies. The code was created using the AWS SDK for .NET version 3.7
// and .NET Core 5.

// snippet-start:[IAM.dotnetv3.ListPoliciesExample]

using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;
using System;

var client = new AmazonIdentityManagementServiceClient();

var request = new ListPoliciesRequest
{
    MaxItems = 10,
};

var response = new ListPoliciesResponse();

do
{
    response = await client.ListPoliciesAsync(request);
    response.Policies.ForEach(policy =>
    {
        Console.Write($"{policy.PolicyName} ");
        Console.Write($"with ID: {policy.PolicyId} ");
        Console.Write($"and ARN: {policy.Arn}. ");
        Console.WriteLine($"It was created on {policy.CreateDate}.");
    });

    if (response.IsTruncated)
    {
        request.Marker = response.Marker;
    }
} while (response.IsTruncated);

// snippet-end:[IAM.dotnetv3.ListPoliciesExample]
