// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
// snippet-start:[comprehend.dotNET.DetectEntities]
using System;
using Amazon;
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
// snippet-end:[comprehend.dotNET.DetectEntities]