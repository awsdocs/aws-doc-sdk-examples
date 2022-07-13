// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// This example shows how to get information about an AWS Identity and Access
// Management (IAM) policy using the Amazon Resource Name (ARN) of the policy.
// The example was created using the AWS SDK for .NET 3.7 and .NET Core 5.

// snippet-start:[IAM.dotnetv3.GetPolicyExample]

using System;
using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;

var client = new AmazonIdentityManagementServiceClient();
var request = new GetPolicyRequest
{
    PolicyArn = "POLICY_ARN",
};

var response = await client.GetPolicyAsync(request);

Console.Write($"{response.Policy.PolicyName} was created on ");
Console.WriteLine($"{response.Policy.CreateDate}");

// snippet-end:[IAM.dotnetv3.GetPolicyExample]