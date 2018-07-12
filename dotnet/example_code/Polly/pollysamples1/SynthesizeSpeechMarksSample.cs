using System;
using System.Collections.Generic;
using System.IO;
using Amazon.Polly;
using Amazon.Polly.Model;

namespace PollySamples
{
    class SynthesizeSpeechMarksSample
    {
        public static void SynthesizeSpeechMarks()
        {
            AmazonPollyClient client = new AmazonPollyClient();
            String outputFileName = "speechMarks.json";

            SynthesizeSpeechRequest synthesizeSpeechRequest = new SynthesizeSpeechRequest()
            {
                OutputFormat = OutputFormat.Json,
                SpeechMarkTypes = new List<String>() { SpeechMarkType.Viseme, SpeechMarkType.Word },
                VoiceId = VoiceId.Joanna,
                Text = "This is a sample text to be synthesized."
            };

            try
            {
                using (FileStream outputStream = new FileStream(outputFileName, FileMode.Create, FileAccess.Write))
                {
                    SynthesizeSpeechResponse synthesizeSpeechResponse = client.SynthesizeSpeech(synthesizeSpeechRequest);
                    byte[] buffer = new byte[2 * 1024];
                    int readBytes;

                    Stream inputStream = synthesizeSpeechResponse.AudioStream;
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