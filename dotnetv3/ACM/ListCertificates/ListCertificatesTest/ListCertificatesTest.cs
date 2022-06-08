// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

using Amazon;
using Amazon.CertificateManager;
using Amazon.CertificateManager.Model;
using Moq;
using System;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Xunit;

namespace ListCertificatesTest
{
    public class ListCertificatesTest
    {
        private AmazonCertificateManagerClient CreateMockACMClient()
        {
            var mockCertificateManagerClient = new Mock<AmazonCertificateManagerClient>(RegionEndpoint.USEast1);
            mockCertificateManagerClient.Setup(client => client.ListCertificatesAsync(
                It.IsAny<ListCertificatesRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<ListCertificatesRequest, CancellationToken>((request, token) =>
            {
                if (request is not null)
                {
                }
            }).Returns((ListCertificatesRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new ListCertificatesResponse()
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
            var request = new ListCertificatesRequest();

            var response = await client.ListCertificatesAsync(request);

            bool gotResult = response != null;
            Assert.True(gotResult, "Copy operation failed.");
            
            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Retrieved certificates list.");
        }
    }
}