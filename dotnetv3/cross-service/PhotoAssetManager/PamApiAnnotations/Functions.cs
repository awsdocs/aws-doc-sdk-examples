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

        /// <summary>
        /// Root route that provides information about the other requests that can be made.
        ///
        /// PackageType is currently required to be set to LambdaPackageType.Image till the upcoming .NET 6 managed
        /// runtime is available. Once the .NET 6 managed runtime is available PackageType will be optional and will
        /// default to Zip.
        /// </summary>
        /// <returns>API descriptions.</returns>
        [LambdaFunction()]
        [HttpApi(LambdaHttpMethod.Get, "/")]
        public string Default()
        {
            var docs = @"Annotations example.";
            return docs;
        }

        // GET /labels
        [LambdaFunction()]
        [HttpApi(LambdaHttpMethod.Get, "/labels")]
        public async Task<LabelsResponse> Get()
        {
            var allLabels = await _labelService.GetAllItems();
            var response = new LabelsResponse(allLabels.ToList());
            return response;
        }

        // PUT /upload
        [LambdaFunction()]
        [HttpApi(LambdaHttpMethod.Put, "/upload")]
        public UploadResponse Upload([FromBody] UploadRequest uploadRequest)
        {
            var storageBucketName = Environment.GetEnvironmentVariable("STORAGE_BUCKET_NAME");

            var presignedUrl = _storageService.GetPresignedUrlForImage(uploadRequest.file_name, storageBucketName!);

            var response = new UploadResponse() { url = presignedUrl };
            return response;
        }

        /// <summary>
        /// Perform x + y
        ///
        /// PackageType is currently required to be set to LambdaPackageType.Image till the upcoming .NET 6 managed
        /// runtime is available. Once the .NET 6 managed runtime is available PackageType will be optional and will
        /// default to Zip.
        /// </summary>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <returns>Sum of x and y.</returns>
        [LambdaFunction()]
        [HttpApi(LambdaHttpMethod.Get, "/add/{x}/{y}")]
        public int Add(int x, int y, ILambdaContext context)
        {
            context.Logger.LogInformation($"{x} plus {y} is {x + y}");
            return x + y;
        }
    }
}
