 
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
