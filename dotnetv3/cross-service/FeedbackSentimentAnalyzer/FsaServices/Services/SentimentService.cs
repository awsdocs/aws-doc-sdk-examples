// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Comprehend;
using Amazon.Comprehend.Model;
using FsaServices.Models;

namespace FsaServices.Services;

/// <summary>
/// Service to handle analyzing sentiment from text.
/// </summary>
public class SentimentService
{
    private readonly IAmazonComprehend _amazonComprehend;

    /// <summary>
    /// Constructor that uses the injected Amazon Comprehend client.
    /// </summary>
    /// <param name="amazonComprehend">Amazon Comprehend client.</param>
    public SentimentService(IAmazonComprehend amazonComprehend)
    {
        _amazonComprehend = amazonComprehend;
    }

    /// <summary>
    /// Analyze and return the sentiment for the source text.
    /// </summary>
    /// <param name="sourceText">The text to analyze.</param>
    /// <returns>The sentiment details.</returns>
    public async Task<SentimentDetails> AnalyzeTextSentiment(string sourceText)
    {
        if (string.IsNullOrEmpty(sourceText))
        {
            throw new InvalidOperationException(
                "Cannot detect sentiment for an empty string.");
        }

        var languages = await _amazonComprehend.DetectDominantLanguageAsync(
            new DetectDominantLanguageRequest()
            {
                Text = sourceText
            });
        var firstLanguage = languages.Languages[0].LanguageCode;

        var sentiment = await _amazonComprehend.DetectSentimentAsync(
            new DetectSentimentRequest()
            {
                LanguageCode = firstLanguage,
                Text = sourceText
            });

        return new SentimentDetails()
        {
            language_code = firstLanguage,
            sentiment = sentiment.Sentiment.Value
        };
    }
}