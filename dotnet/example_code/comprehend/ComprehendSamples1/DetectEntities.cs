//snippet-sourcedescription:[DetectEntities.cs demonstrates how to detect entities in a given sample string using the Comprehend client.]
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
    class DetectEntities
    {
        public static void Sample()
        {
            String text = "It is raining today in Seattle";

            var comprehendClient = new AmazonComprehendClient(Amazon.RegionEndpoint.USWest2);

            // Call DetectEntities API
            Console.WriteLine("Calling DetectEntities\n");
            var detectEntitiesRequest =  new DetectEntitiesRequest()
            {
                Text = text,
                LanguageCode = "en"
            };
            var detectEntitiesResponse = comprehendClient.DetectEntities(detectEntitiesRequest);
            foreach (var e in detectEntitiesResponse.Entities)
                Console.WriteLine("Text: {1}, Type: {1}, Score: {2}, BeginOffset: {3}, EndOffset: {4}",
                    e.Text, e.Type, e.Score, e.BeginOffset, e.EndOffset);
            Console.WriteLine("Done");
        }
    }
}
