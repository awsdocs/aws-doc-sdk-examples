// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// Lists the avaiable SAML providers defined using the Amazon Identity
// Management Service for an account. The example was created using the
// AWS SDK for .NET version 3.7 and .NET Core 5.

// snippet-start:[IAM.dotnetv3.ListSAMLProvidersExample]
using System;
using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;

var client = new AmazonIdentityManagementServiceClient();

var response = await client.ListSAMLProvidersAsync(new ListSAMLProvidersRequest());

response.SAMLProviderList.ForEach(samlProvider =>
{
    Console.WriteLine($"{samlProvider.Arn} created on: {samlProvider.CreateDate}");
});

// snippet-end:[IAM.dotnetv3.ListSAMLProvidersExample]