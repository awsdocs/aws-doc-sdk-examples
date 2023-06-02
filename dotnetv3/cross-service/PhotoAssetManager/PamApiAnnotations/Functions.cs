using Amazon.Lambda.Core;
using Amazon.Lambda.Annotations;
using Amazon.Lambda.Annotations.APIGateway;
using PamApi;
using PamServices;

[assembly: LambdaSerializer(typeof(Amazon.Lambda.Serialization.SystemTextJson.DefaultLambdaJsonSerializer))]

namespace PamApiAnnotations
{
    /// <summary>
    /// A collection of sample Lambda functions that provide a REST api using Lambda annotations for the Photo Analyzer example. 
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
        public async Task<LabelsResponse> GetLabels()
        {
            var allLabels = await _labelService.GetAllItems();
            var response = new LabelsResponse(allLabels.ToList());
            return response;
        }

        // PUT /upload
        /// <summary>
        /// Prepare a presigned url for uploading an image.
        /// </summary>
        /// <param name="uploadRequest">Request including the filename of the image.</param>
        /// <returns>The presigned upload url, valid for 5 minutes.</returns>
        [LambdaFunction()]
        [HttpApi(LambdaHttpMethod.Put, "/upload")]
        public UploadResponse Upload([FromBody] UploadRequest uploadRequest)
        {
            var storageBucketName = Environment.GetEnvironmentVariable("STORAGE_BUCKET_NAME");

            var presignedUrl = _storageService.GetPresignedUrlForImage(uploadRequest.file_name, storageBucketName!);

            var response = new UploadResponse() { url = presignedUrl };
            return response;
        }

    }
}
