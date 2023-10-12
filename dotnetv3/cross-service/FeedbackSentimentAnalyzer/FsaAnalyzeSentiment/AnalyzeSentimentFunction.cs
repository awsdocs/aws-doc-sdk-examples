// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Comprehend;
using Amazon.Lambda.Core;
using AWS.Lambda.Powertools.Logging;
using FsaServices.Models;
using FsaServices.Services;

// Assembly attribute to enable the Lambda function's JSON input to be converted into a .NET class.
[assembly: LambdaSerializer(typeof(Amazon.Lambda.Serialization.SystemTextJson.DefaultLambdaJsonSerializer))]

namespace FsaAnalyzeSentiment;

/// <summary>
/// Function to handle analysis of sentiment.
/// </summary>
public class AnalyzeSentimentFunction
{
    private readonly SentimentService _sentimentService;

    /// <summary>
    /// Default constructor. This constructor is used by AWS Lambda to construct the instance. When invoked in a Lambda environment,
    /// the AWS credentials will come from the AWS Identity and Access Management (IAM) role associated with the function. The AWS Region will be set to the
    /// Region the Lambda function is executed in.
    /// </summary>
    public AnalyzeSentimentFunction()
    {
        _sentimentService = new SentimentService(new AmazonComprehendClient());
    }

    /// <summary>
    /// Constructs an instance with a preconfigured Comprehend client. This can be used for testing outside of the Lambda environment.
    /// </summary>
    /// <param name="s3Client"></param>
    public AnalyzeSentimentFunction(IAmazonComprehend comprehendClient)
    {
        this._sentimentService = new SentimentService(comprehendClient);
    }

    /// <summary>
    /// A function that takes in the source text and returns the sentiment details.
    /// </summary>
    /// <param name="extractTextOutput">The extracted text output to analyze.</param>
    /// <param name="context">The Lambda context</param>
    /// <returns>Sentiment details.</returns>
    public async Task<SentimentDetails> FunctionHandler(SourceTextDetails extractTextOutput, ILambdaContext context)
    {
        // Log the object with Lambda PowerTools logger.
        Logger.LogInformation(extractTextOutput);
        var result = await _sentimentService.AnalyzeTextSentiment(extractTextOutput.source_text);
        Logger.LogInformation(result);
        return result;
    }
}