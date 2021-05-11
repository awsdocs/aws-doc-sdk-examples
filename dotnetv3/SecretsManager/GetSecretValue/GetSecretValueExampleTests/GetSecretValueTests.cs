using Amazon.SecretsManager;
using Amazon.SecretsManager.Model;
using Moq;
using System;
using System.Net;
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
                        HttpStatusCode = HttpStatusCode.OK,
                    });
                });

            var client = mockClient.Object;

            GetSecretValueRequest request = new();
            request.SecretId = "SecretTest";
            request.VersionStage = "AWSCURRENT"; // VersionStage defaults to AWSCURRENT if unspecified.

            var response = client.GetSecretValueAsync(request);
        }
    }
}
