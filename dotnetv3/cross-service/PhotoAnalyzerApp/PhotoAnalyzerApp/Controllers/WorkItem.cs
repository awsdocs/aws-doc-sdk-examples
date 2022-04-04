/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace PhotoAnalyzerApp.Controllers
{
    public class WorkItem
    {
        public String Key { get; set; }
        public String Name { get; set; }
        public String Confidence { get; set; }
    }
}
