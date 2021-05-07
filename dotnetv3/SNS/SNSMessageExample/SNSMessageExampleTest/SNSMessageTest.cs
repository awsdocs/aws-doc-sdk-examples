// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

using Amazon;
using Amazon.SimpleNotificationService;
using Amazon.SimpleNotificationService.Model;
using Moq;
using System;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Xunit;

namespace SNSMessageExampleTest
{
    public class SNSMessageTest
    {
        private static readonly RegionEndpoint _regionEndpoint = RegionEndpoint.USWest2;
        private string _text = "This is a test text message";
        private string _phoneNum = "18005551212";

        private AmazonSimpleNotificationServiceClient CreateMockSNSClient()
        {
            var mockSNSClient = new Mock<AmazonSimpleNotificationServiceClient>(_regionEndpoint);
            mockSNSClient.Setup(client => client.PublishAsync(
                It.IsAny<PublishRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<PublishRequest, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.PhoneNumber))
                {
                    Assert.Equal(request.PhoneNumber, _phoneNum);
                }

                if(!String.IsNullOrEmpty(request.Message))
                {
                    Assert.Equal(request.Message, _text);
                }
            }).Returns((PublishRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new PublishResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                });
            });

            return mockSNSClient.Object;
        }

        [Fact]
        public async Task TestSendTextMessageAsync()
        {
            var MockSNSClient = CreateMockSNSClient();

            var request = new PublishRequest
            {
                Message = _text,
                PhoneNumber = _phoneNum
            };

            var result = await MockSNSClient.PublishAsync(request);

            bool gotResult = result != null;
            Assert.True(gotResult, "Copy operation failed.");
            
            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Could NOT send test message to {_phoneNum}.");


        }
    }
}
