//snippet-sourcedescription:[DownloadUserDoc.java demonstrates how to download a document from Amazon WorkDocs.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon WorkDocs]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[workdocs.java2.download_user_docs.complete]

package com.example.workdocs;

// snippet-start:[workdocs.java2.download_user_docs.import]
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.workdocs.WorkDocsClient;
import software.amazon.awssdk.services.workdocs.model.WorkDocsException;
import software.amazon.awssdk.services.workdocs.model.DescribeUsersRequest;
import software.amazon.awssdk.services.workdocs.model.User;
import software.amazon.awssdk.services.workdocs.model.DescribeUsersResponse;
import software.amazon.awssdk.services.workdocs.model.DescribeFolderContentsRequest;
import software.amazon.awssdk.services.workdocs.model.DescribeFolderContentsResponse;
import software.amazon.awssdk.services.workdocs.model.DocumentMetadata;
import software.amazon.awssdk.services.workdocs.model.DocumentVersionMetadata;
import software.amazon.awssdk.services.workdocs.model.GetDocumentVersionRequest;
import software.amazon.awssdk.services.workdocs.model.GetDocumentVersionResponse;
import software.amazon.awssdk.services.workdocs.model.DocumentSourceType;
// snippet-end:[workdocs.java2.download_user_docs.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DownloadUserDoc {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <organizationId> <userEmail> <workdocsName> <saveDocFullName> \n\n" +
                "Where:\n" +
                "    organizationId - Your organization Id value. You can obtain this value from the AWS Management Console. \n"+
                "    userEmail - A user email. \n"+
                "    workdocsName - The name of the document (for example, book.pdf). \n"+
                "    saveDocFullName - The path to save document (for example, C:/AWS/book2.pdf). \n";

        if (args.length != 4) {
              System.out.println(usage);
              System.exit(1);
        }

        String organizationId = args[0];
        String userEmail = args[1];
        String workdocsName = args[2];
        String saveDocFullName = args[3];
        Region region = Region.US_WEST_2;
        WorkDocsClient workDocs = WorkDocsClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        downloadDoc(workDocs, organizationId, userEmail, workdocsName, saveDocFullName );
        workDocs.close();
    }

    // snippet-start:[workdocs.java2.download_user_docs.main]
    public static void downloadDoc(WorkDocsClient workDocs,
                                   String orgId,
                                   String userEmail,
                                   String workdocsName,
                                   String saveDocFullName ){

        try {

             Map<String, String> map = getDocInfo(workDocs, orgId, userEmail, workdocsName);

            if (map.isEmpty()) {
                System.out.println("Could not get info about workdoc " + workdocsName);
                return;
            }

            String docId = map.get("doc_id");
            String versionId = map.get("version_id");

            if (docId.isEmpty() || versionId.isEmpty()) {
                System.out.println("Could not get info about workdoc " + workdocsName);
                return;
            }

            // Get the URL that is used to download the content.
            String downloadUrl = getDownloadDocUrl(workDocs, docId, versionId, workdocsName);

            // Get doc from provided URL.
            URL docUrl = new URL(downloadUrl);
            URLConnection urlConn = docUrl.openConnection();

            Path destination = Paths.get(saveDocFullName);

            try (final InputStream in = urlConn.getInputStream()) {
                Files.copy(in, destination);
                System.out.println("Downloaded " + workdocsName + " to: "+ saveDocFullName);
            }
            } catch(WorkDocsException | MalformedURLException e) {
                System.out.println(e.getLocalizedMessage());
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

    private static String getUserFolder(WorkDocsClient workDocs, String orgId, String user) {

        try {
            String userFolder = "";
            List<User> wdUsers = new ArrayList<>();
            DescribeUsersRequest request = DescribeUsersRequest.builder()
                .organizationId(orgId)
                .query(user)
                .build();

            String marker;

            do {
                DescribeUsersResponse response = workDocs.describeUsers(request);
                wdUsers.addAll(response.users());
                marker = response.marker();
            } while (marker != null);

            // We should only have one user in this list
            int numUsers = wdUsers.size();

            if (numUsers != 1) {
                System.out.println("WARNING!!!");
                System.out.println("Found " + numUsers + "  but expected to find only one");
            }

            for (User wdUser : wdUsers) {
                //DescribeFolderContentsRequest dfc_request = new DescribeFolderContentsRequest();
                userFolder = wdUser.rootFolderId();
            }

            return userFolder;
        } catch(WorkDocsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
        return "" ;
    }

    private static Map<String, String> getDocInfo(WorkDocsClient workDocs, String orgId, String user, String docName) {

        try {
            Map<String, String> map = new HashMap<>();
            String folderId = getUserFolder(workDocs, orgId, user);

            if (folderId.equals("")) {
                System.out.println("Could not get user folder");
            } else {

                DescribeFolderContentsRequest dfcRequest = DescribeFolderContentsRequest.builder()
                    .folderId(folderId)
                    .build();

                DescribeFolderContentsResponse response = workDocs.describeFolderContents(dfcRequest);
                List<DocumentMetadata> userDocs = new ArrayList<>();
                userDocs.addAll(response.documents());
                for (DocumentMetadata doc: userDocs) {
                    DocumentVersionMetadata md = doc.latestVersionMetadata();

                    if (docName.equals(md.name())) {
                     map.put("doc_id", doc.id());
                     map.put("version_id", md.id());
                }
              }
            }
          return map;

        } catch(WorkDocsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
        return null ;
    }

    private static String getDownloadDocUrl(WorkDocsClient workDocs, String docId, String versionId, String doc) {

        try {
            GetDocumentVersionRequest request = GetDocumentVersionRequest.builder()
                .documentId(docId)
                .versionId(versionId)
                .fields("SOURCE")
                .build();

            GetDocumentVersionResponse response = workDocs.getDocumentVersion(request);
            Map<DocumentSourceType,String> sourceDoc = response.metadata().source();
            Map.Entry<DocumentSourceType,String> entry = sourceDoc.entrySet().iterator().next();
            return entry.getValue();

        } catch(WorkDocsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }

        return "";
    }
    // snippet-end:[workdocs.java2.download_user_docs.main]
}

// snippet-end:[workdocs.java2.download_user_docs.complete]