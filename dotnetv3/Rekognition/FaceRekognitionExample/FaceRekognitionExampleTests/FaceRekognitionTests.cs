// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace FaceRekognitionExampleTests
{
    using System.IO;
    using System.Threading;
    using System.Threading.Tasks;
    using Amazon.Rekognition;
    using Amazon.Rekognition.Model;
    using Moq;
    using Xunit;

    public class FaceRekognitionTests
    {
        private const string _filename = "test.png";

        [Fact]
        public async Task IdentifyFacesTests()
        {
            var mockClient = new Mock<AmazonRekognitionClient>();

            mockClient.Setup(client => client.DetectFacesAsync(
                It.IsAny<DetectFacesRequest>(),
                It.IsAny<CancellationToken>()
            )).Returns((DetectFacesRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new DetectFacesResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            byte[] data = File.ReadAllBytes(_filename);

            DetectFacesRequest request = new DetectFacesRequest
            {
                Image = new Amazon.Rekognition.Model.Image
                {
                    Bytes = new MemoryStream(data),
                },
            };

            var client = mockClient.Object;
            DetectFacesResponse response = await client.DetectFacesAsync(request);

            bool gotResult = response is not null;
            Assert.True(gotResult, "DetectFacesAsync returned a response.");

            bool ok = response.HttpStatusCode == System.Net.HttpStatusCode.OK;
            Assert.True(ok, $"Successfully searched image for faces.");
        }

        [Fact]
        public async Task IdentifyCelebretiesTest()
        {
            var mockClient = new Mock<AmazonRekognitionClient>();

            mockClient.Setup(client => client.RecognizeCelebritiesAsync(
                It.IsAny<RecognizeCelebritiesRequest>(),
                It.IsAny<CancellationToken>()
            )).Returns((RecognizeCelebritiesRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new RecognizeCelebritiesResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            byte[] data = File.ReadAllBytes(_filename);

            RecognizeCelebritiesRequest request = new RecognizeCelebritiesRequest
            {
                Image = new Amazon.Rekognition.Model.Image
                {
                    Bytes = new MemoryStream(data),
                },
            };

            var client = mockClient.Object;
            var response = await client.RecognizeCelebritiesAsync(request);

            bool gotResult = response is not null;
            Assert.True(gotResult, "RecognizeCelebritiesAsync returned a response.");

            bool ok = response.HttpStatusCode == System.Net.HttpStatusCode.OK;
            Assert.True(ok, $"Successfully searched image for celebrity faces.");

            Assert.False(response.CelebrityFaces.Count > 0, $"Found {response.CelebrityFaces.Count} celebrities.");
        }
    }
}
