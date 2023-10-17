// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Textract;
using Amazon.Textract.Model;

namespace FsaServices.Services;

/// <summary>
/// Service to handle extracting text from images.
/// </summary>
public class ExtractionService
{
    private readonly IAmazonTextract _amazonTextract;

    /// <summary>
    /// Constructor that uses the injected Amazon Textract client.
    /// </summary>
    /// <param name="amazonTextract">Amazon Textract client.</param>
    public ExtractionService(IAmazonTextract amazonTextract)
    {
        _amazonTextract = amazonTextract;
    }

    /// <summary>
    /// Extract the words from a given bucket object and return them in a single string.
    /// </summary>
    /// <param name="bucket">The source bucket.</param>
    /// <param name="name">The key of the bucket object.</param>
    /// <returns>Words as a single string.</returns>
    public async Task<string> ExtractWordsFromBucketObject(string bucket, string name)
    {
        var detectTextResponse = await _amazonTextract.DetectDocumentTextAsync(
            new DetectDocumentTextRequest()
            {
                Document = new Document()
                {
                    S3Object = new S3Object() { Bucket = bucket, Name = name }
                }
            });

        var words = detectTextResponse.Blocks
            .Where(b => b.BlockType == BlockType.WORD)
            .Select(w => w.Text);

        return string.Join(' ', words);
    }
}