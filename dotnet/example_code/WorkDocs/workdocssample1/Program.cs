//snippet-sourcedescription:[Program.cs can be used to execute the WorkDocs examples in this solution.]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-sourcesyntax:[.net]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon WorkDocs]
//snippet-service:[workdocs]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
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
