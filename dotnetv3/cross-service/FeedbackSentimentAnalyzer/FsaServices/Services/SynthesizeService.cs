// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Polly;
using Amazon.Polly.Model;
using Amazon.S3;
using Amazon.S3.Transfer;
using FsaServices.Models;

namespace FsaServices.Services;

/// <summary>
/// Service to handle synthesizing audio from text.
/// </summary>
public class SynthesizeService
{
    private readonly IAmazonPolly _amazonPolly;
    private readonly IAmazonS3 _amazonS3;

    /// <summary>
    /// Constructor that uses the injected Amazon Polly and S3 clients.
    /// </summary>
    /// <param name="amazonPolly">Amazon Polly client.</param>
    /// <param name="amazonS3">Amazon S3 client.</param>
    public SynthesizeService(IAmazonPolly amazonPolly, IAmazonS3 amazonS3)
    {
        _amazonPolly = amazonPolly;
        _amazonS3 = amazonS3;
    }

    /// <summary>
    /// Extract the words from a given bucket object and return them in a single string.
    /// </summary>
    /// <param name="sourceDestinationDetails">The source destination bucket, text, and object ke.y</param>
    /// <returns>The name of the result object in the bucket.</returns>
    public async Task<string> SynthesizeSpeechFromText(AudioSourceDestinationDetails sourceDestinationDetails)
    {
        if (string.IsNullOrEmpty(sourceDestinationDetails.translated_text))
        {
            throw new InvalidOperationException(
                "Cannot synthesize audio for an empty string.");
        }

        var synthesizedResponse = await _amazonPolly.SynthesizeSpeechAsync(
            new SynthesizeSpeechRequest()
            {
                Engine = Engine.Neural,
                Text = sourceDestinationDetails.translated_text,
                VoiceId = VoiceId.Ruth,
                OutputFormat = OutputFormat.Mp3
            });

        var audioKey = $"{sourceDestinationDetails.Object}.mp3";

        var transfer = new TransferUtility(_amazonS3);
        var uploadStreamRequest = new TransferUtilityUploadRequest
        {
            BucketName = sourceDestinationDetails.bucket,
            Key = audioKey,
            InputStream = synthesizedResponse.AudioStream,
            ContentType = "audio/mpeg",
        };

        await transfer.UploadAsync(uploadStreamRequest);

        return audioKey;
    }
}