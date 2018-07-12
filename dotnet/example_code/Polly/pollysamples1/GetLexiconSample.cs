using System;
using Amazon.Polly;
using Amazon.Polly.Model;

namespace PollySamples
{
    class GetLexiconSample
    {
        public static void GetLexicon()
        {
            String LEXICON_NAME = "SampleLexicon";

            AmazonPollyClient client = new AmazonPollyClient();
            GetLexiconRequest getLexiconRequest = new GetLexiconRequest()
            {
                Name = LEXICON_NAME
            };

            try
            {
                GetLexiconResponse getLexiconResponse = client.GetLexicon(getLexiconRequest);
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
