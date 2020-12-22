// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache - 2.0

using Amazon;
using Amazon.Polly;
using Amazon.Polly.Model;
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

        private const string _OutputFileName = "speech.mp3";
        private const string _Text = "Twas brillig, and the slithy toves did gyre and gimbol in the wabe";

        static async Task Main(string[] args)
        {
            _PollyClient = new AmazonPollyClient(SERVICE_REGION);
            var response = await PollySynthesizeSpeech(_PollyClient, _Text);

        }

        static async Task<SynthesizeSpeechResponse> PollySynthesizeSpeech (IAmazonPolly client, string text)
        {
            var synthesizeSpeechRequest = new SynthesizeSpeechRequest()
            {
                OutputFormat = OutputFormat.Mp3,
                VoiceId = VoiceId.Joanna,
                Text = text
            };

            var synthesizeSpeechResponse =
                await client.SynthesizeSpeechAsync(synthesizeSpeechRequest);

            return synthesizeSpeechResponse;
        }

        public static async Task WriteSpechToFile()
        {
            //var audioStream = response.Result.AudioStream;
            //var outputStream = new FileStream(outputFileName, FileMode.Create,
            //    FileAccess.Write);
            //byte[] buffer = new byte[2 * 1024];
            //int readBytes;

            //while ((readBytes = audioStream.Read(buffer, 0, 2 * 1024)) > 0)
            //    outputStream.Write(buffer, 0, readBytes);
            //// If we don't flush the buffer, we lose the last second or so of
            //// the syntesized text.
            //outputStream.Flush();

        }

    }
}
