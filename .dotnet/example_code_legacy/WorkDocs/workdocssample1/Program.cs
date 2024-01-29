// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Amazon;
using Amazon.WorkDocs;
using Amazon.WorkDocs.Model;

namespace WorkdocsSample1
{
    class Program
    {
        static void Main(string[] args)
        {
            ListUsers.Sample();
            ListUserDocs.Sample();
            UploadUserDoc.Sample();
            DownloadUserDoc.Sample();
        }
    }
}
