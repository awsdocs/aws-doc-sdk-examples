// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Rekognition;
using Amazon.Rekognition.Model;

namespace PamServices;

/// <summary>
/// Service to handle image operations for PAM.
/// </summary>
public class ImageService
{
    private readonly IAmazonRekognition _amazonRekognition;

    /// <summary>
    /// Constructor that uses the injected Amazon Rekognition client.
    /// </summary>
    /// <param name="amazonRekognition">Amazon Rekognition client.</param>
    public ImageService(IAmazonRekognition amazonRekognition)
    {
        _amazonRekognition = amazonRekognition;
    }

    /// <summary>
    /// Detect and return labels for an image.
    /// </summary>
    /// <param name="newLabel">The new label.</param>
    /// <returns>Async task.</returns>
    public async Task<List<string>> DetectLabels(string objectKey, string bucket)
    {
        var detectResponses = await _amazonRekognition.DetectLabelsAsync(new DetectLabelsRequest
        {
            Image = new Image
            {
                S3Object = new S3Object
                {
                    Bucket = bucket,
                    Name = objectKey
                }
            }
        });

        var labels = detectResponses.Labels.Select(response => response.Name).ToList();
        return labels;
    }
}