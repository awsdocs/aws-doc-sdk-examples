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
