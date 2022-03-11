// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// Shows how to retrieve the list of Amazon Identity Management Service role
// policies attached to a particular role. This example was created using the
// AWS SDK for .NET and .NET Core 5.

// snippet-start:[IAM.dotnetv3.ListAttachedRolePoliciesExample]

using System;
using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;

var client = new AmazonIdentityManagementServiceClient();
var request = new ListAttachedRolePoliciesRequest
{
    MaxItems = 10,
    RoleName = "testAssumeRole",
};

var response = await client.ListAttachedRolePoliciesAsync(request);

do
{
    response.AttachedPolicies.ForEach(policy =>
    {
        Console.WriteLine($"{policy.PolicyName} with ARN: {policy.PolicyArn}");
    });

    if (response.IsTruncated)
    {
        request.Marker = response.Marker;
        response = await client.ListAttachedRolePoliciesAsync(request);
    }

} while (response.IsTruncated);

// snippet-end:[IAM.dotnetv3.ListAttachedRolePoliciesExample]