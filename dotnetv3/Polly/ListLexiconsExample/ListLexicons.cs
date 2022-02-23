// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Lists the Amazon Polly lexicons that have been defined. By default,
/// the list of lexicons defined in the same AWS Region as the default
/// user are shown. If you want to view Amazon Polly lexicons defined in
/// a different AWS Region, supply it as a parameter to the Amazon Polly
/// constructor.
///
/// This example was created using the AWS SDK for .NET version 3.7 and
/// .NET Core 5.0.
/// </summary>
namespace ListLexiconsExample
{
    // snippet-start:[Polly.dotnetv3.ListLexiconsExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.Polly;
    using Amazon.Polly.Model;

    public class ListLexicons
    {
        public static async Task Main()
        {
            var client = new AmazonPollyClient();
            var request = new ListLexiconsRequest();

            try
            {
                Console.WriteLine("All voices: ");

                do
                {
                    var response = await client.ListLexiconsAsync(request);
                    request.NextToken = response.NextToken;

                    response.Lexicons.ForEach(lexicon =>
                    {
                        var attributes = lexicon.Attributes;
                        Console.WriteLine($"Name: {lexicon.Name}");
                        Console.WriteLine($"\tAlphabet: {attributes.Alphabet}");
                        Console.WriteLine($"\tLanguageCode: {attributes.LanguageCode}");
                        Console.WriteLine($"\tLastModified: {attributes.LastModified}");
                        Console.WriteLine($"\tLexemesCount: {attributes.LexemesCount}");
                        Console.WriteLine($"\tLexiconArn: {attributes.LexiconArn}");
                        Console.WriteLine($"\tSize: {attributes.Size}");
                    });
                }
                while (request.NextToken is not null);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
            }
        }
    }

    // snippet-end:[Polly.dotnetv3.ListLexiconsExample]
}
