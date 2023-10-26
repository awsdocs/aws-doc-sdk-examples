// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Lambda.Core;
using Amazon.Textract;
using AWS.Lambda.Powertools.Logging;
using FsaServices.Models;
using FsaServices.Services;

// Assembly attribute to enable the Lambda function's JSON input to be converted into a .NET class.
[assembly: LambdaSerializer(typeof(Amazon.Lambda.Serialization.SystemTextJson.DefaultLambdaJsonSerializer))]

namespace FsaExtractText;

/// <summary>
/// Function to handle extracting the text from an image.
/// </summary>
public class ExtractTextFunction
{
    private readonly ExtractionService _extractionService;

    /// <summary>
    /// Default constructor. This constructor is used by Lambda to construct the instance. When invoked in a Lambda environment
    /// the AWS credentials will come from the IAM role associated with the function and the AWS Region will be set to the
    /// Region the Lambda function is executed in.
    /// </summary>
    public ExtractTextFunction()
    {
        var textractClient = new AmazonTextractClient();
        _extractionService = new ExtractionService(textractClient);
    }

    /// <summary>
    /// Constructs an instance with an Amazon Textract client. This can be used for testing outside of the Lambda environment.
    /// </summary>
    /// <param name="textractClient">Preconfigured Textract client.</param>
    public ExtractTextFunction(IAmazonTextract textractClient)
    {
        _extractionService = new ExtractionService(textractClient);
    }

    /// <summary>
    /// This method is called for every Lambda invocation. This method takes in an S3 event object and can be used 
    /// to respond to S3 notifications.
    /// </summary>
    /// <param name="evnt">The CloudWatch S3 Event.</param>
    /// <param name="context">The Lambda context.</param>
    /// <returns>The extracted words as a single string.</returns>
    public async Task<string?> FunctionHandler(CardObjectCreated evnt, ILambdaContext context)
    {
        // Log the event with Lambda PowerTools logger.
        var s3Event = evnt;
        Logger.LogInformation(evnt);

        var extractResponse = await _extractionService.ExtractWordsFromBucketObject(s3Event.Bucket, s3Event.Object);
        Logger.LogInformation($"Extracted text: {extractResponse}.");
        return extractResponse;

    }
}