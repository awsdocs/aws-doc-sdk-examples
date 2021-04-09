// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon;
using Amazon.Polly;
using Amazon.Polly.Model;
using System.IO;
using System.Threading.Tasks;

namespace SynthesizeSpeech
{
    class SynthesizeSpeech
    {
        // This example uses the Amazon Polly service to convert text to
        // speech. It then saves the converted text to an MP3 file. The
        // code was written against the AWS SDK for .NET 3.5 and .NET 5.0.

        // Specify your AWS Region (an example Region is shown).
        private static readonly RegionEndpoint serviceRegion = RegionEndpoint.USEast2;
        private static IAmazonPolly polyClient;

        private const string outputFileName = "speech.mp3";
        private const string text = "Twas brillig, and the slithy toves did gyre and gimbol in the wabe";

        static async Task Main()
        {
            polyClient = new AmazonPollyClient(serviceRegion);
            var response = await PollySynthesizeSpeech(polyClient, text);
            
            WriteSpeechToStream(response.AudioStream, outputFileName);
        }

        /// <summary>
        /// Calls the Amazon Polly SynthesizeSpeechAsync method to convert text
        /// to speech.
        /// </summary>
        /// <param name="client">The Amazon Polly client object used to connect
        /// to the Amazon Polly service.</param>
        /// <param name="text">The text which will be converted to speech.</param>
        /// <returns>A SynthesizeSpeechResponse object that includes an AudioStream
        /// object with the converted text.</returns>
        private static async Task<SynthesizeSpeechResponse> PollySynthesizeSpeech (IAmazonPolly client, string text)
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

        /// <summary>
        /// Writes the AudioStream returned from the call to
        /// SynthesizeSpeechAsync to a file in MP3 format.
        /// </summary>
        /// <param name="audioStream">The AudioStream returned from the
        /// call to the SynthesizeSpeechAsync method.</param>
        /// <param name="outputFileName">The full path to the file in which to
        /// save the audio stream.</param>
        private static void WriteSpeechToStream(Stream audioStream, string outputFileName)
        {
            var outputStream = new FileStream(outputFileName, FileMode.Create,
                FileAccess.Write);
            byte[] buffer = new byte[2 * 1024];
            int readBytes;

            while ((readBytes = audioStream.Read(buffer, 0, 2 * 1024)) > 0)
                outputStream.Write(buffer, 0, readBytes);

            // If we don't flush the buffer, we lose the last second or so of
            // the synthesized text.
            outputStream.Flush();
        }
    }
}
