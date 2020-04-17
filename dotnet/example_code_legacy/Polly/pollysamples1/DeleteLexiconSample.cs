//snippet-sourcedescription:[DeleteLexiconSample.cs demonstrates how to delete an existing lexicon.]
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
    class DeleteLexiconSample
    {
        public static void DeleteLexicon()
        {
            String LEXICON_NAME = "SampleLexicon";

            var client = new AmazonPollyClient();
            var deleteLexiconRequest = new DeleteLexiconRequest()
            {
                Name = LEXICON_NAME
            };

            try
            {
                client.DeleteLexicon(deleteLexiconRequest);
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception caught: " + e.Message);
            }
        }
    }
}
