// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using AWS.Lambda.Powertools.Logging;
using Microsoft.AspNetCore.Mvc;
using PamServices;

namespace PamApi.Controllers;

public class ApiController : ControllerBase
{
    private readonly LabelService _labelService;
    private readonly StorageService _storageService;
    private readonly ImageService _imageService;

    /// <summary>
    /// Constructor for the controller, uses dependency injection to get the services needed.
    /// </summary>
    /// <param name="storageService">The injected image service.</param>
    /// <param name="labelService">The injected label service.</param>
    /// <param name="imageService">The injected image service.</param>
    public ApiController(StorageService storageService, LabelService labelService, ImageService imageService)
    {
        _storageService = storageService;
        _labelService = labelService;
        _imageService = imageService;
    }

    // PUT /upload
    /// <summary>
    /// Prepare a presigned URL for uploading an image.
    /// </summary>
    /// <param name="uploadRequest">Request including the file name of the image.</param>
    /// <returns>The presigned upload URL, valid for 5 minutes.</returns>
    [HttpPut("upload")]
    public IActionResult Upload([FromBody] UploadRequest uploadRequest)
    {
        var storageBucketName = Environment.GetEnvironmentVariable("STORAGE_BUCKET_NAME");

        var presignedUrl = _storageService.GetPresignedUrlForImage(uploadRequest.file_name, storageBucketName!);

        var response = new UploadResponse() { url = presignedUrl };
        return Ok(response);
    }

    // GET /labels
    /// <summary>
    /// Get the list of all available image labels.
    /// </summary>
    /// <returns>A list of labels with counts.</returns>
    [HttpGet("labels")]
    public async Task<IActionResult> Get()
    {
        Logger.LogInformation($"Getting labels.");
        var allLabels = await _labelService.GetAllItems();
        var response = new LabelsResponse(allLabels.ToList());
        return Ok(response);
    }

    // GET /test-insert
    /// <summary>
    /// Test an insert with a label and image list.
    /// </summary>
    /// <param name="label">The label to add.</param>
    /// <param name="images">The image names for the label.</param>
    /// <returns>An OK result.</returns>
    [HttpGet("test-insert")]
    public async Task<IActionResult> TestInsert(string label, string[] images)
    {
        await _labelService.CreateItem(new Label()
        { LabelID = label, Images = images.ToList(), Count = images.Length });
        return Ok();
    }

    // GET /test-detect
    /// <summary>
    /// Test a detect operation with an Amazon Simple Storage Service (Amazon S3) bucket and image key.
    /// </summary>
    /// <param name="bucket">The S3 bucket of the image.</param>
    /// <param name="key">The image key.</param>
    /// <returns>An OK result.</returns>
    [HttpGet("test-detect")]
    public async Task<IActionResult> TestDetect(string bucket, string key)
    {
        var detectedLabels = await _imageService.DetectLabels(key, bucket);
        await _labelService.AddImageLabels(key, detectedLabels);
        return Ok();
    }
}