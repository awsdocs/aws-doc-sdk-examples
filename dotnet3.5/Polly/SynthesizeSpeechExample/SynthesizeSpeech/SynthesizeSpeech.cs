// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache - 2.0

using Amazon;
using Amazon.Polly;
using System;
using System.Threading.Tasks;

namespace SynthesizeSpeech
{
    class SynthesizeSpeech
    {
        // The following example text to speech and saves it to an .mp3 file
        // using Amazon Polly. It was created using the AWS SDK for .NET 3.5
        // and .NET 5.0.

        // Specify your AWS Region (an example Region is shown).
        private static readonly RegionEndpoint SERVICE_REGION = RegionEndpoint.USWest2;
        private static IAmazonPolly _PollyClient;

        static async Task Main(string[] args)
        {
            _PollyClient = new AmazonPollyClient(SERVICE_REGION);
        }
    }
}
