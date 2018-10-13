 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
