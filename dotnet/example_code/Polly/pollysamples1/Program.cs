//snippet-sourceauthor: [ebattalio]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PollySamples1
{
    class Program
    {
        static void Main(string[] args)
        {
            PutLexiconSample.PutLexicon();
            GetLexiconSample.GetLexicon();
            ListLexiconsSample.ListLexicons();
            DeleteLexiconSample.DeleteLexicon();
            DescribeVoicesSample.DescribeVoices();
            SynthesizeSpeechMarksSample.SynthesizeSpeechMarks();
            SynthesizeSpeechSample.SynthesizeSpeech();
        }
    }
}
