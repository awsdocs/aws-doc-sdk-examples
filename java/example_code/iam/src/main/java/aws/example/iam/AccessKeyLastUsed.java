// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.GetAccessKeyLastUsedRequest;
import com.amazonaws.services.identitymanagement.model.GetAccessKeyLastUsedResult;

/**
 * Displays the time that an access key was last used
 */
public class AccessKeyLastUsed {
    public static void main(String[] args) {

        final String USAGE = "To run this example, supply an access key id\n" +
                "Ex: AccessKeyLastUsed <access-key-id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String access_id = args[0];

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        GetAccessKeyLastUsedRequest request = new GetAccessKeyLastUsedRequest()
                .withAccessKeyId(access_id);

        GetAccessKeyLastUsedResult response = iam.getAccessKeyLastUsed(request);

        System.out.println("Access key was last used at: " +
                response.getAccessKeyLastUsed().getLastUsedDate());
    }
}
