using System;
using System.Collections.Generic;
using System.Net;
using Amazon;
using Amazon.WorkDocs;
using Amazon.WorkDocs.Model;

namespace WorkdocsSample1
{
    class DownloadUserDoc
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

        private static Dictionary<String, String> GetDocumentDownloadInfo(AmazonWorkDocsClient wdClient, String orgId, String user, String docName)
        {
            var documentInfoMap = new Dictionary<String, String>();
            var userFolderId = GetUserFolderId(wdClient, orgId, user);
            if(!String.IsNullOrEmpty(userFolderId))
            {
                var wdDescribeFolderContentsRequest = new DescribeFolderContentsRequest()
                {
                    FolderId = userFolderId
                };

                String marker = null;
                bool docNameFound = false;
                do
                {
                    wdDescribeFolderContentsRequest.Marker = marker;
                    var result = wdClient.DescribeFolderContents(wdDescribeFolderContentsRequest);
                    foreach(var document in result.Documents)
                    {
                        if(document.LatestVersionMetadata.Name == docName)
                        {
                            documentInfoMap.Add("documentId", document.Id);
                            documentInfoMap.Add("versionId", document.LatestVersionMetadata.Id);
                            docNameFound = true;
                            break;
                        }
                    }
                    marker = result.Marker;
                } while (!docNameFound && String.IsNullOrEmpty(marker));
            }
            else
                Console.WriteLine("Could not get user folder");

            return documentInfoMap;
        }

        private static String GetDownloadDocumentURL(AmazonWorkDocsClient workDocs, String docId, String versionId)
        {
            GetDocumentVersionRequest request = new GetDocumentVersionRequest()
            {
                DocumentId = docId,
                VersionId = versionId,
                Fields = "SOURCE"
            };
            var result = workDocs.GetDocumentVersion(request);
            return result.Metadata.Source[DocumentSourceType.ORIGINAL];
        }

        public static void Sample()
        {
            // Based on WorkDocs dev guide code at http://docs.aws.amazon.com/workdocs/latest/developerguide/connect-workdocs-role.html
            var wdClient = new AmazonWorkDocsClient(region: RegionEndpoint.USWest2);

            String orgId = "d-123456789c";
            String userEmail = "nobody@amazon.com";
            String workdocsName = "test.txt";
            String downloadDocFullName = "C:\\test.txt";

            var docInfo = GetDocumentDownloadInfo(wdClient, orgId, userEmail, workdocsName);
            if(docInfo.Count > 0)
            {
                String documentId = docInfo["documentId"];
                String versionId = docInfo["versionId"];

                if(!String.IsNullOrEmpty(documentId) && !String.IsNullOrEmpty(versionId))
                {
                    String downloadUrl = GetDownloadDocumentURL(wdClient, documentId, versionId);
                    using (var client = new WebClient())
                        client.DownloadFile(downloadUrl, downloadDocFullName);
                }
                else
                    Console.WriteLine("Could not get info about workdoc {0}", workdocsName);
            }
            else
                Console.WriteLine("Could not get info about workdoc {0}", workdocsName);
        }
    }
}
