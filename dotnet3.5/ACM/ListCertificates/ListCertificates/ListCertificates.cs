// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

using Amazon;
using Amazon.CertificateManager;
using Amazon.CertificateManager.Model;
using System;
using System.Threading.Tasks;

namespace ListCertificates
{
    // This example lists the certificates associated with
    // the default account. It was created using AWS SDK for .NET 3.5
    // and .NET 5.0.
    class ListCertificates
    {
        // Specify your AWS Region (an example Region is shown).
        private static readonly RegionEndpoint ACMRegionRegion = RegionEndpoint.USEast1;
        private static AmazonCertificateManagerClient _client;

        static void Main(string[] args)
        {
            var _client = new AmazonCertificateManagerClient(ACMRegionRegion);
            var certificateList = ListCertificatesResponseAsync(client: _client);

            Console.WriteLine("Certificate Summary List\n");

            foreach (var certificate in certificateList.Result.CertificateSummaryList)
            {
                Console.WriteLine($"Certificate Domain: {certificate.DomainName}");
                Console.WriteLine($"Certificate ARN: {certificate.CertificateArn}\n");
            }

        }

        /// <summary>
        /// Retrieves a list of the certificates defined in this region.
        /// </summary>
        /// <param name="client">The Amazon Certificate Manager client object
        /// passed to the ListCertificateResAsync method call.</param>
        /// <param name="request"></param>
        /// <returns></returns>
        static async Task<ListCertificatesResponse> ListCertificatesResponseAsync(AmazonCertificateManagerClient client)
        {
            var request = new ListCertificatesRequest();

            var response = await client.ListCertificatesAsync(request);
            return response;
        }
    }
}
