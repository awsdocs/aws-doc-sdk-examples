// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using System.Net;
using System.Text.Json;
using Amazon.Lambda.Annotations;
using Amazon.Lambda.Annotations.APIGateway;
using Amazon.Lambda.APIGatewayEvents;
using Amazon.Lambda.Core;
using AWS.Lambda.Powertools.Logging;
using PamServices;

[assembly: LambdaSerializer(typeof(Amazon.Lambda.Serialization.SystemTextJson.DefaultLambdaJsonSerializer))]

namespace PamApiAnnotations
{
    /// <summary>
    /// A collection of sample AWS Lambda functions that provide a REST API using Lambda annotations for the Photo Analyzer example. 
    /// </summary>
    public class Functions
    {
        private readonly LabelService _labelService;
        private readonly StorageService _storageService;

        /// <summary>
        /// Default constructor.
        /// </summary>
        public Functions(StorageService storageService, LabelService labelService)
        {
            _storageService = storageService;
            _labelService = labelService;
        }

        // GET /labels
        /// <summary>
        /// Get the list of all available image labels.
        /// </summary>
        /// <returns>A list of labels with counts.</returns>
        [LambdaFunction()]
        [HttpApi(LambdaHttpMethod.Get, "/labels")]
        public async Task<APIGatewayHttpApiV2ProxyResponse> GetLabels(ILambdaContext context)
        {
            Logger.LogInformation($"Getting labels.");
            var allLabels = await _labelService.GetAllItems();
            var labelsResponse = new LabelsResponse(allLabels.ToList());

            // Return the proxy response so the headers can be customized.
            return new APIGatewayHttpApiV2ProxyResponse
            {
                StatusCode = (int)HttpStatusCode.OK,
                Body = JsonSerializer.Serialize(labelsResponse),
                Headers = new Dictionary<string, string> { { "Access-Control-Allow-Origin", "*" } }
            };
        }

        // PUT /upload
        /// <summary>
        /// Prepare a presigned URL for uploading an image.
        /// </summary>
        /// <param name="uploadRequest">Request including the file name of the image.</param>
        /// <returns>The presigned upload URL, valid for 5 minutes.</returns>
        [LambdaFunction()]
        [HttpApi(LambdaHttpMethod.Put, "/upload")]
        public APIGatewayHttpApiV2ProxyResponse Upload([FromBody] UploadRequest uploadRequest)
        {
            var storageBucketName = Environment.GetEnvironmentVariable("STORAGE_BUCKET_NAME");

            var presignedUrl = _storageService.GetPresignedUrlForImage(uploadRequest.file_name, storageBucketName!);

            var uploadResponse = new UploadResponse() { url = presignedUrl };

            // Return the proxy response so the headers can be customized.
            return new APIGatewayHttpApiV2ProxyResponse
            {
                StatusCode = (int)HttpStatusCode.OK,
                Body = JsonSerializer.Serialize(uploadResponse),
                Headers = new Dictionary<string, string> { { "Access-Control-Allow-Origin", "*" } }
            };
        }
    }
}