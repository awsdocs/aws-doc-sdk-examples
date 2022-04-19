// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// Retrieves the password policy for the account and then displays the
// information on the console. The code was written using the AWS SDK
// for .NET version 3.7 and .NET Core 5.

// snippet-start:[IAM.dotnetv3.GetAccountPasswordPolicyExample]
using System;
using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;

var client = new AmazonIdentityManagementServiceClient();

try
{
    var request = new GetAccountPasswordPolicyRequest();
    var response = await client.GetAccountPasswordPolicyAsync(request);

    Console.WriteLine($"{response.PasswordPolicy}");
}
catch (NoSuchEntityException ex)
{
    Console.WriteLine($"Error: {ex.Message}");
}

// snippet-end:[IAM.dotnetv3.GetAccountPasswordPolicyExample]
