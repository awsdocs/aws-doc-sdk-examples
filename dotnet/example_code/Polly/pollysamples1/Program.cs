//snippet-sourcedescription:[Program.cs can be used to execute the Polly examples in this solution.]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-keyword:[Code Sample]
//snippet-service:[polly]
//snippet-sourcetype:[full-example]
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
