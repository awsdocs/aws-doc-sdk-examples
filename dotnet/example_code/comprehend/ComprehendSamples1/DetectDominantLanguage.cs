//snippet-sourcedescription:[DetechDominantLanguage.cs demonstrates how to get the language code for the dominant language detected for a sample string.]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Comprehend]
//snippet-service:[comprehend]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
using System;
using Amazon.Comprehend;
using Amazon.Comprehend.Model;

namespace ComprehendSamples1
{
    class DetectDominantLanguage
    {
        public static void Sample()
        {
            String text = "It is raining today in Seattle";

            var comprehendClient = new AmazonComprehendClient(Amazon.RegionEndpoint.USWest2);

            // Call DetectDominantLanguage API
            Console.WriteLine("Calling DetectDominantLanguage\n");
            var detectDominantLanguageRequest = new DetectDominantLanguageRequest()
            {
                Text = text
            };
            var detectDominantLanguageResponse = comprehendClient.DetectDominantLanguage(detectDominantLanguageRequest);
            foreach (var dl in detectDominantLanguageResponse.Languages)
                Console.WriteLine("Language Code: {0}, Score: {1}", dl.LanguageCode, dl.Score);
            Console.WriteLine("Done");
        }
    }
}
