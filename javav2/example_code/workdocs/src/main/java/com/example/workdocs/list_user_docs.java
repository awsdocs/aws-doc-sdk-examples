package com.example.workdocs;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.services.workdocs.WorkDocsClient;
import software.amazon.awssdk.services.workdocs.model.DescribeFolderContentsRequest;
import software.amazon.awssdk.services.workdocs.model.DescribeFolderContentsResponse;
import software.amazon.awssdk.services.workdocs.model.DescribeUsersRequest;
import software.amazon.awssdk.services.workdocs.model.DescribeUsersResponse;
import software.amazon.awssdk.services.workdocs.model.DocumentMetadata;
import software.amazon.awssdk.services.workdocs.model.DocumentVersionMetadata;
import software.amazon.awssdk.services.workdocs.model.User;

public class list_user_docs {

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

	public static void main(String[] args) throws Exception {
		// Based on WorkDocs dev guide code at http://docs.aws.amazon.com/workdocs/latest/developerguide/connect-workdocs-role.html

		final String USAGE = "\n" +
	            "To run this example, supply your organization ID and a user email\n" +
	            "\n" +
	            "Ex: list_user_docs <organizationId> <useremail>\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }
        
        String orgId = args[0];
        String userEmail = args[1];
        
		// Use the default client. Look at Window, Preferences, AWS Toolkit to see the values
		WorkDocsClient workDocs = WorkDocsClient.create();

		String folderId = get_user_folder(workDocs, orgId, userEmail);

		DescribeFolderContentsRequest dfc_request = DescribeFolderContentsRequest.builder().folderId(folderId).build();

		DescribeFolderContentsResponse result = workDocs.describeFolderContents(dfc_request);

		List<DocumentMetadata> userDocs = new ArrayList<>();

		userDocs.addAll(result.documents());

		System.out.println("Docs for user " + userEmail + ":");
		System.out.println("");

		for (DocumentMetadata doc: userDocs) {
			DocumentVersionMetadata md = doc.latestVersionMetadata();
			System.out.println("Name:          " + md.name());
			System.out.println("Size (bytes):  " + md.size());
			System.out.println("Last modified: " + md.modifiedTimestamp());
			System.out.println("Doc ID:        " + doc.id());
			System.out.println("");
		}
	}
}
