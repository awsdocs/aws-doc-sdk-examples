// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon.SecretsManager;
using Amazon.SecretsManager.Model;
using GetSecretValueExample;
using Moq;
using System;
using System.IO;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Xunit;

namespace GetSecretValueExampleTests
{
    public class GetSecretValueTests
    {
        [Fact]
        public void TestGetSecretValueAsync()
        {
            var mockClient = new Mock<IAmazonSecretsManager>();
            mockClient.Setup(client => client.GetSecretValueAsync(
                    It.IsAny<GetSecretValueRequest>(),
                    It.IsAny<CancellationToken>()))
                .Returns((GetSecretValueRequest r,
                    CancellationToken token) =>
                {
                    return Task.FromResult(new GetSecretValueResponse()
                    {
                        SecretString = "Sample Secret String",
                        HttpStatusCode = HttpStatusCode.OK,
                    });
                });

            var client = mockClient.Object;

            GetSecretValueRequest request = new ();
            request.SecretId = "SecretTest";
            request.VersionStage = "AWSCURRENT"; // VersionStage defaults to AWSCURRENT if unspecified.

            var response = client.GetSecretValueAsync(request);

            Assert.True(response.Result.SecretString == "Sample Secret String");
        }

        [Fact]
        public void TestDecodeStringNullSecretValue()
        {
            var secretValue = new GetSecretValueResponse();
            string secret = GetSecretValue.DecodeString(secretValue);

            Assert.True(secret == string.Empty);
        }

        [Fact]
        public void TestDecodeStringStringValue()
        {
            var secretValue = new GetSecretValueResponse
            {
                SecretString = "Example Secret"
            };

            string secret = GetSecretValue.DecodeString(secretValue);

            Assert.True(secret == "Example Secret");
        }

        [Fact]
        public void TestDecodeStringBinaryValue()
        {
            var base64 = Convert.ToBase64String(Encoding.UTF8.GetBytes("SecretExample"));
            var secretBinary =  new MemoryStream(Encoding.UTF8.GetBytes(base64));

            var secretValue = new GetSecretValueResponse
            {
                SecretBinary = secretBinary,
            };

            string secret = GetSecretValue.DecodeString(secretValue);

            Assert.True(secret == "SecretExample", $"secret value is {secret}.");
        }
    }
}
