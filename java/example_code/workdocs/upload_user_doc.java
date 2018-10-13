//snippet-sourceauthor: [soo-aws]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.amazonaws.services.workdocs.AmazonWorkDocs;
import com.amazonaws.services.workdocs.AmazonWorkDocsClientBuilder;
import com.amazonaws.services.workdocs.model.DescribeFolderContentsRequest;
import com.amazonaws.services.workdocs.model.DescribeUsersRequest;
import com.amazonaws.services.workdocs.model.DescribeUsersResult;
import com.amazonaws.services.workdocs.model.DocumentVersionStatus;
import com.amazonaws.services.workdocs.model.InitiateDocumentVersionUploadRequest;
import com.amazonaws.services.workdocs.model.InitiateDocumentVersionUploadResult;
import com.amazonaws.services.workdocs.model.UpdateDocumentVersionRequest;
import com.amazonaws.services.workdocs.model.UploadMetadata;
import com.amazonaws.services.workdocs.model.User;

public class upload_user_doc {

	private static String get_user_folder(AmazonWorkDocs workDocs, String orgId, String user) throws Exception {
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

		for (User wdUser : wdUsers) {
			DescribeFolderContentsRequest dfc_request = new DescribeFolderContentsRequest();
			return wdUser.getRootFolderId();
		}

		return "";
	}

	private static Map<String, String> get_doc_info(AmazonWorkDocs workDocs, String orgId, String user, String doc) throws Exception {
		String folderId = get_user_folder(workDocs, orgId, user);

		InitiateDocumentVersionUploadRequest request = new InitiateDocumentVersionUploadRequest();
		request.setParentFolderId(folderId);
		request.setName(doc);
		request.setContentType("application/octet-stream"); // MISSING SEMI-COLON!!!

		InitiateDocumentVersionUploadResult result = workDocs.initiateDocumentVersionUpload(request);

		UploadMetadata uploadMetadata = result.getUploadMetadata();

		Map<String, String> map = new HashMap<String, String>();

		map.put("doc_id", result.getMetadata().getId());
		map.put("version_id", result.getMetadata().getLatestVersionMetadata().getId());
		map.put("upload_url", uploadMetadata.getUploadUrl());

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
		com.amazonaws.util.IOUtils.copy(fileInputStream, outputStream);

		// Very misleading. Getting a 200 only means the call succeeded, not that the copy worked.
		return connection.getResponseCode();  // int where 200 == success
	}

	private static void complete_upload(AmazonWorkDocs workDocs, String doc_id, String version_id) throws Exception {
		UpdateDocumentVersionRequest request = new UpdateDocumentVersionRequest();
		request.setDocumentId(doc_id);
		request.setVersionId(version_id);
		request.setVersionStatus(DocumentVersionStatus.ACTIVE);
		workDocs.updateDocumentVersion(request);
	}

	public static void main(String[] args) throws Exception {
		// Based on WorkDocs dev guide code at http://docs.aws.amazon.com/workdocs/latest/developerguide/upload-documents.html

		// Use the default client. Look at Window, Preferences, AWS Toolkit to see the values
		AmazonWorkDocs workDocs = AmazonWorkDocsClientBuilder.defaultClient();

		// Set to the OrganizationId of your WorkDocs site.
		String orgId = "d-123456789c";

		// Set to the email address of a real user
		String userEmail = "nobody@amazon.com";

		// Set to the name of the doc
		String docName = "test.txt";

		// Set to the full path to the doc
		String doc = "C:\\test.txt";

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
