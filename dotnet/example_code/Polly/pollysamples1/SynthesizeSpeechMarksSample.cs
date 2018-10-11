//snippet-sourceauthor: [ebattalio]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

﻿using System;
using System.Collections.Generic;
using System.IO;
using Amazon.Polly;
using Amazon.Polly.Model;

namespace PollySamples1
{
    class SynthesizeSpeechMarksSample
    {
        public static void SynthesizeSpeechMarks()
        {
            var client = new AmazonPollyClient();
            String outputFileName = "speechMarks.json";

            var synthesizeSpeechRequest = new SynthesizeSpeechRequest()
            {
                OutputFormat = OutputFormat.Json,
                SpeechMarkTypes = new List<String>() { SpeechMarkType.Viseme, SpeechMarkType.Word },
                VoiceId = VoiceId.Joanna,
                Text = "This is a sample text to be synthesized."
            };

            try
            {
                using (var outputStream = new FileStream(outputFileName, FileMode.Create, FileAccess.Write))
                {
                    var synthesizeSpeechResponse = client.SynthesizeSpeech(synthesizeSpeechRequest);
                    var buffer = new byte[2 * 1024];
                    int readBytes;

                    var inputStream = synthesizeSpeechResponse.AudioStream;
                    while ((readBytes = inputStream.Read(buffer, 0, 2 * 1024)) > 0)
                        outputStream.Write(buffer, 0, readBytes);
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception caught: " + e.Message);
            }
        }
    }
}