//snippet-sourceauthor: [ebattalio]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

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
