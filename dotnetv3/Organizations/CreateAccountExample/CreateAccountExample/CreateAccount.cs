// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace CreateAccountExample
{
    // snippet-start:[Organizations.dotnetv3.CreateAccountExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.Organizations;
    using Amazon.Organizations.Model;

    /// <summary>
    /// Creates a new Amazon Organizations Account. The example was created
    /// using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    class CreateAccount
    {
        /// <summary>
        /// Initializes an Orginizations client object and uses it to create
        /// the new Account with the name specified in accountName.
        /// </summary>
        static async Task Main()
        {
            IAmazonOrganizations client = new AmazonOrganizationsClient();
            var accountName = "ExampleAccount";
            var email = "someone@example.com";

            var request = new CreateAccountRequest
            {
                AccountName = accountName,
                Email = email,
            };

            var response = await client.CreateAccountAsync(request);
            var status = response.CreateAccountStatus;

            Console.WriteLine($"The staus of {status.AccountName} is {status.State}.");
        }
    }
    // snippet-end:[Organizations.dotnetv3.CreateAccountExample]
}
