// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[comprehend.dotNET.DetectSentiment]
using System;
using Amazon;
using Amazon.Comprehend;
using Amazon.Comprehend.Model;

namespace ComprehendSamples1
{
    class DetectSentiment
    {
        public static void Sample()
        {
            String text = "It is raining today in Seattle";

            var comprehendClient = new AmazonComprehendClient(Amazon.RegionEndpoint.USWest2);

            // Call DetectKeyPhrases API
            Console.WriteLine("Calling DetectSentiment");
            var detectSentimentRequest = new DetectSentimentRequest()
            {
                Text = text,
                LanguageCode = "en"
            };
            var detectSentimentResponse = comprehendClient.DetectSentiment(detectSentimentRequest);
            Console.WriteLine(detectSentimentResponse.Sentiment);
            Console.WriteLine("Done");
        }
    }
}
// snippet-end:[comprehend.dotNET.DetectSentiment]