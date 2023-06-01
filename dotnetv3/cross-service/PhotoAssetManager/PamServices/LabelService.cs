// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.DynamoDBv2.DataModel;
using Amazon.DynamoDBv2.DocumentModel;

namespace PamServices;

/// <summary>
/// Service to handle label operations for PAM.
/// </summary>
public class LabelService
{
    private readonly IDynamoDBContext _amazonDynamoDbContext;

    /// <summary>
    /// Constructor that uses the injected Amazon DynamoDB context.
    /// </summary>
    /// <param name="amazonDynamoDbContext">Amazon DynamoDB context.</param>
    public LabelService(IDynamoDBContext amazonDynamoDbContext)
    {
        _amazonDynamoDbContext = amazonDynamoDbContext;
    }

    /// <summary>
    /// Create a new label in the table.
    /// </summary>
    /// <param name="newLabel">The new label.</param>
    /// <returns>Async task.</returns>
    public async Task<Label> CreateItem(Label newLabel)
    {
        // Save the label in the table.
        await _amazonDynamoDbContext.SaveAsync(newLabel);
        // Return the newly created label.
        var labelRecord = await _amazonDynamoDbContext.LoadAsync<Label>(newLabel.LabelID);
        return labelRecord;
    }

    /// <summary>
    /// Get all items.
    /// </summary>
    /// <returns>A collection of Labels in alphabetical order.</returns>
    public async Task<IList<Label>> GetAllItems()
    {
        var scan = _amazonDynamoDbContext.FromScanAsync<Label>(
            new ScanOperationConfig()
        );

        var scanResponse = await scan.GetRemainingAsync();
        return scanResponse.OrderBy(l => l.LabelID.ToLower()).ToList();
    }

    /// <summary>
    /// Update the Labels with a new image.
    /// </summary>
    /// <param name="imageName">The name of the image.</param>
    /// <param name="labels">The labels associated with the image.</param>
    /// <returns>Async Task.</returns>
    public async Task AddImageLabels(string imageName, List<string> labels)
    {
        foreach (var label in labels)
        {
            await AddImageToLabel(imageName, label);
        }
    }

    /// <summary>
    /// Get all of the images for a set of labels.
    /// </summary>
    /// <param name="labels">The labels to fetch for images.</param>
    /// <returns>A collection of unique image names.</returns>
    public async Task<List<string>> GetAllImagesForLabels(List<string> labels)
    {
        var uniqueImages = new List<string>();
        // Get all of the records for the labels.
        foreach (var labelKey in labels)
        {
            var record = await _amazonDynamoDbContext.LoadAsync<Label>(labelKey);
            uniqueImages.AddRange(record.Images);
        }
        // Get a unique list of images.
        uniqueImages = uniqueImages.Distinct().ToList();

        return uniqueImages;
    }

    /// <summary>
    /// Update the Labels with a new image.
    /// </summary>
    /// <param name="imageName">The name of the image.</param>
    /// <param name="label">The label to add.</param>
    /// <returns>Async Task.</returns>
    private async Task AddImageToLabel(string imageName, string label)
    {
        var labelRecord = await GetNewOrExistingLabelRecord(label);

        if (!labelRecord.Images.Contains(imageName))
        {
            labelRecord.Images.Add(imageName);
            labelRecord.Count = labelRecord.Images.Count;
            await _amazonDynamoDbContext.SaveAsync<Label>(labelRecord);
        }
    }

    /// <summary>
    /// Get an existing label or a new label if none already exists.
    /// </summary>
    /// <param name="labelKey">The label key.</param>
    /// <returns>The new or existing label.</returns>
    private async Task<Label> GetNewOrExistingLabelRecord(string labelKey)
    {
        var existingLabel = await _amazonDynamoDbContext.LoadAsync<Label>(labelKey);
        return existingLabel ?? new Label { LabelID = labelKey, Images = new List<string>() };
    }
}