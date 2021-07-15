// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0

using System;
using Amazon;
using Amazon.CertificateManager;
using Amazon.CertificateManager.Model;
using System.Threading.Tasks;
namespace DescribeCertificateExample
{
    class DescribeCertificate
    {
        static void Main(string[] args)
        {
            AmazonCertificateManagerConfig acmConfig = new AmazonCertificateManagerConfig();
            acmConfig.RegionEndpoint = Amazon.RegionEndpoint.USEast1;

            var client = new Amazon.CertificateManager.AmazonCertificateManagerClient(acmConfig);

            var describeCertificateReq = new DescribeCertificateRequest();
            describeCertificateReq.CertificateArn = "arn:aws:acm:us-east-1:123456789012:certificate/8cfd7dae-9b6a-2d07-92bc-1c3093edb218";

            var certificateDetailResp = DescribeCertificateResponseAsync(client: client, request: describeCertificateReq);
            var certificateDetail = certificateDetailResp.Result.Certificate;

            Console.WriteLine(Environment.NewLine + "Certificate Details: ");
            Console.WriteLine("Certificate Domain: " + certificateDetail.DomainName);
            Console.WriteLine("Certificate Arn: " + certificateDetail.CertificateArn);            
            Console.WriteLine("Certificate Subject: " + certificateDetail.Subject);
            Console.WriteLine("Certificate Status: " + certificateDetail.Status);
            foreach (var san in certificateDetail.SubjectAlternativeNames)
            {
                Console.WriteLine("Certificate SubjectAlternativeName: " + san);
            }

        }

        static async Task<DescribeCertificateResponse> DescribeCertificateResponseAsync(AmazonCertificateManagerClient client, DescribeCertificateRequest request)
        {
            var response = await client.DescribeCertificateAsync(request);
            return response;
        }
    }
}
