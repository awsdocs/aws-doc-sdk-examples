using Amazon.S3;
using Amazon.S3.Model;
using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Mvc;
using System.Data.SqlTypes;
using System.Net.Mime;
using Newtonsoft.Json;

namespace PamApi.Controllers;

[Route("")]
public class ApiController : ControllerBase
{
    private readonly IAmazonS3 _amazonS3;
    public ApiController(IAmazonS3 amazonS3)
    {
        _amazonS3 = amazonS3;
    }

    // PUT prod/upload
    [HttpPut("upload")]
    public async Task<IActionResult> Upload()
    {
        var rawRequestBody = await new StreamReader(Request.Body).ReadToEndAsync();
        var uploadRequest =
            JsonConvert.DeserializeObject<UploadRequest>(rawRequestBody);
        var uuid = Guid.NewGuid().ToString();
        var uniqueFileName = $"{uuid}-{uploadRequest.file_name}";
        var uploadBucketName = Environment.GetEnvironmentVariable("STORAGE_BUCKET_NAME");
        
        var preSignedUrlResponse = _amazonS3.GetPreSignedURL(
            new GetPreSignedUrlRequest()
            {
                BucketName = uploadBucketName,
                Key = uniqueFileName,
                ContentType = "image/jpeg",
                Expires = DateTime.UtcNow.AddMinutes(5),
                Verb = HttpVerb.PUT
            });

        var response = new UploadResponse() { url = preSignedUrlResponse };
        return Ok(response);
    }

    // GET prod/labels
    [HttpGet("labels")]
    public IActionResult Get(int id)
    {
        var response = new Dictionary<string, Dictionary<string, Dictionary<string, int>>> ();
        var labels = new Dictionary<string, Dictionary<string, int>>();
        labels.Add("sunrise", new Dictionary<string, int> { { "count", 3 } });
        labels.Add("beach", new Dictionary<string, int> { { "count", 1 } });
        response.Add("labels", labels);
        return Ok(response);
    }

    // POST prod/download
    [HttpPost("download")]
    public IActionResult Post([FromBody] string[] labels)
    {
        return Ok();
    }
}