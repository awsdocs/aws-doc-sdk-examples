//snippet-sourcedescription:[GetLexiconSample.cs demonstrates how to get the content of the specified pronunciation lexicon.]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-sourcesyntax:[.net]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Polly]
//snippet-service:[polly]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
﻿using System;
using Amazon.Polly;
using Amazon.Polly.Model;

namespace PollySamples1
{
    class GetLexiconSample
    {
        public static void GetLexicon()
        {
            String LEXICON_NAME = "SampleLexicon";

            var client = new AmazonPollyClient();
            var getLexiconRequest = new GetLexiconRequest()
            {
                Name = LEXICON_NAME
            };

            try
            {
                var getLexiconResponse = client.GetLexicon(getLexiconRequest);
                Console.WriteLine("Lexicon:\n Name: {0}\nContent: {1}", getLexiconResponse.Lexicon.Name,
                    getLexiconResponse.Lexicon.Content);
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception caught: " + e.Message);
            }
        }
    }
}
