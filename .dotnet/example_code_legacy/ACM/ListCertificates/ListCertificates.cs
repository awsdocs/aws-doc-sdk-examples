// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0

using System;
using Amazon;
using Amazon.CertificateManager;
using Amazon.CertificateManager.Model;
using System.Threading.Tasks;
namespace ListCertificatesExample
{
    class ListCertificates
    {
        static void Main(string[] args)
        {
            AmazonCertificateManagerConfig acmConfig = new AmazonCertificateManagerConfig();
            acmConfig.RegionEndpoint = Amazon.RegionEndpoint.USEast1;

            var client = new Amazon.CertificateManager.AmazonCertificateManagerClient(acmConfig);
            var listCertificatesReq = new ListCertificatesRequest();
            var certificateList = ListCertificatesResponseAsync(client: client, request: listCertificatesReq);


            Console.WriteLine("Certificate Summary List : " + Environment.NewLine);

            foreach (var certificate in certificateList.Result.CertificateSummaryList)
            {
                 Console.WriteLine("Certificate Domain: " + certificate.DomainName);
                 Console.WriteLine("Certificate ARN: " + certificate.CertificateArn + Environment.NewLine);
            } 

        }

        static async Task<ListCertificatesResponse> ListCertificatesResponseAsync(AmazonCertificateManagerClient client, ListCertificatesRequest request)
        {
            var response = await client.ListCertificatesAsync(request);
            return response;
        }
    }
}
