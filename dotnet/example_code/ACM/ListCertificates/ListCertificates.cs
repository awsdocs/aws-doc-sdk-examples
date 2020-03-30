//snippet-sourcedescription:[ListCertificates.cs demonstrates how to list ACM Certificates.]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-sourcesyntax:[.net]
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
