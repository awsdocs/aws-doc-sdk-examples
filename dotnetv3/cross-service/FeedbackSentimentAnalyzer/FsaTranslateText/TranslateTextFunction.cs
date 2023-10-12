// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Lambda.Core;
using Amazon.Translate;
using AWS.Lambda.Powertools.Logging;
using FsaServices.Models;
using FsaServices.Services;

// Assembly attribute to enable the Lambda function's JSON input to be converted into a .NET class.
[assembly: LambdaSerializer(typeof(Amazon.Lambda.Serialization.SystemTextJson.DefaultLambdaJsonSerializer))]

namespace FsaTranslateText;

/// <summary>
/// Function to handle translating the extracted text to English.
/// </summary>
public class TranslateTextFunction
{
    private readonly TranslationService _translationService;

    /// <summary>
    /// Default constructor. This constructor is used by AWS Lambda to construct the instance. When invoked in a Lambda environment,
    /// the AWS credentials will come from the AWS Identity and Access Management (IAM) role associated with the function. The AWS Region will be set to the
    /// Region the Lambda function is executed in.
    /// </summary>
    public TranslateTextFunction()
    {
        _translationService = new TranslationService(new AmazonTranslateClient());
    }

    /// <summary>
    /// Constructs an instance with a preconfigured Translate client. This can be used for testing outside of the Lambda environment.
    /// </summary>
    /// <param name="s3Client"></param>
    public TranslateTextFunction(IAmazonTranslate translateClient)
    {
        this._translationService = new TranslationService(translateClient);
    }

    /// <summary>
    /// A function that takes in the source text and returns the sentiment details.
    /// </summary>
    /// <param name="source_text">The text to analyze.</param>
    /// <param name="context">The Lambda context</param>
    /// <returns>Sentiment details.</returns>
    public async Task<TranslatedTextDetails> FunctionHandler(TextWithSourceLanguage source_text, ILambdaContext context)
    {
        // Log the object with Lambda PowerTools logger.
        Logger.LogInformation(source_text);
        var translatedText = await _translationService.TranslateToEnglish(source_text.extracted_text,
            source_text.source_language_code);
        Logger.LogInformation(translatedText);
        return new TranslatedTextDetails() { translated_text = translatedText };
    }
}