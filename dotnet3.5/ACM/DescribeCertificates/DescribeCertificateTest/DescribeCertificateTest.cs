// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon;
using Amazon.CertificateManager;
using Amazon.CertificateManager.Model;
using Moq;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Xunit;

namespace DescribeCertificateTest
{
    public class DescribeCertificateTest
    {
        private AmazonCertificateManagerClient CreateMockACMClient()
        {
            var mockCertificateManagerClient = new Mock<AmazonCertificateManagerClient>(RegionEndpoint.USEast1);
            mockCertificateManagerClient.Setup(client => client.DescribeCertificateAsync(
                It.IsAny<DescribeCertificateRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<DescribeCertificateRequest, CancellationToken>((request, token) =>
            {
                if (request is not null)
                {
                }
            }).Returns((DescribeCertificateRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new DescribeCertificateResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                }); ;
            });

            return mockCertificateManagerClient.Object;
        }

        [Fact]
        public async Task ListCertificatesAsyncTest()
        {
            var client = CreateMockACMClient();
            var request = new DescribeCertificateRequest();
            request.CertificateArn = "arn:aws:acm:us-east-1:123456789012:certificate/8cfd7dae-9b6a-2d07-92bc-1c3093edb218";

            var response = await client.DescribeCertificateAsync(request);

            bool gotResult = response != null;
            Assert.True(gotResult, "Copy operation failed.");

            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Describe certificate list.");
        }
    }
}
