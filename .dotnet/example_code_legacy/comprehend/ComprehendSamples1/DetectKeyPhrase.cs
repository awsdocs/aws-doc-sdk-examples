// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
// snippet-start:[comprehend.dotNET.DetectKeyPhrase]
using System;
using Amazon;
using Amazon.Comprehend;
using Amazon.Comprehend.Model;

namespace ComprehendSamples1
{
    class DetectKeyPhrases
    {
        public static void Sample()
        {
            String text = "It is raining today in Seattle";

            var comprehendClient = new AmazonComprehendClient(Amazon.RegionEndpoint.USWest2);

            // Call DetectKeyPhrases API
            Console.WriteLine("Calling DetectKeyPhrases");
            var detectKeyPhrasesRequest = new DetectKeyPhrasesRequest()
            {
                Text = text,
                LanguageCode = "en"
            };
            var detectKeyPhrasesResponse = comprehendClient.DetectKeyPhrases(detectKeyPhrasesRequest);
            foreach (var kp in detectKeyPhrasesResponse.KeyPhrases)
                Console.WriteLine("Text: {1}, Type: {1}, BeginOffset: {2}, EndOffset: {3}",
                    kp.Text, kp.Text, kp.BeginOffset, kp.EndOffset);
            Console.WriteLine("Done");
        }
    }
}
// snippet-end:[comprehend.dotNET.DetectKeyPhrase]