//snippet-sourcedescription:[download_user_doc.java demonstrates how to download a document from AWS Workdocs.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[workdocs]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[Doug-AWS]
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

import com.amazonaws.services.workdocs.AmazonWorkDocs;
import com.amazonaws.services.workdocs.AmazonWorkDocsClientBuilder;
import com.amazonaws.services.workdocs.model.DescribeFolderContentsRequest;
import com.amazonaws.services.workdocs.model.DescribeFolderContentsResult;
import com.amazonaws.services.workdocs.model.DescribeUsersRequest;
import com.amazonaws.services.workdocs.model.DescribeUsersResult;
import com.amazonaws.services.workdocs.model.DocumentMetadata;
import com.amazonaws.services.workdocs.model.DocumentSourceType;
import com.amazonaws.services.workdocs.model.DocumentVersionMetadata;
import com.amazonaws.services.workdocs.model.GetDocumentVersionRequest;
import com.amazonaws.services.workdocs.model.GetDocumentVersionResult;
import com.amazonaws.services.workdocs.model.InitiateDocumentVersionUploadRequest;
import com.amazonaws.services.workdocs.model.InitiateDocumentVersionUploadResult;
import com.amazonaws.services.workdocs.model.UploadMetadata;
import com.amazonaws.services.workdocs.model.User;

public class download_user_doc {

	private static String get_user_folder(AmazonWorkDocs workDocs, String orgId, String user) throws Exception {
		String user_folder = "";
		List<User> wdUsers = new ArrayList<>();
		DescribeUsersRequest request = new DescribeUsersRequest();

		request.setOrganizationId(orgId);

		String marker = null;

		do {
			request.setMarker(marker);
			request.setQuery(user);

			DescribeUsersResult result = workDocs.describeUsers(request);

			wdUsers.addAll(result.getUsers());
			marker = result.getMarker();
		} while (marker != null);

		// We should only have one user in this list
		int num_users = wdUsers.size();

		if (num_users != 1) {
			System.out.println("WARNING!!!");
			System.out.println("Found " + num_users + "  but expected to find only one");
		}

		for (User wdUser : wdUsers) {
			//DescribeFolderContentsRequest dfc_request = new DescribeFolderContentsRequest();
			user_folder = wdUser.getRootFolderId();
		}

		return user_folder;
	}

	private static Map<String, String> get_doc_info(AmazonWorkDocs workDocs, String orgId, String user, String docName) throws Exception {
		// Java code: http://docs.aws.amazon.com/workdocs/latest/developerguide/download-documents.html
		Map<String, String> map = new HashMap<String, String>();
		String folderId = get_user_folder(workDocs, orgId, user);

        if (folderId == "") {
            System.out.println("Could not get user folder");
        } else {

            DescribeFolderContentsRequest dfc_request = new DescribeFolderContentsRequest();
            dfc_request.setFolderId(folderId);

            DescribeFolderContentsResult result = workDocs.describeFolderContents(dfc_request);

            List<DocumentMetadata> userDocs = new ArrayList<>();

            userDocs.addAll(result.getDocuments());

            for (DocumentMetadata doc: userDocs) {
                DocumentVersionMetadata md = doc.getLatestVersionMetadata();

                if (docName.equals(md.getName())) {
                    map.put("doc_id", doc.getId());
                    map.put("version_id", md.getId());
                }
            }
        }

		return map;
	}

	private static String get_download_doc_url(AmazonWorkDocs workDocs, String docId, String versionId, String doc) {
		GetDocumentVersionRequest request = new GetDocumentVersionRequest();
		request.setDocumentId(docId);
		request.setVersionId(versionId);
		request.setFields("SOURCE");
		GetDocumentVersionResult result = workDocs.getDocumentVersion(request);

		return result.getMetadata().getSource().get(DocumentSourceType.ORIGINAL.name());
	}

	public static void main(String[] args) throws Exception {
		// Create default client
		AmazonWorkDocs workDocs = AmazonWorkDocsClientBuilder.defaultClient();

        // Set to the OrganizationId of your WorkDocs site.
		String orgId = "d-123456789c";

		// Set to the email address of a real user
		String userEmail = "nobody@amazon.com";

		// Set to the name of the doc
		String workdocsName = "test.txt";

        // Set to the full path to the doc
		String saveDocFullName = "C:\\test.txt";

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

		GetDocumentVersionRequest request = new GetDocumentVersionRequest();
		request.setDocumentId(doc_id);
		request.setVersionId(version_id);
		request.setFields("SOURCE");

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
