using Amazon.SQS;
using Amazon.SQS.Model;
using Moq;
using System;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Xunit;
using System.Collections.Generic;

namespace ReceiveDeleteExampleTests
{
    public class ReceiveDeleteExampleTests
    {
        private readonly string _queueUrl = "https://sqs.us-east-2.amazonaws.com/704812345678/Example_Queue";

        [Fact]
        public async Task GetQueueURLTest()
        {
            var mockClient = new Mock<IAmazonSQS>();
            mockClient.Setup(client => client.GetQueueUrlAsync(
                   It.IsAny<GetQueueUrlRequest>(),
                   It.IsAny<CancellationToken>()))
               .Returns((GetQueueUrlRequest r,
                   CancellationToken token) =>
               {
                   return Task.FromResult(new GetQueueUrlResponse()
                   {
                       QueueUrl = _queueUrl,
                       HttpStatusCode = HttpStatusCode.OK,
                   });
               });

            var client = mockClient.Object;

            var request = new GetQueueUrlRequest
            {
                QueueName = "Example_Queue"
            };

            var getQueueUrlRequest = new GetQueueUrlRequest();
            var response = await client.GetQueueUrlAsync(request);

            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Retrieved the URL for the queue: {response.QueueUrl}.");
        }

        [Fact]
        public async Task GetMessageTest()
        {
            var msgs = new List<Message>();
            var message = new Message();
            message.Body = "This is a test message.";
            msgs.Add(message);

            var mockClient = new Mock<IAmazonSQS>();
            mockClient.Setup(client => client.ReceiveMessageAsync(
                   It.IsAny<ReceiveMessageRequest>(),
                   It.IsAny<CancellationToken>()))
               .Returns((ReceiveMessageRequest r,
                   CancellationToken token) =>
               {
                   return Task.FromResult(new ReceiveMessageResponse()
                   {
                       Messages = msgs,
                       HttpStatusCode = HttpStatusCode.OK,
                   });
               });

            var client = mockClient.Object;

            var receiveMessageRequest = new ReceiveMessageRequest
            {
                AttributeNames = { "SentTimestamp" },
                MaxNumberOfMessages = 1,
                MessageAttributeNames = { "All" },
                QueueUrl = _queueUrl,
                VisibilityTimeout = 0,
                WaitTimeSeconds = 0
            };

            var response = await client.ReceiveMessageAsync(receiveMessageRequest);

            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Retrieved the following message: {response.Messages[0].Body}.");
        }

        [Fact]
        public async Task DeleteMessageTest()
        {
            var mockClient = new Mock<IAmazonSQS>();
            mockClient.Setup(client => client.DeleteMessageAsync(
                   It.IsAny<DeleteMessageRequest>(),
                   It.IsAny<CancellationToken>()))
               .Returns((DeleteMessageRequest r,
                   CancellationToken token) =>
               {
                   return Task.FromResult(new DeleteMessageResponse()
                   {
                       HttpStatusCode = HttpStatusCode.OK,
                   });
               });

            var client = mockClient.Object;

            var deleteMessageRequest = new DeleteMessageRequest
            {
                QueueUrl = _queueUrl,
                ReceiptHandle = "f1e931bc-92e0-415f-ab01-123456789abc"
            };

            var response = await client.DeleteMessageAsync(deleteMessageRequest);

            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Successfully deleted the Message.");
        }
    }
}
