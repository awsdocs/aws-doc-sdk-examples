 
//snippet-sourcedescription:[list_user_docs.java demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[workdocs]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.workdocs.AmazonWorkDocs;
import com.amazonaws.services.workdocs.AmazonWorkDocsClientBuilder;
import com.amazonaws.services.workdocs.model.DescribeFolderContentsRequest;
import com.amazonaws.services.workdocs.model.DescribeFolderContentsResult;
import com.amazonaws.services.workdocs.model.DescribeUsersRequest;
import com.amazonaws.services.workdocs.model.DescribeUsersResult;
import com.amazonaws.services.workdocs.model.DocumentMetadata;
import com.amazonaws.services.workdocs.model.DocumentVersionMetadata;
import com.amazonaws.services.workdocs.model.User;

public class list_user_docs {

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

	public static void main(String[] args) throws Exception {
		// Based on WorkDocs dev guide code at http://docs.aws.amazon.com/workdocs/latest/developerguide/connect-workdocs-role.html

		// Use the default client. Look at Window, Preferences, AWS Toolkit to see the values
		AmazonWorkDocs workDocs = AmazonWorkDocsClientBuilder.defaultClient();

		// Set to the OrganizationId of your WorkDocs site.
		String orgId = "d-123456789c";

		// Set to the email address of a real user
		String userEmail = "nobody@amazon.com";

		String folderId = get_user_folder(workDocs, orgId, userEmail);

		DescribeFolderContentsRequest dfc_request = new DescribeFolderContentsRequest();
		dfc_request.setFolderId(folderId);

		DescribeFolderContentsResult result = workDocs.describeFolderContents(dfc_request);

		List<DocumentMetadata> userDocs = new ArrayList<>();

		userDocs.addAll(result.getDocuments());

		System.out.println("Docs for user " + userEmail + ":");
		System.out.println("");

		for (DocumentMetadata doc: userDocs) {
			DocumentVersionMetadata md = doc.getLatestVersionMetadata();
			System.out.println("Name:          " + md.getName());
			System.out.println("Size (bytes):  " + md.getSize());
			System.out.println("Last modified: " + md.getModifiedTimestamp());
			System.out.println("Doc ID:        " + doc.getId());
			System.out.println("");
		}
	}
}
