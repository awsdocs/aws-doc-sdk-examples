package com.example.workdocs;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;

import software.amazon.awssdk.services.workdocs.WorkDocsClient;
import software.amazon.awssdk.services.workdocs.model.DescribeUsersRequest;
import software.amazon.awssdk.services.workdocs.model.DescribeUsersResponse;
import software.amazon.awssdk.services.workdocs.model.DocumentVersionStatus;
import software.amazon.awssdk.services.workdocs.model.InitiateDocumentVersionUploadRequest;
import software.amazon.awssdk.services.workdocs.model.InitiateDocumentVersionUploadResponse;
import software.amazon.awssdk.services.workdocs.model.UpdateDocumentVersionRequest;
import software.amazon.awssdk.services.workdocs.model.UploadMetadata;
import software.amazon.awssdk.services.workdocs.model.User;

public class upload_user_doc {

	private static String get_user_folder(WorkDocsClient workDocs, String orgId, String user) throws Exception {
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
			}
			else {
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

	private static Map<String, String> get_doc_info(WorkDocsClient workDocs, String orgId, String user, String doc) throws Exception {
		String folderId = get_user_folder(workDocs, orgId, user);

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

	private static int start_doc_upload(String uploadUrl, String doc) throws Exception {
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
	}

	private static void complete_upload(WorkDocsClient workDocs, String doc_id, String version_id) throws Exception {
		UpdateDocumentVersionRequest request = UpdateDocumentVersionRequest.builder()
				.documentId(doc_id)
				.versionId(version_id)
				.versionStatus(DocumentVersionStatus.ACTIVE)
				.build();
		workDocs.updateDocumentVersion(request);
	}

	public static void main(String[] args) throws Exception {
		// Based on WorkDocs dev guide code at http://docs.aws.amazon.com/workdocs/latest/developerguide/upload-documents.html
		
		final String USAGE = "\n" +
	            "To run this example, supply your organization ID and a user email\n" +
	            "\n" +
	            "Ex: list_user_docs <organizationId> <useremail> <filename> <filepath>\n";

        if (args.length < 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String orgId = args[0];
        String userEmail = args[1];
 		String docName = args[2];
 		String doc = args[3];

		// Use the default client. Look at Window, Preferences, AWS Toolkit to see the values
		WorkDocsClient workDocs = WorkDocsClient.create();

		String doc_id, version_id, uploadUrl = "";
		int rc = 0;

		try {
			Map<String, String> map = get_doc_info(workDocs, orgId, userEmail, docName);

			doc_id = map.get("doc_id");
			version_id = map.get("version_id");
			uploadUrl = map.get("upload_url");
		} catch (Exception ex) {
			System.out.println("Caught exception " + ex.getMessage() + " calling start_doc_upload");
			return;
		}

		try {
			rc = start_doc_upload(uploadUrl, doc);

			if (rc != 200) {
				System.out.println("Error code uploading: " + rc);
			} else {
				System.out.println("Success uploading doc " + docName);
			}
		} catch (Exception ex) {
			System.out.println("Caught exception " + ex.getMessage() + " calling finish_doc_upload");
			return;
		}

		System.out.println("");

		try {
			complete_upload(workDocs, doc_id, version_id);
		} catch (Exception ex) {
			System.out.println("Caught exception " + ex.getMessage() + " calling complete_upload");
			return;
		}

		System.out.println("");
	}
}
