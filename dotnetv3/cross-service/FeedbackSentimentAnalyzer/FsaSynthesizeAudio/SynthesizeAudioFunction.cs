// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Lambda.Core;
using Amazon.Polly;
using Amazon.S3;
using AWS.Lambda.Powertools.Logging;
using FsaServices.Models;
using FsaServices.Services;

// Assembly attribute to enable the Lambda function's JSON input to be converted into a .NET class.
[assembly: LambdaSerializer(typeof(Amazon.Lambda.Serialization.SystemTextJson.DefaultLambdaJsonSerializer))]

namespace FsaSynthesizeAudio;

/// <summary>
/// Function to handle synthesizing audio.
/// </summary>
public class SynthesizeAudioFunction
{
    private readonly SynthesizeService _synthesizeService;

    /// <summary>
    /// Default constructor. This constructor is used by Lambda to construct the instance. When invoked in a Lambda environment
    /// the AWS credentials will come from the IAM role associated with the function and the AWS region will be set to the
    /// region the Lambda function is executed in.
    /// </summary>
    public SynthesizeAudioFunction()
    {
        var pollyClient = new AmazonPollyClient();
        var s3Client = new AmazonS3Client();
        _synthesizeService = new SynthesizeService(pollyClient, s3Client);
    }

    /// <summary>
    /// Constructs an instance with an Amazon Textract client. This can be used for testing outside of the Lambda environment.
    /// </summary>
    /// <param name="s3Client"></param>
    public SynthesizeAudioFunction(IAmazonPolly pollyClient, IAmazonS3 s3Client)
    {
        _synthesizeService = new SynthesizeService(pollyClient, s3Client);
    }


    /// <summary>
    /// Takes in the audio source and destination details, and returns the key of the new media object after synthesis.
    /// </summary>
    /// <param name="input">The source and destination details input.</param>
    /// <param name="context">The Lambda context.</param>
    /// <returns>The key of the new media object.</returns>
    public async Task<string> FunctionHandler(AudioSourceDestinationDetails input, ILambdaContext context)
    {
        // Log the input with Lambda PowerTools logger.
        Logger.LogInformation(input);
        var synthesizeResponse = await _synthesizeService.SynthesizeSpeechFromText(input);
        Logger.LogInformation(synthesizeResponse);
        return synthesizeResponse;
    }
}