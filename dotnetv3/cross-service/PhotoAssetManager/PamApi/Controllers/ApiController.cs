// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.S3;
using Amazon.S3.Model;
using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Mvc;
using System.Data.SqlTypes;
using System.Net.Mime;
using System.Text.Json;
using Amazon.Lambda.Core;
using PamServices;

namespace PamApi.Controllers;

[Route("")]
public class ApiController : ControllerBase
{
    private readonly LabelService _labelService;
    private readonly StorageService _storageService;
    private readonly ImageService _imageService;

    /// <summary>
    /// Constructor for the controller, uses dependency injection to get the services needed.
    /// </summary>
    /// <param name="storageService">The injected image service.</param>
    /// <param name="labelService">the injected label service.</param>
    /// <param name="imageService">the injected image service.</param>
    public ApiController(StorageService storageService, LabelService labelService, ImageService imageService)
    {
        _storageService = storageService;
        _labelService = labelService;
        _imageService = imageService;
    }

    // PUT /upload
    [HttpPut("upload")]
    public IActionResult Upload([FromBody] UploadRequest uploadRequest)
    {
        var storageBucketName = Environment.GetEnvironmentVariable("STORAGE_BUCKET_NAME");

        var presignedUrl = _storageService.GetPresignedUrlForImage(uploadRequest.file_name, storageBucketName!);

        var response = new UploadResponse() { url = presignedUrl };
        return Ok(response);
    }

    // GET /labels
    [HttpGet("labels")]
    public async Task<IActionResult> Get()
    {
        LambdaLogger.Log($"test logging: getting labels.");
        var allLabels = await _labelService.GetAllItems();
        var response = new LabelsResponse(allLabels.ToList());
        return Ok(response);
    }

    // GET /test-insert
    [HttpGet("test-insert")]
    public async Task<IActionResult> TestInsert(string label, string[] images)
    {
        await _labelService.CreateItem(new Label()
        { LabelID = label, Images = images.ToList(), Count = images.Length });
        return Ok();
    }

    // GET /test-detect
    [HttpGet("test-detect")]
    public async Task<IActionResult> TestDetect(string bucket, string key)
    {
        var detectedLabels = await _imageService.DetectLabels(key, bucket);
        await _labelService.AddImageLabels(key, detectedLabels);
        return Ok();
    }
}