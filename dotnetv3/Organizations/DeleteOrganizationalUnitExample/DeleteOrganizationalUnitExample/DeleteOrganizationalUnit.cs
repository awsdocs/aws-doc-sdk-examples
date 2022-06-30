// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace DeleteOrganizationalUnitExample
{
    // snippet-start:[Organizations.dotnetv3.DeleteOrganizationalUnitExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.Organizations;
    using Amazon.Organizations.Model;

    /// <summary>
    /// Shows how to delete an existing Amazon Organizations Organization Unit.
    /// This example was created using the AWS SDK for .NET version 3.7 and
    /// .NET Core 5.0.
    /// </summary>
    public class DeleteOrganizationalUnit
    {
        /// <summary>
        /// Initializes the Organizations client object and calls
        /// DeleteOrganizationalUnitAsync to delete the Organizational Unit
        /// with the selected ID.
        /// </summary>
        public static async Task Main()
        {
            // Create the client object using the default account.
            IAmazonOrganizations client = new AmazonOrganizationsClient();

            var orgUnitId = "ou-sso8-r0fkibej";

            var request = new DeleteOrganizationalUnitRequest
            {
                OrganizationalUnitId = orgUnitId,
            };

            var response = await client.DeleteOrganizationalUnitAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"Successfully deleted the Organization Unit with ID: {orgUnitId}.");
            }
            else
            {
                Console.WriteLine($"Could not delete the Organization Unit with ID: {orgUnitId}.");
            }
        }
    }
    // snippet-end:[Organizations.dotnetv3.DeleteOrganizationalUnitExample]
}
