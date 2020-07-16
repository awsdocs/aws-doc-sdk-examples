// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
// snippet-start:[comprehend.dotNET.DetectDominantLanguage]
using System;
using Amazon;
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
// snippet-end:[comprehend.dotNET.DetectDominantLanguage]