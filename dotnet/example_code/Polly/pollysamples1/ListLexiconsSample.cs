using System;
using Amazon.Polly;
using Amazon.Polly.Model;

namespace PollySamples
{
    class ListLexiconsSample
    {
        public static void ListLexicons()
        {
            AmazonPollyClient client = new AmazonPollyClient();

            ListLexiconsRequest listLexiconsRequest = new ListLexiconsRequest();

            try
            {
                String nextToken;
                do
                {
                    ListLexiconsResponse listLexiconsResponse = client.ListLexicons(listLexiconsRequest);
                    nextToken = listLexiconsResponse.NextToken;
                    listLexiconsResponse.NextToken = nextToken;

                    Console.WriteLine("All voices: ");
                    foreach (LexiconDescription lexiconDescription in listLexiconsResponse.Lexicons)
                    {
                        LexiconAttributes attributes = lexiconDescription.Attributes;
                        Console.WriteLine("Name: " + lexiconDescription.Name
                            + ", Alphabet: " + attributes.Alphabet
                            + ", LanguageCode: " + attributes.LanguageCode
                            + ", LastModified: " + attributes.LastModified
                            + ", LexemesCount: " + attributes.LexemesCount
                            + ", LexiconArn: " + attributes.LexiconArn
                            + ", Size: " + attributes.Size);
                    }
                } while (nextToken != null);
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception caught: " + e.Message);
            }
        }
    }
}
