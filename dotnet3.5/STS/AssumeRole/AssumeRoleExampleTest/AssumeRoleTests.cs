# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

using Amazon;
using Amazon.SecurityToken;
using Amazon.SecurityToken.Model;
using Moq;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Xunit;

namespace AssumeRoleExampleTest
{
    public class AssumeRoleTests
    {
        private string roleArnToAssume = "arn:aws:iam::123456789012:role/testAssumeRole";
        private static readonly RegionEndpoint REGION = RegionEndpoint.USWest2;

        [Fact]
        public async Task GetCallerIdentityAsyncTest()
        {
            // Create the mock SecurityToken client.
            // Define variable
            var mockClient = new Mock<AmazonSecurityTokenServiceClient>(REGION);
            mockClient.Setup(client => client.GetCallerIdentityAsync(
                    It.IsAny<GetCallerIdentityRequest>(),
                    It.IsAny<CancellationToken>()))
                .Callback<GetCallerIdentityRequest,
                    CancellationToken>((request, token) =>
                    {
                    })
                .Returns((GetCallerIdentityRequest r,
                    CancellationToken token) =>
                {
                    return Task.FromResult(new GetCallerIdentityResponse()
                    {
                        Arn = roleArnToAssume,
                        HttpStatusCode = HttpStatusCode.OK,
                    });
                });

            var client = mockClient.Object;
            var callerIdRequest = new GetCallerIdentityRequest();
            var response = await client.GetCallerIdentityAsync(callerIdRequest);

            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Successfully retrieved caller Identity.");

        }

        [Fact]
        public async Task AssumeRoleAsyncTest()
        {
            // Create the mock client object
            var mockClient = new Mock<AmazonSecurityTokenServiceClient>(REGION);
            mockClient.Setup(client => client.AssumeRoleAsync(
                    It.IsAny<AssumeRoleRequest>(),
                    It.IsAny<CancellationToken>()))
                .Callback<AssumeRoleRequest,
                    CancellationToken>((request, token) =>
                    {
                        if (request is not null)
                        {

                        }
                    })
                .Returns((AssumeRoleRequest r,
                    CancellationToken token) =>
                {
                    var roleUser = new AssumedRoleUser()
                    {
                        Arn = roleArnToAssume,
                    };

                    return Task.FromResult(new AssumeRoleResponse()
                    {
                        AssumedRoleUser = roleUser,
                        HttpStatusCode = HttpStatusCode.OK,
                    });
                });

            var client = mockClient.Object;

            // Create the request to use with the AssumeRoleAsync call.
            var assumeRoleReq = new AssumeRoleRequest()
            {
                DurationSeconds = 1600,
                RoleSessionName = "Session1",
                RoleArn = roleArnToAssume
            };

            var response = await client.AssumeRoleAsync(assumeRoleReq);
            Assert.True(response.AssumedRoleUser.Arn == roleArnToAssume, "Successfully call to assume role.");

            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Successfully retrieved caller Identity.");
        }
    }
}
