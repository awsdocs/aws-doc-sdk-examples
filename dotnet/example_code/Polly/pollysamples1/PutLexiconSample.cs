using System;
using Amazon.Polly;
using Amazon.Polly.Model;

namespace PollySamples
{
    class PutLexiconSample
    {
        public static void PutLexicon()
        {
        String LEXICON_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<lexicon version=\"1.0\" xmlns=\"http://www.w3.org/2005/01/pronunciation-lexicon\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xsi:schemaLocation=\"http://www.w3.org/2005/01/pronunciation-lexicon http://www.w3.org/TR/2007/CR-pronunciation-lexicon-20071212/pls.xsd\" " +
            "alphabet=\"ipa\" xml:lang=\"en-US\">" +
            "<lexeme><grapheme>test1</grapheme><alias>test2</alias></lexeme>" +
            "</lexicon>";
        String LEXICON_NAME = "SampleLexicon";

            AmazonPollyClient client = new AmazonPollyClient();
            PutLexiconRequest putLexiconRequest = new PutLexiconRequest()
            {
                Name = LEXICON_NAME,
                Content = LEXICON_CONTENT
            };

            try
            {
                client.PutLexicon(putLexiconRequest);
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception caught: " + e.Message);
            }
        }
    }
}
