//snippet-sourcedescription:[ListUsers.cs demonstrates how to list the users for Amazon WorkDocs.]
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
    class ListUsers
    {
        public static void Sample()
        {
            // Based on WorkDocs dev guide code at http://docs.aws.amazon.com/workdocs/latest/developerguide/connect-workdocs-iam.html
            var wdClient = new AmazonWorkDocsClient(region: RegionEndpoint.USWest2);

            var request = new DescribeUsersRequest()
            {
                OrganizationId = "d-123456789c"
            };

            // Get all of the users
            var wdUsers = new List<User>();
            String marker = null;
            do
            {
                request.Marker = marker;
                var result = wdClient.DescribeUsers(request);
                wdUsers.AddRange(result.Users);
                marker = result.Marker;
            } while (String.IsNullOrEmpty(marker));

            Console.WriteLine("List of {0} users:", wdUsers.Count);
            foreach (var wdUser in wdUsers)
                Console.WriteLine("Firstname:{0} | Lastname:{1} | Email:{2} | root-folder-id:{3}\n",
                        wdUser.GivenName, wdUser.Surname, wdUser.EmailAddress, wdUser.RootFolderId);
        }
    }
}
