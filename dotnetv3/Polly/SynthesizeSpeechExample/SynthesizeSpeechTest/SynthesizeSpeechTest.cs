using Amazon.Polly;
using Amazon.Polly.Model;
using Moq;
using System;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Xunit;
using Xunit.Abstractions;

namespace SynthesizeSpeechTest
{
    public class SynthesizeSpeechTest
    {
        private static ITestOutputHelper output;
        private static readonly string text = "This is a sample text to be synthesized.";

        private IAmazonPolly CreateMockPollyClient()
        {
            var mockPollyClient = new Mock<IAmazonPolly>();

            mockPollyClient.Setup(client => client.SynthesizeSpeechAsync(
                    It.IsAny<SynthesizeSpeechRequest>(),
                    It.IsAny<CancellationToken>()))
                .Callback<SynthesizeSpeechRequest,
                    CancellationToken>((request, token) =>
                    {
                        if (!string.IsNullOrEmpty(text))
                        {
                            Assert.Equal(text, request.Text);
                        }
                    })
                .Returns((SynthesizeSpeechRequest r,
                    CancellationToken token) =>
                {
                    return Task.FromResult(new SynthesizeSpeechResponse()
                    { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockPollyClient.Object;
        }

        [Fact]
        public async Task SynthesizeSpeechExampleTest()
        {
            var mockPollyClient = CreateMockPollyClient();

            SynthesizeSpeechRequest pollyRequest = new()
            {
                OutputFormat = OutputFormat.Mp3,
                VoiceId = VoiceId.Joanna,
                Text = text
            };

            var result = await mockPollyClient.SynthesizeSpeechAsync(pollyRequest);
            bool gotResult = result != null;
            Assert.True(gotResult, "Result returned successfully.");

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Text conversion completed successfully.");
        }
    }
}
