using System;
using Amazon.Polly;
using Amazon.Polly.Model;

namespace PollySamples
{
    class DescribeVoicesSample
    {
        public static void DescribeVoices()
        {
            AmazonPollyClient client = new AmazonPollyClient();

            DescribeVoicesRequest allVoicesRequest = new DescribeVoicesRequest();
            DescribeVoicesRequest enUsVoicesRequest = new DescribeVoicesRequest()
            {
                LanguageCode = "en-US"
            };

            try
            {
                String nextToken;
                do
                {
                    DescribeVoicesResponse allVoicesResponse = client.DescribeVoices(allVoicesRequest);
                    nextToken = allVoicesResponse.NextToken;
                    allVoicesRequest.NextToken = nextToken;

                    Console.WriteLine("All voices: ");
                    foreach (Voice voice in allVoicesResponse.Voices)
                        Console.WriteLine(" Name: {0}, Gender: {1}, LanguageName: {2}", voice.Name,
                            voice.Gender, voice.LanguageName);
                } while (nextToken != null);

                do
                {
                    DescribeVoicesResponse enUsVoicesResponse = client.DescribeVoices(enUsVoicesRequest);
                    nextToken = enUsVoicesResponse.NextToken;
                    enUsVoicesRequest.NextToken = nextToken;

                    Console.WriteLine("en-US voices: ");
                    foreach (Voice voice in enUsVoicesResponse.Voices)
                        Console.WriteLine(" Name: {0}, Gender: {1}, LanguageName: {2}", voice.Name,
                            voice.Gender, voice.LanguageName);
                } while (nextToken != null);
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception caught: " + e.Message);
            }
        }
    }
}
