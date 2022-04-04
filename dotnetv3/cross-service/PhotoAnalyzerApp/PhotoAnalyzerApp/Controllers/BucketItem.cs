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
    public class BucketItem
    {
        public string Key { get; set; }
        public string Owner { get; set; }
        public string Size { get; set; }
    }
}
