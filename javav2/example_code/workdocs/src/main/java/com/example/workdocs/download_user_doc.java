//snippet-sourcedescription:[download_user_doc.java demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
package com.example.workdocs;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import software.amazon.awssdk.services.workdocs.WorkDocsClient;
import software.amazon.awssdk.services.workdocs.model.DescribeFolderContentsRequest;
import software.amazon.awssdk.services.workdocs.model.DescribeFolderContentsResponse;
import software.amazon.awssdk.services.workdocs.model.DescribeUsersRequest;
import software.amazon.awssdk.services.workdocs.model.DescribeUsersResponse;
import software.amazon.awssdk.services.workdocs.model.DocumentMetadata;
import software.amazon.awssdk.services.workdocs.model.DocumentSourceType;
import software.amazon.awssdk.services.workdocs.model.DocumentVersionMetadata;
import software.amazon.awssdk.services.workdocs.model.GetDocumentVersionRequest;
import software.amazon.awssdk.services.workdocs.model.GetDocumentVersionResponse;
import software.amazon.awssdk.services.workdocs.model.User;

public class download_user_doc {

	private static String get_user_folder(WorkDocsClient workDocs, String orgId, String user) throws Exception {
		String user_folder = "";
		List<User> wdUsers = new ArrayList<>();
		DescribeUsersRequest request = DescribeUsersRequest.builder()
				.organizationId(orgId)
				.query(user)
				.build();

		String marker = null;

		do {

			DescribeUsersResponse Response = workDocs.describeUsers(request);

			wdUsers.addAll(Response.users());
			marker = Response.marker();
		} while (marker != null);

		// We should only have one user in this list
		int num_users = wdUsers.size();

		if (num_users != 1) {
			System.out.println("WARNING!!!");
			System.out.println("Found " + num_users + "  but expected to find only one");
		}

		for (User wdUser : wdUsers) {
			//DescribeFolderContentsRequest dfc_request = new DescribeFolderContentsRequest();
			user_folder = wdUser.rootFolderId();
		}

		return user_folder;
	}

	private static Map<String, String> get_doc_info(WorkDocsClient workDocs, String orgId, String user, String docName) throws Exception {
		// Java code: http://docs.aws.amazon.com/workdocs/latest/developerguide/download-documents.html
		Map<String, String> map = new HashMap<String, String>();
		String folderId = get_user_folder(workDocs, orgId, user);

        if (folderId == "") {
            System.out.println("Could not get user folder");
        } else {

            DescribeFolderContentsRequest dfc_request = DescribeFolderContentsRequest.builder()
            		.folderId(folderId)
            		.build();

            DescribeFolderContentsResponse Response = workDocs.describeFolderContents(dfc_request);

            List<DocumentMetadata> userDocs = new ArrayList<>();

            userDocs.addAll(Response.documents());

            for (DocumentMetadata doc: userDocs) {
                DocumentVersionMetadata md = doc.latestVersionMetadata();

                if (docName.equals(md.name())) {
                    map.put("doc_id", doc.id());
                    map.put("version_id", md.id());
                }
            }
        }

		return map;
	}

	private static String get_download_doc_url(WorkDocsClient workDocs, String docId, String versionId, String doc) {
		GetDocumentVersionRequest request = GetDocumentVersionRequest.builder()
				.documentId(docId)
				.versionId(versionId)
				.fields("SOURCE")
				.build();

		GetDocumentVersionResponse Response = workDocs.getDocumentVersion(request);

		return Response.metadata().source().get(DocumentSourceType.ORIGINAL.name());
	}

	public static void main(String[] args) throws Exception {
		final String USAGE = "\n" +
	            "To run this example, supply your organization ID and a user email\n" +
	            "\n" +
	            "Ex: download_user_doc <organizationId> <useremail>\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }
        
        String orgId = args[0];
        String userEmail = args[1];
        
		// Create default client
		WorkDocsClient workDocs = WorkDocsClient.create();

		// Set to the name of the doc
		String workdocsName = "test-json.txt";

        // Set to the full path to the doc
		String saveDocFullName = "/Users/soosung/Download/test.txt";

		Map<String, String> map = get_doc_info(workDocs, orgId, userEmail, workdocsName);

		if (map.isEmpty()) {
			System.out.println("Could not get info about workdoc " + workdocsName);
			return;
		}

		String doc_id = map.get("doc_id");
		String version_id = map.get("version_id");

		if (doc_id == "" || version_id == "") {
			System.out.println("Could not get info about workdoc " + workdocsName);
			return;
		}

		String downloadUrl = get_download_doc_url(workDocs, doc_id, version_id, workdocsName);

		// Get doc from provided URL
		URL doc_url = new URL(downloadUrl);
        URLConnection url_conn = doc_url.openConnection();

		final Path destination = Paths.get(saveDocFullName);

		try (final InputStream in = url_conn.getInputStream();) {
		    Files.copy(in, destination);

		    System.out.println("Downloaded " + workdocsName + " to: "+ saveDocFullName);
		}
	}
}
