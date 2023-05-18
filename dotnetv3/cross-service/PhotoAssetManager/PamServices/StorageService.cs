// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.S3;
using Amazon.S3.Model;
using System;
using Amazon.SimpleNotificationService.Model;

namespace PamServices;

/// <summary>
/// Service for working with storage for the photo analyzer.
/// </summary>
public class StorageService
{
    private readonly IAmazonS3 _amazonS3;

    public StorageService(IAmazonS3 amazonS3)
    {
        _amazonS3 = amazonS3;
    }

    /// <summary>
    /// Generate a unique filename and presigned url for uploading an image.
    /// </summary>
    /// <param name="fileName">The filename of the image.</param>
    /// /// <param name="storageBucket">The name of the storage bucket.</param>
    /// <returns>The presigned url.</returns>
    public string GetPresignedUrlForImage(string fileName, string storageBucket)
    {
        var uuid = Guid.NewGuid().ToString();
        var uniqueFileName = $"{uuid}-{fileName}";

        // todo: check that filename exists and is of an image type, generate an error if not

        var preSignedUrlResponse = _amazonS3.GetPreSignedURL(
            new GetPreSignedUrlRequest()
            {
                BucketName = storageBucket,
                Key = uniqueFileName,
                ContentType = "image/jpeg",
                Expires = DateTime.UtcNow.AddMinutes(5),
                Verb = HttpVerb.PUT
            });

        return preSignedUrlResponse;
    }

    /// <summary>
    /// Generate a zip file for download for a set of images in a bucket.
    /// </summary>
    /// <param name="imageKeys">The list of image keys.</param>
    /// <param name="storageBucket">The storage bucket for the images.</param>
    /// <param name="workingBucket"></param>
    /// <returns></returns>
    public async Task<string> GenerateZipFromImages(List<string> imageKeys, string storageBucket, string workingBucket)
    {

    }
}