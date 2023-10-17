// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Translate;
using Amazon.Translate.Model;

namespace FsaServices.Services;

/// <summary>
/// Service to handle translating text to English.
/// </summary>
public class TranslationService
{
    private readonly IAmazonTranslate _amazonTranslate;

    /// <summary>
    /// Constructor that uses the injected Amazon Translate client.
    /// </summary>
    /// <param name="amazonTranslate">Amazon Translate client.</param>
    public TranslationService(IAmazonTranslate amazonTranslate)
    {
        _amazonTranslate = amazonTranslate;
    }

    /// <summary>
    /// Translate a string to the specified target language.
    /// </summary>
    /// <param name="sourceText">The source text to translate.</param>
    /// <param name="languageCode">The language code of the source text.</param>
    /// <returns>The translated string.</returns>
    public async Task<string> TranslateToEnglish(string sourceText, string languageCode)
    {
        if (string.IsNullOrEmpty(sourceText))
        {
            throw new InvalidOperationException(
                "Cannot translate an empty string.");
        }

        var translateResponse = await _amazonTranslate.TranslateTextAsync(
            new TranslateTextRequest()
            {
                Text = sourceText,
                SourceLanguageCode = languageCode,
                TargetLanguageCode = "en"
            });

        return translateResponse.TranslatedText;
    }

}