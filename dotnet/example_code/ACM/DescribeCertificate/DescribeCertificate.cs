//snippet-sourcedescription:[DescribeCertificate.cs demonstrates how to describe an ACM Certificate.]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Certificate Manager]
//snippet-service:[acm]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[walkerk1980]
/*******************************************************************************
* Copyright 2009-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License"). You may
* not use this file except in compliance with the License. A copy of the
* License is located at
*
* http://aws.amazon.com/apache2.0/
*
* or in the "license" file accompanying this file. This file is
* distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the specific
* language governing permissions and limitations under the License.
*******************************************************************************/

using System;
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

