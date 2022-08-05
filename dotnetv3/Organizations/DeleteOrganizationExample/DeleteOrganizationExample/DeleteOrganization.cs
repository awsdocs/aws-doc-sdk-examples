// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace DeleteOrganizationExample
{
    // snippet-start:[Organizations.dotnetv3.DeleteOrganizationExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.Organizations;
    using Amazon.Organizations.Model;

    /// <summary>
    /// Shows how to delete an existing Organization using the Amazon
    /// Organizations Service. This example was created using the AWS SDK for
    /// .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class DeleteOrganization
    {
        /// <summary>
        /// Initializes the Organizations client and then calls
        /// DeleteOrganizationAsync to delete the organization.
        /// </summary>
        public static async Task Main()
        {
            // Create the client object using the default account.
            IAmazonOrganizations client = new AmazonOrganizationsClient();

            var response = await client.DeleteOrganizationAsync(new DeleteOrganizationRequest());

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine("Successfully deleted Organization.");
            }
            else
            {
                Console.WriteLine("Could not delete Organization.");
            }
        }
    }
    // snippet-end:[Organizations.dotnetv3.DeleteOrganizationExample]
}
