//snippet-sourcedescription:[UploadUserDocs.java demonstrates how to upload a document to Amazon Workdocs.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon WorkDocs]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/06/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[workdocs.java2.upload_user_doc.complete]

package com.example.workdocs;

// snippet-start:[workdocs.java2.upload_user_doc.import]
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.workdocs.WorkDocsClient;
import software.amazon.awssdk.services.workdocs.model.InitiateDocumentVersionUploadRequest;
import software.amazon.awssdk.services.workdocs.model.InitiateDocumentVersionUploadResponse;
import software.amazon.awssdk.services.workdocs.model.UploadMetadata;
import software.amazon.awssdk.services.workdocs.model.DescribeUsersResponse;
import software.amazon.awssdk.services.workdocs.model.User;
import software.amazon.awssdk.services.workdocs.model.DescribeUsersRequest;
import software.amazon.awssdk.services.workdocs.model.WorkDocsException;
import software.amazon.awssdk.services.workdocs.model.UpdateDocumentVersionRequest;
import software.amazon.awssdk.services.workdocs.model.DocumentVersionStatus;
// snippet-end:[workdocs.java2.upload_user_doc.import]

public class UploadUserDocs {

    public static void main(String[] args) {
        // Based on WorkDocs dev guide code at http://docs.aws.amazon.com/workdocs/latest/developerguide/upload-documents.html

        final String USAGE = "\n" +
                "Usage:\n" +
                "    UploadUserDocs <organizationId> <userEmail> <docName> <docPath> \n\n" +
                "Where:\n" +
                "    organizationId - your organization Id value. You can obtain this value from the AWS Management Console. \n"+
                "    userEmail - a user email. \n"+
                "    docName - the name of the document (for example, book.pdf). \n"+
                "    docPath - the path where the document is located (for example, C:/AWS/book.pdf). \n";

        if (args.length != 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String organizationId = args[0];
        String userEmail = args[1];
        String docName = args[2];
        String docPath = args[3];

        Region region = Region.US_WEST_2;
        WorkDocsClient workDocs = WorkDocsClient.builder()
                .region(region)
                .build();

        uploadDoc(workDocs, organizationId, userEmail, docName, docPath) ;
        workDocs.close();
    }

    // snippet-start:[workdocs.java2.upload_user_doc.main]
    public static void uploadDoc(WorkDocsClient workDocs, String orgId, String userEmail, String docName, String docPath) {

        String docId ;
        String versionId ;
        String uploadUrl ;
        int statusValue = 0;
        Map<String, String> map = getDocInfo(workDocs, orgId, userEmail, docName);
        docId = map.get("doc_id");
        versionId = map.get("version_id");
        uploadUrl = map.get("upload_url");

        statusValue = startDocUpload(uploadUrl, docPath);

        if (statusValue != 200) {
             System.out.println("Error code uploading: " + statusValue);
        } else {
             System.out.println("Success uploading doc " + docName);
        }

        completeUpload(workDocs, docId, versionId);

    }

    private static Map<String, String> getDocInfo(WorkDocsClient workDocs, String orgId, String user, String doc) {

        String folderId = getUserFolder(workDocs, orgId, user);

        InitiateDocumentVersionUploadRequest request = InitiateDocumentVersionUploadRequest.builder()
                .parentFolderId(folderId)
                .name(doc)
                .contentType("application/octet-stream")
                .build();

        InitiateDocumentVersionUploadResponse result = workDocs.initiateDocumentVersionUpload(request);

        UploadMetadata uploadMetadata = result.uploadMetadata();

        Map<String, String> map = new HashMap<String, String>();

        map.put("doc_id", result.metadata().id());
        map.put("version_id", result.metadata().latestVersionMetadata().id());
        map.put("upload_url", uploadMetadata.uploadUrl());

        return map;
    }

    private static String getUserFolder(WorkDocsClient workDocs, String orgId, String user) {
        List<User> wdUsers = new ArrayList<>();

        String marker = null;

        do {
            DescribeUsersResponse result;

            if(marker == null) {
                DescribeUsersRequest request = DescribeUsersRequest.builder()
                        .organizationId(orgId)
                        .query(user)
                        .build();
                result = workDocs.describeUsers(request);
            } else {
                DescribeUsersRequest request = DescribeUsersRequest.builder()
                        .organizationId(orgId)
                        .query(user)
                        .marker(marker)
                        .build();
                result = workDocs.describeUsers(request);
            }

            wdUsers.addAll(result.users());
            marker = result.marker();
        } while (marker != null);

        for (User wdUser : wdUsers) {
            return wdUser.rootFolderId();
        }

        return "";
    }

    private static int startDocUpload(String uploadUrl, String doc) {

        try {

            URL url = new URL(uploadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");

            // Content-Type supplied here should match with the Content-Type set
            // in the InitiateDocumentVersionUpload request.
            connection.setRequestProperty("Content-Type","application/octet-stream");
            connection.setRequestProperty("x-amz-server-side-encryption", "AES256");
            File file = new File(doc);
            FileInputStream fileInputStream = new FileInputStream(file);
            OutputStream outputStream = connection.getOutputStream();
            IOUtils.copy(fileInputStream, outputStream);

            // Very misleading. Getting a 200 only means the call succeeded, not that the copy worked.
            return connection.getResponseCode();  // int where 200 == success

        } catch(WorkDocsException | ProtocolException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private static void completeUpload(WorkDocsClient workDocs, String docId, String versionId) {
        UpdateDocumentVersionRequest request = UpdateDocumentVersionRequest.builder()
                .documentId(docId)
                .versionId(versionId)
                .versionStatus(DocumentVersionStatus.ACTIVE)
                .build();
        workDocs.updateDocumentVersion(request);
    }
}
// snippet-end:[workdocs.java2.upload_user_doc.main]
// snippet-end:[workdocs.java2.upload_user_doc.complete]