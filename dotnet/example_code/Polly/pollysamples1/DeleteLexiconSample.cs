using System;
using Amazon.Polly;
using Amazon.Polly.Model;

namespace PollySamples
{
    class DeleteLexiconSample
    {
        public static void DeleteLexicon()
        {
            String LEXICON_NAME = "SampleLexicon";

            AmazonPollyClient client = new AmazonPollyClient();
            DeleteLexiconRequest deleteLexiconRequest = new DeleteLexiconRequest()
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
