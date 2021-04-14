// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon.SQS;
using Amazon.SQS.Model;
using Moq;
using System.Collections.Generic;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Xunit;

namespace CreateSendExampleTests
{
    public class CreateSendExampleTests
    {
        private string _queueUrl = "https://mockQueueURL";

        [Fact]
        public async Task CreateQueueTest()
        {
            var mockClient = new Mock<IAmazonSQS>();
            mockClient.Setup(client => client.CreateQueueAsync(
                   It.IsAny<CreateQueueRequest>(),
                   It.IsAny<CancellationToken>()))
               .Returns((CreateQueueRequest r,
                   CancellationToken token) =>
               {
                   return Task.FromResult(new CreateQueueResponse()
                   {
                       QueueUrl = _queueUrl,
                       HttpStatusCode = HttpStatusCode.OK,
                   });
               });

            var client = mockClient.Object;
            var createQueueRequest = new CreateQueueRequest();
            var response = await client.CreateQueueAsync(createQueueRequest);

            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Successfully created queue: {response.QueueUrl}.");
        }

        [Fact]
        public async Task SendMessageTest()
        {
            var mockClient = new Mock<IAmazonSQS>();
            mockClient.Setup(client => client.SendMessageAsync(
                   It.IsAny<SendMessageRequest>(),
                   It.IsAny<CancellationToken>()))
               .Returns((SendMessageRequest r,
                   CancellationToken token) =>
               {
                   return Task.FromResult(new SendMessageResponse()
                   {
                       MessageId = "70994bc6-9473-4c3c-b9c9-f9be44209bec",
                       HttpStatusCode = HttpStatusCode.OK,
                   });
               });

            var client = mockClient.Object;

            Dictionary<string, MessageAttributeValue> messageAttributes = new Dictionary<string, MessageAttributeValue>
            {
                {"Title",   new MessageAttributeValue{DataType = "String", StringValue = "Test Message"}},
                {"Author",  new MessageAttributeValue{DataType = "String", StringValue = "AWS SDK Examples"}}
            };

            var sendMessageRequest = new SendMessageRequest
            {
                DelaySeconds = 10,
                MessageAttributes = messageAttributes,
                MessageBody = "This is a test message.",
                QueueUrl = _queueUrl
            };

            var response = await client.SendMessageAsync(sendMessageRequest);

            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Successfully created queue: {response.MessageId}.");
        }
    }
}
