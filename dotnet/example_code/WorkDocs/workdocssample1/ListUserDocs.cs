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
using Amazon;
using Amazon.WorkDocs;
using Amazon.WorkDocs.Model;

namespace WorkdocsSample1
{
    class ListUserDocs
    {
        private static String GetUserFolderId(AmazonWorkDocsClient wdClient, String orgId, String user) 
        {
            var wdDescribeUsersRequest = new DescribeUsersRequest()
            {
                Marker = null,
                OrganizationId = orgId,
                Query = user
            };

			var wdDescribeUsersResponse = wdClient.DescribeUsers(wdDescribeUsersRequest);
            // Return the first matching user root folder id
            if (wdDescribeUsersResponse.Users.Count > 0)
                return wdDescribeUsersResponse.Users[0].RootFolderId;
            return String.Empty;
	    }

        public static void Sample()
        {
            // Based on WorkDocs dev guide code at http://docs.aws.amazon.com/workdocs/latest/developerguide/connect-workdocs-role.html
            var wdClient = new AmazonWorkDocsClient(region: RegionEndpoint.USWest2);

            String orgId = "d-123456789c";
            String userEmail = "nobody@amazon.com";
            String userFolderId = GetUserFolderId(wdClient, orgId, userEmail);

            var wdDescribeFolderContentsRequest = new DescribeFolderContentsRequest()
            {
                FolderId = userFolderId
            };

            // Get all of the folders
            var wdUserDocuments = new List<DocumentMetadata>();
            String marker = null;
            do
            {
                wdDescribeFolderContentsRequest.Marker = marker;
                var result = wdClient.DescribeFolderContents(wdDescribeFolderContentsRequest);
                wdUserDocuments.AddRange(result.Documents);
                marker = result.Marker;
            } while (String.IsNullOrEmpty(marker));

            Console.WriteLine("Docs for user {0}:", userEmail);
            foreach(var document in wdUserDocuments)
            {
                var md = document.LatestVersionMetadata;
                Console.WriteLine("Name:           {0}", md.Name);
                Console.WriteLine("Size (bytes):   {0}", md.Size);
                Console.WriteLine("Last modified:  {0}", md.ModifiedTimestamp);
                Console.WriteLine("Doc ID:         {0}", document.Id);
                Console.WriteLine();
            }
        }
    }
}
