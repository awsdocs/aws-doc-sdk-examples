import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.workdocs.AmazonWorkDocs;
import com.amazonaws.services.workdocs.AmazonWorkDocsClientBuilder;
import com.amazonaws.services.workdocs.model.DescribeUsersRequest;
import com.amazonaws.services.workdocs.model.DescribeUsersResult;
import com.amazonaws.services.workdocs.model.User;

public class list_users {

	public static void main(String[] args) {
		// Based on WorkDocs dev guide code at http://docs.aws.amazon.com/workdocs/latest/developerguide/connect-workdocs-iam.html

		// Use the default client. Look at Window, Preferences, AWS Toolkit to see the values
		AmazonWorkDocs workDocs = AmazonWorkDocsClientBuilder.defaultClient();

		List<User> wdUsers = new ArrayList<>();
		DescribeUsersRequest request = new DescribeUsersRequest();

		// Set to the OrganizationId of your WorkDocs site.
		request.setOrganizationId("d-123456789c");

		String marker = null;

		do {
			request.setMarker(marker);

			System.out.println("List of users:");

			DescribeUsersResult result = workDocs.describeUsers(request);

			wdUsers.addAll(result.getUsers());
			marker = result.getMarker();
		} while (marker != null);

		for (User wdUser : wdUsers) {
			System.out.printf("Firstname:%s | Lastname:%s | Email:%s | root-folder-id:%s\n",
					wdUser.getGivenName(), wdUser.getSurname(), wdUser.getEmailAddress(),
					wdUser.getRootFolderId());
		}
	}
}
