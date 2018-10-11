//snippet-sourceauthor: [ebattalio]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

﻿using System;
using System.Collections.Generic;
using System.Net;
using Amazon;
using Amazon.WorkDocs;
using Amazon.WorkDocs.Model;

namespace WorkdocsSample1
{
    class UploadUserDoc
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

        private static Dictionary<String, String> GetDocumentUploadInfo(AmazonWorkDocsClient wdClient, String orgId, String user, String docName)
        {
            String folderId = GetUserFolderId(wdClient, orgId, user);

            var request = new InitiateDocumentVersionUploadRequest()
            {
                Name = docName,
                ParentFolderId = folderId,
                ContentType = "application/octet-stream"
            };

            var result = wdClient.InitiateDocumentVersionUpload(request);

            var documentInfoMap = new Dictionary<String, String>();
            documentInfoMap.Add("documentId", result.Metadata.Id);
            documentInfoMap.Add("versionId", result.Metadata.LatestVersionMetadata.Id);
            documentInfoMap.Add("uploadURL", result.UploadMetadata.UploadUrl);

            return documentInfoMap;
        }

        public static void Sample()
        {
            // Based on WorkDocs dev guide code at http://docs.aws.amazon.com/workdocs/latest/developerguide/connect-workdocs-role.html
            var wdClient = new AmazonWorkDocsClient(region: RegionEndpoint.USWest2);

            String orgId = "d-123456789c";
            String userEmail = "nobody@amazon.com";
            String workdocsName = "test.txt";
            String uploadDocFullName = "C:\\test.txt";

            var docInfo = GetDocumentUploadInfo(wdClient, orgId, userEmail, workdocsName);
            if (docInfo.Count > 0)
            {
                String documentId = docInfo["documentId"];
                String versionId = docInfo["versionId"];
                String uploadURL = docInfo["uploadURL"];

                if (!String.IsNullOrEmpty(documentId) && !String.IsNullOrEmpty(versionId))
                {
                    using (var client = new WebClient())
                        client.UploadFile(uploadURL, uploadDocFullName);
                    var request = new UpdateDocumentVersionRequest()
                    {
                        DocumentId = documentId,
                        VersionId = versionId,
                        VersionStatus = DocumentVersionStatus.ACTIVE
                    };
                    wdClient.UpdateDocumentVersion(request);
                }
                else
                    Console.WriteLine("Could not get info about workdoc {0}", workdocsName);
            }
            else
                Console.WriteLine("Could not get info about workdoc {0}", workdocsName);
        }
    }
}
