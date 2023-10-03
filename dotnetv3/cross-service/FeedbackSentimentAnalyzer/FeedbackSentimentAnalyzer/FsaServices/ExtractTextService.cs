// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Textract;
using Amazon.Textract.Model;

namespace FsaServices;

/// <summary>
/// Service to handle extracting text from images.
/// </summary>
public class ExtractTextService
{
    private readonly IAmazonTextract _amazonTextract;

    /// <summary>
    /// Constructor that uses the injected Amazon Textract client.
    /// </summary>
    /// <param name="amazonTextract">Amazon Textract client.</param>
    public ExtractTextService(IAmazonTextract amazonTextract)
    {
        _amazonTextract = amazonTextract;
    }

    /// <summary>
    /// 
    /// </summary>
    /// <param name="bucket"></param>
    /// <param name="name"></param>
    /// <returns></returns>
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