//snippet-sourcedescription:[Program.cs can be used to run the examples in this ComprehendSamples1 solution.]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-sourcesyntax:[.net]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Comprehend]
//snippet-service:[comprehend]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ComprehendSamples1
{
    class Program
    {
        static void Main(string[] args)
        {
            DetectDominantLanguage.Sample();
            DetectEntities.Sample();
            DetectKeyPhrases.Sample();
            DetectSentiment.Sample();
            TopicModeling.Sample();
            UsingBatchAPIs.Sample();
        }
    }
}
